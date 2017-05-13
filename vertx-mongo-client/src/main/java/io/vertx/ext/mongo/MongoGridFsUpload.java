package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface MongoGridFsUpload {

  /**
   * Accepts a string of base 64 encoded bytes and adds them to the file to be saved in gridfs.
   *
   * @param base64EncodedBytes string of base 64 encoded bytes
   * @param resultHandler the number of bytes saved to the file.
   */
  @Fluent MongoGridFsUpload uploadBuffer(GridFsBuffer buffer, Handler<AsyncResult<Integer>> resultHandler);

  /**
   * Ends the upload of bytes saved in gridfs.
   *
   * @param resultHandler the ID of the file saved in gridfs.
   */
  @Fluent MongoGridFsUpload end(Handler<AsyncResult<String>> resultHandler);

  /**
   * Close the client and release its resources
   */
  void close();
}
