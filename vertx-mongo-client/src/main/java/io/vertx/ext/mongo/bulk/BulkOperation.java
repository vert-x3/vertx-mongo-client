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

  static BulkOperationType createDelete(JsonObject filter) {
    return new BulkOperationType(BulkOperationType.TYPE_DELETE).setFilter(filter);
  }

  static BulkOperationType createInsert(JsonObject document) {
    return new BulkOperationType(BulkOperationType.TYPE_INSERT).setDocument(document);
  }

  static BulkOperationType createReplace(JsonObject filter, JsonObject document) {
    return new BulkOperationType(BulkOperationType.TYPE_REPLACE).setFilter(filter).setDocument(document);
  }
  static BulkOperationType createReplace(JsonObject filter, JsonObject document, boolean upsert) {
    return new BulkOperationType(BulkOperationType.TYPE_REPLACE).setFilter(filter).setDocument(document)
        .setUpsert(upsert);
  }

  static BulkOperationType createUpdate(JsonObject filter, JsonObject document) {
    return new BulkOperationType(BulkOperationType.TYPE_UPDATE).setFilter(filter).setDocument(document);
  }
  static BulkOperationType createUpdate(JsonObject filter, JsonObject document, boolean upsert, boolean multi) {
    return new BulkOperationType(BulkOperationType.TYPE_UPDATE).setFilter(filter).setDocument(document)
        .setUpsert(upsert).setMulti(multi);
  }

}
