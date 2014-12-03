package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class UpdateOptions {

  public static final boolean DEFAULT_UPSERT = false;
  public static final boolean DEFAULT_MULTI = false;

  private WriteOption writeOption;
  private boolean upsert;
  private boolean multi;

  public UpdateOptions() {
    this.upsert = DEFAULT_UPSERT;
    this.multi = DEFAULT_MULTI;
  }

  public UpdateOptions(boolean upsert) {
    this.upsert = upsert;
    this.multi = DEFAULT_MULTI;
  }

  public UpdateOptions(boolean upsert, boolean multi) {
    this.upsert = upsert;
    this.multi = multi;
  }

  public UpdateOptions(UpdateOptions other) {
    this.writeOption = other.writeOption;
    this.upsert = other.upsert;
    this.multi = other.multi;
  }

  public UpdateOptions(JsonObject json) {
    String wo = json.getString("writeOption");
    if (wo != null) {
      writeOption = WriteOption.valueOf(wo.toUpperCase());
    }
    upsert = json.getBoolean("upsert", DEFAULT_UPSERT);
    multi = json.getBoolean("multi", DEFAULT_MULTI);
  }

  public WriteOption getWriteOption() {
    return writeOption;
  }

  public UpdateOptions setWriteOption(WriteOption writeOption) {
    this.writeOption = writeOption;
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
    if (writeOption != null) {
      json.put("writeOption", writeOption.name());
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

    UpdateOptions options = (UpdateOptions) o;

    if (multi != options.multi) return false;
    if (upsert != options.upsert) return false;
    if (writeOption != options.writeOption) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = writeOption != null ? writeOption.hashCode() : 0;
    result = 31 * result + (upsert ? 1 : 0);
    result = 31 * result + (multi ? 1 : 0);
    return result;
  }
}
