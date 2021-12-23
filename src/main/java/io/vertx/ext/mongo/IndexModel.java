package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class IndexModel {
  private JsonObject key;
  private IndexOptions options;

  /**
   * Default constructor
   */
  public IndexModel() {
    key = null;
    options = null;
  }

  /**
   * Json constructor
   *
   * @param json - the json object
   */
  public IndexModel(JsonObject json) {
    IndexModelConverter.fromJson(json, this);
  }

  public IndexModel(final JsonObject key, final IndexOptions options) {
    this.key = key;
    this.options = options;
  }

  /**
   * Get the index key
   *
   * @return - the index keys
   */
  public JsonObject getKey() {
    return key;
  }

  /**
   * Sets the index key
   *
   * @param key - the index keys
   * @return this for fluency
   */
  public IndexModel setKey(JsonObject key) {
    this.key = key;
    return this;
  }

  /**
   * Get the index options
   *
   * @return - the index options
   */
  public IndexOptions getOptions() {
    return options;
  }

  /**
   * Sets the index options
   *
   * @param options - the index options
   * @return this for fluency
   */
  public IndexModel setOptions(IndexOptions options) {
    this.options = options;
    return this;
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    IndexModelConverter.toJson(this, json);
    return json;
  }

  @Override
  public String toString() {
    return "IndexModel{" +
      "key=" + key +
      ", options=" + options +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IndexModel that = (IndexModel) o;
    return Objects.equals(getKey(), that.getKey()) && Objects.equals(getOptions(), that.getOptions());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKey(), getOptions());
  }
}
