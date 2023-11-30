package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.CollationOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.CollationOptions} original class using Vert.x codegen.
 */
public class CollationOptionsConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CollationOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "normalization":
          if (member.getValue() instanceof Boolean) {
            obj.setNormalization((Boolean)member.getValue());
          }
          break;
        case "locale":
          if (member.getValue() instanceof String) {
            obj.setLocale((String)member.getValue());
          }
          break;
        case "caseLevel":
          if (member.getValue() instanceof Boolean) {
            obj.setCaseLevel((Boolean)member.getValue());
          }
          break;
        case "strength":
          if (member.getValue() instanceof String) {
            obj.setStrength(com.mongodb.client.model.CollationStrength.valueOf((String)member.getValue()));
          }
          break;
        case "numericOrdering":
          if (member.getValue() instanceof Boolean) {
            obj.setNumericOrdering((Boolean)member.getValue());
          }
          break;
        case "backwards":
          if (member.getValue() instanceof Boolean) {
            obj.setBackwards((Boolean)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(CollationOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(CollationOptions obj, java.util.Map<String, Object> json) {
    if (obj.isNormalization() != null) {
      json.put("normalization", obj.isNormalization());
    }
    if (obj.getLocale() != null) {
      json.put("locale", obj.getLocale());
    }
    if (obj.isCaseLevel() != null) {
      json.put("caseLevel", obj.isCaseLevel());
    }
    if (obj.getStrength() != null) {
      json.put("strength", obj.getStrength().name());
    }
    if (obj.isNumericOrdering() != null) {
      json.put("numericOrdering", obj.isNumericOrdering());
    }
    if (obj.isBackwards() != null) {
      json.put("backwards", obj.isBackwards());
    }
  }
}
