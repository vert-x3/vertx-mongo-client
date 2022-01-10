package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Contains all data needed for one operation of a bulk write operation.
 *
 * @author sschmitt
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

  private BulkOperationType type;
  private JsonObject filter;
  private JsonObject document;
  private boolean upsert;
  private boolean multi;
  private CollationOptions collation;

  /**
   * Constructor for a new instance with the given type
   *
   * @param type the type
   */
  private BulkOperation(BulkOperationType type) {
    this.type = type;
    this.filter = null;
    this.document = null;
    this.upsert = DEFAULT_UPSERT;
    this.multi = DEFAULT_MULTI;
    this.collation = null;
  }

  /**
   * Json constructor
   *
   * @param json the json object
   */
  public BulkOperation(JsonObject json) {
    String typeValue = json.getString("type");
    type = typeValue != null ? BulkOperationType.valueOf(typeValue.toUpperCase()): null;
    filter = json.getJsonObject("filter");
    document = json.getJsonObject("document");
    upsert = json.getBoolean("upsert");
    multi = json.getBoolean("multi");
    collation = json.getJsonObject("collation") != null ? new CollationOptions(json.getJsonObject("collation")) : null;
  }

  /**
   * Create a new delete operation with the given filter
   *
   * @param filter the filter
   * @return a new delete operation instance
   */
  public static BulkOperation createDelete(JsonObject filter) {
    return new BulkOperation(BulkOperationType.DELETE).setFilter(filter);
  }

  /**
   * Create a new insert operation with the given document
   *
   * @param document the document to insert
   * @return a new insert operation instance
   */
  public static BulkOperation createInsert(JsonObject document) {
    return new BulkOperation(BulkOperationType.INSERT).setDocument(document);
  }

  /**
   * Create a new replace operation with the given filter and replace document
   *
   * @param filter   the filter
   * @param document the replace document
   * @return a new replace operation instance
   */
  public static BulkOperation createReplace(JsonObject filter, JsonObject document) {
    return new BulkOperation(BulkOperationType.REPLACE).setFilter(filter).setDocument(document);
  }

  /**
   * Create a new replace operation with the given filter, replace document, and the upsert flag
   *
   * @param filter   the filter
   * @param document the replace document
   * @param upsert   the upsert flag
   * @return a new replace operation instance
   */
  public static BulkOperation createReplace(JsonObject filter, JsonObject document, boolean upsert) {
    return new BulkOperation(BulkOperationType.REPLACE).setFilter(filter).setDocument(document).setUpsert(upsert);
  }

  /**
   * Create a new update operation with the given filter and update document
   *
   * @param filter   the filter
   * @param document the update document
   * @return a new update operation instance
   */
  public static BulkOperation createUpdate(JsonObject filter, JsonObject document) {
    return new BulkOperation(BulkOperationType.UPDATE).setFilter(filter).setDocument(document);
  }

  /**
   * Create a new update operation with the given filter, update document, the upsert flag, and multi flag
   *
   * @param filter   the filter
   * @param document the update document
   * @param upsert   the upsert flag
   * @param multi    the multi flag
   * @return a new update operation instance
   */
  public static BulkOperation createUpdate(JsonObject filter, JsonObject document, boolean upsert, boolean multi) {
    return new BulkOperation(BulkOperationType.UPDATE).setFilter(filter).setDocument(document).setUpsert(upsert)
      .setMulti(multi);
  }

  public CollationOptions getCollation() {
    return collation;
  }

  public BulkOperation setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
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
    json.put("collation", collation != null ? collation.toJson() : null);
    return json;
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
   * @param type the operation type
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
   * @param filter the filter document
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
   * @param document the document
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
   * @param upsert the upsert flag
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
   * @param multi the mutli flag
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
    BulkOperation operation = (BulkOperation) o;
    return upsert == operation.upsert && multi == operation.multi && type == operation.type && Objects.equals(filter, operation.filter) && Objects.equals(document, operation.document) && Objects.equals(collation, operation.collation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, filter, document, upsert, multi, collation);
  }

  @Override
  public String toString() {
    return "BulkOperation{" +
      "type=" + type +
      ", filter=" + filter +
      ", document=" + document +
      ", upsert=" + upsert +
      ", multi=" + multi +
      ", collation=" + collation +
      '}';
  }
}
