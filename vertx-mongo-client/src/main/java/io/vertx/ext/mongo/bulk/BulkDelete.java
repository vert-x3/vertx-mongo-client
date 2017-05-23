package io.vertx.ext.mongo.bulk;

import io.vertx.core.json.JsonObject;

public class BulkDelete implements BulkOperation {

  public static final boolean DEFAULT_MULTI = false;

  private JsonObject filter;
  private boolean multi;

  public BulkDelete(JsonObject filter) {
    this(filter, DEFAULT_MULTI);
  }

  public BulkDelete(JsonObject filter, boolean multi) {
    this.filter = filter;
    this.multi = multi;
  }

  public JsonObject getFilter() {
    return filter;
  }

  public void setFilter(JsonObject filter) {
    this.filter = filter;
  }

  public boolean isMulti() {
    return multi;
  }

  public void setMulti(boolean multi) {
    this.multi = multi;
  }
}
