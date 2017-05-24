package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class BulkOperationType implements BulkOperation {

  private static final boolean DEFAULT_MULTI = false;
  private static final boolean DEFAULT_UPSERT = false;
  public static final String TYPE_UPDATE = "update";
  public static final String TYPE_REPLACE = "replace";
  public static final String TYPE_INSERT = "insert";
  public static final String TYPE_DELETE = "delete";

  private String type;
  private JsonObject filter;
  private JsonObject document;
  private boolean upsert;
  private boolean multi;

  public BulkOperationType(String type) {
    this.type = type;
    this.filter = null;
    this.document = null;
    this.upsert = DEFAULT_UPSERT;
    this.multi = DEFAULT_MULTI;
  }

  public BulkOperationType(JsonObject json) {
    type = json.getString("type");
    filter = json.getJsonObject("filter");
    document = json.getJsonObject("document");
    upsert = json.getBoolean("upsert");
    multi = json.getBoolean("multi");
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("type", type);
    json.put("filter", filter);
    json.put("document", document);
    json.put("upsert", upsert);
    json.put("multi", multi);
    return json;
  }

  public String getType() {
    return type;
  }

  public BulkOperationType setType(String type) {
    this.type = type;
    return this;
  }

  public JsonObject getFilter() {
    return filter;
  }

  public BulkOperationType setFilter(JsonObject filter) {
    this.filter = filter;
    return this;
  }

  public JsonObject getDocument() {
    return document;
  }

  public BulkOperationType setDocument(JsonObject document) {
    this.document = document;
    return this;
  }

  public boolean isUpsert() {
    return upsert;
  }

  public BulkOperationType setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  public boolean isMulti() {
    return multi;
  }

  public BulkOperationType setMulti(boolean multi) {
    this.multi = multi;
    return this;
  }

}
