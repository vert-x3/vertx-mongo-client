package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options for configuring bulk write operations.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkWriteOptions {

  /**
   * Field name for the ordered value in json representation
   */
  public static final String ORDERED = "ordered";

  /**
   * Field name for the writeOption value in json representation
   */
  public static final String WRITE_OPTION = "writeOption";

  /**
   * The default value of ordered = true
   */
  public static final boolean DEFAULT_ORDERED = true;

  private WriteOption writeOption;
  private boolean ordered;

  /**
   * Default constructor
   */
  public BulkWriteOptions() {
    this.setOrdered(DEFAULT_ORDERED);
  }

  /**
   * Constructor specifying ordered
   * 
   * @param ordered
   *          the value of ordered
   */
  public BulkWriteOptions(boolean ordered) {
    this.setOrdered(ordered);
  }

  /**
   * Copy constructor
   * 
   * @param other
   *          the one to copy
   */
  public BulkWriteOptions(BulkWriteOptions other) {
    this.setWriteOption(other.getWriteOption());
    this.setOrdered(other.isOrdered());
  }

  /**
   * Constructor from JSON
   *
   * @param json
   *          the json
   */
  public BulkWriteOptions(JsonObject json) {
    String wo = json.getString(WRITE_OPTION);
    if (wo != null) {
      setWriteOption(WriteOption.valueOf(wo.toUpperCase()));
    }
    setOrdered(json.getBoolean(ORDERED, DEFAULT_ORDERED));
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(WRITE_OPTION, writeOption);
    json.put(ORDERED, ordered);
    return json;
  }

  /**
   * Get the write option
   * 
   * @return the write option
   */
  public WriteOption getWriteOption() {
    return writeOption;
  }

  /**
   * Set the write option
   * 
   * @param writeOption
   *          the write option
   * @return fluent reference to this
   */
  public BulkWriteOptions setWriteOption(WriteOption writeOption) {
    this.writeOption = writeOption;
    return this;
  }

  /**
   * Get whether the operations will be executed in the given order
   * 
   * @return if ordered is enabled
   */
  public boolean isOrdered() {
    return ordered;
  }

  /**
   * Set the ordered option
   * 
   * @param ordered
   *          the ordered option
   * @return fluent reference to this
   */
  public BulkWriteOptions setOrdered(boolean ordered) {
    this.ordered = ordered;
    return this;
  }
}
