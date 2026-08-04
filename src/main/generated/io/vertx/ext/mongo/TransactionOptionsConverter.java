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
        case "readConcernLevel":
          if (member.getValue() instanceof String) {
            obj.setReadConcernLevel((String)member.getValue());
          }
          break;
        case "writeConcern":
          if (member.getValue() instanceof String) {
            obj.setWriteConcern((String)member.getValue());
          }
          break;
        case "readPreference":
          if (member.getValue() instanceof String) {
            obj.setReadPreference((String)member.getValue());
          }
          break;
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
    if (obj.getReadConcernLevel() != null) {
      json.put("readConcernLevel", obj.getReadConcernLevel());
    }
    if (obj.getWriteConcern() != null) {
      json.put("writeConcern", obj.getWriteConcern());
    }
    if (obj.getReadPreference() != null) {
      json.put("readPreference", obj.getReadPreference());
    }
    if (obj.getMaxCommitTimeMillis() != null) {
      json.put("maxCommitTimeMillis", obj.getMaxCommitTimeMillis());
    }
    if (obj.getTimeoutMillis() != null) {
      json.put("timeoutMillis", obj.getTimeoutMillis());
    }
  }
}
