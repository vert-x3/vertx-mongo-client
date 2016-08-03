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

import com.mongodb.Block;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.DistinctIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.ListIndexesIterable;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.IndexOptions;
import io.vertx.ext.mongo.InsertOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;

import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link io.vertx.ext.mongo.MongoClient}. This implementation is based on the async driver
 * provided by Mongo.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoClientImpl implements io.vertx.ext.mongo.MongoClient {

  private static final Logger log = LoggerFactory.getLogger(MongoClientImpl.class);

  private static final UpdateOptions DEFAULT_UPDATE_OPTIONS = new UpdateOptions();
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();
  private static final String ID_FIELD = "_id";

  private static final String DS_LOCAL_MAP_NAME = "__vertx.MongoClient.datasources";

  private final Vertx vertx;
  protected com.mongodb.async.client.MongoClient mongo;
  protected final MongoHolder holder;
  protected boolean useObjectId;

  public MongoClientImpl(Vertx vertx, JsonObject config, String dataSourceName) {
    Objects.requireNonNull(vertx);
    Objects.requireNonNull(config);
    Objects.requireNonNull(dataSourceName);
    this.vertx = vertx;
    this.holder = lookupHolder(dataSourceName, config);
    this.mongo = holder.mongo();
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

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Object id = document.getValue(ID_FIELD);
    if (id == null) {
      coll.insertOne(document, convertCallback(resultHandler, wr -> useObjectId ? document.getJsonObject(ID_FIELD).getString(JsonObjectCodec.OID_FIELD) : document.getString(ID_FIELD)));
    } else {
      JsonObject filter = new JsonObject();
      JsonObject encodedDocument = encodeKeyWhenUseObjectId(document);
      filter.put(ID_FIELD, encodedDocument.getValue(ID_FIELD));

      com.mongodb.client.model.UpdateOptions updateOptions = new com.mongodb.client.model.UpdateOptions()
          .upsert(true);

      coll.replaceOne(wrap(filter), encodedDocument, updateOptions, convertCallback(resultHandler, result -> null));
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

    boolean id = document.containsKey(ID_FIELD);

    JsonObject encodedDocument = encodeKeyWhenUseObjectId(document);

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    coll.insertOne(encodedDocument, convertCallback(resultHandler, wr -> {
      if (id) {
        return null;
      } else {
        JsonObject decodedDocument = decodeKeyWhenUseObjectId(encodedDocument);
        return decodedDocument.getString(ID_FIELD);
      }
    }));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insertMany(String collection, List<JsonObject> documents, Handler<AsyncResult<Void>> resultHandler) {
    insertManyWithOptions(collection, documents, null, null, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insertManyWithOptions(String collection, List<JsonObject> documents, InsertOptions insertOptions, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(documents, "documents cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    for (JsonObject document : documents) {
      encodeKeyWhenUseObjectId(document);
    }

    InsertManyOptions options = new InsertManyOptions();
    if (insertOptions != null) {
      options.ordered(insertOptions.isOrdered());
      options.bypassDocumentValidation(insertOptions.getBypassDocumentValidation());
    }

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    coll.insertMany(documents, options, convertCallback(resultHandler, result -> null));
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) {
    updateWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    updateCollectionWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    updateCollectionWithOptions(collection, query, update, options, toVoidAsyncResult(resultHandler));
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

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(query);
    Bson bupdate = wrap(update);
    if (options.isMulti()) {
      coll.updateMany(bquery, bupdate, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    } else {
      coll.updateOne(bquery, bupdate, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    }
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) {
    replaceWithOptions(collection, query, replace, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoClient replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    replaceDocumentsWithOptions(collection, query, replace, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    return replaceDocumentsWithOptions(collection, query, replace, options, toVoidAsyncResult(resultHandler));
  }

  @Override
  public MongoClient replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(replace, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    boolean id = query.containsKey(ID_FIELD);
    query = encodeKeyWhenUseObjectId(query);  //TODO: Need to write test for this and delete

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(query);
    coll.replaceOne(bquery, replace, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findBatch(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
    findBatchWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindIterable<JsonObject> view = doFind(collection, query, options);
    List<JsonObject> results = new ArrayList<>();
    view.into(results, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findBatchWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindIterable<JsonObject> view = doFind(collection, query, options);
    Block<JsonObject> documentBlock = document -> resultHandler.handle(Future.succeededFuture(document));
    SingleResultCallback<Void> callbackWhenFinished = (result, throwable) -> {
      if (throwable != null) {
        resultHandler.handle(Future.failedFuture(throwable));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    };
    vertx.runOnContext(v -> view.forEach(documentBlock, callbackWhenFinished));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    Bson bfields = wrap(fields);
    getCollection(collection).find(bquery).projection(bfields).first(wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    Bson bquery = wrap(query);
    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.count(bquery, wrapCallback(resultHandler));
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentsWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    removeDocumentsWithOptions(collection, query, writeOption, toVoidAsyncResult(resultHandler));
    return this;
  }

  @Override
  public MongoClient removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    coll.deleteMany(bquery, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeOneWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoClient removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Deprecated @Override
  public io.vertx.ext.mongo.MongoClient removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    removeDocumentWithOptions(collection, query, writeOption, toVoidAsyncResult(resultHandler));
    return this;
  }

  @Override
  public MongoClient removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    coll.deleteOne(bquery, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    holder.db.createCollection(collection, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");
    List<String> names = new ArrayList<>();
    Context context = vertx.getOrCreateContext();
    holder.db.listCollectionNames().into(names, (res, error) -> {
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

    MongoCollection<JsonObject> coll = getCollection(collection);
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
    MongoCollection<JsonObject> coll = getCollection(collection);
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
    coll.createIndex(wrap(key), driverOpts, wrapCallback(toVoidAsyncResult(resultHandler)));
    return this;
  }

  private static Bson toBson(JsonObject json) {
    return json == null ? null : BsonDocument.parse(json.encode());
  }

  @Override
  public io.vertx.ext.mongo.MongoClient listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    MongoCollection<JsonObject> coll = getCollection(collection);
    ListIndexesIterable indexes = coll.listIndexes(JsonObject.class);
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
    MongoCollection<JsonObject> coll = getCollection(collection);
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

    holder.db.runCommand(wrap(json), JsonObject.class, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler) {
    DistinctIterable distinctValues = findDistinctValues(collection, fieldName, resultClassname, resultHandler);

    if (distinctValues != null) {
      convertMongoIterable(distinctValues, resultHandler);
    }
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient distinctBatch(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonObject>> resultHandler) {
    DistinctIterable distinctValues = findDistinctValues(collection, fieldName, resultClassname, resultHandler);

    if (distinctValues != null) {
      Context context = vertx.getOrCreateContext();
      Block valueBlock = value -> {
        context.runOnContext(v -> {
          Map mapValue = new HashMap();
          mapValue.put(fieldName, value);
          resultHandler.handle(Future.succeededFuture(new JsonObject(mapValue)));
        });
      };
      SingleResultCallback<Void> callbackWhenFinished = (result, throwable) -> {
        if (throwable != null) {
          resultHandler.handle(Future.failedFuture(throwable));
        }
      };
      try {
        distinctValues.forEach(valueBlock, callbackWhenFinished);
      } catch (Exception unhandledEx) {
        resultHandler.handle(Future.failedFuture(unhandledEx));
      }
    }
    return this;
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

  private DistinctIterable findDistinctValues(String collection, String fieldName, String resultClassname, Handler resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(fieldName, "fieldName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    final Class resultClass;
    try {
      resultClass = Class.forName(resultClassname);
    } catch (ClassNotFoundException e) {
      resultHandler.handle(Future.failedFuture(e));
      return null;
    }
    MongoCollection<JsonObject> mongoCollection = getCollection(collection);
    return mongoCollection.distinct(fieldName, resultClass);
  }


  private JsonObject encodeKeyWhenUseObjectId(JsonObject json) {
    if (useObjectId && json.containsKey(ID_FIELD) && json.getValue(ID_FIELD) instanceof String) {
      String idString = json.getString(ID_FIELD);
      if (ObjectId.isValid(idString)) {
        json.put(ID_FIELD, new JsonObject().put(JsonObjectCodec.OID_FIELD, idString));
      }
    }
    return json;
  }

  private JsonObject decodeKeyWhenUseObjectId(JsonObject json) {
    if (useObjectId && json.containsKey(ID_FIELD)) {
      Object idValue = json.getValue(ID_FIELD);
      if (idValue instanceof JsonObject && ((JsonObject) idValue).containsKey(JsonObjectCodec.OID_FIELD)) {
        json.put(ID_FIELD, json.getJsonObject(ID_FIELD).getString(JsonObjectCodec.OID_FIELD));
      }
    }
    return json;
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
      if(result.succeeded()) {
        resultHandler.handle(Future.succeededFuture(null));
      } else {
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    };
  }

  private SingleResultCallback<UpdateResult> toMongoClientUpdateResult(Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    return convertCallback(resultHandler, result -> {
      if (result.wasAcknowledged()) {
        return convertToMongoClientUpdateResult(result.getMatchedCount(), result.getUpsertedId(), result.getModifiedCount());
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

  private FindIterable<JsonObject> doFind(String collection, JsonObject query, FindOptions options) {
    return doFind(collection, null, query, options);
  }

  private FindIterable<JsonObject> doFind(String collection, WriteOption writeOption, JsonObject query, FindOptions options) {
    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    FindIterable<JsonObject> find = coll.find(bquery, JsonObject.class);
    if (options.getLimit() != -1) {
      find.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      find.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      find.sort(wrap(options.getSort()));
    }
    if (options.getFields() != null) {
      find.projection(wrap(options.getFields()));
    }
    return find;
  }

  private MongoCollection<JsonObject> getCollection(String name) {
    return getCollection(name, null);
  }

  private MongoCollection<JsonObject> getCollection(String name, WriteOption writeOption) {
    MongoCollection<JsonObject> coll = holder.db.getCollection(name, JsonObject.class);
    if (coll != null && writeOption != null) {
      coll = coll.withWriteConcern(WriteConcern.valueOf(writeOption.name()));
    }
    return coll;
  }

  private static com.mongodb.client.model.UpdateOptions mongoUpdateOptions(UpdateOptions options) {
    return new com.mongodb.client.model.UpdateOptions().upsert(options.isUpsert());
  }

  private JsonObjectBsonAdapter wrap(JsonObject jsonObject) {
    return jsonObject == null ? null : new JsonObjectBsonAdapter(jsonObject);
  }

  private void removeFromMap(LocalMap<String, MongoHolder> map, String dataSourceName) {
    synchronized (vertx) {
      map.remove(dataSourceName);
      if (map.isEmpty()) {
        map.close();
      }
    }
  }

  private MongoHolder lookupHolder(String datasourceName, JsonObject config) {
    synchronized (vertx) {
      LocalMap<String, MongoHolder> map = vertx.sharedData().getLocalMap(DS_LOCAL_MAP_NAME);
      MongoHolder theHolder = map.get(datasourceName);
      if (theHolder == null) {
        theHolder = new MongoHolder(config, () -> removeFromMap(map, datasourceName));
        map.put(datasourceName, theHolder);
      } else {
        theHolder.incRefCount();
      }
      return theHolder;
    }
  }

  private MongoClientUpdateResult convertToMongoClientUpdateResult(long docMatched, BsonValue upsertId, long docModified) {
    JsonObject jsonUpsertId;
    if (upsertId != null) {
      JsonObjectCodec jsonObjectCodec = new JsonObjectCodec(new JsonObject());

      BsonDocument upsertIdDocument = new BsonDocument();
      upsertIdDocument.append(MongoClientUpdateResult.ID_FIELD, upsertId);

      BsonDocumentReader bsonDocumentReader = new BsonDocumentReader(upsertIdDocument);
      jsonUpsertId = jsonObjectCodec.decode(bsonDocumentReader, DecoderContext.builder().build());
    } else {
      jsonUpsertId = null;
    }

    return new MongoClientUpdateResult(docMatched, jsonUpsertId, docModified);
  }

  private static class MongoHolder implements Shareable {
    com.mongodb.async.client.MongoClient mongo;
    MongoDatabase db;
    JsonObject config;
    Runnable closeRunner;
    int refCount = 1;

    public MongoHolder(JsonObject config, Runnable closeRunner) {
      this.config = config;
      this.closeRunner = closeRunner;
    }

    synchronized com.mongodb.async.client.MongoClient mongo() {
      if (mongo == null) {
        MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
        mongo = MongoClients.create(parser.settings());
        db = mongo.getDatabase(parser.database());
      }
      return mongo;
    }

    synchronized void incRefCount() {
      refCount++;
    }

    synchronized void close() {
      if (--refCount == 0) {
        if (mongo != null) {
          mongo.close();
        }
        if (closeRunner != null) {
          closeRunner.run();
        }
      }
    }
  }

}
