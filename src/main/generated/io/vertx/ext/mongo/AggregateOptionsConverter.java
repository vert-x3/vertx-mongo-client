package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import io.vertx.core.spi.json.JsonCodec;

/**
 * Converter and Codec for {@link io.vertx.ext.mongo.AggregateOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.AggregateOptions} original class using Vert.x codegen.
 */
public class AggregateOptionsConverter implements JsonCodec<AggregateOptions, JsonObject> {

  public static final AggregateOptionsConverter INSTANCE = new AggregateOptionsConverter();

  @Override public JsonObject encode(AggregateOptions value) { return (value != null) ? value.toJson() : null; }

  @Override public AggregateOptions decode(JsonObject value) { return (value != null) ? new AggregateOptions(value) : null; }

  @Override public Class<AggregateOptions> getTargetClass() { return AggregateOptions.class; }

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, AggregateOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "allowDiskUse":
          if (member.getValue() instanceof Boolean) {
            obj.setAllowDiskUse((Boolean)member.getValue());
          }
          break;
        case "batchSize":
          if (member.getValue() instanceof Number) {
            obj.setBatchSize(((Number)member.getValue()).intValue());
          }
          break;
        case "maxAwaitTime":
          if (member.getValue() instanceof Number) {
            obj.setMaxAwaitTime(((Number)member.getValue()).longValue());
          }
          break;
        case "maxTime":
          if (member.getValue() instanceof Number) {
            obj.setMaxTime(((Number)member.getValue()).longValue());
          }
          break;
      }
    }
  }

  public static void toJson(AggregateOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(AggregateOptions obj, java.util.Map<String, Object> json) {
    if (obj.getAllowDiskUse() != null) {
      json.put("allowDiskUse", obj.getAllowDiskUse());
    }
    json.put("batchSize", obj.getBatchSize());
    json.put("maxAwaitTime", obj.getMaxAwaitTime());
    json.put("maxTime", obj.getMaxTime());
  }
}
