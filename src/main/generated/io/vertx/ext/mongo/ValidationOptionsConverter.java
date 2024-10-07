package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.ValidationOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.ValidationOptions} original class using Vert.x codegen.
 */
public class ValidationOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ValidationOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "validator":
          if (member.getValue() instanceof JsonObject) {
            obj.setValidator(((JsonObject)member.getValue()).copy());
          }
          break;
        case "validationLevel":
          if (member.getValue() instanceof String) {
            obj.setValidationLevel(com.mongodb.client.model.ValidationLevel.valueOf((String)member.getValue()));
          }
          break;
        case "validationAction":
          if (member.getValue() instanceof String) {
            obj.setValidationAction(com.mongodb.client.model.ValidationAction.valueOf((String)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(ValidationOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(ValidationOptions obj, java.util.Map<String, Object> json) {
    if (obj.getValidator() != null) {
      json.put("validator", obj.getValidator());
    }
    if (obj.getValidationLevel() != null) {
      json.put("validationLevel", obj.getValidationLevel().name());
    }
    if (obj.getValidationAction() != null) {
      json.put("validationAction", obj.getValidationAction().name());
    }
  }
}
