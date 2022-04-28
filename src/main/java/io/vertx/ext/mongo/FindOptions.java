package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options used to configure find operations.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@DataObject(generateConverter = true)
public class FindOptions {

  /**
   * The default value of limit = -1, signifying no limit
   */
  public static final int DEFAULT_LIMIT = -1;

  /**
   * The default value of skip = 0
   */
  public static final int DEFAULT_SKIP = 0;

  /**
   * The default value of batchSize = 20.
   */
  public static final int DEFAULT_BATCH_SIZE = 20;

  private JsonObject fields;
  private JsonObject sort;
  private int limit;
  private int skip;
  private int batchSize;
  private JsonObject hint;
  private String hintString;
  private CollationOptions collation;

  /**
   * Default constructor
   */
  public FindOptions() {
    this.fields = new JsonObject();
    this.sort = new JsonObject();
    this.limit = DEFAULT_LIMIT;
    this.skip = DEFAULT_SKIP;
    this.batchSize = DEFAULT_BATCH_SIZE;
  }

  /**
   * Copy constructor
   *
   * @param options the one to copy
   */
  public FindOptions(FindOptions options) {
    this.fields = options.fields != null ? options.fields.copy() : new JsonObject();
    this.sort = options.sort != null ? options.sort.copy() : new JsonObject();
    this.limit = options.limit;
    this.skip = options.skip;
    this.batchSize = options.batchSize;
    this.hint = options.hint;
    this.hintString = options.hintString;
    this.collation = options.getCollation();
  }

  /**
   * Constructor from JSON
   *
   * @param options the JSON
   */
  public FindOptions(JsonObject options) {
    this();
    FindOptionsConverter.fromJson(options, this);
  }

  public CollationOptions getCollation() {
    return collation;
  }

  /**
   * Set the collation
   * @param collation
   * @return reference to this, for fluency
   */
  public FindOptions setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FindOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * Get the fields
   *
   * @return the fields
   */
  public JsonObject getFields() {
    return fields;
  }

  /**
   * Set the fields
   *
   * @param fields the fields
   * @return reference to this, for fluency
   */
  public FindOptions setFields(JsonObject fields) {
    this.fields = fields;
    return this;
  }

  /**
   * Get the sort document
   *
   * @return the sort document
   */
  public JsonObject getSort() {
    return sort;
  }

  /**
   * Set the sort document
   *
   * @param sort the sort document
   * @return reference to this, for fluency
   */
  public FindOptions setSort(JsonObject sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get the limit - this determines the max number of rows to return
   *
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Set the limit
   *
   * @param limit the limit
   * @return reference to this, for fluency
   */
  public FindOptions setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get the skip. This determines how many results to skip before returning results.
   *
   * @return the skip
   */
  public int getSkip() {
    return skip;
  }

  /**
   * Set the skip
   *
   * @param skip the skip
   * @return reference to this, for fluency
   */
  public FindOptions setSkip(int skip) {
    this.skip = skip;
    return this;
  }

  /**
   * @return the batch size for methods loading found data in batches
   */
  public int getBatchSize() {
    return batchSize;
  }

  /**
   * Set the batch size for methods loading found data in batches.
   *
   * @param batchSize the number of documents in a batch
   * @return reference to this, for fluency
   */
  public FindOptions setBatchSize(int batchSize) {
    this.batchSize = batchSize;
    return this;
  }

  /**
   * Get the hint. This determines the index to use.
   *
   * @return the hint
   */
  public JsonObject getHint() {
    return hint;
  }

  /**
   * Set the hint
   *
   * @param hint the hint
   * @return reference to this, for fluency
   */
  public FindOptions setHint(JsonObject hint) {
    this.hint = hint;
    return this;
  }

  /**
   * Get the hint string. This determines the index to use.
   *
   * @return the hint string
   */
  public String getHintString() {
    return hintString;
  }

  /**
   * Set the hint string
   *
   * @param hintString the hint string
   * @return reference to this, for fluency
   */
  public FindOptions setHintString(String hintString) {
    this.hintString = hintString;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FindOptions that = (FindOptions) o;
    return limit == that.limit && skip == that.skip && batchSize == that.batchSize && Objects.equals(fields, that.fields) && Objects.equals(sort, that.sort) && Objects.equals(hint, that.hint) && Objects.equals(hintString, that.hintString) && Objects.equals(collation, that.collation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields, sort, limit, skip, batchSize, hint, hintString, collation);
  }

  @Override
  public String toString() {
    return "FindOptions{" +
      "fields=" + fields +
      ", sort=" + sort +
      ", limit=" + limit +
      ", skip=" + skip +
      ", batchSize=" + batchSize +
      ", hint=" + hint +
      ", hintString='" + hintString + '\'' +
      ", collation=" + collation +
      '}';
  }
}
