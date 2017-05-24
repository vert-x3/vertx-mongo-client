package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Replace operation for bulk write operations. Replaces one document that matches the filter with a given document. Can
 * also create a new document depending on the "upsert" flag.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkReplace implements BulkOperation {

  public static final boolean DEFAULT_UPSERT = false;

  private JsonObject replacement;
  private JsonObject filter;
  private boolean upsert;

  /**
   * Constructor to set the filter and the replacement document.
   * 
   * @param filter
   *          the filter
   * @param replacement
   *          the replacement document
   */
  public BulkReplace(JsonObject filter, JsonObject replacement) {
    this(filter, replacement, DEFAULT_UPSERT);
  }

  /**
   * Constructor to set the filter, the replacement document, and the upsert flag
   * 
   * @param filter
   *          the filter
   * @param replacement
   *          the replacement document
   * @param upsert
   *          the upsert flag
   */
  public BulkReplace(JsonObject filter, JsonObject replacement, boolean upsert) {
    this.filter = filter;
    this.replacement = replacement;
    this.upsert = upsert;
  }

  /**
   * Returns the filter
   * 
   * @return the filter
   */
  public JsonObject getFilter() {
    return filter;
  }

  /**
   * Set the filter
   * 
   * @param filter
   *          the filter
   * @return this for fluency
   */
  public BulkReplace setFilter(JsonObject filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Returns the replacement document
   * 
   * @return the replacement document
   */
  public JsonObject getReplacement() {
    return replacement;
  }

  /**
   * Sets the replacement document
   * 
   * @param replacement
   *          the replacement document
   * @return this for fluency
   */
  public BulkReplace setReplacement(JsonObject replacement) {
    this.replacement = replacement;
    return this;
  }

  /**
   * Returns the state of the upsert flag
   * 
   * @return the upsert flag
   */
  public boolean isUpsert() {
    return upsert;
  }

  /**
   * Set the upsert flag
   * 
   * @param upsert
   *          the upsert flag
   * @return this for fluencys
   */
  public BulkReplace setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

}
