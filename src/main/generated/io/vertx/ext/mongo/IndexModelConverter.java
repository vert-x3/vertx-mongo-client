package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.IndexModel}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.IndexModel} original class using Vert.x codegen.
 */
public class IndexModelConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

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
