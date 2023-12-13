package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.TimeSeriesOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.TimeSeriesOptions} original class using Vert.x codegen.
 */
public class TimeSeriesOptionsConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, TimeSeriesOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "granularity":
          if (member.getValue() instanceof String) {
            obj.setGranularity(io.vertx.ext.mongo.TimeSeriesGranularity.valueOf((String)member.getValue()));
          }
          break;
        case "metaField":
          if (member.getValue() instanceof String) {
            obj.setMetaField((String)member.getValue());
          }
          break;
        case "timeField":
          if (member.getValue() instanceof String) {
            obj.setTimeField((String)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(TimeSeriesOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(TimeSeriesOptions obj, java.util.Map<String, Object> json) {
    if (obj.getGranularity() != null) {
      json.put("granularity", obj.getGranularity().name());
    }
    if (obj.getMetaField() != null) {
      json.put("metaField", obj.getMetaField());
    }
    if (obj.getTimeField() != null) {
      json.put("timeField", obj.getTimeField());
    }
  }
}
