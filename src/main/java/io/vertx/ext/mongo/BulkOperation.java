package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonObject;

/**
 * Contains all data needed for one operation of a bulk write operation.
 * 
 * @author sschmitt
 *
 */
@DataObject
public class BulkOperation {

  /**
   * Default value for the multi flag = false
   */
  private static final boolean DEFAULT_MULTI = false;

  /**
   * Default value for the upsert flag = false
   */
  private static final boolean DEFAULT_UPSERT = false;

  /**
   * Enum for the different possible operations
   */
  @VertxGen
  public enum BulkOperationType {
    UPDATE,
    REPLACE,
    INSERT,
    DELETE;
  }

  private BulkOperationType type;
  private JsonObject filter;
  private JsonObject document;
  private boolean upsert;
  private boolean multi;

  /**
   * Constructor for a new instance with the given type
   * 
   * @param type
   *          the type
   */
  private BulkOperation(BulkOperationType type) {
    this.type = type;
    this.filter = null;
    this.document = null;
    this.upsert = DEFAULT_UPSERT;
    this.multi = DEFAULT_MULTI;
  }

  /**
   * Json constructor
   * 
   * @param json
   *          the json object
   */
  public BulkOperation(JsonObject json) {
    String typeValue = json.getString("type");
    if (typeValue != null)
      this.type = BulkOperationType.valueOf(typeValue.toUpperCase());
    filter = json.getJsonObject("filter");
    document = json.getJsonObject("document");
    upsert = json.getBoolean("upsert");
    multi = json.getBoolean("multi");
  }

  /**
   * Generate a json from this object
   * 
   * @return the json representation
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("type", type);
    json.put("filter", filter);
    json.put("document", document);
    json.put("upsert", upsert);
    json.put("multi", multi);
    return json;
  }

  /**
   * Create a new delete operation with the given filter
   * 
   * @param filter
   *          the filter
   * @return a new delete operation instance
   */
  public static BulkOperation createDelete(JsonObject filter) {
    return new BulkOperation(BulkOperationType.DELETE).setFilter(filter);
  }

  /**
   * Create a new insert operation with the given document
   * 
   * @param document
   *          the document to insert
   * @return a new insert operation instance
   */
  public static BulkOperation createInsert(JsonObject document) {
    return new BulkOperation(BulkOperationType.INSERT).setDocument(document);
  }

  /**
   * Create a new replace operation with the given filter and replace document
   * 
   * @param filter
   *          the filter
   * @param document
   *          the replace document
   * @return a new replace operation instance
   */
  public static BulkOperation createReplace(JsonObject filter, JsonObject document) {
    return new BulkOperation(BulkOperationType.REPLACE).setFilter(filter).setDocument(document);
  }

  /**
   * Create a new replace operation with the given filter, replace document, and the upsert flag
   * 
   * @param filter
   *          the filter
   * @param document
   *          the replace document
   * @param upsert
   *          the upsert flag
   * @return a new replace operation instance
   */
  public static BulkOperation createReplace(JsonObject filter, JsonObject document, boolean upsert) {
    return new BulkOperation(BulkOperationType.REPLACE).setFilter(filter).setDocument(document).setUpsert(upsert);
  }

  /**
   * Create a new update operation with the given filter and update document
   * 
   * @param filter
   *          the filter
   * @param document
   *          the update document
   * @return a new update operation instance
   */
  public static BulkOperation createUpdate(JsonObject filter, JsonObject document) {
    return new BulkOperation(BulkOperationType.UPDATE).setFilter(filter).setDocument(document);
  }

  /**
   * Create a new update operation with the given filter, update document, the upsert flag, and multi flag
   * 
   * @param filter
   *          the filter
   * @param document
   *          the update document
   * @param upsert
   *          the upsert flag
   * @param multi
   *          the multi flag
   * @return a new update operation instance
   */
  public static BulkOperation createUpdate(JsonObject filter, JsonObject document, boolean upsert, boolean multi) {
    return new BulkOperation(BulkOperationType.UPDATE).setFilter(filter).setDocument(document).setUpsert(upsert)
        .setMulti(multi);
  }

  /**
   * Returns the operation type
   * 
   * @return the operation type
   */
  public BulkOperationType getType() {
    return type;
  }

  /**
   * Sets the operation type
   * 
   * @param type
   *          the operation type
   * @return this for fluency
   */
  public BulkOperation setType(BulkOperationType type) {
    this.type = type;
    return this;
  }

  /**
   * Returns the filter document, used by replace, update, and delete operations
   * 
   * @return the filter document
   */
  public JsonObject getFilter() {
    return filter;
  }

  /**
   * Sets the filter document, used by replace, update, and delete operations
   * 
   * @param filter
   *          the filter document
   * @return this for fluency
   */
  public BulkOperation setFilter(JsonObject filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Returns the document, used by insert, replace, and update operations
   * 
   * @return the document
   */
  public JsonObject getDocument() {
    return document;
  }

  /**
   * Sets the document, used by insert, replace, and update operations
   * 
   * @param document
   *          the document
   * @return this for fluency
   */
  public BulkOperation setDocument(JsonObject document) {
    this.document = document;
    return this;
  }

  /**
   * Returns the upsert flag, used by update and replace operations
   * 
   * @return the upsert flag
   */
  public boolean isUpsert() {
    return upsert;
  }

  /**
   * Sets the upsert flag, used by update and replace operations
   * 
   * @param upsert
   *          the upsert flag
   * @return this for fluency
   */
  public BulkOperation setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  /**
   * Returns the multi flag, used by update and delete operations
   * 
   * @return the multi flag
   */
  public boolean isMulti() {
    return multi;
  }

  /**
   * Sets the multi flag, used by update and delete operations
   * 
   * @param multi
   *          the mutli flag
   * @return this for fluency
   */
  public BulkOperation setMulti(boolean multi) {
    this.multi = multi;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BulkOperation other = (BulkOperation) o;

    if (type != other.type) return false;
    if (filter != null ? !filter.equals(other.filter) : other.filter != null) return false;
    if (document != null ? !document.equals(other.document) : other.document != null) return false;

    return upsert == other.upsert && multi == other.multi;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + filter.hashCode();
    result = 31 * result + document.hashCode();
    result = 31 * result + (upsert ? 1 : 0);
    result = 31 * result + (multi ? 1 : 0);
    return result;
  }
}
