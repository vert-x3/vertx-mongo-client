package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

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

  @Override
  public String toString() {
    return "IndexModel{"
      + "keys="+key
      + ", options="+options
      + '}';
  }
}
