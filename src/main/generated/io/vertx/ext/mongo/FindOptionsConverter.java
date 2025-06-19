package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.FindOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.FindOptions} original class using Vert.x codegen.
 */
public class FindOptionsConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, FindOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
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
        case "fields":
          if (member.getValue() instanceof JsonObject) {
            obj.setFields(((JsonObject)member.getValue()).copy());
          }
          break;
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
        case "sort":
          if (member.getValue() instanceof JsonObject) {
            obj.setSort(((JsonObject)member.getValue()).copy());
          }
          break;
      }
    }
  }

   static void toJson(FindOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(FindOptions obj, java.util.Map<String, Object> json) {
    json.put("batchSize", obj.getBatchSize());
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
    if (obj.getFields() != null) {
      json.put("fields", obj.getFields());
    }
    if (obj.getHint() != null) {
      json.put("hint", obj.getHint());
    }
    if (obj.getHintString() != null) {
      json.put("hintString", obj.getHintString());
    }
    json.put("limit", obj.getLimit());
    json.put("skip", obj.getSkip());
    if (obj.getSort() != null) {
      json.put("sort", obj.getSort());
    }
  }
}
