package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.TimeSeriesOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.TimeSeriesOptions} original class using Vert.x codegen.
 */
public class TimeSeriesOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, TimeSeriesOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "timeField":
          if (member.getValue() instanceof String) {
            obj.setTimeField((String)member.getValue());
          }
          break;
        case "metaField":
          if (member.getValue() instanceof String) {
            obj.setMetaField((String)member.getValue());
          }
          break;
        case "granularity":
          if (member.getValue() instanceof String) {
            obj.setGranularity(io.vertx.ext.mongo.TimeSeriesGranularity.valueOf((String)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(TimeSeriesOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(TimeSeriesOptions obj, java.util.Map<String, Object> json) {
    if (obj.getTimeField() != null) {
      json.put("timeField", obj.getTimeField());
    }
    if (obj.getMetaField() != null) {
      json.put("metaField", obj.getMetaField());
    }
    if (obj.getGranularity() != null) {
      json.put("granularity", obj.getGranularity().name());
    }
  }
}
