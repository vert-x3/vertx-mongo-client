package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonObject;

/**
 * Interface to mark all operations that can be send inside one bulk write operation.
 * 
 * @author sschmitt
 *
 */
@VertxGen
public interface BulkOperation {

  static BulkDelete createDelete(JsonObject filter) {
    return new BulkDelete(filter);
  }

  static BulkInsert createInsert(JsonObject document) {
    return new BulkInsert(document);
  }

  static BulkUpdate createUpdate(JsonObject filter, JsonObject update) {
    return new BulkUpdate(filter, update);
  }

  static BulkReplace createReplace(JsonObject filter, JsonObject replace) {
    return new BulkReplace(filter, replace);
  }
}
