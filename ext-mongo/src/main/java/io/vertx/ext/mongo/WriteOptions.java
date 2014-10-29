package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class WriteOptions {
  private String writeConcern;

  public WriteOptions() {
  }

  public WriteOptions(JsonObject json) {
    writeConcern = json.getString("writeConcern");
  }

  public WriteOptions(WriteOptions other) {
    this.writeConcern = other.writeConcern;
  }

  public String getWriteConcern() {
    return writeConcern;
  }

  public WriteOptions setWriteConcern(String writeConcern) {
    this.writeConcern = writeConcern;
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (writeConcern != null) {
      json.put("writeConcern", writeConcern);
    }

    return json;
  }
}
