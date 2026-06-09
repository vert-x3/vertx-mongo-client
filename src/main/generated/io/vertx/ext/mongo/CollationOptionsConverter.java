package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.CollationOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.CollationOptions} original class using Vert.x codegen.
 */
public class CollationOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CollationOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "caseLevel":
          if (member.getValue() instanceof Boolean) {
            obj.setCaseLevel((Boolean)member.getValue());
          }
          break;
        case "numericOrdering":
          if (member.getValue() instanceof Boolean) {
            obj.setNumericOrdering((Boolean)member.getValue());
          }
          break;
        case "normalization":
          if (member.getValue() instanceof Boolean) {
            obj.setNormalization((Boolean)member.getValue());
          }
          break;
        case "strength":
          if (member.getValue() instanceof String) {
            obj.setStrength(com.mongodb.client.model.CollationStrength.valueOf((String)member.getValue()));
          }
          break;
        case "backwards":
          if (member.getValue() instanceof Boolean) {
            obj.setBackwards((Boolean)member.getValue());
          }
          break;
        case "locale":
          if (member.getValue() instanceof String) {
            obj.setLocale((String)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(CollationOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(CollationOptions obj, java.util.Map<String, Object> json) {
    if (obj.isCaseLevel() != null) {
      json.put("caseLevel", obj.isCaseLevel());
    }
    if (obj.isNumericOrdering() != null) {
      json.put("numericOrdering", obj.isNumericOrdering());
    }
    if (obj.isNormalization() != null) {
      json.put("normalization", obj.isNormalization());
    }
    if (obj.getStrength() != null) {
      json.put("strength", obj.getStrength().name());
    }
    if (obj.isBackwards() != null) {
      json.put("backwards", obj.isBackwards());
    }
    if (obj.getLocale() != null) {
      json.put("locale", obj.getLocale());
    }
  }
}
