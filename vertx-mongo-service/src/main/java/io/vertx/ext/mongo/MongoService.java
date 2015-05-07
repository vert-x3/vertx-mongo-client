package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@ProxyGen
@VertxGen
public interface MongoService extends MongoClient {

  /**
   * Create a proxy to a service that is deployed somewhere on the event bus
   *
   * @param vertx  the Vert.x instance
   * @param address  the address the service is listening on on the event bus
   * @return the service
   */
  static MongoService createEventBusProxy(Vertx vertx, String address) {
    return ProxyHelper.createProxy(MongoService.class, vertx, address);
  }


  @Override
  @Fluent
  MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  @Override
  @Fluent
  MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  @Override
  @Fluent
  MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  @Override
  @Fluent
  MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  @Override
  @Fluent
  MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  @Override
  @Fluent
  MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  @Override
  @Fluent
  MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  @Override
  @Fluent
  MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler);

  @Override
  @Fluent
  MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @ProxyIgnore
  void close();
}
