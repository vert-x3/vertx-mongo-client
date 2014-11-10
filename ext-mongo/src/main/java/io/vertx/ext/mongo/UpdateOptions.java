package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class UpdateOptions extends WriteOptions {
  private boolean upsert;
  private boolean multi;

  public UpdateOptions() {
  }

  public UpdateOptions(UpdateOptions other) {
    super(other);
    this.upsert = other.upsert;
    this.multi = other.multi;
  }

  public UpdateOptions(JsonObject json) {
    super(json);
    upsert = json.getBoolean("upsert", false);
    multi = json.getBoolean("multi", false);
  }

  public boolean isUpsert() {
    return upsert;
  }

  public UpdateOptions setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  public boolean isMulti() {
    return multi;
  }

  public UpdateOptions setMulti(boolean multi) {
    this.multi = multi;
    return this;
  }

  @Override
  public UpdateOptions setWriteConcern(String writeConcern) {
    super.setWriteConcern(writeConcern);
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = super.toJson();
    if (upsert) {
      json.put("upsert", true);
    }
    if (multi) {
      json.put("multi", true);
    }

    return json;
  }
}
