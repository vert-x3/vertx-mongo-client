package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options for configuring updates.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@DataObject
public class UpdateOptions {

  /**
   * The default value of upsert = false
   */
  public static final boolean DEFAULT_UPSERT = false;

  /**
   * The default value of multi = false
   */
  public static final boolean DEFAULT_MULTI = false;

  private WriteOption writeOption;
  private boolean upsert;
  private boolean multi;

  /**
   * Default constructor
   */
  public UpdateOptions() {
    this.upsert = DEFAULT_UPSERT;
    this.multi = DEFAULT_MULTI;
  }

  /**
   * Constructor specify upsert
   * @param upsert  the value of upsert
   */
  public UpdateOptions(boolean upsert) {
    this.upsert = upsert;
    this.multi = DEFAULT_MULTI;
  }

  /**
   * Constructor specify upsert and multu
   * @param upsert  the value of upsert
   * @param multi  the value of multi
   */
  public UpdateOptions(boolean upsert, boolean multi) {
    this.upsert = upsert;
    this.multi = multi;
  }

  /**
   * Copy constructor
   * @param other  the one to copy
   */
  public UpdateOptions(UpdateOptions other) {
    this.writeOption = other.writeOption;
    this.upsert = other.upsert;
    this.multi = other.multi;
  }

  /**
   * Constructor from JSON
   *
   * @param json  the json
   */
  public UpdateOptions(JsonObject json) {
    String wo = json.getString("writeOption");
    if (wo != null) {
      writeOption = WriteOption.valueOf(wo.toUpperCase());
    }
    upsert = json.getBoolean("upsert", DEFAULT_UPSERT);
    multi = json.getBoolean("multi", DEFAULT_MULTI);
  }

  /**
   * Get the write option.
   *
   * @return the write option
   */
  public WriteOption getWriteOption() {
    return writeOption;
  }

  /**
   * Set the write option
   * @param writeOption  the write option
   * @return reference to this, for fluency
   */
  public UpdateOptions setWriteOption(WriteOption writeOption) {
    this.writeOption = writeOption;
    return this;
  }

  /**
   * Get whether upsert is enabled
   *
   * @return upsert is enabled?
   */
  public boolean isUpsert() {
    return upsert;
  }

  /**
   * Set whether upsert is enabled
   *
   * @param upsert  true if enabled
   * @return reference to this, for fluency
   */
  public UpdateOptions setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  /**
   * Get whether multi is enabled. Multi means more than one document can be updated.
   *
   * @return multi is enabled?
   */
  public boolean isMulti() {
    return multi;
  }

  /**
   * Set whether multi is enabled
   *
   * @param multi  true if enabled
   * @return reference to this, for fluency
   */
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
