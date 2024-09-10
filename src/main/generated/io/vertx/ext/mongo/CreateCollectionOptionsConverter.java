package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.CreateCollectionOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.CreateCollectionOptions} original class using Vert.x codegen.
 */
public class CreateCollectionOptionsConverter {

  private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();
  private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CreateCollectionOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "maxDocuments":
          if (member.getValue() instanceof Number) {
            obj.setMaxDocuments(((Number)member.getValue()).longValue());
          }
          break;
        case "capped":
          if (member.getValue() instanceof Boolean) {
            obj.setCapped((Boolean)member.getValue());
          }
          break;
        case "timeSeriesOptions":
          if (member.getValue() instanceof JsonObject) {
            obj.setTimeSeriesOptions(new io.vertx.ext.mongo.TimeSeriesOptions((io.vertx.core.json.JsonObject)member.getValue()));
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
        case "indexOptionDefaults":
          if (member.getValue() instanceof JsonObject) {
            obj.setIndexOptionDefaults(((JsonObject)member.getValue()).copy());
          }
          break;
        case "validationOptions":
          if (member.getValue() instanceof JsonObject) {
            obj.setValidationOptions(new io.vertx.ext.mongo.ValidationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
        case "collation":
          if (member.getValue() instanceof JsonObject) {
            obj.setCollation(new io.vertx.ext.mongo.CollationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
        case "expireAfterSeconds":
          if (member.getValue() instanceof Number) {
            obj.setExpireAfterSeconds(((Number)member.getValue()).longValue());
          }
          break;
      }
    }
  }

   static void toJson(CreateCollectionOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(CreateCollectionOptions obj, java.util.Map<String, Object> json) {
    if (obj.getMaxDocuments() != null) {
      json.put("maxDocuments", obj.getMaxDocuments());
    }
    if (obj.getCapped() != null) {
      json.put("capped", obj.getCapped());
    }
    if (obj.getTimeSeriesOptions() != null) {
      json.put("timeSeriesOptions", obj.getTimeSeriesOptions().toJson());
    }
    if (obj.getSizeInBytes() != null) {
      json.put("sizeInBytes", obj.getSizeInBytes());
    }
    if (obj.getStorageEngineOptions() != null) {
      json.put("storageEngineOptions", obj.getStorageEngineOptions());
    }
    if (obj.getIndexOptionDefaults() != null) {
      json.put("indexOptionDefaults", obj.getIndexOptionDefaults());
    }
    if (obj.getValidationOptions() != null) {
      json.put("validationOptions", obj.getValidationOptions().toJson());
    }
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
    if (obj.getExpireAfterSeconds() != null) {
      json.put("expireAfterSeconds", obj.getExpireAfterSeconds());
    }
  }
}
