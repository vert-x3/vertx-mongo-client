package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class IndexModel {
  private JsonObject key;
  private IndexOptions options;

  /**
   * Construct an isntance with the given key
   * @param key - the index key
   */
  public IndexModel(final JsonObject key) {
    this.key = key;
  }

  public IndexModel(final JsonObject key, final IndexOptions options) {
    this.key = key;
    this.options = options;
  }

  /**
   * Get the index key
   * @return - the index keys
   */
  public JsonObject getKey() { return key; }

  /**
   * Get the index options
   * @return - the index options
   */
  public IndexOptions getOptions() { return options; }

  public JsonObject toJson() {
    return key;
  }

  @Override
  public String toString() {
    return "IndexModel{"
      + "keys="+key
      + ", options="+options
      + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IndexModel that = (IndexModel) o;
    return Objects.equals(key, that.key) &&
      Objects.equals(options, that.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, options);
  }
}
