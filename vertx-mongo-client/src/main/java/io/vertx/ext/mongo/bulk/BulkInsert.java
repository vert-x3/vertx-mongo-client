package io.vertx.ext.mongo.bulk;

import io.vertx.core.json.JsonObject;

public class BulkInsert implements BulkOperation {

  private JsonObject document;

  public BulkInsert(JsonObject document) {
    this.document = document;
  }

  public JsonObject getDocument() {
    return document;
  }

  public void setDocument(JsonObject document) {
    this.document = document;
  }
}
