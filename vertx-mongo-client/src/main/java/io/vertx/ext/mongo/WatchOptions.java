package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class WatchOptions {

  public static final int DEFAULT_MAX_AWAIT_TIME_MS = 1000;
  public static final String FULL_DOCUMENT_UPDATE_LOOKUP = "updateLookup";
  public static final int DEFAULT_BATCH_SIZE = 1;

  private JsonObject resumeAfter;
  private String fullDocument;
  private int batchSize;
  private int maxAwaitTimeMS;
  private JsonObject collation;

  public WatchOptions() {
    maxAwaitTimeMS = DEFAULT_MAX_AWAIT_TIME_MS;
    batchSize = DEFAULT_BATCH_SIZE;
  }

  public WatchOptions(WatchOptions options) {
    resumeAfter = options.resumeAfter;
    fullDocument = options.fullDocument;
    batchSize = options.batchSize;
    maxAwaitTimeMS = options.maxAwaitTimeMS;
    collation = options.collation;
  }

  public WatchOptions(JsonObject options) {
    resumeAfter = options.getJsonObject("resumeAfter");
    fullDocument = options.getString("fullDocument");
    batchSize = options.getInteger("batchSize", DEFAULT_BATCH_SIZE);
    maxAwaitTimeMS = options.getInteger("maxAwaitTimeMS", DEFAULT_MAX_AWAIT_TIME_MS);
    collation = options.getJsonObject("collation");
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("maxAwaitTimeMS", maxAwaitTimeMS);
    json.put("batchSize", batchSize);
    if (resumeAfter != null) {
      json.put("resumeAfter", resumeAfter);
    }
    if (fullDocument != null) {
      json.put("fullDocument", fullDocument);
    }
    if (collation != null) {
      json.put("collation", collation);
    }
    return json;
  }

  public WatchOptions resumeAfter(JsonObject resumeAfter) {
    this.resumeAfter = resumeAfter;
    return this;
  }

  public WatchOptions resumeAfter(String resumeToken) {
    this.resumeAfter = new JsonObject().put("_id", resumeToken);
    return this;
  }

  public WatchOptions fullDocument(String fullDocument) {
    this.fullDocument = fullDocument;
    return this;
  }

  public WatchOptions fullDocument(boolean fullDocument) {
    if (fullDocument) {
      this.fullDocument = FULL_DOCUMENT_UPDATE_LOOKUP;
    } else {
      this.fullDocument = null;
    }
    return this;
  }

  public WatchOptions batchSize(int batchSize) {
    this.batchSize = batchSize;
    return this;
  }

  public WatchOptions maxAwaitTimeMS(int time) {
    this.maxAwaitTimeMS = time;
    return this;
  }

  public WatchOptions collation(JsonObject collation) {
    this.collation = collation;
    return this;
  }

  public JsonObject getResumeAfter() {
    return resumeAfter;
  }

  public String getFullDocument() {
    return fullDocument;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getMaxAwaitTimeMS() {
    return maxAwaitTimeMS;
  }

  public JsonObject getCollation() {
    return collation;
  }
}
