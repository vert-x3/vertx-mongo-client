package io.vertx.ext.mongo.impl;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;

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
  @Fluent
  public MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    client.save(collection, document, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    client.saveWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    client.insert(collection, document, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    client.insertWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) {
    client.update(collection, query, update, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.updateCollection(collection, query, update, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    client.updateWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.updateCollectionWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) {
    client.replace(collection, query, replace, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.replaceDocuments(collection, query, replace, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    client.replaceWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    client.replaceDocumentsWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    client.find(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService findBatch(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findBatch(collection, query, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    client.findWithOptions(collection, query, options, resultHandler);
    return this;
  }

  @Override
  public MongoService findBatchWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findBatchWithOptions(collection, query, options, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.findOne(collection, query, fields, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    client.count(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    client.remove(collection, query, resultHandler);
    return this;
  }

  @Override
  public MongoService removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocuments(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    client.removeWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocumentsWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    client.removeOne(collection, query, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocument(collection, query, resultHandler);
    return this;
  }

  @Deprecated
  @Override
  @Fluent
  public MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    client.removeOneWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    client.removeDocumentWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    client.createCollection(collectionName, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    client.getCollections(resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    client.dropCollection(collection, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService runCommand(String commandName, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.runCommand(commandName, command, resultHandler);
    return this;
  }


  @Override
  @Fluent
  public MongoService distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler) {
    client.distinct(collection, fieldName, resultClassname, resultHandler);
    return this;
  }

  @Override
  @Fluent
  public MongoService distinctBatch(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonObject>> resultHandler) {
    client.distinctBatch(collection, fieldName, resultClassname, resultHandler);
    return this;
  }

  @Override
  public void close() {
    client.close();
  }
}
