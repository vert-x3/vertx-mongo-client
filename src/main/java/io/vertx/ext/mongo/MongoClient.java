package io.vertx.ext.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.impl.MongoClientImpl;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

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
   *
   * @param vertx  the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  static MongoClient createShared(Vertx vertx, JsonObject config) {
    return new MongoClientImpl(vertx, config, DEFAULT_POOL_NAME);
  }

  /**
   * Constructor targeting the jvm, like standard constructor {@link #createShared(Vertx, JsonObject, String)}, but it accepts default mongoClientSettings
   * to configure mongo
   *
   * @param vertx          the Vert.x instance
   * @param config         the configuration use only to provide objectId and database name
   * @param dataSourceName the data source name
   * @param settings       the native java mongo settings
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
   * @param collection    the collection
   * @param document      the document
   * @return a future provided with the id if document didn't already have one
   */
  Future<@Nullable String> save(String collection, JsonObject document);

  /**
   * Save a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection    the collection
   * @param document      the document
   * @param writeOption   the write option to use
   * @return a future provided with the id if document didn't already have one
   */
  Future<@Nullable String> saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption);

  /**
   * Insert a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection    the collection
   * @param document      the document
   * @return a future provided with the id if document didn't already have one
   */
  Future<@Nullable String> insert(String collection, JsonObject document);

  /**
   * Insert a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   *
   * @param collection    the collection
   * @param document      the document
   * @param writeOption   the write option to use
   * @return a future provided with the id if document didn't already have one
   */
  Future<@Nullable String> insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption);

  /**
   * Update matching documents in the specified collection and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param update        used to describe how the documents will be updated
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonObject update);

  /**
   * Use an aggregation pipeline to update documents in the specified collection and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param update        used to describe how the documents will be updated
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonArray update);

  /**
   * Update matching documents in the specified collection, specifying options and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param update        used to describe how the documents will be updated
   * @param options       options to configure the update
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options);

  /**
   * Use an aggregation pipeline to update documents in the specified collection, specifying options and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param update        aggregation pipeline used to describe how documents will be updated
   * @param options       options to configure the update
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonArray update, UpdateOptions options);

  /**
   * Replace matching documents in the specified collection and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param replace       all matching documents will be replaced with this
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> replaceDocuments(String collection, JsonObject query, JsonObject replace);

  /**
   * Replace matching documents in the specified collection, specifying options and return the handler with {@code MongoClientUpdateResult} result
   *
   * @param collection    the collection
   * @param query         query used to match the documents
   * @param replace       all matching documents will be replaced with this
   * @param options       options to configure the replace
   * @return a future notified with a {@link MongoClientUpdateResult} when complete
   */
  Future<@Nullable MongoClientUpdateResult> replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options);

  /**
   * Execute a bulk operation. Can insert, update, replace, and/or delete multiple documents with one request.
   *
   * @param collection    the collection
   * @param operations    the operations to execute
   * @return a future notified with a {@link MongoClientBulkWriteResult} when complete
   */
  Future<@Nullable MongoClientBulkWriteResult> bulkWrite(String collection, List<BulkOperation> operations);

  /**
   * Execute a bulk operation with the specified write options. Can insert, update, replace, and/or delete multiple
   * documents with one request.
   *
   * @param collection       the collection
   * @param operations       the operations to execute
   * @param bulkWriteOptions the write options
   * @return a future notified with a {@link MongoClientBulkWriteResult} when complete
   */
  Future<@Nullable MongoClientBulkWriteResult> bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions);

  /**
   * Find matching documents in the specified collection
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @return a future provided with list of documents
   */
  Future<List<JsonObject>> find(String collection, JsonObject query);

  /**
   * Find matching documents in the specified collection.
   * This method use batchCursor for returning each found document.
   *
   * @param collection the collection
   * @param query      query used to match documents
   * @return a {@link ReadStream} emitting found documents
   */
  ReadStream<JsonObject> findBatch(String collection, JsonObject query);

  /**
   * Find matching documents in the specified collection, specifying options
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @param options       options to configure the find
   * @return a future provided with list of documents
   */
  Future<List<JsonObject>> findWithOptions(String collection, JsonObject query, FindOptions options);

  /**
   * Find matching documents in the specified collection, specifying options.
   * This method use batchCursor for returning each found document.
   *
   * @param collection the collection
   * @param query      query used to match documents
   * @param options    options to configure the find
   * @return a {@link ReadStream} emitting found documents
   */
  ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options);

  /**
   * Find a single matching document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param fields        the fields
   * @return a future provided with the document, if any
   */
  Future<@Nullable JsonObject> findOne(String collection, JsonObject query, @Nullable JsonObject fields);

  /**
   * Find a single matching document in the specified collection and update it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param update        used to describe how the documents will be updated
   * @return a future provided with the document, if any
   */
  Future<@Nullable JsonObject> findOneAndUpdate(String collection, JsonObject query, JsonObject update);

  /**
   * Find a single matching document in the specified collection and update it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param update        used to describe how the documents will be updated
   * @param findOptions   options to configure the find
   * @param updateOptions options to configure the update
   * @return a future provided with the document, if any
   */
  Future<@Nullable JsonObject> findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions);

  /**
   * Find a single matching document in the specified collection and replace it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param replace       the replacement document
   * @return a future provided with the document, if any
   */
  Future<@Nullable JsonObject> findOneAndReplace(String collection, JsonObject query, JsonObject replace);

  /**
   * Find a single matching document in the specified collection and replace it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param replace       the replacement document
   * @param findOptions   options to configure the find
   * @param updateOptions options to configure the update
   * @return a future provided with the document, if any
   */
  Future<@Nullable JsonObject> findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions);

  /**
   * Find a single matching document in the specified collection and delete it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @return a future provided with the deleted document, if any
   */
  Future<@Nullable JsonObject> findOneAndDelete(String collection, JsonObject query);

  /**
   * Find a single matching document in the specified collection and delete it.
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   *
   * @param collection    the collection
   * @param query         the query used to match the document
   * @param findOptions   options to configure the find
   * @return a future provided with the deleted document, if any
   */
  Future<@Nullable JsonObject> findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions);

  /**
   * Count matching documents in a collection.
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @return a future provided with the number of matching documents
   */
  Future<Long> count(String collection, JsonObject query);

  /**
   * Count matching documents in a collection.
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @param countOptions
   * @return a future provided with the number of matching documents
   */
  Future<Long> countWithOptions(String collection, JsonObject query, CountOptions countOptions);

  /**
   * Remove matching documents from a collection and return the handler with {@code MongoClientDeleteResult} result
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @return a future notified with a {@link MongoClientDeleteResult} when complete
   */
  Future<@Nullable MongoClientDeleteResult> removeDocuments(String collection, JsonObject query);

  /**
   * Remove matching documents from a collection with the specified write option and return the handler with {@code MongoClientDeleteResult} result
   *
   * @param collection    the collection
   * @param query         query used to match documents
   * @param writeOption   the write option to use
   * @return a future notified with a {@link MongoClientDeleteResult} when complete
   */
  Future<@Nullable MongoClientDeleteResult> removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption);

  /**
   * Remove a single matching document from a collection and return the handler with {@code MongoClientDeleteResult} result
   *
   * @param collection    the collection
   * @param query         query used to match document
   * @return a future notified with a {@link MongoClientDeleteResult} when complete
   */
  Future<@Nullable MongoClientDeleteResult> removeDocument(String collection, JsonObject query);

  /**
   * Remove a single matching document from a collection with the specified write option and return the handler with {@code MongoClientDeleteResult} result
   *
   * @param collection    the collection
   * @param query         query used to match document
   * @param writeOption   the write option to use
   * @return a future notified with a {@link MongoClientDeleteResult} when complete
   */
  Future<@Nullable MongoClientDeleteResult> removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption);

  /**
   * Create a new collection
   *
   * @param collectionName the name of the collection
   * @return a future notified once complete
   */
  Future<Void> createCollection(String collectionName);

  /**
   * Create a new collection with options
   *
   * @param collectionName    the name of the collection
   * @param collectionOptions options of the collection
   * @return a future notified once complete
   */
  Future<Void> createCollectionWithOptions(String collectionName, CreateCollectionOptions collectionOptions);

  /**
   * Get a list of all collections in the database.
   *
   * @return a future notified with a list of collections.
   */
  Future<List<String>> getCollections();

  /**
   * Drop a collection
   *
   * @param collection    the collection
   * @return a future notified when complete
   */
  Future<Void> dropCollection(String collection);

  /**
   * Rename a collection
   *
   * @param oldCollectionName the name of the collection
   * @param newCollectionName the new name of the collection
   * @return a future notified when complete
   */
  Future<Void> renameCollection(String oldCollectionName, String newCollectionName);

  /**
   * Rename a collection
   *
   * @param oldCollectionName the name of the collection
   * @param newCollectionName the new name of the collection
   * @param collectionOptions options of the collection
   * @return a future notified when complete
   */
  Future<Void> renameCollectionWithOptions(String oldCollectionName, String newCollectionName, RenameCollectionOptions collectionOptions);

  /**
   * Creates an index.
   *
   * @param collection    the collection
   * @param key           A document that contains the field and value pairs where the field is the index key and the value
   *                      describes the type of index for that field. For an ascending index on a field,
   *                      specify a value of 1; for descending index, specify a value of -1.
   * @return a future notified when complete
   */
  Future<Void> createIndex(String collection, JsonObject key);

  /**
   * Creates an index.
   *
   * @param collection    the collection
   * @param key           A document that contains the field and value pairs where the field is the index key and the value
   *                      describes the type of index for that field. For an ascending index on a field,
   *                      specify a value of 1; for descending index, specify a value of -1.
   * @param options       the options for the index
   * @return a future notified when complete
   */
  Future<Void> createIndexWithOptions(String collection, JsonObject key, IndexOptions options);

  /**
   * creates an indexes
   *
   * @param collection    the collection
   * @param indexes       A model that contains pairs of document and indexOptions, document contains the field and value pairs
   *                      where the field is the index key and the value describes the type of index for that field.
   *                      For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @return a future notified when complete
   */
  Future<Void> createIndexes(String collection, List<IndexModel> indexes);

  /**
   * Get all the indexes in this collection.
   *
   * @param collection    the collection
   * @return a future notified when complete
   */
  Future<JsonArray> listIndexes(String collection);

  /**
   * Drops the index given its name.
   *
   * @param collection    the collection
   * @param indexName     the name of the index to remove
   * @return a future notified when complete
   */
  Future<Void> dropIndex(String collection, String indexName);

  /**
   * Drops the index given the keys used to create it.
   *
   * @param collection the collection
   * @param key        the key(s) of the index to remove
   * @return a future notified when complete
   */
  Future<Void> dropIndex(String collection, JsonObject key);

  /**
   * Run an arbitrary MongoDB command.
   *
   * @param commandName   the name of the command
   * @param command       the command
   * @return a future notified with the result.
   */
  Future<@Nullable JsonObject> runCommand(String commandName, JsonObject command);

  /**
   * Gets the distinct values of the specified field name.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *
   * @param collection    the collection
   * @param fieldName     the field name
   * @return a future provided with array of values.
   */
  Future<JsonArray> distinct(String collection, String fieldName, String resultClassname);

  /**
   * Gets the distinct values of the specified field name.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *  @param collection      the collection
   * @param fieldName       the field name
   * @param distinctOptions options (e.g. collation)
   * @return a future provided with array of values.
   */
  Future<JsonArray> distinct(String collection, String fieldName, String resultClassname, DistinctOptions distinctOptions);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *
   * @param collection    the collection
   * @param fieldName     the field name
   * @param query         the query
   * @return a future provided with array of values.
   */
  Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   *  @param collection      the collection
   * @param fieldName       the field name
   * @param query           the query
   * @param distinctOptions options (e.g. collation)
   * @return a future provided with array of values.
   */
  Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, DistinctOptions distinctOptions);

  /**
   * Gets the distinct values of the specified field name.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection the collection
   * @param fieldName  the field name
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname);

  /**
   * Gets the distinct values of the specified field name.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection      the collection
   * @param fieldName       the field name
   * @param distinctOptions options (e.g. collation)
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname, DistinctOptions distinctOptions);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection the collection
   * @param fieldName  the field name
   * @param query      the query
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query);

  /**
   * Gets the distinct values of the specified field name filtered by specified query.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection      the collection
   * @param fieldName       the field name
   * @param query           the query
   * @param distinctOptions options (e.g. collation)
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, DistinctOptions distinctOptions);

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
   * Gets the distinct values of the specified field name filtered by specified query.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   *
   * @param collection      the collection
   * @param fieldName       the field name
   * @param query           the query
   * @param batchSize       the number of documents to load in a batch
   * @param distinctOptions options (e.g. collation)
   * @return a {@link ReadStream} emitting json fragments
   */
  ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize, DistinctOptions distinctOptions);

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
   *
   * @param collection     the collection
   * @param pipeline       watching pipeline to be executed
   * @param withUpdatedDoc whether to get updated fullDocument for "update" operation
   * @param batchSize      the number of documents to load in a batch
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  ReadStream<ChangeStreamDocument<JsonObject>> watch(String collection, JsonArray pipeline, boolean withUpdatedDoc, int batchSize);

  /**
   * Creates a {@link MongoGridFsClient} used to interact with Mongo GridFS.
   *
   * @return a future notified with the {@link MongoGridFsClient} to interact with the bucket named bucketName
   */
  Future<MongoGridFsClient> createDefaultGridFsBucketService();

  /**
   * Creates a {@link MongoGridFsClient} used to interact with Mongo GridFS.
   *
   * @param bucketName    the name of the GridFS bucket
   * @return a future notified with the {@link MongoGridFsClient} to interact with the bucket named bucketName
   */
  Future<MongoGridFsClient> createGridFsBucketService(String bucketName);

  /**
   * Starts a session and returns a {@link MongoSession} which is a wrapper over the client
   * that also allows manual control of the transaction.
   * By default, the session is closed automatically after the transaction ends.
   *
   * @return a future notified with a {@link MongoSession} used to control the transaction scope
   */
  Future<MongoSession> startSession();

  /**
   * Starts a session and returns a {@link MongoSession} which is a wrapper over the client
   * that also allows manual control of the transaction. The specified {@link ClientSessionOptions}
   * will be applied to the session and all of its transactions.
   * By default, the session is closed automatically after the transaction ends,
   * this can be also overruled using {@link ClientSessionOptions#setAutoClose(boolean)}}.
   *
   * @param options    options to use for the session and transactions
   *
   * @return a future notified with a {@link MongoSession} used to control the transaction scope
   */
  Future<MongoSession> startSession(ClientSessionOptions options);

  /**
   * Starts a session and executes the passed operations in a distributed transaction.
   * By default, the session is closed automatically after the transaction ends.
   *
   * @param operations     the operations to execute inside the transaction
   * @param <T>      the return type from the operations function
   *
   * @return a future notified with the result of operations
   */
  <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations);

  /**
   * Starts a session and executes the passed operations in a distributed transaction.
   * The specified {@link ClientSessionOptions} will be applied to the session and all of its transactions.
   * By default, the session is closed automatically after the transaction ends,
   * this can be also overruled using {@link ClientSessionOptions#setAutoClose(boolean)}}.
   *
   * @param operations       the operations to execute inside the transaction
   *                   @param options    options to use for the session and transaction
   * @param <T>        the return type from the operations function
   *
   * @return a future notified with the result of operations
   */
  <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations, ClientSessionOptions options);

  /**
   * Close the client and release its resources
   */
  Future<Void> close();

}
