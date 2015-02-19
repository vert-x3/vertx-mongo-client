package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
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
 * A Vert.x service used to interact with MongoDB server instances.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
@ProxyGen
public interface MongoService {

  /**
   * Create a service
   *
   * @param vertx  the Vert.x instance
   * @param config  the config
   * @return the service
   */
  static MongoService create(Vertx vertx, JsonObject config) {
    return new MongoServiceImpl(vertx, config);
  }

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

  /**
   * Save a document in the specified collection
   *
   * @param collection  the collection
   * @param document  the document
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  /**
   * Save a document in the specified collection with the specified write option
   *
   * @param collection  the collection
   * @param document  the document
   * @param writeOption  the write option to use
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  /**
   * Insert a document in the specified collection
   *
   * @param collection  the collection
   * @param document  the document
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler);

  /**
   * Insert a document in the specified collection with the specified write option
   *
   * @param collection  the collection
   * @param document  the document
   * @param writeOption  the write option to use
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler);

  /**
   * Update matching documents in the specified collection
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Update matching documents in the specified collection, specifying options
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Replace matching documents in the specified collection
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param replace  all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Replace matching documents in the specified collection, specifying options
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param replace  all matching documents will be replaced with this
   * @param options options to configure the replace
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Find matching documents in the specified collection
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler  will be provided with list of documents
   */
  @Fluent
  MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  /**
   * Find matching documents in the specified collection, specifying options
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param options options to configure the find
   * @param resultHandler  will be provided with list of documents
   */
  @Fluent
  MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  /**
   * Find a single matching document in the specified collection
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param fields  the fields
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * Count matching documents in a collection.
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler will be provided with the number of matching documents
   */
  @Fluent
  MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  /**
   * Remove matching documents from a collection
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Remove matching documents from a collection with the specified write option
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param writeOption  the write option to use
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Remove a single matching document from a collection
   *
   * @param collection  the collection
   * @param query  query used to match document
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Remove a single matching document from a collection with the specified write option
   *
   * @param collection  the collection
   * @param query  query used to match document
   * @param writeOption  the write option to use
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Create a new collection
   *
   * @param collectionName  the name of the collection
   * @param resultHandler  will be called when complete
   */
  @Fluent
  MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get a list of all collections in the database.
   *
   * @param resultHandler  will be called with a list of collections.
   */
  @Fluent
  MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler);

  /**
   * Drop a collection
   *
   * @param collection  the collection
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Run an arbitrary MongoDB command.
   *
   * @param command  the command
   * @param resultHandler  will be called with the result.
   */
  @Fluent
  MongoService runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * Start the service
   */
  @ProxyIgnore
  void start();

  /**
   * Stop the service
   */
  @ProxyIgnore
  void stop();

}
