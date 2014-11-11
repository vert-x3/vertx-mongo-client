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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    UpdateOptions that = (UpdateOptions) o;

    if (multi != that.multi) return false;
    if (upsert != that.upsert) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (upsert ? 1 : 0);
    result = 31 * result + (multi ? 1 : 0);
    return result;
  }
}
