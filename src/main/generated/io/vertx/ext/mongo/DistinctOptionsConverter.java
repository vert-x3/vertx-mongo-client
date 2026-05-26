package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.DistinctOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.DistinctOptions} original class using Vert.x codegen.
 */
public class DistinctOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, DistinctOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "collation":
          if (member.getValue() instanceof JsonObject) {
            obj.setCollation(new io.vertx.ext.mongo.CollationOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(DistinctOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(DistinctOptions obj, java.util.Map<String, Object> json) {
    if (obj.getCollation() != null) {
      json.put("collation", obj.getCollation().toJson());
    }
  }
}
