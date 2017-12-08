package io.vertx.ext.mongo.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.BulkWriteOptions;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.IndexOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceImpl implements MongoService {

  private final MongoClient client;

  public MongoServiceImpl(MongoClient client) {
    this.client = client;
  }

  @Override
  public MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    client.save(collection, document, resultHandler);
    return this;
  }

  @Override
  public MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption,
      Handler<AsyncResult<String>> resultHandler) {
    client.saveWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  @Override
  public MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    client.insert(collection, document, resultHandler);
    return this;
  }

  @Override
  public MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption,
      Handler<AsyncResult<String>> resultHandler) {
    client.insertWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService update(String collection, JsonObject query, JsonObject update,
      Handler<AsyncResult<Void>> resultHandler) {
    client.update(collection, query, update, resultHandler);
    return this;
  }

  @Override
  public MongoService updateCollection(String collection, JsonObject query, JsonObject update,
      Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.updateCollection(collection, query, update, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
      Handler<AsyncResult<Void>> resultHandler) {
    client.updateWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  @Override
  public MongoService updateCollectionWithOptions(String collection, JsonObject query, JsonObject update,
      UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.updateCollectionWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService replace(String collection, JsonObject query, JsonObject replace,
      Handler<AsyncResult<Void>> resultHandler) {
    client.replace(collection, query, replace, resultHandler);
    return this;
  }

  @Override
  public MongoService replaceDocuments(String collection, JsonObject query, JsonObject replace,
      Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.replaceDocuments(collection, query, replace, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options,
      Handler<AsyncResult<Void>> resultHandler) {
    client.replaceWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  @Override
  public MongoService replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace,
      UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.replaceDocumentsWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  @Override
  public MongoService bulkWrite(String collection, List<BulkOperation> operations,
      Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    client.bulkWrite(collection, operations, resultHandler);
    return this;
  }

  @Override
  public MongoService bulkWriteWithOptions(String collection, List<BulkOperation> operations,
      BulkWriteOptions bulkWriteOptions, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    client.bulkWriteWithOptions(collection, operations, bulkWriteOptions, resultHandler);
    return this;
  }

  @Override
  public MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    client.find(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService findWithOptions(String collection, JsonObject query, FindOptions options,
      Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    client.findWithOptions(collection, query, options, resultHandler);
    return this;
  }

  @Override
  public MongoService findOne(String collection, JsonObject query, JsonObject fields,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOne(collection, query, fields, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndUpdate(String collection, JsonObject query, JsonObject update,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndUpdate(collection, query, update, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update,
      FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndUpdateWithOptions(collection, query, update, findOptions, updateOptions, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndReplace(String collection, JsonObject query, JsonObject replace,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndReplace(collection, query, replace, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject update,
      FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndReplaceWithOptions(collection, query, update, findOptions, updateOptions, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndDelete(String collection, JsonObject query,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndDelete(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOneAndDeleteWithOptions(collection, query, findOptions, resultHandler);
    return this;
  }

  @Override
  public MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    client.count(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    client.remove(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService removeDocuments(String collection, JsonObject query,
      Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocuments(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption,
      Handler<AsyncResult<Void>> resultHandler) {
    client.removeWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  public MongoService removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption,
      Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocumentsWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    client.removeOne(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService removeDocument(String collection, JsonObject query,
      Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocument(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  public MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption,
      Handler<AsyncResult<Void>> resultHandler) {
    client.removeOneWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  public MongoService removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption,
      Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocumentWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    client.createCollection(collectionName, resultHandler);
    return this;
  }

  @Override
  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    client.getCollections(resultHandler);
    return this;
  }

  @Override
  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    client.dropCollection(collection, resultHandler);
    return this;
  }

  @Override
  public MongoService createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler) {
    client.createIndex(collection, key, resultHandler);
    return this;
  }

  @Override
  public MongoService createIndexWithOptions(String collection, JsonObject key, IndexOptions options,
      Handler<AsyncResult<Void>> resultHandler) {
    client.createIndexWithOptions(collection, key, options, resultHandler);
    return this;
  }

  @Override
  public MongoService listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler) {
    client.listIndexes(collection, resultHandler);
    return this;
  }

  @Override
  public MongoService dropIndex(String collection, String indexName, Handler<AsyncResult<Void>> resultHandler) {
    client.dropIndex(collection, indexName, resultHandler);
    return this;
  }

  @Override
  public MongoService runCommand(String commandName, JsonObject command,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    client.runCommand(commandName, command, resultHandler);
    return this;
  }

  @Override
  public MongoService distinct(String collection, String fieldName, String resultClassname,
      Handler<AsyncResult<JsonArray>> resultHandler) {
    client.distinct(collection, fieldName, resultClassname, resultHandler);
    return this;
  }

  @Override
  public MongoService distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, Handler<AsyncResult<JsonArray>> resultHandler) {
    client.distinctWithQuery(collection, fieldName, resultClassname, query, resultHandler);
    return this;
  }

  @Override
  public void close() {
    client.close();
  }
}
