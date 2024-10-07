package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.CountOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.CountOptions} original class using Vert.x codegen.
 */
public class CountOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CountOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "hint":
          if (member.getValue() instanceof JsonObject) {
            obj.setHint(((JsonObject)member.getValue()).copy());
          }
          break;
        case "hintString":
          if (member.getValue() instanceof String) {
            obj.setHintString((String)member.getValue());
          }
          break;
        case "limit":
          if (member.getValue() instanceof Number) {
            obj.setLimit(((Number)member.getValue()).intValue());
          }
          break;
        case "skip":
          if (member.getValue() instanceof Number) {
            obj.setSkip(((Number)member.getValue()).intValue());
          }
          break;
        case "maxTime":
          if (member.getValue() instanceof Number) {
            obj.setMaxTime(((Number)member.getValue()).longValue());
          }
          break;
        case "collation":
          if (member.getValue() instanceof JsonObject) {
            obj.setCollation(new io.vertx.ext.mongo.CollationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(CountOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(CountOptions obj, java.util.Map<String, Object> json) {
    if (obj.getHint() != null) {
      json.put("hint", obj.getHint());
    }
    if (obj.getHintString() != null) {
      json.put("hintString", obj.getHintString());
    }
    if (obj.getLimit() != null) {
      json.put("limit", obj.getLimit());
    }
    if (obj.getSkip() != null) {
      json.put("skip", obj.getSkip());
    }
    if (obj.getMaxTime() != null) {
      json.put("maxTime", obj.getMaxTime());
    }
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
  }
}
