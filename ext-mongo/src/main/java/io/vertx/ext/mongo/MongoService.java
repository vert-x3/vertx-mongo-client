package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.ServiceHelper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface MongoService {

  static MongoService create(Vertx vertx, JsonObject config) {
    return factory.create(vertx, config);
  }

  static MongoService createEventBusProxy(Vertx vertx, String address) {
    return factory.createEventBusProxy(vertx, address);
  }

  // Saves the object - returns the id

  void save(String collection, JsonObject document, String writeConcern, Handler<AsyncResult<String>> resultHandler);

  void insert(String collection, JsonObject document, String writeConcern, Handler<AsyncResult<String>> resultHandler);

  void update(String collection, JsonObject query, JsonObject update, String writeConcern, boolean upsert, boolean multi,
              Handler<AsyncResult<Void>> resultHandler);

  // Currently firehose of data as Mongo client doesn't support flow control
  void find(String collection, JsonObject query, JsonObject fields, JsonObject sort, int limit, int skip, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  void delete(String collection, JsonObject query, String writeConcern, Handler<AsyncResult<Void>> resultHandler);

  void createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

  void getCollections(Handler<AsyncResult<List<String>>> resultHandler);

  void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

  // Drop collection

  // Run command

  void runCommand(String collection, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  void start();

  void stop();

  // Collection stats ????

  // boilerplate
  static final MongoServiceFactory factory = ServiceHelper.loadFactory(MongoServiceFactory.class);
}
