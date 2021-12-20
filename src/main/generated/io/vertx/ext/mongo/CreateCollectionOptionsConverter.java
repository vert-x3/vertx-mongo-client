package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.CreateCollectionOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.CreateCollectionOptions} original class using Vert.x codegen.
 */
public class CreateCollectionOptionsConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CreateCollectionOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "capped":
          if (member.getValue() instanceof Boolean) {
            obj.setCapped((Boolean)member.getValue());
          }
          break;
        case "collation":
          if (member.getValue() instanceof JsonObject) {
            obj.setCollation(new io.vertx.ext.mongo.CollationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
        case "indexOptionDefaults":
          if (member.getValue() instanceof JsonObject) {
            obj.setIndexOptionDefaults(((JsonObject)member.getValue()).copy());
          }
          break;
        case "maxDocuments":
          if (member.getValue() instanceof Number) {
            obj.setMaxDocuments(((Number)member.getValue()).longValue());
          }
          break;
        case "sizeInBytes":
          if (member.getValue() instanceof Number) {
            obj.setSizeInBytes(((Number)member.getValue()).longValue());
          }
          break;
        case "storageEngineOptions":
          if (member.getValue() instanceof JsonObject) {
            obj.setStorageEngineOptions(((JsonObject)member.getValue()).copy());
          }
          break;
        case "validationOptions":
          if (member.getValue() instanceof JsonObject) {
            obj.setValidationOptions(new io.vertx.ext.mongo.ValidationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
      }
    }
  }

  public static void toJson(CreateCollectionOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(CreateCollectionOptions obj, java.util.Map<String, Object> json) {
    json.put("capped", obj.isCapped());
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
    if (obj.getIndexOptionDefaults() != null) {
      json.put("indexOptionDefaults", obj.getIndexOptionDefaults());
    }
    json.put("maxDocuments", obj.getMaxDocuments());
    json.put("sizeInBytes", obj.getSizeInBytes());
    if (obj.getStorageEngineOptions() != null) {
      json.put("storageEngineOptions", obj.getStorageEngineOptions());
    }
    if (obj.getValidationOptions() != null) {
      json.put("validationOptions", obj.getValidationOptions().toJson());
    }
  }
}
