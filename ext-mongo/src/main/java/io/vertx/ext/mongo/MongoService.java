package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoServiceImpl;
import io.vertx.proxygen.ProxyHelper;

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

  // Saves the object - returns the id

  void save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  void saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  void insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  void insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  void update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler);

  void updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  void replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler);

  void replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  // Currently firehose of data as Mongo client doesn't support flow control
  void find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  void findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  void count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  void remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  void removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  void removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  void removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  void createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

  void getCollections(Handler<AsyncResult<List<String>>> resultHandler);

  void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

  void runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  @ProxyIgnore
  void start();

  @ProxyIgnore
  void stop();

}
