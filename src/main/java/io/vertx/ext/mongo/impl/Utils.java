/*
 * Copyright 2019 The Vert.x Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertx.ext.mongo.impl;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class Utils {
  static final String ID_FIELD = "_id";

  static MongoClientDeleteResult toMongoClientDeleteResult(DeleteResult deleteResult) {
    return deleteResult.wasAcknowledged() ? new MongoClientDeleteResult(deleteResult.getDeletedCount()) : null;
  }

  static MongoClientUpdateResult toMongoClientUpdateResult(UpdateResult updateResult) {
    return updateResult.wasAcknowledged() ? new MongoClientUpdateResult(updateResult.getMatchedCount(), convertUpsertId(updateResult.getUpsertedId()), updateResult.getModifiedCount()) : null;
  }

  static MongoClientBulkWriteResult toMongoClientBulkWriteResult(BulkWriteResult bulkWriteResult) {
    if (!bulkWriteResult.wasAcknowledged()) {
      return null;
    }

    List<JsonObject> upsertResult = bulkWriteResult.getUpserts().stream().map(upsert -> {
      JsonObject upsertValue = convertUpsertId(upsert.getId());
      upsertValue.put(MongoClientBulkWriteResult.INDEX, upsert.getIndex());
      return upsertValue;
    }).collect(Collectors.toList());

    return new MongoClientBulkWriteResult(
      bulkWriteResult.getInsertedCount(),
      bulkWriteResult.getMatchedCount(),
      bulkWriteResult.getDeletedCount(),
      bulkWriteResult.getModifiedCount(),
      upsertResult
    );
  }

  private static JsonObject convertUpsertId(BsonValue upsertId) {
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

  /**
   * Set the {@code handler} on the given {@code future}, if the {@code handler} is not null.
   */
  static <T> void setHandler(Future<T> future, Handler<AsyncResult<T>> handler) {
    Objects.requireNonNull(future, "future must not be null");
    if (handler != null) {
      future.setHandler(handler);
    }
  }

  private Utils() {
    // Utility class
  }
}
