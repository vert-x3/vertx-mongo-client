package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.serviceproxy.ServiceProxyBuilder;

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
   * @param vertx   the Vert.x instance
   * @param address the address the service is listening on on the event bus
   * @return the service
   */
  static MongoService createEventBusProxy(Vertx vertx, String address) {
    return new ServiceProxyBuilder(vertx)
      .setAddress(address)
      .build(MongoService.class);
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
  MongoService updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

  @Override
  @Fluent
  MongoService updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

  @Override
  @Fluent
  MongoService replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

  @Override
  @Fluent
  MongoService replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler);

  @Override
  @Fluent
  MongoService bulkWrite(String collection, List<BulkOperation> operations,
                         Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler);

  @Override
  @Fluent
  MongoService bulkWriteWithOptions(String collection, List<BulkOperation> operations,
                                    BulkWriteOptions bulkWriteOptions, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler);

  @Override
  @Fluent
  MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  @Override
  @GenIgnore
  default ReadStream<JsonObject> findBatch(String collection, JsonObject query) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Fluent
  MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  @Override
  @GenIgnore
  default ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Fluent
  MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndUpdate(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndReplace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndDelete(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  @Override
  @Fluent
  MongoService removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);

  @Override
  @Fluent
  MongoService removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);

  @Override
  @Fluent
  MongoService removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);

  @Override
  @Fluent
  MongoService removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);

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
  MongoService createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService createIndexWithOptions(String collection, JsonObject key, IndexOptions options, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler);

  @Override
  @Fluent
  MongoService dropIndex(String collection, String indexName, Handler<AsyncResult<Void>> resultHandler);

  @Override
  @Fluent
  MongoService runCommand(String commandName, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler);

  @Override
  @Fluent
  MongoService distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler);

  @Override
  @GenIgnore
  default ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Fluent
  MongoService distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, Handler<AsyncResult<JsonArray>> resultHandler);

  @Override
  @GenIgnore
  default ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) {
    throw new UnsupportedOperationException();
  }

  @Override
  @GenIgnore
  default ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize) {
    throw new UnsupportedOperationException();
  }

  @Override
  @ProxyIgnore
  void close();
}
