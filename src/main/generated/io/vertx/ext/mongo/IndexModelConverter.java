package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.IndexModel}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.IndexModel} original class using Vert.x codegen.
 */
public class IndexModelConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, IndexModel obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "key":
          if (member.getValue() instanceof JsonObject) {
            obj.setKey(((JsonObject)member.getValue()).copy());
          }
          break;
        case "options":
          if (member.getValue() instanceof JsonObject) {
            obj.setOptions(new io.vertx.ext.mongo.IndexOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(IndexModel obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(IndexModel obj, java.util.Map<String, Object> json) {
    if (obj.getKey() != null) {
      json.put("key", obj.getKey());
    }
    if (obj.getOptions() != null) {
      json.put("options", obj.getOptions().toJson());
    }
  }
}
