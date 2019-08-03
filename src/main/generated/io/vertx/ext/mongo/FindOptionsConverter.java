package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import io.vertx.core.spi.json.JsonCodec;

/**
 * Converter and Codec for {@link io.vertx.ext.mongo.FindOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.FindOptions} original class using Vert.x codegen.
 */
public class FindOptionsConverter implements JsonCodec<FindOptions, JsonObject> {

  public static final FindOptionsConverter INSTANCE = new FindOptionsConverter();

  @Override public JsonObject encode(FindOptions value) { return (value != null) ? value.toJson() : null; }

  @Override public FindOptions decode(JsonObject value) { return (value != null) ? new FindOptions(value) : null; }

  @Override public Class<FindOptions> getTargetClass() { return FindOptions.class; }

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, FindOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "batchSize":
          if (member.getValue() instanceof Number) {
            obj.setBatchSize(((Number)member.getValue()).intValue());
          }
          break;
        case "fields":
          if (member.getValue() instanceof JsonObject) {
            obj.setFields(((JsonObject)member.getValue()).copy());
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

  public static void toJson(FindOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(FindOptions obj, java.util.Map<String, Object> json) {
    json.put("batchSize", obj.getBatchSize());
    if (obj.getFields() != null) {
      json.put("fields", obj.getFields());
    }
    json.put("limit", obj.getLimit());
    json.put("skip", obj.getSkip());
    if (obj.getSort() != null) {
      json.put("sort", obj.getSort());
    }
  }
}
