package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.SessionOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.SessionOptions} original class using Vert.x codegen.
 */
public class SessionOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, SessionOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "autoStart":
          if (member.getValue() instanceof Boolean) {
            obj.setAutoStart((Boolean)member.getValue());
          }
          break;
        case "autoClose":
          if (member.getValue() instanceof Boolean) {
            obj.setAutoClose((Boolean)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(SessionOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(SessionOptions obj, java.util.Map<String, Object> json) {
    json.put("autoStart", obj.isAutoStart());
    json.put("autoClose", obj.isAutoClose());
  }
}
