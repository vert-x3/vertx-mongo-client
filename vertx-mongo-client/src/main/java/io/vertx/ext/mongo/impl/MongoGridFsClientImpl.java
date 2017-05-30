package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import io.vertx.ext.mongo.GridFSInputStream;
import io.vertx.ext.mongo.GridFSOutputStream;
import io.vertx.ext.mongo.MongoGridFsClient;
import io.vertx.ext.mongo.MongoGridFsStreamClient;
import io.vertx.ext.mongo.UploadOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link MongoGridFsClient}. This implementation is based on the async driver
 * provided by Mongo.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class MongoGridFsClientImpl implements MongoGridFsClient {

  private final GridFSBucket bucket;
  private final MongoClientImpl clientImpl;
  private final Vertx vertx;

  public MongoGridFsClientImpl(Vertx vertx, MongoClientImpl mongoClient, GridFSBucket gridFSBucket) {
    this.vertx = vertx;
    this.clientImpl = mongoClient;
    this.bucket = gridFSBucket;
  }

  @Override
  public MongoGridFsClient uploadFile(String fileName, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    uploadFileWithOptions(fileName, null, resultHandler);
    return this;
  }

  @Override
  public MongoGridFsClient uploadFileWithOptions(String fileName, UploadOptions options, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    OpenOptions openOptions = new OpenOptions().setRead(true);

    vertx.fileSystem().open(fileName, openOptions, asyncResultHandler -> {
      if (asyncResultHandler.succeeded()) {
        AsyncFile file = asyncResultHandler.result();

        GridFSInputStream gridFSInputStream = GridFSInputStream.create();
        file.endHandler(endHandler -> gridFSInputStream.end());
        Pump.pump(file, gridFSInputStream).start();

        if (options == null) {
          bucket.uploadFromStream(fileName, gridFSInputStream, clientImpl.convertCallback(resultHandler, ObjectId::toHexString));
        } else {
          GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
          uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
          if (options.getMetadata() != null) {
            uploadOptions.metadata(new Document(options.getMetadata().getMap()));
          }
          bucket.uploadFromStream(fileName, gridFSInputStream, uploadOptions, clientImpl.convertCallback(resultHandler, ObjectId::toHexString));
        }
      } else {
        resultHandler.handle(Future.failedFuture(asyncResultHandler.cause()));
      }
    });
    return this;
  }

  @Override
  public MongoGridFsStreamClient getGridFsStreamClient() {
    return new GridFsStreamClientImpl(vertx, bucket);
  }

  @Override
  public void close() {
  }

  @Override
  public MongoGridFsClient delete(String id, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(id, "id cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    ObjectId objectId = new ObjectId(id);
    bucket.delete(objectId, clientImpl.wrapCallback(resultHandler));

    return this;
  }

  @Override
  public MongoGridFsClient downloadFile(String fileName, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    return downloadFileAs(fileName, fileName, resultHandler);
  }

  @Override
  public MongoGridFsClient downloadFileAs(String fileName, String newFileName, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(newFileName, "newFileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    vertx.fileSystem().open(newFileName, options, asyncFileAsyncResult -> {
      if (asyncFileAsyncResult.succeeded()) {
        AsyncFile file = asyncFileAsyncResult.result();
        GridFSOutputStream gridFSOutputStream = GridFSOutputStream.create(file);
        bucket.downloadToStream(fileName, gridFSOutputStream, clientImpl.wrapCallback(resultHandler));
      } else {
        resultHandler.handle(Future.failedFuture(asyncFileAsyncResult.cause()));
      }
    });

    return this;
  }

  @Override
  public MongoGridFsClient downloadFileByID(String id, String fileName, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    vertx.fileSystem().open(fileName, options, asyncFileAsyncResult -> {
      if (asyncFileAsyncResult.succeeded()) {
        AsyncFile file = asyncFileAsyncResult.result();
        GridFSOutputStream gridFSOutputStream = GridFSOutputStream.create(file);
        ObjectId objectId = new ObjectId(id);
        bucket.downloadToStream(objectId, gridFSOutputStream, clientImpl.wrapCallback(resultHandler));
      } else {
        resultHandler.handle(Future.failedFuture(asyncFileAsyncResult.cause()));
      }
    });

    return this;
  }

  @Override
  public MongoGridFsClient drop(Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");

    bucket.drop(clientImpl.wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoGridFsClient findAllIds(Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");

    List<String> ids = new ArrayList<>();

    Context context = vertx.getOrCreateContext();
    bucket.find()
      .forEach(gridFSFile -> {
          ids.add(gridFSFile.getObjectId().toHexString());
        },
        (result, throwable) -> {
          context.runOnContext(v -> {
            if (throwable != null) {
              resultHandler.handle(Future.failedFuture(throwable));
            } else {
              resultHandler.handle(Future.succeededFuture(ids));
            }
          });
        });

    return this;
  }

  @Override
  public MongoGridFsClient findIds(JsonObject query, Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = clientImpl.encodeKeyWhenUseObjectId(query);

    Bson bquery = clientImpl.wrap(encodedQuery);

    List<String> ids = new ArrayList<>();

    Context context = vertx.getOrCreateContext();
    bucket.find(bquery)
      .forEach(gridFSFile -> {
          ids.add(gridFSFile.getObjectId().toHexString());
        },
        (result, throwable) -> {
          context.runOnContext(voidHandler -> {
            if (throwable != null) {
              resultHandler.handle(Future.failedFuture(throwable));
            } else {
              List<String> idsCopy = ids.stream().map(id -> new String(id)).collect(Collectors.toList());
              resultHandler.handle(Future.succeededFuture(idsCopy));
            }
          });
        });

    return this;
  }
}
