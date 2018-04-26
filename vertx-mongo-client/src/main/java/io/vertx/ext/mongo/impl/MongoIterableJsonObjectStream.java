package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.MongoIterable;
import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;

/**
 * @author Thomas Segismont
 */
class MongoIterableJsonObjectStream extends MongoIterableStream<JsonObject> {

  MongoIterableJsonObjectStream(Context context, MongoIterable<JsonObject> mongoIterable, int batchSize) {
    super(mongoIterable, context, batchSize);
  }

}
