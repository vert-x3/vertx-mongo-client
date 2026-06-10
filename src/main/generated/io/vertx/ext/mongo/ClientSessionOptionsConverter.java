package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter and mapper for {@link io.vertx.ext.mongo.ClientSessionOptions}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.ClientSessionOptions} original class using Vert.x codegen.
 */
public class ClientSessionOptionsConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ClientSessionOptions obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "autoStart":
          if (member.getValue() instanceof Boolean) {
            obj.setAutoStart((Boolean)member.getValue());
          }
          break;
        case "autoClose":
          if (member.getValue() instanceof Boolean) {
            obj.setAutoClose((Boolean)member.getValue());
          }
          break;
        case "causallyConsistent":
          if (member.getValue() instanceof Boolean) {
            obj.setCausallyConsistent((Boolean)member.getValue());
          }
          break;
        case "snapshot":
          if (member.getValue() instanceof Boolean) {
            obj.setSnapshot((Boolean)member.getValue());
          }
          break;
        case "defaultTimeoutMillis":
          if (member.getValue() instanceof Number) {
            obj.setDefaultTimeoutMillis(((Number)member.getValue()).longValue());
          }
          break;
        case "defaultTransactionOptions":
          if (member.getValue() instanceof JsonObject) {
            obj.setDefaultTransactionOptions(new io.vertx.ext.mongo.TransactionOptions((io.vertx.core.json.JsonObject)member.getValue()));
          }
          break;
      }
    }
  }

   static void toJson(ClientSessionOptions obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(ClientSessionOptions obj, java.util.Map<String, Object> json) {
    json.put("autoStart", obj.isAutoStart());
    json.put("autoClose", obj.isAutoClose());
    if (obj.getCausallyConsistent() != null) {
      json.put("causallyConsistent", obj.getCausallyConsistent());
    }
    if (obj.getSnapshot() != null) {
      json.put("snapshot", obj.getSnapshot());
    }
    if (obj.getDefaultTimeoutMillis() != null) {
      json.put("defaultTimeoutMillis", obj.getDefaultTimeoutMillis());
    }
    if (obj.getDefaultTransactionOptions() != null) {
      json.put("defaultTransactionOptions", obj.getDefaultTransactionOptions().toJson());
    }
  }
}
