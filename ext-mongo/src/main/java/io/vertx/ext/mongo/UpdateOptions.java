package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class UpdateOptions extends WriteOptions {
  private Boolean upsert;
  private Boolean multi;

  public UpdateOptions() {
  }

  public UpdateOptions(UpdateOptions other) {
    super(other);
    this.upsert = other.upsert;
    this.multi = other.multi;
  }

  public UpdateOptions(JsonObject json) {
    super(json);
    upsert = json.getBoolean("upsert");
    multi = json.getBoolean("multi");
  }

  public Boolean isUpsert() {
    return upsert;
  }

  public UpdateOptions setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  public Boolean isMulti() {
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
    if (upsert != null) {
      json.put("upsert", upsert);
    }
    if (multi != null) {
      json.put("multi", multi);
    }

    return json;
  }
}
