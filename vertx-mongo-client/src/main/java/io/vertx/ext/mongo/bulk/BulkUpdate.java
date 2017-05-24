package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Update operation for the bulk write operation. Can execute update commands on one or more documents.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkUpdate implements BulkOperation {

  public static final boolean DEFAULT_UPSERT = false;
  public static final boolean DEFAULT_MULTI = false;

  private JsonObject document;
  private JsonObject filter;
  private boolean upsert;
  private boolean multi;

  /**
   * Constructor that specifies the filter and the update document
   * 
   * @param filter
   *          the filter
   * @param document
   *          the update document
   */
  public BulkUpdate(JsonObject filter, JsonObject document) {
    this(filter, document, DEFAULT_UPSERT, DEFAULT_MULTI);
  }

  /**
   * Constructor that specifies the filter, the update document, and the upsert flag
   * 
   * @param filter
   *          the filter
   * @param document
   *          the update document
   * @param upsert
   *          the upsert flag
   */
  public BulkUpdate(JsonObject filter, JsonObject document, boolean upsert) {
    this(filter, document, upsert, DEFAULT_MULTI);
  }

  /**
   * Constructor that specifies the filter, the update document, the upsert flag, and the multi flag
   * 
   * @param filter
   *          the filter
   * @param document
   *          the update document
   * @param upsert
   *          the upsert flag
   * @param multi
   *          the multi flag
   */
  public BulkUpdate(JsonObject filter, JsonObject document, boolean upsert, boolean multi) {
    this.filter = filter;
    this.document = document;
    this.upsert = upsert;
    this.multi = multi;
  }

  /**
   * Returns the update document
   * 
   * @return the update document
   */
  public JsonObject getDocument() {
    return document;
  }

  /**
   * Sets the update document
   * 
   * @param document
   *          the update document
   * @return this for fluencys
   */
  public BulkUpdate setDocument(JsonObject document) {
    this.document = document;
    return this;
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
   * Sets the filter
   * 
   * @param filter
   *          the filter
   * @return this for fluency
   */
  public BulkUpdate setFilter(JsonObject filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Returns the upsert flag
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
   * @return this for fluency
   */
  public BulkUpdate setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  /**
   * Returns the multi flag
   * 
   * @return the multi flag
   */
  public boolean isMulti() {
    return multi;
  }

  /**
   * Sets the multi flag
   * 
   * @param multi
   *          the multi flag
   * @return this for fluency
   */
  public BulkUpdate setMulti(boolean multi) {
    this.multi = multi;
    return this;
  }
}
