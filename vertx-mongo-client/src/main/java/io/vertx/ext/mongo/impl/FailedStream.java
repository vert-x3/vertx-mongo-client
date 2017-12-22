package io.vertx.ext.mongo.impl;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

class FailedStream implements ReadStream<JsonObject> {
  private final ClassNotFoundException e;

  public FailedStream(ClassNotFoundException e) {
    this.e = e;
  }

  @Override
  public ReadStream<JsonObject> exceptionHandler(Handler<Throwable> handler) {
    handler.handle(e);
    return this;
  }

  @Override
  public ReadStream<JsonObject> handler(Handler<JsonObject> handler) {
    return this;
  }

  @Override
  public ReadStream<JsonObject> pause() {
    return this;
  }

  @Override
  public ReadStream<JsonObject> resume() {
    return this;
  }

  @Override
  public ReadStream<JsonObject> endHandler(Handler<Void> endHandler) {
    return this;
  }

}
