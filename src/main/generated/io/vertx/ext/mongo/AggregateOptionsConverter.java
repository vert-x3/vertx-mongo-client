package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.AggregateOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.AggregateOptions} original class using Vert.x codegen.
 */
public class AggregateOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, AggregateOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "maxTime":
          if (member.getValue() instanceof Number) {
            obj.setMaxTime(((Number)member.getValue()).longValue());
          }
          break;
        case "batchSize":
          if (member.getValue() instanceof Number) {
            obj.setBatchSize(((Number)member.getValue()).intValue());
          }
          break;
        case "collation":
          if (member.getValue() instanceof JsonObject) {
            obj.setCollation(new io.vertx.ext.mongo.CollationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
        case "allowDiskUse":
          if (member.getValue() instanceof Boolean) {
            obj.setAllowDiskUse((Boolean)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(AggregateOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(AggregateOptions obj, java.util.Map<String, Object> json) {
    json.put("maxTime", obj.getMaxTime());
    json.put("batchSize", obj.getBatchSize());
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
    if (obj.getAllowDiskUse() != null) {
      json.put("allowDiskUse", obj.getAllowDiskUse());
    }
  }
}
