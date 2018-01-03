package io.vertx.ext.mongo.impl;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec.ID_FIELD;

/**
 * This delegates to MongoClient. Its purpose is to ensure that whenever a call to com.mongodb.async.client.MongoCollection
 * is made, that the ObjectId is correctly encoded first
 */
class VertxMongoCollection {

  private final MongoCollection<JsonObject> collection;
  private final boolean useObjectId;
  private final Vertx vertx;

  VertxMongoCollection(MongoCollection<JsonObject> collection, boolean useObjectId, Vertx vertx) {
    this.collection = collection;
    this.useObjectId = useObjectId;
    this.vertx = vertx;
  }

  private static Bson toBson(JsonObject json) {
    return json == null ? null : BsonDocument.parse(json.encode());
  }

  private JsonObject encodeKeyWhenUseObjectId(JsonObject json) {
    if (!useObjectId) return json;
    if (json == null) return null;

    Object idString = json.getValue(ID_FIELD, null);
    if (idString instanceof String && ObjectId.isValid((String) idString)) {
      json.put(ID_FIELD, new JsonObject().put(JsonObjectCodec.OID_FIELD, idString));
    }

    return json;
  }

  private JsonObject decodeKeyWhenUseObjectId(JsonObject json) {
    if (!useObjectId) return json;
    if (json == null) return null;

    Object idField = json.getValue(ID_FIELD, null);
    if (!(idField instanceof JsonObject)) return json;

    Object idString = ((JsonObject) idField).getValue(JsonObjectCodec.OID_FIELD, null);
    if (!(idString instanceof String)) return json;

    json.put(ID_FIELD, (String) idString);

    return json;
  }

  private Bson encodeAndConvert(JsonObject doc) {
    return toBson(encodeKeyWhenUseObjectId(doc));
  }

  private static JsonObject encodeKeyWhenUseObjectId(JsonObject json, boolean useObjectId) {
    if (!useObjectId) return json;
    if (json == null) return null;

    Object idString = json.getValue(ID_FIELD, null);
    if (idString instanceof String && ObjectId.isValid((String) idString)) {
      json.put(ID_FIELD, new JsonObject().put(JsonObjectCodec.OID_FIELD, idString));
    }

    return json;
  }

  private SingleResultCallback<JsonObject> wrapCallback(Handler<AsyncResult<io.vertx.core.json.JsonObject>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    return (result, error) -> {
      context.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(decodeKeyWhenUseObjectId(result)));
        }
      });
    };
  }

  void updateMany(JsonObject filter, JsonObject update, UpdateOptions options, SingleResultCallback<UpdateResult> callback) {
    collection.updateMany(encodeAndConvert(filter), encodeAndConvert(update), options, callback);
  }

  void findOneAndDelete(JsonObject filter, FindOneAndDeleteOptions options, Handler<AsyncResult<JsonObject>> callback) {
    collection.findOneAndDelete(encodeAndConvert(filter), options, wrapCallback(callback));
  }

  private FindIterable<JsonObject> find(JsonObject filter) {
    return collection.find(encodeAndConvert(filter), JsonObject.class);
  }

  void findOneAndReplace(JsonObject filter, JsonObject replacement, FindOneAndReplaceOptions options, Handler<AsyncResult<JsonObject>> callback) {
    collection.findOneAndReplace(encodeAndConvert(filter), encodeKeyWhenUseObjectId(replacement), options, wrapCallback(callback));
  }

  void deleteMany(JsonObject filter, SingleResultCallback<DeleteResult> callback) {
    collection.deleteMany(encodeAndConvert(filter), callback);
  }

  void findOneAndUpdate(JsonObject filter, JsonObject update, FindOneAndUpdateOptions options, Handler<AsyncResult<JsonObject>> callback) {
    collection.findOneAndUpdate(encodeAndConvert(filter), encodeAndConvert(update), options, wrapCallback(callback));
  }

  void deleteOne(JsonObject filter, SingleResultCallback<DeleteResult> callback) {
    collection.deleteOne(encodeAndConvert(filter), callback);
  }

  void updateOne(JsonObject filter, JsonObject update, UpdateOptions options, SingleResultCallback<UpdateResult> callback) {
    collection.updateOne(encodeAndConvert(filter), encodeAndConvert(update), options, callback);
  }

  <TResult> DistinctIterable<TResult> distinct(String fieldName, JsonObject filter, Class<TResult> tResultClass) {
    return collection.distinct(fieldName, encodeAndConvert(filter), tResultClass);
  }

  ListIndexesIterable<JsonObject> listIndexes() {
    return collection.listIndexes(JsonObject.class);
  }

  void bulkWrite(List<BulkOperation> requests, BulkWriteOptions options, SingleResultCallback<BulkWriteResult> callback) {
    collection.bulkWrite(convertBulkOperations(requests), options, callback);
  }

  void drop(SingleResultCallback<Void> callback) {
    collection.drop(callback);
  }

  void count(JsonObject filter, SingleResultCallback<Long> callback) {
    collection.count(encodeAndConvert(filter), callback);
  }

  void insertOne(JsonObject entries, SingleResultCallback<String> callback) {
    JsonObject document = encodeKeyWhenUseObjectId(entries);
    collection.insertOne(document, (result, error) -> {
      String id = null;
      if (error == null) {
        id = getId(document);
      }
      callback.onResult(id, error);
    });
  }


  private String getId(JsonObject document) {
    if (document == null) return null;
    return useObjectId ? document.getJsonObject(ID_FIELD).getString(JsonObjectCodec.OID_FIELD) : document.getValue(ID_FIELD).toString();
  }

  void createIndex(Bson key, IndexOptions options, SingleResultCallback<String> callback) {
    collection.createIndex(key, options, callback);
  }

  void dropIndex(String indexName, SingleResultCallback<Void> callback) {
    collection.dropIndex(indexName, callback);
  }

  void replaceOne(JsonObject filter, JsonObject replacement, UpdateOptions options, SingleResultCallback<UpdateResult> callback) {
    collection.replaceOne(encodeAndConvert(filter), encodeKeyWhenUseObjectId(replacement), options, callback);
  }

  MongoIterable<JsonObject> find(JsonObject query, JsonObject fields) {
    Bson bfields = toBson(fields);
    return collection
      .find(encodeAndConvert(query))
      .projection(bfields)
      .map(this::decodeKeyWhenUseObjectId);
  }

  MongoIterable<JsonObject> find(JsonObject query, FindOptions options) {
    FindIterable<JsonObject> find = find(query);
    if (options.getLimit() != -1) {
      find.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      find.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      find.sort(toBson(options.getSort()));
    }
    if (options.getFields() != null) {
      find.projection(toBson(options.getFields()));
    }

    return find
      .map(this::decodeKeyWhenUseObjectId);
  }

  private List<WriteModel<JsonObject>> convertBulkOperations(List<BulkOperation> operations) {
    List<WriteModel<JsonObject>> result = new ArrayList<>(operations.size());
    for (BulkOperation bulkOperation : operations) {
      Bson filter = encodeAndConvert(bulkOperation.getFilter());
      JsonObject document = encodeKeyWhenUseObjectId(bulkOperation.getDocument(), useObjectId);
      Bson bDocument = toBson(document);

      switch (bulkOperation.getType()) {
        case DELETE:
          if (bulkOperation.isMulti()) {
            result.add(new DeleteManyModel<>(filter));
          } else {
            result.add(new DeleteOneModel<>(filter));
          }
          break;
        case INSERT:
          result.add(new InsertOneModel<>(document));
          break;
        case REPLACE:
          result.add(new ReplaceOneModel<>(filter,
            document,
            new com.mongodb.client.model.UpdateOptions().upsert(bulkOperation.isUpsert())));
          break;
        case UPDATE:
          com.mongodb.client.model.UpdateOptions updateOptions = new com.mongodb.client.model.UpdateOptions()
            .upsert(bulkOperation.isUpsert());
          if (bulkOperation.isMulti()) {
            result.add(new UpdateManyModel<>(filter, bDocument, updateOptions));
          } else {
            result.add(new UpdateOneModel<>(filter, bDocument, updateOptions));
          }
          break;
        default:
          throw new IllegalArgumentException("Unknown bulk operation type: " + bulkOperation.getClass());
      }
    }
    return result;
  }


}
