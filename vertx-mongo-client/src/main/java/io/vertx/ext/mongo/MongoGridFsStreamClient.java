package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

@VertxGen
public interface MongoGridFsStreamClient {

  public static MongoGridFsStreamClient create(MongoGridFsClient client) {
    return client.getGridFsStreamClient();
  }

  void uploadByFileName(ReadStream stream, String fileName, Handler<AsyncResult<String>> resultHandler);
  void uploadByFileNameWithOptions(ReadStream stream, String fileName, UploadOptions options, Handler<AsyncResult<String>> resultHandler);

  void downloadByFileName(WriteStream stream, String fileName, Handler<AsyncResult<Long>> resultHandler);
  void downloadByFileNameWithOptions(WriteStream stream, String fileName, DownloadOptions options, Handler<AsyncResult<Long>> resultHandler);
  void downloadById(WriteStream stream, String id, Handler<AsyncResult<Long>> resultHandler);

}
