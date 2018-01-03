/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.DistinctIterable;
import com.mongodb.async.client.ListIndexesIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.BulkWriteUpsert;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.*;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link io.vertx.ext.mongo.MongoClient}. This implementation is based on the async driver
 * provided by Mongo.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoClientImpl implements io.vertx.ext.mongo.MongoClient {

  private static final UpdateOptions DEFAULT_UPDATE_OPTIONS = new UpdateOptions();
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();
  private static final BulkWriteOptions DEFAULT_BULK_WRITE_OPTIONS = new BulkWriteOptions();
  private static final String ID_FIELD = "_id";

  private static final String DS_LOCAL_MAP_NAME = "__vertx.MongoClient.datasources";

  private final Vertx vertx;

  private final MongoDatabaseFactory holder;
  private final boolean useObjectId;

  public MongoClientImpl(Vertx vertx, JsonObject config, String dataSourceName) {
    Objects.requireNonNull(vertx);
    Objects.requireNonNull(config);
    Objects.requireNonNull(dataSourceName);
    this.vertx = vertx;
    this.holder = lookupHolder(dataSourceName, config);
    this.useObjectId = config.getBoolean("useObjectId", false);
  }

  @Override
  public void close() {
    holder.close();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    saveWithOptions(collection, document, null, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, writeOption);
    Object id = document.getValue(ID_FIELD);
    if (id == null) {
      coll.insertOne(document, convertCallback(resultHandler, Function.identity()));
    } else {
      JsonObject filter = new JsonObject()
        .put(ID_FIELD, document.getValue(ID_FIELD));

      com.mongodb.client.model.UpdateOptions updateOptions = new com.mongodb.client.model.UpdateOptions()
        .upsert(true);

      coll.replaceOne(filter, document, updateOptions, convertCallback(resultHandler, result -> null));
    }
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    insertWithOptions(collection, document, null, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, writeOption);
    coll.insertOne(document, convertCallback(resultHandler, Function.identity()));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    updateCollectionWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
                                                                    Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, options.getWriteOption());
    if (options.isMulti()) {
      coll.updateMany(query, update, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    } else {
      coll.updateOne(query, update, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    }
    return this;
  }

  @Override
  public MongoClient replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    replaceDocumentsWithOptions(collection, query, replace, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoClient replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(replace, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, options.getWriteOption());
    coll.replaceOne(query, replace, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public ReadStream<JsonObject> findBatch(String collection, JsonObject query) {
    return findBatchWithOptions(collection, query, DEFAULT_FIND_OPTIONS);
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoIterable<JsonObject> view = doFind(collection, query, options);
    List<JsonObject> results = new ArrayList<>();
    view.into(results, convertCallback(resultHandler, Function.identity()));
    return this;
  }

  @Override
  public ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    MongoIterable<JsonObject> view = doFind(collection, query, options);
    return new MongoIterableStream(vertx.getOrCreateContext(), view, options.getBatchSize());
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    getCollection(collection).find(query, fields).first(convertCallback(resultHandler, Function.identity()));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndUpdate(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndUpdateWithOptions(collection, query, update, DEFAULT_FIND_OPTIONS, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(updateOptions, "update options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindOneAndUpdateOptions foauOptions = new FindOneAndUpdateOptions();
    foauOptions.sort(toBson(findOptions.getSort()));
    foauOptions.projection(toBson(findOptions.getFields()));
    foauOptions.upsert(updateOptions.isUpsert());
    foauOptions.returnDocument(updateOptions.isReturningNewDocument() ? ReturnDocument.AFTER : ReturnDocument.BEFORE);

    VertxMongoCollection coll = getCollection(collection);
    coll.findOneAndUpdate(query, update, foauOptions, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndReplace(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndReplaceWithOptions(collection, query, update, DEFAULT_FIND_OPTIONS, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(updateOptions, "update options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindOneAndReplaceOptions foarOptions = new FindOneAndReplaceOptions();
    foarOptions.sort(toBson(findOptions.getSort()));
    foarOptions.projection(toBson(findOptions.getFields()));
    foarOptions.upsert(updateOptions.isUpsert());
    foarOptions.returnDocument(updateOptions.isReturningNewDocument() ? ReturnDocument.AFTER : ReturnDocument.BEFORE);

    VertxMongoCollection coll = getCollection(collection);
    coll.findOneAndReplace(query, replace, foarOptions, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndDelete(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndDeleteWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindOneAndDeleteOptions foadOptions = new FindOneAndDeleteOptions();
    foadOptions.sort(toBson(findOptions.getSort()));
    foadOptions.projection(toBson(findOptions.getFields()));

    VertxMongoCollection coll = getCollection(collection);
    coll.findOneAndDelete(query, foadOptions, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection);
    coll.count(query, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentsWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoClient removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, writeOption);
    coll.deleteMany(query, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Override
  public MongoClient removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoClient removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection, writeOption);
    coll.deleteOne(query, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Override
  public MongoClient bulkWrite(String collection, List<BulkOperation> operations,
                               Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    bulkWriteWithOptions(collection, operations, DEFAULT_BULK_WRITE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoClient bulkWriteWithOptions(String collection, List<BulkOperation> operations,
                                          BulkWriteOptions bulkWriteOptions, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(operations, "operations cannot be null");
    requireNonNull(bulkWriteOptions, "bulkWriteOptions cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    VertxMongoCollection coll = getCollection(collection, bulkWriteOptions.getWriteOption());
    coll.bulkWrite(operations, mongoBulkWriteOptions(bulkWriteOptions),
      toMongoClientBulkWriteResult(resultHandler));

    return this;
  }

  private static com.mongodb.client.model.BulkWriteOptions mongoBulkWriteOptions(BulkWriteOptions bulkWriteOptions) {
    return new com.mongodb.client.model.BulkWriteOptions()
      .ordered(bulkWriteOptions.isOrdered());
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    holder.getDb().createCollection(collection, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");
    List<String> names = new ArrayList<>();
    Context context = vertx.getOrCreateContext();
    holder.getDb().listCollectionNames().into(names, (res, error) -> {
      context.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(names));
        }
      });
    });
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    VertxMongoCollection coll = getCollection(collection);
    coll.drop(wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler) {
    return createIndexWithOptions(collection, key, new IndexOptions(), resultHandler);
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createIndexWithOptions(String collection, JsonObject key, IndexOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(key, "fieldName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    VertxMongoCollection coll = getCollection(collection);
    com.mongodb.client.model.IndexOptions driverOpts = new com.mongodb.client.model.IndexOptions()
      .background(options.isBackground())
      .unique(options.isUnique())
      .name(options.getName())
      .sparse(options.isSparse())
      .expireAfter(options.getExpireAfter(TimeUnit.SECONDS), TimeUnit.SECONDS)
      .version(options.getVersion())
      .weights(toBson(options.getWeights()))
      .defaultLanguage(options.getDefaultLanguage())
      .languageOverride(options.getLanguageOverride())
      .textVersion(options.getTextVersion())
      .sphereVersion(options.getSphereVersion())
      .bits(options.getBits())
      .min(options.getMin())
      .max(options.getMax())
      .bucketSize(options.getBucketSize())
      .storageEngine(toBson(options.getStorageEngine()))
      .partialFilterExpression(toBson(options.getPartialFilterExpression()));
    coll.createIndex(toBson(key), driverOpts, wrapCallback(toVoidAsyncResult(resultHandler)));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    VertxMongoCollection coll = getCollection(collection);
    ListIndexesIterable indexes = coll.listIndexes();
    if (indexes != null) {
      convertMongoIterable(indexes, resultHandler);
    }
    return this;
  }

  @Override
  public MongoClient dropIndex(String collection, String indexName, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(indexName, "indexName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    VertxMongoCollection coll = getCollection(collection);
    coll.dropIndex(indexName, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient runCommand(String commandName, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(commandName, "commandName cannot be null");
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    // The command name must be the first entry in the bson, so to ensure this we must recreate and add the command
    // name as first (JsonObject is internally ordered)
    JsonObject json = new JsonObject();
    Object commandVal = command.getValue(commandName);
    if (commandVal == null) {
      throw new IllegalArgumentException("commandBody does not contain key for " + commandName);
    }
    json.put(commandName, commandVal);
    command.forEach(entry -> {
      if (!entry.getKey().equals(commandName)) {
        json.put(entry.getKey(), entry.getValue());
      }
    });

    holder.getDb().runCommand(toBson(json), JsonObject.class, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler) {
    return distinctWithQuery(collection, fieldName, resultClassname, new JsonObject(), resultHandler);
  }

  @Override
  public io.vertx.ext.mongo.MongoClient distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, Handler<AsyncResult<JsonArray>> resultHandler) {
    try {
      DistinctIterable distinctValues = findDistinctValuesWithQuery(collection, fieldName, resultClassname, query);
      if (distinctValues != null) {
        convertMongoIterable(distinctValues, resultHandler);
      }
    } catch (ClassNotFoundException e) {
      resultHandler.handle(Future.failedFuture(e));
    }
    return this;
  }

  @Override
  public ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname) {
    return distinctBatchWithQuery(collection, fieldName, resultClassname, new JsonObject());
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) {
    return distinctBatchWithQuery(collection, fieldName, resultClassname, query, FindOptions.DEFAULT_BATCH_SIZE);
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize) {
    try {
      MongoIterable<JsonObject> distinctValues = findDistinctValuesWithQuery(collection, fieldName, resultClassname, query)
        .map(value -> new JsonObject().put(fieldName, value));
      return new MongoIterableStream(vertx.getOrCreateContext(), distinctValues, batchSize);
    } catch (ClassNotFoundException e) {
      return new FailedStream(e);
    }
  }

  private void convertMongoIterable(MongoIterable iterable, Handler<AsyncResult<JsonArray>> resultHandler) {
    List results = new ArrayList();
    try {
      Context context = vertx.getOrCreateContext();
      iterable.into(results, (result, throwable) -> {
        context.runOnContext(v -> {
          if (throwable != null) {
            resultHandler.handle(Future.failedFuture(throwable));
          } else {
            resultHandler.handle(Future.succeededFuture(new JsonArray((List) result)));
          }
        });
      });
    } catch (Exception unhandledEx) {
      resultHandler.handle(Future.failedFuture(unhandledEx));
    }

  }

  private DistinctIterable<?> findDistinctValuesWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) throws ClassNotFoundException {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(fieldName, "fieldName cannot be null");
    requireNonNull(query, "query cannot be null");

    VertxMongoCollection mongoCollection = getCollection(collection);
    Class<?> resultClass = this.getClass().getClassLoader().loadClass(resultClassname);
    return mongoCollection.distinct(fieldName, query, resultClass);
  }

  private <T, R> SingleResultCallback<T> convertCallback(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
    Context context = vertx.getOrCreateContext();
    return (result, error) -> {
      context.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(converter.apply(result)));
        }
      });
    };
  }

  private <T> Handler<AsyncResult<T>> toVoidAsyncResult(Handler<AsyncResult<Void>> resultHandler) {
    return result -> {
      if (result.succeeded()) {
        resultHandler.handle(Future.succeededFuture(null));
      } else {
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    };
  }

  private SingleResultCallback<UpdateResult> toMongoClientUpdateResult(Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    return convertCallback(resultHandler, result -> {
      if (result.wasAcknowledged()) {
        return new MongoClientUpdateResult(result.getMatchedCount(), convertUpsertId(result.getUpsertedId()), result.getModifiedCount());
      } else {
        return null;
      }
    });
  }

  private SingleResultCallback<DeleteResult> toMongoClientDeleteResult(Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    return convertCallback(resultHandler, result -> {
      if (result.wasAcknowledged()) {
        return new MongoClientDeleteResult(result.getDeletedCount());
      } else {
        return null;
      }
    });
  }

  private SingleResultCallback<BulkWriteResult> toMongoClientBulkWriteResult(
    Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    return convertCallback(resultHandler, result -> {
      if (result.wasAcknowledged()) {
        return convertToMongoClientBulkWriteResult(result.getInsertedCount(),
          result.getMatchedCount(), result.getDeletedCount(), result.isModifiedCountAvailable()
            ? result.getModifiedCount() : (int) MongoClientBulkWriteResult.DEFAULT_MODIFIED_COUNT,
          result.getUpserts());
      } else {
        return null;
      }
    });
  }

  private MongoClientBulkWriteResult convertToMongoClientBulkWriteResult(int insertedCount, int matchedCount,
                                                                         int deletedCount, int modifiedCount, List<BulkWriteUpsert> upserts) {
    List<JsonObject> upsertResult = upserts.stream().map(upsert -> {
      JsonObject upsertValue = convertUpsertId(upsert.getId());
      upsertValue.put(MongoClientBulkWriteResult.INDEX, upsert.getIndex());
      return upsertValue;
    }).collect(Collectors.toList());
    return new MongoClientBulkWriteResult(insertedCount, matchedCount, deletedCount, modifiedCount, upsertResult);
  }

  private <T> SingleResultCallback<T> wrapCallback(Handler<AsyncResult<T>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    return (result, error) -> {
      context.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(result));
        }
      });
    };
  }

  private MongoIterable<JsonObject> doFind(String collection, JsonObject query, FindOptions options) {
    VertxMongoCollection coll = getCollection(collection, null);
    return coll.find(query, options);
  }

  private VertxMongoCollection getCollection(String name) {
    return getCollection(name, null);
  }

  private VertxMongoCollection getCollection(String name, WriteOption writeOption) {
    MongoCollection<JsonObject> coll = holder.getDb().getCollection(name, JsonObject.class);
    if (coll != null && writeOption != null) {
      coll = coll.withWriteConcern(WriteConcern.valueOf(writeOption.name()));
    }
    return new VertxMongoCollection(coll, useObjectId, vertx);
  }

  private static com.mongodb.client.model.UpdateOptions mongoUpdateOptions(UpdateOptions options) {
    return new com.mongodb.client.model.UpdateOptions().upsert(options.isUpsert());
  }


  private static Bson toBson(JsonObject json) {
    return json == null ? null : BsonDocument.parse(json.encode());
  }

  private void removeFromMap(LocalMap<String, MongoDatabaseFactory> map, String dataSourceName) {
    synchronized (vertx) {
      map.remove(dataSourceName);
      if (map.isEmpty()) {
        map.close();
      }
    }
  }

  private MongoDatabaseFactory lookupHolder(String datasourceName, JsonObject config) {
    synchronized (vertx) {
      LocalMap<String, MongoDatabaseFactory> map = vertx.sharedData().getLocalMap(DS_LOCAL_MAP_NAME);
      MongoDatabaseFactory theHolder = map.get(datasourceName);
      if (theHolder == null) {
        theHolder = new MongoDatabaseFactory(config, () -> removeFromMap(map, datasourceName));
        map.put(datasourceName, theHolder);
      } else {
        theHolder.incRefCount();
      }
      return theHolder;
    }
  }

  private JsonObject convertUpsertId(BsonValue upsertId) {
    JsonObject jsonUpsertId;
    if (upsertId != null) {
      JsonObjectCodec jsonObjectCodec = new JsonObjectCodec(new JsonObject());

      BsonDocument upsertIdDocument = new BsonDocument();
      upsertIdDocument.append(ID_FIELD, upsertId);

      BsonDocumentReader bsonDocumentReader = new BsonDocumentReader(upsertIdDocument);
      jsonUpsertId = jsonObjectCodec.decode(bsonDocumentReader, DecoderContext.builder().build());
    } else {
      jsonUpsertId = null;
    }
    return jsonUpsertId;
  }
}

