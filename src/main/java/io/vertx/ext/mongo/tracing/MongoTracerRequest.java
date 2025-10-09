package io.vertx.ext.mongo.tracing;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Describes a MongoDB command executed by the Vert.x Mongo client. Instances of this class are passed to
 * {@link io.vertx.core.spi.tracing.VertxTracer} implementations so that tracers can extract attributes that are
 * relevant for span enrichment.
 */
public final class MongoTracerRequest {

  private final String database;
  private final String collection;
  private final String operation;
  private final JsonObject command;
  private final JsonObject options;

  private MongoTracerRequest(String database, String collection, String operation, JsonObject command, JsonObject options) {
    this.database = database;
    this.collection = collection;
    this.operation = operation;
    this.command = command;
    this.options = options;
  }

  public String database() {
    return database;
  }

  public String collection() {
    return collection;
  }

  public String operation() {
    return operation;
  }

  public JsonObject command() {
    return command;
  }

  public JsonObject options() {
    return options;
  }

  public static Builder create(String database, String collection, String operation) {
    Objects.requireNonNull(database, "database cannot be null");
    Objects.requireNonNull(collection, "collection cannot be null");
    Objects.requireNonNull(operation, "operation cannot be null");
    return new Builder(database, collection, operation);
  }

  public static final class Builder {

    private final String database;
    private final String collection;
    private final String operation;
    private final JsonObject command = new JsonObject();
    private final JsonObject options = new JsonObject();

    private Builder(String database, String collection, String operation) {
      this.database = database;
      this.collection = collection;
      this.operation = operation;
    }

    public Builder command(String key, Object value) {
      if (value != null) {
        command.put(key, sanitize(value));
      }
      return this;
    }

    public Builder options(JsonObject json) {
      if (json != null && !json.isEmpty()) {
        options.mergeIn(json.copy(), true);
      }
      return this;
    }

    public Builder option(String key, Object value) {
      if (value != null) {
        options.put(key, sanitize(value));
      }
      return this;
    }

    public MongoTracerRequest build() {
      return new MongoTracerRequest(
        database,
        collection,
        operation,
        command.isEmpty() ? null : command,
        options.isEmpty() ? null : options
      );
    }

    private static Object sanitize(Object value) {
      if (value instanceof JsonObject) {
        return ((JsonObject) value).copy();
      }
      if (value instanceof JsonArray) {
        return ((JsonArray) value).copy();
      }
      if (value instanceof Iterable) {
        JsonArray array = new JsonArray();
        for (Object entry : (Iterable<?>) value) {
          array.add(sanitize(entry));
        }
        return array;
      }
      return value;
    }
  }
}
