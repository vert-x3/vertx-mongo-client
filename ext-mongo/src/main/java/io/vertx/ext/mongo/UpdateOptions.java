package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class UpdateOptions {
  private String writeConcern;
  private boolean upsert;
  private boolean multi;

  public UpdateOptions() {
  }

  public UpdateOptions(boolean upsert) {
    this.upsert = upsert;
  }

  public UpdateOptions(boolean upsert, boolean multi) {
    this.upsert = upsert;
    this.multi = multi;
  }

  public UpdateOptions(UpdateOptions other) {
    this.writeConcern = other.writeConcern;
    this.upsert = other.upsert;
    this.multi = other.multi;
  }

  public UpdateOptions(JsonObject json) {
    writeConcern = json.getString("writeConcern");
    upsert = json.getBoolean("upsert", false);
    multi = json.getBoolean("multi", false);
  }

  public String getWriteConcern() {
    return writeConcern;
  }

  public UpdateOptions setWriteConcern(String writeConcern) {
    this.writeConcern = writeConcern;
    return this;
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

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (writeConcern != null) {
      json.put("writeConcern", writeConcern);
    }
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

    UpdateOptions that = (UpdateOptions) o;

    if (multi != that.multi) return false;
    if (upsert != that.upsert) return false;
    if (writeConcern != null ? !writeConcern.equals(that.writeConcern) : that.writeConcern != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = writeConcern != null ? writeConcern.hashCode() : 0;
    result = 31 * result + (upsert ? 1 : 0);
    result = 31 * result + (multi ? 1 : 0);
    return result;
  }
}
