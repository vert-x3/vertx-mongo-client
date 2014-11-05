package io.vertx.ext.mongo.impl.config;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class AbstractParser {
  protected static Logger log = LoggerFactory.getLogger("io.vertx.ext.mongo.config");

  protected static <T> T get(JsonObject json, String key, Class<T> type) {
    return get(json, key, type, null);
  }

  protected static <T> T get(JsonObject json, String key, Class<T> type, T def) {
    Object value = json.getValue(key);
    try {
      T val = type.cast(value);
      return (val == null) ? def : val;
    } catch (ClassCastException cce) {
      throw new IllegalArgumentException("Invalid type " + value.getClass().getName() + " for '" + key + "'. Was expecting type " + type.getName());
    }
  }

  protected static <T> void forEach(JsonArray array, String key, Class<T> type, Consumer<T> consumer) {
    for (Object o : array) {
      try {
        consumer.accept(type.cast(o));
      } catch (ClassCastException cce) {
        throw new IllegalArgumentException("Invalid type " + o.getClass().getName() + " for array '" + key + "'. Was expecting type " + type.getName());
      }
    }
  }
}
