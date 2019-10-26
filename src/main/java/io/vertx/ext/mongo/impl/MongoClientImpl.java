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
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.DistinctIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.ListIndexesIterable;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.async.client.gridfs.GridFSBuckets;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.BulkWriteUpsert;
import com.mongodb.client.model.DeleteManyModel;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.Closeable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.*;
import io.vertx.ext.mongo.IndexModel;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

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
public class MongoClientImpl implements io.vertx.ext.mongo.MongoClient, Closeable {

  private static final Logger log = LoggerFactory.getLogger(MongoClientImpl.class);

  private static final UpdateOptions DEFAULT_UPDATE_OPTIONS = new UpdateOptions();
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();
  private static final AggregateOptions DEFAULT_AGGREGATE_OPTIONS = new AggregateOptions();
  private static final BulkWriteOptions DEFAULT_BULK_WRITE_OPTIONS = new BulkWriteOptions();
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
    Context context = vertx.getOrCreateContext();
    context.addCloseHook(this);
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
  public Future<@Nullable String> save(String collection, JsonObject document) {
    Promise<String> promise = Promise.promise();
    save(collection, document, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
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
  public Future<@Nullable String> saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption) {
    Promise<String> promise = Promise.promise();
    saveWithOptions(collection, document, writeOption, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    insertWithOptions(collection, document, null, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable String> insert(String collection, JsonObject document) {
    Promise<String> promise = Promise.promise();
    insert(collection, document, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedDocument = encodeKeyWhenUseObjectId(document);
    boolean hasCustomId = document.containsKey(ID_FIELD);

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    coll.insertOne(encodedDocument, convertCallback(resultHandler, wr -> {
      if (hasCustomId) return null;

      JsonObject decodedDocument = decodeKeyWhenUseObjectId(encodedDocument);
      return decodedDocument.getString(ID_FIELD);
    }));
    return this;
  }

  @Override
  public Future<@Nullable String> insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption) {
    Promise<String> promise = Promise.promise();
    insertWithOptions(collection, document, writeOption, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    updateCollectionWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonObject update) {
    Promise<MongoClientUpdateResult> promise = Promise.promise();
    updateCollection(collection, query, update, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    update = generateIdIfNeeded(query, update, options);

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
    Bson bupdate = wrap(encodeKeyWhenUseObjectId(update));


    if (options.isMulti()) {
      coll.updateMany(bquery, bupdate, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    } else {
      coll.updateOne(bquery, bupdate, mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    }
    return this;
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options) {
    Promise<MongoClientUpdateResult> promise = Promise.promise();
    updateCollectionWithOptions(collection, query, update, options, promise);
    return promise.future();
  }

  private JsonObject generateIdIfNeeded(JsonObject query, JsonObject update, UpdateOptions options) {
    if (options.isUpsert() && !update.containsKey(ID_FIELD) && !useObjectId) {
      JsonObject setId = update.getJsonObject("$setOnInsert", new JsonObject());
      String id;

      //This seems odd, but if you filter based on _id, mongo expects the generated _id to match
      if (query.containsKey(ID_FIELD)) {
        id = query.getString(ID_FIELD);
      } else {
        id = JsonObjectCodec.generateHexObjectId();
      }
      setId.put(ID_FIELD, id);
      update.put("$setOnInsert", setId);
    }
    return update;
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

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
    coll.replaceOne(bquery, encodeKeyWhenUseObjectId(replace), mongoUpdateOptions(options), toMongoClientUpdateResult(resultHandler));
    return this;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<List<JsonObject>> find(String collection, JsonObject query) {
    Promise<List<JsonObject>> promise = Promise.promise();
    find(collection, query, promise);
    return promise.future();
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

    FindIterable<JsonObject> view = doFind(collection, encodeKeyWhenUseObjectId(query), options);
    List<JsonObject> results = new ArrayList<>();
    view.into(results, convertCallback(resultHandler, wr -> {
      results.forEach(this::decodeKeyWhenUseObjectId);
      return results;
    }));
    return this;
  }

  @Override
  public Future<List<JsonObject>> findWithOptions(String collection, JsonObject query, FindOptions options) {
    Promise<List<JsonObject>> promise = Promise.promise();
    findWithOptions(collection, query, options, promise);
    return promise.future();
  }

  @Override
  public ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    FindIterable<JsonObject> view = doFind(collection, query, options);
    return new MongoIterableStream(vertx.getOrCreateContext(), view, options.getBatchSize());
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOne(String collection, JsonObject query, @Nullable JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    Bson bfields = wrap(fields);
    getCollection(collection).find(bquery).projection(bfields).first(convertCallback(resultHandler, object -> {
      if (object == null) return null;
      return decodeKeyWhenUseObjectId(object);
    }));
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOne(String collection, JsonObject query, @Nullable JsonObject fields) {
    Promise<JsonObject> promise = Promise.promise();
    findOne(collection, query, fields, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndUpdate(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndUpdateWithOptions(collection, query, update, DEFAULT_FIND_OPTIONS, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndUpdate(String collection, JsonObject query, JsonObject update) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndUpdate(collection, query, update, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(updateOptions, "update options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    Bson bupdate = wrap(update);
    FindOneAndUpdateOptions foauOptions = new FindOneAndUpdateOptions();
    foauOptions.sort(wrap(findOptions.getSort()));
    foauOptions.projection(wrap(findOptions.getFields()));
    foauOptions.upsert(updateOptions.isUpsert());
    foauOptions.returnDocument(updateOptions.isReturningNewDocument() ? ReturnDocument.AFTER : ReturnDocument.BEFORE);
    if (updateOptions.getArrayFilters() != null && !updateOptions.getArrayFilters().isEmpty()) {
      final List<Bson> bArrayFilters = new ArrayList<>(updateOptions.getArrayFilters().size());
      updateOptions.getArrayFilters().getList().forEach(entry -> bArrayFilters.add(wrap(JsonObject.mapFrom(entry))));
      foauOptions.arrayFilters(bArrayFilters);
    }

    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.findOneAndUpdate(bquery, bupdate, foauOptions, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndUpdateWithOptions(collection, query, update, findOptions, updateOptions, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndReplace(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndReplaceWithOptions(collection, query, update, DEFAULT_FIND_OPTIONS, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndReplace(String collection, JsonObject query, JsonObject replace) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndReplace(collection, query, replace, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(updateOptions, "update options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    FindOneAndReplaceOptions foarOptions = new FindOneAndReplaceOptions();
    foarOptions.sort(wrap(findOptions.getSort()));
    foarOptions.projection(wrap(findOptions.getFields()));
    foarOptions.upsert(updateOptions.isUpsert());
    foarOptions.returnDocument(updateOptions.isReturningNewDocument() ? ReturnDocument.AFTER : ReturnDocument.BEFORE);

    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.findOneAndReplace(bquery, replace, foarOptions, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndReplaceWithOptions(collection, query, replace, findOptions, updateOptions, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndDelete(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
    findOneAndDeleteWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndDelete(String collection, JsonObject query) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndDelete(collection, query, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(findOptions, "find options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);
    FindOneAndDeleteOptions foadOptions = new FindOneAndDeleteOptions();
    foadOptions.sort(wrap(findOptions.getSort()));
    foadOptions.projection(wrap(findOptions.getFields()));

    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.findOneAndDelete(bquery, foadOptions, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions) {
    Promise<JsonObject> promise = Promise.promise();
    findOneAndDeleteWithOptions(collection, query, findOptions, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.count(bquery, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<Long> count(String collection, JsonObject query) {
    Promise<Long> promise = Promise.promise();
    count(collection, query, promise);
    return promise.future();
  }

  @Override
  public MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentsWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocuments(String collection, JsonObject query) {
    Promise<MongoClientDeleteResult> promise = Promise.promise();
    removeDocuments(collection, query, promise);
    return promise.future();
  }

  @Override
  public MongoClient removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
    coll.deleteMany(bquery, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption) {
    Promise<MongoClientDeleteResult> promise = Promise.promise();
    removeDocumentsWithOptions(collection, query, writeOption, promise);
    return promise.future();
  }

  @Override
  public MongoClient removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    removeDocumentWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocument(String collection, JsonObject query) {
    Promise<MongoClientDeleteResult> promise = Promise.promise();
    removeDocuments(collection, query, promise);
    return promise.future();
  }

  @Override
  public MongoClient removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
    coll.deleteOne(bquery, toMongoClientDeleteResult(resultHandler));
    return this;
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption) {
    Promise<MongoClientDeleteResult> promise = Promise.promise();
    removeDocumentWithOptions(collection, query, writeOption, promise);
    return promise.future();
  }

  @Override
  public MongoClient bulkWrite(String collection, List<BulkOperation> operations, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    bulkWriteWithOptions(collection, operations, DEFAULT_BULK_WRITE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public Future<@Nullable MongoClientBulkWriteResult> bulkWrite(String collection, List<BulkOperation> operations) {
    Promise<MongoClientBulkWriteResult> promise = Promise.promise();
    bulkWrite(collection, operations, promise);
    return promise.future();
  }

  @Override
  public MongoClient bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions, Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(operations, "operations cannot be null");
    requireNonNull(bulkWriteOptions, "bulkWriteOptions cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    MongoCollection<JsonObject> coll = getCollection(collection, bulkWriteOptions.getWriteOption());
    List<WriteModel<JsonObject>> bulkOperations = convertBulkOperations(operations);
    coll.bulkWrite(bulkOperations, mongoBulkWriteOptions(bulkWriteOptions),
      toMongoClientBulkWriteResult(resultHandler));

    return this;
  }

  @Override
  public Future<@Nullable MongoClientBulkWriteResult> bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions) {
    Promise<MongoClientBulkWriteResult> promise = Promise.promise();
    bulkWriteWithOptions(collection, operations, bulkWriteOptions, promise);
    return promise.future();
  }

  private static com.mongodb.client.model.BulkWriteOptions mongoBulkWriteOptions(BulkWriteOptions bulkWriteOptions) {
    com.mongodb.client.model.BulkWriteOptions mongoBulkOptions = new com.mongodb.client.model.BulkWriteOptions()
      .ordered(bulkWriteOptions.isOrdered());
    return mongoBulkOptions;
  }

  private List<WriteModel<JsonObject>> convertBulkOperations(List<BulkOperation> operations) {
    List<WriteModel<JsonObject>> result = new ArrayList<>(operations.size());
    for (BulkOperation bulkOperation : operations) {
      switch (bulkOperation.getType()) {
        case DELETE:
          Bson bsonFilter = toBson(encodeKeyWhenUseObjectId(bulkOperation.getFilter()));
          if (bulkOperation.isMulti()) {
            result.add(new DeleteManyModel<>(bsonFilter));
          } else {
            result.add(new DeleteOneModel<>(bsonFilter));
          }
          break;
        case INSERT:
          result.add(new InsertOneModel<>(encodeKeyWhenUseObjectId(bulkOperation.getDocument())));
          break;
        case REPLACE:
          result.add(new ReplaceOneModel<>(toBson(encodeKeyWhenUseObjectId(bulkOperation.getFilter())), bulkOperation.getDocument(),
            new com.mongodb.client.model.UpdateOptions().upsert(bulkOperation.isUpsert())));
          break;
        case UPDATE:
          Bson filter = toBson(encodeKeyWhenUseObjectId(bulkOperation.getFilter()));
          Bson document = toBson(encodeKeyWhenUseObjectId(bulkOperation.getDocument()));
          com.mongodb.client.model.UpdateOptions updateOptions = new com.mongodb.client.model.UpdateOptions()
            .upsert(bulkOperation.isUpsert());
          if (bulkOperation.isMulti()) {
            result.add(new UpdateManyModel<>(filter, document, updateOptions));
          } else {
            result.add(new UpdateOneModel<>(filter, document, updateOptions));
          }
          break;
        default:
          throw new IllegalArgumentException("Unknown bulk operation type: " + bulkOperation.getClass());
      }
    }
    return result;
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    holder.db.createCollection(collection, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public Future<Void> createCollection(String collectionName) {
    Promise<Void> promise = Promise.promise();
    createCollection(collectionName, promise);
    return promise.future();
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
  public Future<List<String>> getCollections() {
    Promise<List<String>> promise = Promise.promise();
    getCollections(promise);
    return promise.future();
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
  public Future<Void> dropCollection(String collection) {
    Promise<Void> promise = Promise.promise();
    dropCollection(collection, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler) {
    return createIndexWithOptions(collection, key, new IndexOptions(), resultHandler);
  }

  @Override
  public Future<Void> createIndex(String collection, JsonObject key) {
    Promise<Void> promise = Promise.promise();
    createIndex(collection, key, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createIndexWithOptions(String collection, JsonObject key, IndexOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(key, "fieldName cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    MongoCollection<JsonObject> coll = getCollection(collection);
    com.mongodb.client.model.IndexOptions driverOpts = mongoIndexOptions(options);
    coll.createIndex(wrap(key), driverOpts, wrapCallback(toVoidAsyncResult(resultHandler)));
    return this;
  }

  @Override
  public Future<Void> createIndexWithOptions(String collection, JsonObject key, IndexOptions options) {
    Promise<Void> promise = Promise.promise();
    createIndexWithOptions(collection, key, options, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient createIndexes(String collection, List<IndexModel> indexes, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    final List<com.mongodb.client.model.IndexModel> transformIndexes = indexes.stream().map(it -> {
      if (it.getOptions() != null)
        return new com.mongodb.client.model.IndexModel(wrap(it.getKey()), mongoIndexOptions(it.getOptions()));
      else return new com.mongodb.client.model.IndexModel(wrap(it.getKey()));
    }).collect(Collectors.toList());

    getCollection(collection).createIndexes(transformIndexes, wrapCallback(toVoidAsyncResult(resultHandler)));
    return this;
  }

  @Override
  public Future<Void> createIndexes(String collection, List<IndexModel> indexes) {
    Promise<Void> promise = Promise.promise();
    createIndexes(collection, indexes, promise);
    return promise.future();
  }

  @Nullable
  private static Bson toBson(@Nullable JsonObject json) {
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
  public Future<JsonArray> listIndexes(String collection) {
    Promise<JsonArray> promise = Promise.promise();
    listIndexes(collection, promise);
    return promise.future();
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
  public Future<Void> dropIndex(String collection, String indexName) {
    Promise<Void> promise = Promise.promise();
    dropIndex(collection, indexName, promise);
    return promise.future();
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
  public Future<@Nullable JsonObject> runCommand(String commandName, JsonObject command) {
    Promise<JsonObject> promise = Promise.promise();
    runCommand(commandName, command, promise);
    return promise.future();
  }

  @Override
  public io.vertx.ext.mongo.MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler) {
    return distinctWithQuery(collection, fieldName, resultClassname, new JsonObject(), resultHandler);
  }

  @Override
  public Future<JsonArray> distinct(String collection, String fieldName, String resultClassname) {
    Promise<JsonArray> promise = Promise.promise();
    distinct(collection, fieldName, resultClassname, promise);
    return promise.future();
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
  public Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) {
    Promise<JsonArray> promise = Promise.promise();
    distinctWithQuery(collection, fieldName, resultClassname, query, promise);
    return promise.future();
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

  @Override
  public MongoClient createDefaultGridFsBucketService(Handler<AsyncResult<MongoGridFsClient>> resultHandler) {
    return this.createGridFsBucketService("fs", resultHandler);
  }

  @Override
  public Future<MongoGridFsClient> createDefaultGridFsBucketService() {
    Promise<MongoGridFsClient> promise = Promise.promise();
    createDefaultGridFsBucketService(promise);
    return promise.future();
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options) {
    Promise<MongoClientUpdateResult> promise = Promise.promise();
    replaceDocumentsWithOptions(collection, query, replace, options, promise);
    return promise.future();
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> replaceDocuments(String collection, JsonObject query, JsonObject replace) {
    Promise<MongoClientUpdateResult> promise = Promise.promise();
    replaceDocuments(collection, query, replace, promise);
    return promise.future();
  }

  @Override
  public MongoClient createGridFsBucketService(String bucketName, Handler<AsyncResult<MongoGridFsClient>> resultHandler) {
    MongoGridFsClientImpl impl = new MongoGridFsClientImpl(vertx, this, getGridFSBucket(bucketName));
    resultHandler.handle(Future.succeededFuture(impl));
    return this;
  }

  @Override
  public Future<MongoGridFsClient> createGridFsBucketService(String bucketName) {
    Promise<MongoGridFsClient> promise = Promise.promise();
    createGridFsBucketService(bucketName, promise);
    return promise.future();
  }

  private GridFSBucket getGridFSBucket(String bucketName) {
    return GridFSBuckets.create(holder.db, bucketName);
  }

  @Override
  public ReadStream<JsonObject> aggregate(final String collection, final JsonArray pipeline) {
    return aggregateWithOptions(collection, pipeline, DEFAULT_AGGREGATE_OPTIONS);
  }

  @Override
  public ReadStream<JsonObject> aggregateWithOptions(final String collection, final JsonArray pipeline, final AggregateOptions options) {
    final MongoIterable<JsonObject> view = doAggregate(collection, pipeline, options);
    return new MongoIterableStream(vertx.getOrCreateContext(), view, options.getBatchSize());
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

    JsonObject encodedQuery = encodeKeyWhenUseObjectId(query);

    Bson bquery = wrap(encodedQuery);

    MongoCollection<JsonObject> mongoCollection = getCollection(collection);
    Class<?> resultClass = this.getClass().getClassLoader().loadClass(resultClassname);
    return mongoCollection.distinct(fieldName, bquery, resultClass);
  }

  private AggregateIterable<JsonObject> doAggregate(final String collection, final JsonArray pipeline, final AggregateOptions aggregateOptions) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(pipeline, "pipeline cannot be null");
    requireNonNull(aggregateOptions, "aggregateOptions cannot be null");
    final MongoCollection<JsonObject> coll = getCollection(collection);
    final List<Bson> bpipeline = new ArrayList<>(pipeline.size());
    pipeline.getList().forEach(entry -> bpipeline.add(wrap(JsonObject.mapFrom(entry))));
    final AggregateIterable<JsonObject> aggregate = coll.aggregate(bpipeline, JsonObject.class);

    if (aggregateOptions.getBatchSize() != -1) {
      aggregate.batchSize(aggregateOptions.getBatchSize());
    }
    if (aggregateOptions.getMaxTime() > 0) {
      aggregate.maxTime(aggregateOptions.getMaxTime(), TimeUnit.MILLISECONDS);
    }
    if (aggregateOptions.getAllowDiskUse() != null) {
      aggregate.allowDiskUse(aggregateOptions.getAllowDiskUse());
    }
    return aggregate;
  }

  protected JsonObject encodeKeyWhenUseObjectId(JsonObject json) {
    if (!useObjectId) return json;

    Object idString = json.getValue(ID_FIELD, null);
    if (idString instanceof String && ObjectId.isValid((String) idString)) {
      json.put(ID_FIELD, new JsonObject().put(JsonObjectCodec.OID_FIELD, idString));
    }

    return json;
  }

  private JsonObject decodeKeyWhenUseObjectId(JsonObject json) {
    if (!useObjectId) return json;

    Object idField = json.getValue(ID_FIELD, null);
    if (!(idField instanceof JsonObject)) return json;

    Object idString = ((JsonObject) idField).getValue(JsonObjectCodec.OID_FIELD, null);
    if (!(idString instanceof String)) return json;

    json.put(ID_FIELD, (String) idString);

    return json;
  }

  protected <T, R> SingleResultCallback<T> convertCallback(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
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

  private SingleResultCallback<BulkWriteResult> toMongoClientBulkWriteResult(Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
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

  protected <T> SingleResultCallback<T> wrapCallback(Handler<AsyncResult<T>> resultHandler) {
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
    MongoCollection<JsonObject> coll = getCollection(collection);
    Bson bquery = wrap(encodeKeyWhenUseObjectId(query));
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

  private MongoCollection<JsonObject> getCollection(String name, @Nullable WriteOption writeOption) {
    MongoCollection<JsonObject> coll = holder.db.getCollection(name, JsonObject.class);
    if (coll != null && writeOption != null) {
      coll = coll.withWriteConcern(WriteConcern.valueOf(writeOption.name()));
    }
    return coll;
  }

  private static com.mongodb.client.model.UpdateOptions mongoUpdateOptions(UpdateOptions options) {
    com.mongodb.client.model.UpdateOptions updateOptions = new com.mongodb.client.model.UpdateOptions().upsert(options.isUpsert());
    if (options.getArrayFilters() != null && !options.getArrayFilters().isEmpty()) {
      final List<Bson> bArrayFilters = new ArrayList<>(options.getArrayFilters().size());
      options.getArrayFilters().getList().forEach(entry -> bArrayFilters.add(wrap(JsonObject.mapFrom(entry))));
      updateOptions.arrayFilters(bArrayFilters);
    }
    return updateOptions;
  }

  private com.mongodb.client.model.IndexOptions mongoIndexOptions(IndexOptions options) {
    return new com.mongodb.client.model.IndexOptions()
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
  }

  @Nullable
  protected static JsonObjectBsonAdapter wrap(@Nullable JsonObject jsonObject) {
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
    return new MongoClientUpdateResult(docMatched, convertUpsertId(upsertId), docModified);
  }

  @Nullable
  private JsonObject convertUpsertId(@Nullable BsonValue upsertId) {
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

  @Override
  public void close(Handler<AsyncResult<Void>> handler) {
    holder.close();
    handler.handle(Future.succeededFuture());
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

  private static class FailedStream implements ReadStream<JsonObject> {
    private final ClassNotFoundException e;

    public FailedStream(ClassNotFoundException e) {
      this.e = e;
    }

    @Override
    public ReadStream<JsonObject> exceptionHandler(Handler<Throwable> handler) {
      handler.handle(e);
      return this;
    }

    @Override
    public ReadStream<JsonObject> handler(Handler<JsonObject> handler) {
      return this;
    }

    @Override
    public ReadStream<JsonObject> pause() {
      return this;
    }

    @Override
    public ReadStream<JsonObject> fetch(long l) {
      return this;
    }

    @Override
    public ReadStream<JsonObject> resume() {
      return this;
    }

    @Override
    public ReadStream<JsonObject> endHandler(Handler<Void> endHandler) {
      return this;
    }
  }
}
