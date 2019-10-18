package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSInputStream;
import io.vertx.ext.mongo.GridFSOutputStream;
import io.vertx.ext.mongo.GridFsDownloadOptions;
import io.vertx.ext.mongo.GridFsUploadOptions;
import io.vertx.ext.mongo.MongoGridFsClient;
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
  public MongoGridFsClient uploadByFileName(ReadStream<Buffer> stream, String fileName, Handler<AsyncResult<String>> resultHandler) {

    GridFSInputStream gridFsInputStream = new GridFSInputStreamImpl();

    stream.endHandler(endHandler -> gridFsInputStream.end());
    Pump.pump(stream, gridFsInputStream).start();

    Context context = vertx.getOrCreateContext();
    bucket.uploadFromStream(fileName, gridFsInputStream, (bsonId, throwable) -> {
      context.runOnContext(nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(bsonId.toHexString()));
        }
      });
    });

    return this;
  }

  @Override
  public Future<String> uploadByFileName(ReadStream<Buffer> stream, String fileName) {
    Promise<String> promise = Promise.promise();
    uploadByFileName(stream, fileName, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient uploadByFileNameWithOptions(ReadStream<Buffer> stream, String fileName, GridFsUploadOptions options, Handler<AsyncResult<String>> resultHandler) {

    GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
    uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
    if (options.getMetadata() != null) uploadOptions.metadata(new Document(options.getMetadata().getMap()));

    GridFSInputStream gridFsInputStream = new GridFSInputStreamImpl();

    stream.endHandler(endHandler -> gridFsInputStream.end());
    Pump.pump(stream, gridFsInputStream).start();

    Context context = vertx.getOrCreateContext();
    bucket.uploadFromStream(fileName, gridFsInputStream, uploadOptions, (bsonId, throwable) -> {
      context.runOnContext(nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(bsonId.toHexString()));
        }
      });
    });

    return this;
  }

  @Override
  public Future<String> uploadByFileNameWithOptions(ReadStream<Buffer> stream, String fileName, GridFsUploadOptions options) {
    Promise<String> promise = Promise.promise();
    uploadByFileNameWithOptions(stream, fileName, options, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient uploadFile(String fileName, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    uploadFileWithOptions(fileName, null, resultHandler);
    return this;
  }

  @Override
  public Future<String> uploadFile(String fileName) {
    Promise<String> promise = Promise.promise();
    uploadFile(fileName, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient uploadFileWithOptions(String fileName, GridFsUploadOptions options, Handler<AsyncResult<String>> resultHandler) {
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
  public Future<String> uploadFileWithOptions(String fileName, GridFsUploadOptions options) {
    Promise<String> promise = Promise.promise();
    uploadFileWithOptions(fileName, options, promise);
    return promise.future();
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
  public Future<Void> delete(String id) {
    Promise<Void> promise = Promise.promise();
    delete(id, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient downloadByFileName(WriteStream<Buffer> stream, String fileName, Handler<AsyncResult<Long>> resultHandler) {
    GridFSOutputStream gridFsOutputStream = new GridFSOutputStreamImpl(stream);
    Context context = vertx.getOrCreateContext();
    bucket.downloadToStream(fileName, gridFsOutputStream, (length, throwable) -> {
      context.runOnContext(nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(length));
        }
      });
    });

    return this;
  }

  @Override
  public Future<Long> downloadByFileName(WriteStream<Buffer> stream, String fileName) {
    Promise<Long> promise = Promise.promise();
    downloadByFileName(stream, fileName, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient downloadByFileNameWithOptions(WriteStream<Buffer> stream, String fileName, GridFsDownloadOptions options, Handler<AsyncResult<Long>> resultHandler) {
    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();

    GridFSOutputStream gridFsOutputStream = new GridFSOutputStreamImpl(stream);
    Context context = vertx.getOrCreateContext();
    bucket.downloadToStream(fileName, gridFsOutputStream, downloadOptions, (length, throwable) -> {
      context.runOnContext(nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(length));
        }
      });
    });

    return this;
  }

  @Override
  public Future<Long> downloadByFileNameWithOptions(WriteStream<Buffer> stream, String fileName, GridFsDownloadOptions options) {
    Promise<Long> promise = Promise.promise();
    downloadByFileNameWithOptions(stream, fileName, options, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient downloadById(WriteStream<Buffer> stream, String id, Handler<AsyncResult<Long>> resultHandler) {
    ObjectId objectId = new ObjectId(id);
    GridFSOutputStream gridFsOutputStream = new GridFSOutputStreamImpl(stream);
    Context context = vertx.getOrCreateContext();
    bucket.downloadToStream(objectId, gridFsOutputStream, (length, throwable) -> {
      context.runOnContext(nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(length));
        }
      });
    });

    return this;
  }

  @Override
  public Future<Long> downloadById(WriteStream<Buffer> stream, String id) {
    Promise<Long> promise = Promise.promise();
    downloadById(stream, id, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient downloadFile(String fileName, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    return downloadFileAs(fileName, fileName, resultHandler);
  }

  @Override
  public Future<Long> downloadFile(String fileName) {
    Promise<Long> promise = Promise.promise();
    downloadFile(fileName, promise);
    return promise.future();
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
        bucket.downloadToStream(fileName, gridFSOutputStream, (result, error) -> {
          file.close();
          Context context = vertx.getOrCreateContext();
          context.runOnContext(v -> {
            if (error != null) {
              resultHandler.handle(Future.failedFuture(error));
            } else {
              resultHandler.handle(Future.succeededFuture(result));
            }
          });
        });

      } else {
        resultHandler.handle(Future.failedFuture(asyncFileAsyncResult.cause()));
      }
    });

    return this;
  }

  @Override
  public Future<Long> downloadFileAs(String fileName, String newFileName) {
    Promise<Long> promise = Promise.promise();
    downloadFileAs(fileName, newFileName, promise);
    return promise.future();
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
        bucket.downloadToStream(objectId, gridFSOutputStream, (result, error) -> {
          file.close();
          Context context = vertx.getOrCreateContext();
          context.runOnContext(v -> {
            if (error != null) {
              resultHandler.handle(Future.failedFuture(error));
            } else {
              resultHandler.handle(Future.succeededFuture(result));
            }
          });
        });
      } else {
        resultHandler.handle(Future.failedFuture(asyncFileAsyncResult.cause()));
      }
    });

    return this;
  }

  @Override
  public Future<Long> downloadFileByID(String id, String fileName) {
    Promise<Long> promise = Promise.promise();
    downloadFileByID(id, fileName, promise);
    return promise.future();
  }

  @Override
  public MongoGridFsClient drop(Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");

    bucket.drop(clientImpl.wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<Void> drop() {
    Promise<Void> promise = Promise.promise();
    drop(promise);
    return promise.future();
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
  public Future<List<String>> findAllIds() {
    Promise<List<String>> promise = Promise.promise();
    findAllIds(promise);
    return promise.future();
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
              List<String> idsCopy = ids.stream().map(String::new).collect(Collectors.toList());
              resultHandler.handle(Future.succeededFuture(idsCopy));
            }
          });
        });

    return this;
  }

  @Override
  public Future<List<String>> findIds(JsonObject query) {
    Promise<List<String>> promise = Promise.promise();
    findIds(query, promise);
    return promise.future();
  }
}
