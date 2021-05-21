package io.vertx.ext.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.impl.MongoClientImpl;

import java.util.List;
import java.util.UUID;

/**
 * A Vert.x service used to interact with MongoDB server instances.
 * <p>
 * Some of the operations might change <i>_id</i> field of passed {@link JsonObject} document.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface MongoClient {

  /**
   * The name of the default pool
   */
  String DEFAULT_POOL_NAME = "DEFAULT_POOL";

  /**
   * The name of the default database
   */
  String DEFAULT_DB_NAME = "DEFAULT_DB";

  /**
   * Create a Mongo client which maintains its own data source.
   *
   * @param vertx  the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  static MongoClient create(Vertx vertx, JsonObject config) {
    return new MongoClientImpl(vertx, config, UUID.randomUUID().toString());
  }

  /**
   * Create a Mongo client which shares its data source with any other Mongo clients created with the same
   * data source name
   *
   * @param vertx          the Vert.x instance
   * @param config         the configuration
   * @param dataSourceName the data source name
   * @return the client
   */
  static MongoClient createShared(Vertx vertx, JsonObject config, String dataSourceName) {
    return new MongoClientImpl(vertx, config, dataSourceName);
  }

  /**
   * Like {@link #createShared(io.vertx.core.Vertx, JsonObject, String)} but with the default data source name
   * @param vertx  the Vert.x instance
   * @param config  the configuration
   * @return the client
   */
  static MongoClient createShared(Vertx vertx, JsonObject config) {
    return new MongoClientImpl(vertx, config, DEFAULT_POOL_NAME);
  }

  /**
   * Constructor targeting the jvm, like standard constructor {@link #createShared(Vertx, JsonObject, String)}, but it accepts default mongoClientSettings
   * to configure mongo
   * @param vertx the Vert.x instance
   * @param config the configuration use only to provide objectId and database name
   * @param dataSourceName the data source name
   * @param settings the native java mongo settings
   * @return the client
   */
  @GenIgnore
  static MongoClient createWithMongoSettings(Vertx vertx, JsonObject config, String dataSourceName, MongoClientSettings settings) {
    return new MongoClientImpl(vertx, config, dataSourceName, settings);
  }

  /**
   * Save a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection  the collection
   * @param document  the document
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoClient save(String collection, JsonObject document, Handler<AsyncResult<@Nullable String>> resultHandler);

  Future<@Nullable String> save(String collection, JsonObject document);

  /**
   * Save a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection  the collection
   * @param document  the document
   * @param writeOption  the write option to use
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoClient saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption, Handler<AsyncResult<@Nullable String>> resultHandler);

  Future<@Nullable String> saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption);

  /**
   * Insert a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection  the collection
   * @param document  the document
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoClient insert(String collection, JsonObject document, Handler<AsyncResult<@Nullable String>> resultHandler);

  Future<@Nullable String> insert(String collection, JsonObject document);

  /**
   * Insert a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection  the collection
   * @param document  the document
   * @param writeOption  the write option to use
   * @param resultHandler  result handler will be provided with the id if document didn't already have one
   */
  @Fluent
  MongoClient insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption, Handler<AsyncResult<@Nullable String>> resultHandler);

  Future<@Nullable String> insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption);

  /**
   * Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient updateCollection(String collection, JsonObject query, JsonObject update,
                               Handler<AsyncResult<@Nullable MongoClientUpdateResult>> resultHandler);

  Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonObject update);

  /**
   * Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
                                          Handler<AsyncResult<@Nullable MongoClientUpdateResult>> resultHandler);

  Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options);

  /**
   * Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param replace  all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<@Nullable MongoClientUpdateResult>> resultHandler);

  Future<@Nullable MongoClientUpdateResult> replaceDocuments(String collection, JsonObject query, JsonObject replace);

  /**
   * Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   *
   * @param collection  the collection
   * @param query  query used to match the documents
   * @param replace  all matching documents will be replaced with this
   * @param options options to configure the replace
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<@Nullable MongoClientUpdateResult>> resultHandler);

  Future<@Nullable MongoClientUpdateResult> replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options);

  /**
   * Execute a bulk operation. Can insert, update, replace, and/or delete multiple documents with one request.
   *
   * @param collection
   *          the collection
   * @param operations
   *          the operations to execute
   * @param resultHandler
   *          will be called with a {@link MongoClientBulkWriteResult} when complete
   */
  @Fluent
  MongoClient bulkWrite(String collection, List<BulkOperation> operations,
                        Handler<AsyncResult<@Nullable MongoClientBulkWriteResult>> resultHandler);

  /**
   * Like {@link #bulkWrite(String, List, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientBulkWriteResult> bulkWrite(String collection, List<BulkOperation> operations);

  /**
   * Execute a bulk operation with the specified write options. Can insert, update, replace, and/or delete multiple
   * documents with one request.
   *
   * @param collection
   *          the collection
   * @param operations
   *          the operations to execute
   * @param bulkWriteOptions
   *          the write options
   * @param resultHandler
   *          will be called with a {@link MongoClientBulkWriteResult} when complete
   */
  @Fluent
  MongoClient bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions,
                                   Handler<AsyncResult<@Nullable MongoClientBulkWriteResult>> resultHandler);

  /**
   * Like {@link #bulkWriteWithOptions(String, List, BulkWriteOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientBulkWriteResult> bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions);

  /**
   * Find matching documents in the specified collection
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler  will be provided with list of documents
   */
  @Fluent
  MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  /**
   * Like {@link #find(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<List<JsonObject>> find(String collection, JsonObject query);

  /**
   * Find matching documents in the specified collection.
   * This method use batchCursor for returning each found document.
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @return a {@link ReadStream} emitting found documents
   */
  ReadStream<JsonObject> findBatch(String collection, JsonObject query);

  /**
   * Find matching documents in the specified collection, specifying options
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param options options to configure the find
   * @param resultHandler  will be provided with list of documents
   */
  @Fluent
  MongoClient findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  /**
   * Like {@link #findWithOptions(String, JsonObject, FindOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<List<JsonObject>> findWithOptions(String collection, JsonObject query, FindOptions options);

  /**
   * Find matching documents in the specified collection, specifying options.
   * This method use batchCursor for returning each found document.
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param options options to configure the find
   * @return a {@link ReadStream} emitting found documents
   */
  ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options);

  /**
   * Find a single matching document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param fields  the fields
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoClient findOne(String collection, JsonObject query, @Nullable JsonObject fields, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOne(String, JsonObject, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOne(String collection, JsonObject query, @Nullable JsonObject fields);

  /**
   * Like {@link #findOne(String, JsonObject, JsonObject, Handler)} but give an option to set ReadPreference
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  @Fluent
  MongoClient findOne(String collection, JsonObject query, @Nullable JsonObject fields, @Nullable ReadPreference readPreference, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOne(String, JsonObject, JsonObject)} but give an option to set ReadPreference
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  Future<@Nullable JsonObject> findOne(String collection, JsonObject query, @Nullable JsonObject fields, @Nullable ReadPreference readPreference);

  /**
   * Find a single matching document in the specified collection and update it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoClient findOneAndUpdate(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndUpdate(String, JsonObject, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndUpdate(String collection, JsonObject query, JsonObject update);

  /**
   * Find a single matching document in the specified collection and update it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param update used to describe how the documents will be updated
   * @param findOptions options to configure the find
   * @param updateOptions options to configure the update
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoClient findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndUpdateWithOptions(String, JsonObject, JsonObject, FindOptions, UpdateOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions);

  /**
   * Find a single matching document in the specified collection and replace it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param replace  the replacement document
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoClient findOneAndReplace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndReplace(String, JsonObject, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndReplace(String collection, JsonObject query, JsonObject replace);

  /**
   * Find a single matching document in the specified collection and replace it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param replace  the replacement document
   * @param findOptions options to configure the find
   * @param updateOptions options to configure the update
   * @param resultHandler will be provided with the document, if any
   */
  @Fluent
  MongoClient findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndReplaceWithOptions(String, JsonObject, JsonObject, FindOptions, UpdateOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions);

  /**
   * Find a single matching document in the specified collection and delete it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param resultHandler will be provided with the deleted document, if any
   */
  @Fluent
  MongoClient findOneAndDelete(String collection, JsonObject query, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndDelete(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndDelete(String collection, JsonObject query);

  /**
   * Find a single matching document in the specified collection and delete it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection  the collection
   * @param query  the query used to match the document
   * @param findOptions options to configure the find
   * @param resultHandler will be provided with the deleted document, if any
   */
  @Fluent
  MongoClient findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #findOneAndDeleteWithOptions(String, JsonObject, FindOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions);

  /**
   * Count matching documents in a collection.
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler will be provided with the number of matching documents
   */
  @Fluent
  MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  /**
   * Like {@link #count(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Long> count(String collection, JsonObject query);

  /**
   * Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<@Nullable MongoClientDeleteResult>> resultHandler);

  /**
   * Like {@link #removeDocuments(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientDeleteResult> removeDocuments(String collection, JsonObject query);

  /**
   * Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   *
   * @param collection  the collection
   * @param query  query used to match documents
   * @param writeOption  the write option to use
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption, Handler<AsyncResult<@Nullable MongoClientDeleteResult>> resultHandler);

  /**
   * Like {@link #removeDocumentsWithOptions(String, JsonObject, WriteOption, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientDeleteResult> removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption);

  /**
   * Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
   *
   * @param collection  the collection
   * @param query  query used to match document
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient removeDocument(String collection, JsonObject query, Handler<AsyncResult<@Nullable MongoClientDeleteResult>> resultHandler);

  /**
   * Like {@link #removeDocument(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientDeleteResult> removeDocument(String collection, JsonObject query);

  /**
   * Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   *
   * @param collection  the collection
   * @param query  query used to match document
   * @param writeOption  the write option to use
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption, Handler<AsyncResult<@Nullable MongoClientDeleteResult>> resultHandler);

  /**
   * Like {@link #removeDocumentWithOptions(String, JsonObject, WriteOption, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable MongoClientDeleteResult> removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption);

  /**
   * Create a new collection
   *
   * @param collectionName  the name of the collection
   * @param resultHandler  will be called when complete
   */
  @Fluent
  MongoClient createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #createCollection(String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> createCollection(String collectionName);

  /**
   * Get a list of all collections in the database.
   *
   * @param resultHandler  will be called with a list of collections.
   */
  @Fluent
  MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler);

  /**
   * Like {@link #getCollections(Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<List<String>> getCollections();

  /**
   * Drop a collection
   *
   * @param collection  the collection
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #dropCollection(String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> dropCollection(String collection);

  /**
   * Creates an index.
   *
   * @param collection  the collection
   * @param key  A document that contains the field and value pairs where the field is the index key and the value
   *             describes the type of index for that field. For an ascending index on a field,
   *             specify a value of 1; for descending index, specify a value of -1.
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #createIndex(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> createIndex(String collection, JsonObject key);

  /**
   * Creates an index.
   *
   * @param collection  the collection
   * @param key  A document that contains the field and value pairs where the field is the index key and the value
   *             describes the type of index for that field. For an ascending index on a field,
   *             specify a value of 1; for descending index, specify a value of -1.
   * @param options  the options for the index
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient createIndexWithOptions(String collection, JsonObject key, IndexOptions options, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #createIndexWithOptions(String, JsonObject, IndexOptions, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> createIndexWithOptions(String collection, JsonObject key, IndexOptions options);

  /**
   * creates an indexes
   * @param collection the collection
   * @param indexes A model that contains pairs of document and indexOptions, document contains the field and value pairs
   *                where the field is the index key and the value describes the type of index for that field.
   *                For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient createIndexes(String collection, List<IndexModel> indexes, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #createIndexes(String, List, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> createIndexes(String collection, List<IndexModel> indexes);

  /**
   * Get all the indexes in this collection.
   *
   * @param collection  the collection
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * Like {@link #listIndexes(String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<JsonArray> listIndexes(String collection);

  /**
   * Drops the index given its name.
   *
   * @param collection  the collection
   * @param indexName the name of the index to remove
   * @param resultHandler will be called when complete
   */
  @Fluent
  MongoClient dropIndex(String collection, String indexName, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Like {@link #dropIndex(String, String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> dropIndex(String collection, String indexName);

  /**
   * Run an arbitrary MongoDB command.
   *
   * @param commandName  the name of the command
   * @param command  the command
   * @param resultHandler  will be called with the result.
   */
  @Fluent
  MongoClient runCommand(String commandName, JsonObject command, Handler<AsyncResult<@Nullable JsonObject>> resultHandler);

  /**
   * Like {@link #runCommand(String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<@Nullable JsonObject> runCommand(String commandName, JsonObject command);

  /**
   * Gets the distinct values of the specified field name.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *
   * @param collection  the collection
   * @param fieldName  the field name
   * @param resultHandler  will be provided with array of values.
   */
  @Fluent
  MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * Like {@link #distinct(String, String, String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<JsonArray> distinct(String collection, String fieldName, String resultClassname);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *
   * @param collection  the collection
   * @param fieldName  the field name
   * @param query the query
   * @param resultHandler  will be provided with array of values.
   */
  @Fluent
  MongoClient distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * Like {@link #distinctWithQuery(String, String, String, JsonObject, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query);

  /**
   * Gets the distinct values of the specified field name.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection  the collection
   * @param fieldName  the field name
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection  the collection
   * @param fieldName  the field name
   * @param query the query
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection the collection
   * @param fieldName  the field name
   * @param query      the query
   * @param batchSize  the number of documents to load in a batch
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize);


  /**
   * Run aggregate MongoDB command with default {@link AggregateOptions}.
   *
   * @param collection the collection
   * @param pipeline   aggregation pipeline to be executed
   */
  ReadStream<JsonObject> aggregate(final String collection, final JsonArray pipeline);

  /**
   * Run aggregate MongoDB command.
   *
   * @param collection the collection
   * @param pipeline   aggregation pipeline to be executed
   * @param options    options to configure the aggregation command
   */
  ReadStream<JsonObject> aggregateWithOptions(String collection, final JsonArray pipeline, final AggregateOptions options);

  /**
   * Watch the collection change.
   * @param collection the collection
   * @param pipeline   watching pipeline to be executed
   * @param withUpdatedDoc whether to get updated fullDocument for "update" operation
   * @param batchSize  the number of documents to load in a batch
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  ReadStream<ChangeStreamDocument<JsonObject>> watch(String collection, JsonArray pipeline, boolean withUpdatedDoc, int batchSize);

  /**
   * Creates a {@link MongoGridFsClient} used to interact with Mongo GridFS.
   *
   * @param resultHandler  the {@link MongoGridFsClient} to interact with the bucket named bucketName
   */
  @Fluent
  MongoClient createDefaultGridFsBucketService(Handler<AsyncResult<MongoGridFsClient>> resultHandler);

  /**
   * Like {@link #createDefaultGridFsBucketService(Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<MongoGridFsClient> createDefaultGridFsBucketService();

  /**
   * Creates a {@link MongoGridFsClient} used to interact with Mongo GridFS.
   *
   * @param bucketName  the name of the GridFS bucket
   * @param resultHandler  the {@link MongoGridFsClient} to interact with the bucket named bucketName
   */
  @Fluent
  MongoClient createGridFsBucketService(String bucketName, Handler<AsyncResult<MongoGridFsClient>> resultHandler);

  /**
   * Like {@link #createGridFsBucketService(String, Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<MongoGridFsClient> createGridFsBucketService(String bucketName);

  /**
   * Like {@link #close(Handler)} but returns a {@code Future} of the asynchronous result
   */
  Future<Void> close();

  /**
   * Close the client and release its resources
   */
  void close(Handler<AsyncResult<Void>> handler);

}
