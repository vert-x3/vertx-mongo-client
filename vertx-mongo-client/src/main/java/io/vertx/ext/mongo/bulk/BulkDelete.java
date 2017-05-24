package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Delete operation for bulk write operations. Deletes one or more documents that match the filter, depending on the
 * "multi" configuration.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkDelete implements BulkOperation {

  /**
   * The default value of multi = false
   */
  public static final boolean DEFAULT_MULTI = false;

  private JsonObject filter;
  private boolean multi;

  /**
   * Constructor that specifies the filter
   * 
   * @param filter
   *          the filter for the delete operation
   */
  public BulkDelete(JsonObject filter) {
    this(filter, DEFAULT_MULTI);
  }

  /**
   * Constructor that specifies the filter and the multi setting
   * 
   * @param filter
   *          the filter for the delete operation
   * @param multi
   *          if more than one matching document should be deleted
   */
  public BulkDelete(JsonObject filter, boolean multi) {
    this.filter = filter;
    this.multi = multi;
  }

  /**
   * Returns the filter for this delete operation
   * 
   * @return the filter
   */
  public JsonObject getFilter() {
    return filter;
  }

  /**
   * Set the filter for this delete operation.
   * 
   * @param filter
   *          the filter
   * @return this for fluency
   */
  public BulkDelete setFilter(JsonObject filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Returns if the operation should delete multiple document, or only the first matching document
   * 
   * @return the multi setting
   */
  public boolean isMulti() {
    return multi;
  }

  /**
   * Set if the operation should delete multiple document, or only the first matching document
   * 
   * @param multi
   *          the multi setting
   * @return this for fluency
   */
  public BulkDelete setMulti(boolean multi) {
    this.multi = multi;
    return this;
  }
}
