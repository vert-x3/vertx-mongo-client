package io.vertx.ext.mongo.bulk;

import io.vertx.core.json.JsonObject;

public class BulkUpdate implements BulkOperation {

  public static final boolean DEFAULT_UPSERT = false;
  public static final boolean DEFAULT_MULTI = false;

  private JsonObject document;
  private JsonObject filter;
  private boolean upsert;
  private boolean multi;

  public BulkUpdate(JsonObject filter, JsonObject document) {
    this(filter, document, DEFAULT_UPSERT, DEFAULT_MULTI);
  }

  public BulkUpdate(JsonObject filter, JsonObject document, boolean upsert) {
    this(filter, document, upsert, DEFAULT_MULTI);
  }

  public BulkUpdate(JsonObject filter, JsonObject document, boolean upsert, boolean multi) {
    this.filter = filter;
    this.document = document;
    this.upsert = upsert;
    this.multi = multi;
  }

  public JsonObject getDocument() {
    return document;
  }

  public void setDocument(JsonObject document) {
    this.document = document;
  }

  public JsonObject getFilter() {
    return filter;
  }

  public void setFilter(JsonObject filter) {
    this.filter = filter;
  }

  public boolean isUpsert() {
    return upsert;
  }

  public void setUpsert(boolean upsert) {
    this.upsert = upsert;
  }

  public boolean isMulti() {
    return multi;
  }

  public void setMulti(boolean multi) {
    this.multi = multi;
  }
}
