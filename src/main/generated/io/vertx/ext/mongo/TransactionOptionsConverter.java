package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.TransactionOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.TransactionOptions} original class using Vert.x codegen.
 */
public class TransactionOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, TransactionOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "maxCommitTimeMillis":
          if (member.getValue() instanceof Number) {
            obj.setMaxCommitTimeMillis(((Number)member.getValue()).longValue());
          }
          break;
        case "timeoutMillis":
          if (member.getValue() instanceof Number) {
            obj.setTimeoutMillis(((Number)member.getValue()).longValue());
          }
          break;
      }
    }
  }

   static void toJson(TransactionOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(TransactionOptions obj, java.util.Map<String, Object> json) {
    if (obj.getMaxCommitTimeMillis() != null) {
      json.put("maxCommitTimeMillis", obj.getMaxCommitTimeMillis());
    }
    if (obj.getTimeoutMillis() != null) {
      json.put("timeoutMillis", obj.getTimeoutMillis());
    }
  }
}
