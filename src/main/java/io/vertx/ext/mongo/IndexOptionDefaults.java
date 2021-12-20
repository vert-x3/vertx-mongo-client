package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * The default options for a collection to apply on the creation of indexes.
 */
@DataObject(generateConverter = true)
public class IndexOptionDefaults {
  private JsonObject storageEngine = new JsonObject();

  public IndexOptionDefaults() {
    storageEngine = new JsonObject();
  }

  public IndexOptionDefaults(JsonObject json) {
    IndexOptionDefaultsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    IndexOptionDefaultsConverter.toJson(this, json);
    return json;
  }

  /**
   * Returns the mongo-java-driver specific object.
   * @return com.mongodb.client.model.IndexOptionDefaults
   */
  public com.mongodb.client.model.IndexOptionDefaults toMongoDriverObject() {
    return new com.mongodb.client.model.IndexOptionDefaults()
      .storageEngine(org.bson.BsonDocument.parse(this.storageEngine.encode()));
  }

  /**
   * Gets the default storage engine options document for indexes.
   * @return storageEngine as JsonObject
   */
  public JsonObject getStorageEngine() {
    return storageEngine;
  }

  /**
   * Sets the default storage engine options document for indexes.
   * @param storageEngine
   * @return IndexOptionDefaults
   */
  public IndexOptionDefaults setStorageEngine(JsonObject storageEngine) {
    this.storageEngine = storageEngine;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IndexOptionDefaults that = (IndexOptionDefaults) o;
    return Objects.equals(getStorageEngine(), that.getStorageEngine());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStorageEngine());
  }
}
