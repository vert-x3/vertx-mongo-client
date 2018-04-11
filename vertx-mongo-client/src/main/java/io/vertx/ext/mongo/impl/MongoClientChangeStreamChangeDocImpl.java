package io.vertx.ext.mongo.impl;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientChange;
import io.vertx.ext.mongo.MongoClientChangeStream;

class MongoClientReplacedRootStreamImpl implements MongoClientChangeStream<JsonObject> {
  private final MongoIterableStream<JsonObject> cursor;

  MongoClientReplacedRootStreamImpl(MongoIterableStream<JsonObject> cursor) {
    this.cursor = cursor;
  }

  @Override
  public void close(Handler<AsyncResult<Void>> completionHandler) {
    cursor.close(completionHandler);
  }

  @Override
  public JsonObject lastResumeToken() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MongoClientChangeStream<JsonObject> exceptionHandler(Handler<Throwable> handler) {
    cursor.exceptionHandler(handler);
    return this;
  }

  @Override
  public MongoClientChangeStream<JsonObject> handler(@Nullable Handler<JsonObject> handler) {
    cursor.handler(handler);
    return this;
  }

  @Override
  public MongoClientChangeStream<JsonObject> pause() {
    cursor.pause();
    return this;
  }

  @Override
  public MongoClientChangeStream<JsonObject> resume() {
    cursor.resume();
    return this;
  }

  @Override
  public MongoClientChangeStream<JsonObject> endHandler(@Nullable Handler<Void> endHandler) {
    cursor.endHandler(endHandler);
    return this;
  }
}

class MongoClientChangeStreamChangeDocImpl implements MongoClientChangeStream<MongoClientChange> {
  private final MongoIterableStream<ChangeStreamDocument<JsonObject>> cursor;
  private JsonObject lastResumeToken;

  MongoClientChangeStreamChangeDocImpl(MongoIterableStream<ChangeStreamDocument<JsonObject>> cursor) {
    this.cursor = cursor;
  }

  @Override
  public MongoClientChangeStream<MongoClientChange> exceptionHandler(Handler<Throwable> handler) {
    cursor.exceptionHandler(handler);
    return this;
  }

  @Override
  public MongoClientChangeStream<MongoClientChange> handler(@Nullable Handler<MongoClientChange> handler) {
    cursor.handler(csd -> {
      final MongoClientChange mcc = new MongoClientChange(csd);
      lastResumeToken = mcc.getResumeToken();
      handler.handle(mcc);
    });
    return this;
  }

  @Override
  public MongoClientChangeStream<MongoClientChange> pause() {
    cursor.pause();
    return this;
  }

  @Override
  public MongoClientChangeStream<MongoClientChange> resume() {
    cursor.resume();
    return this;
  }

  @Override
  public MongoClientChangeStream<MongoClientChange> endHandler(@Nullable Handler<Void> handler) {
    cursor.endHandler(handler);
    return this;
  }

  @Override
  public void close(Handler<AsyncResult<Void>> handler) {
    cursor.close(handler);
  }

  @Override
  public JsonObject lastResumeToken() {
    return lastResumeToken;
  }
}
