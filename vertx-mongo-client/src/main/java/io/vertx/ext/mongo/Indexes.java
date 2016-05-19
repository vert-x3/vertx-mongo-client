package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Index builder
 *
 * @author <a href="mailto:sergey.kobec@gmail.com">Sergey Kobets</a>
 */
public class Indexes {
  private Indexes() {
  }

  public static JsonObject ascending(String... fieldNames) {
    return ascending(Arrays.asList(fieldNames));
  }

  public static JsonObject ascending(List<String> fieldNames) {
    requireNonNull(fieldNames, "fieldNames cannot be null");
    return compoundIndex(fieldNames, 1);
  }

  public static JsonObject descending(String... fieldNames) {
    return descending(Arrays.asList(fieldNames));
  }

  public static JsonObject descending(List<String> fieldNames) {
    requireNonNull(fieldNames, "fieldNames cannot be null");
    return compoundIndex(fieldNames, -1);
  }

  public static JsonObject text(String fieldName) {
    return new JsonObject().put(fieldName, "text");
  }

  public static JsonObject hashed(String fieldName) {
    return new JsonObject().put(fieldName, "hashed");
  }

  public static JsonObject compoundIndex(JsonObject... indexes) {
    return compoundIndex(Arrays.asList(indexes));
  }


  public static JsonObject compoundIndex(List<JsonObject> indexes) {
    JsonObject jsonObject = new JsonObject();
    for (JsonObject index : indexes) {
      Set<String> strings = index.fieldNames();
      for (String string : strings) {
        jsonObject.put(string, index.getValue(string));
      }
    }
    return jsonObject;
  }

  private static JsonObject compoundIndex(List<String> fieldNames, Object value) {
    JsonObject document = new JsonObject();
    for (String fieldName : fieldNames) {
      document.put(fieldName, value);
    }
    return document;
  }
}
