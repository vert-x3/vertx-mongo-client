package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface MongoGridFsDownload {

  /**
   * Read bytes and returns them as a base 64 encoded string. Use a decoder to turn
   * them back into an array of bytes.
   *
   * @param bufferSize the maximum number of bytes to return
   * @param resultHandler  a string of base 64 encoded bytes.
   */
  @Fluent
  MongoGridFsDownload read(Integer bufferSize, Handler<AsyncResult<String>> resultHandler);

  /**
   * Close the client and release its resources
   */
  void close();
}
