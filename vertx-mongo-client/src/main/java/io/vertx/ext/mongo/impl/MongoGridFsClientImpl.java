package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.async.client.gridfs.GridFSDownloadStream;
import com.mongodb.async.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.vertx.core.*;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import io.vertx.ext.mongo.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link MongoGridFsClient}. This implementation is based on the async driver
 * provided by Mongo.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class MongoGridFsClientImpl extends MongoBaseImpl implements MongoGridFsClient {

  GridFSBucket bucket;

  public MongoGridFsClientImpl(Vertx vertx, JsonObject config, GridFSBucket bucket) {

    super(vertx, config);
    this.bucket = bucket;

  }

  @Override
  public MongoGridFsClient uploadFile(String fileName, Handler<AsyncResult<String>> resultHandler) {

    uploadFileWithOptions(fileName, null, resultHandler);
    return this;
  }

  @Override
  public MongoGridFsClient uploadFileWithOptions(String fileName, UploadOptions options, Handler<AsyncResult<String>> resultHandler) {

    OpenOptions openOptions = new OpenOptions().setRead(true);

    vertx.fileSystem().open(fileName, openOptions, asyncResultHandler -> {
      if (asyncResultHandler.succeeded()) {
        AsyncFile file = asyncResultHandler.result();

        GridFSInputStream gridFSInputStream = GridFSInputStream.create();
        //AlternateGridFsInputStream gridFSInputStream = new AlternateGridFsInputStream(vertx);
        file.endHandler(endHandler -> gridFSInputStream.end());
        Pump.pump(file, gridFSInputStream).start();

        if (options == null) {
          bucket.uploadFromStream(fileName, gridFSInputStream, convertCallback(resultHandler, ObjectId::toHexString));
        } else {
          GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
          uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
          if (options.getMetadata() != null) uploadOptions.metadata(new Document(options.getMetadata().getMap()));
          bucket.uploadFromStream(fileName, gridFSInputStream, uploadOptions, convertCallback(resultHandler, ObjectId::toHexString));
        }
      } else {
        resultHandler.handle(Future.failedFuture(asyncResultHandler.cause()));
      }
    });
    return this;
  }

  @Override
  public void close() {
  }

  @Override
  public MongoGridFsClient delete(String id, Handler<AsyncResult<Void>> resultHandler) {

    ObjectId objectId = new ObjectId(id);
    bucket.delete(objectId, wrapCallback(resultHandler));

    return this;
  }

  @Override
  public MongoGridFsClient downloadBuffer(String fileName, Handler<AsyncResult<MongoGridFsDownload>> resultHandler) {
    GridFSDownloadStream stream = bucket.openDownloadStream(fileName);
    MongoGridFsDownload download = new MongoGridFsDownloadImpl(stream, vertx, config);
    resultHandler.handle(Future.succeededFuture(download));
    return this;
  }

  @Override
  public MongoGridFsClient downloadFile(String fileName, Handler<AsyncResult<Long>> resultHandler) {
    return downloadFileAs(fileName, fileName, resultHandler);
  }

  @Override
  public MongoGridFsClient downloadFileAs(String fileName, String newFileName, Handler<AsyncResult<Long>> resultHandler) {

    OpenOptions options = new OpenOptions().setWrite(true);

    vertx.fileSystem().open(newFileName, options, asyncFileAsyncResult -> {
      if (asyncFileAsyncResult.succeeded()) {
        AsyncFile file = asyncFileAsyncResult.result();
        GridFSOutputStream gridFSOutputStream = GridFSOutputStream.create(file);
        bucket.downloadToStream(fileName, gridFSOutputStream, wrapCallback(resultHandler));
      } else {
        resultHandler.handle(Future.failedFuture(asyncFileAsyncResult.cause()));
      }
    });

    return this;
  }

  @Override
  public MongoGridFsClient drop(Handler<AsyncResult<Void>> resultHandler) {

    bucket.drop(wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoGridFsClient findAllIds(Handler<AsyncResult<List<String>>> resultHandler) {

    List<String> ids = new ArrayList<>();

    Context context = vertx.getOrCreateContext();
    bucket.find()
      .forEach(gridFSFile -> {
          System.out.println("adding file");
          ids.add(gridFSFile.getObjectId().toHexString());
        },
        (result, throwable) -> {
          context.runOnContext(v -> {
            if (throwable != null) {
              resultHandler.handle(Future.failedFuture(throwable));
            } else {
              System.out.println("returning " + ids.size() + " files");
              resultHandler.handle(Future.succeededFuture(ids));
            }
          });
        });

    return this;
  }

  @Override
  public MongoGridFsClient findIds(JsonObject query, Handler<AsyncResult<List<String>>> resultHandler) {

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    System.out.println("Query: " + encodedQuery.encodePrettily());

    //Document document = new Document("metadata.nick_name", "Puhi the eel");
    List<String> ids = new ArrayList<>();

    Context context = vertx.getOrCreateContext();
    bucket.find(bquery)
      .forEach(gridFSFile -> {
          System.out.println("Adding file");
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
  public MongoGridFsClient uploadBuffer(String fileName, Handler<AsyncResult<MongoGridFsUpload>> resultHandler) {

    GridFSUploadStream stream = bucket.openUploadStream(fileName);
    MongoGridFsUpload upload = new MongoGridFsUploadImpl(stream, vertx, config);
    resultHandler.handle(Future.succeededFuture(upload));

    return this;
  }

  @Override
  public MongoGridFsClient uploadBufferWithOptions(String fileName, UploadOptions options, Handler<AsyncResult<MongoGridFsUpload>> resultHandler) {
    GridFSUploadStream stream;
    if (options == null) {
      stream = bucket.openUploadStream(fileName);
    } else {
      GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
      uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
      System.out.println("options: " + options.toJson().encodePrettily());
      if (options.getMetadata() != null) uploadOptions.metadata(new Document(options.getMetadata().getMap()));
      stream = bucket.openUploadStream(fileName, uploadOptions);
    }
    MongoGridFsUpload upload = new MongoGridFsUploadImpl(stream, vertx, config);
    resultHandler.handle(Future.succeededFuture(upload));
    return this;
  }

}
