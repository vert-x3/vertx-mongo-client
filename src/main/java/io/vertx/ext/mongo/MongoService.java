package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoServiceImpl;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
@ProxyGen
public interface MongoService {

  static MongoService create(Vertx vertx, JsonObject config) {
    return new MongoServiceImpl(vertx, config);
  }

  static MongoService createEventBusProxy(Vertx vertx, String address) {
    return ProxyHelper.createProxy(MongoService.class, vertx, address);
  }

  void getCollection(String name, Handler<AsyncResult<MongoCollection>> resultHandler);

  void getCollectionWithWriteConcern(String name, WriteConcern wc, Handler<AsyncResult<MongoCollection>> resultHandler);

  void createCollection(String name, Handler<AsyncResult<Void>> resultHandler);

  void getCollectionNames(Handler<AsyncResult<List<String>>> resultHandler);

  void dropCollection(String name, Handler<AsyncResult<Void>> resultHandler);

  void runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  @ProxyIgnore
  void start();

  @ProxyIgnore
  void stop();

}
