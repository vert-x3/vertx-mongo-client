package io.vertx.ext.mongo.bulk;

import io.vertx.core.json.JsonObject;

public class BulkReplace implements BulkOperation {

  public static final boolean DEFAULT_UPSERT = false;

  private JsonObject replacement;
  private JsonObject filter;
  private boolean upsert;

  public BulkReplace(JsonObject filter, JsonObject replacement) {
    this(filter, replacement, DEFAULT_UPSERT);
  }

  public BulkReplace(JsonObject filter, JsonObject replacement, boolean upsert) {
    this.filter = filter;
    this.replacement = replacement;
    this.upsert = upsert;
  }

  public JsonObject getFilter() {
    return filter;
  }

  public void setFilter(JsonObject filter) {
    this.filter = filter;
  }

  public JsonObject getReplacement() {
    return replacement;
  }

  public void setReplacement(JsonObject replacement) {
    this.replacement = replacement;
  }

  public boolean isUpsert() {
    return upsert;
  }

  public void setUpsert(boolean upsert) {
    this.upsert = upsert;
  }

}
