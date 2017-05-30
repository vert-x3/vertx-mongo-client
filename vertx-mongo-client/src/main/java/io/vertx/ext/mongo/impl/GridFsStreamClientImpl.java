package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.DownloadOptions;
import io.vertx.ext.mongo.GridFSInputStream;
import io.vertx.ext.mongo.GridFSOutputStream;
import io.vertx.ext.mongo.MongoGridFsStreamClient;
import io.vertx.ext.mongo.UploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

public class GridFsStreamClientImpl implements MongoGridFsStreamClient {

  private final GridFSBucket bucket;
  private final Vertx vertx;

  public GridFsStreamClientImpl(Vertx vertx, GridFSBucket gridFSBucket) {
    this.vertx = vertx;
    this.bucket = gridFSBucket;
  }

  @Override
  public void uploadByFileName(ReadStream stream, String fileName, Handler<AsyncResult<String>> resultHandler) {

    GridFSInputStream gridFsInputStream = new GridFSInputStreamImpl();

    stream.endHandler(endHandler -> gridFsInputStream.end());
    Pump.pump(stream, gridFsInputStream).start();

    Context context = vertx.getOrCreateContext();
    bucket.uploadFromStream(fileName, gridFsInputStream, (bsonId, throwable) -> {
      context.runOnContext( nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(bsonId.toHexString()));
        }
      });
    });
  }

  @Override
  public void uploadByFileNameWithOptions(ReadStream stream, String fileName, UploadOptions options, Handler<AsyncResult<String>> resultHandler) {

    GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
    uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
    if (options.getMetadata() != null) uploadOptions.metadata(new Document(options.getMetadata().getMap()));

    GridFSInputStream gridFsInputStream = new GridFSInputStreamImpl();

    stream.endHandler(endHandler -> gridFsInputStream.end());
    Pump.pump(stream, gridFsInputStream).start();

    Context context = vertx.getOrCreateContext();
    bucket.uploadFromStream(fileName, gridFsInputStream, uploadOptions, (bsonId, throwable) -> {
      context.runOnContext( nothing -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        } else {
          resultHandler.handle(Future.succeededFuture(bsonId.toHexString()));
        }
      });
    });
  }

  @Override
  public void downloadByFileName(WriteStream stream, String fileName, Handler<AsyncResult<Long>> resultHandler) {

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
  }

  @Override
  public void downloadByFileNameWithOptions(WriteStream stream, String fileName, DownloadOptions options, Handler<AsyncResult<Long>> resultHandler) {

    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();
    downloadOptions.revision(options.getRevision());

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
  }

  @Override
  public void downloadById(WriteStream stream, String id, Handler<AsyncResult<Long>> resultHandler) {

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
  }

}
