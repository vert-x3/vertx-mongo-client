package io.vertx.ext.mongo.bulk;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Insert operation for bulk operations. Inserts one document.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkInsert implements BulkOperation {

  private JsonObject document;

  /**
   * Constructor that specifies the document to insert
   * 
   * @param document
   *          the document
   */
  public BulkInsert(JsonObject document) {
    this.document = document;
  }

  /**
   * Returns the document that should be inserted
   * 
   * @return the document
   */
  public JsonObject getDocument() {
    return document;
  }

  /**
   * Set the document that should be inserted
   * 
   * @param document
   *          the document
   * @return this for fluency
   */
  public BulkInsert setDocument(JsonObject document) {
    this.document = document;
    return this;
  }
}
