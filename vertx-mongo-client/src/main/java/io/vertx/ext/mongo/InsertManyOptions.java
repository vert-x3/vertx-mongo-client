package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options for configuring inserts.
 *
 * @author <a href="mailto:tomasz.groch@gmail.com">Tomasz Groch</a>
 */
@DataObject
public class InsertManyOptions {

  /**
   * The default value of ordered = true
   */
  public static final boolean DEFAULT_ORDERED = true;

  private boolean ordered;

  /**
   * Default constructor
   */
  public InsertManyOptions() {
    this.ordered = DEFAULT_ORDERED;
  }

  /**
   * Constructor specify ordered
   * @param ordered  the value of ordered
   */
  public InsertManyOptions(boolean ordered) {
    this.ordered = ordered;
  }

  /**
   * Constructor from JSON
   *
   * @param json  the json
   */
  public InsertManyOptions(JsonObject json) {
    ordered = json.getBoolean("ordered", DEFAULT_ORDERED);
  }
  
  /**
   * Copy constructor
   * @param other  the one to copy
   */
  public InsertManyOptions(InsertManyOptions other) {
    this.ordered = other.ordered;
  }

  /**
   * Get whether ordered is enabled
   *
   * @return ordered is enabled?
   */
  public boolean isOrdered() {
    return ordered;
  }

  /**
   * Set whether ordered is enabled
   *
   * @param ordered  true if enabled
   * @return reference to this, for fluency
   */
  public InsertManyOptions setOrdered(boolean ordered) {
    this.ordered = ordered;
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if(!ordered) {
        json.put("ordered", false);
    }
    return json;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (ordered ? 1 : 0);
    return result;    
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    InsertManyOptions other = (InsertManyOptions) obj;
    if (ordered != other.ordered)
        return false;
    return true;
  }
}
