package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

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
   * The default value of batchSize = 10.
   */
  public static final int DEFAULT_BATCH_SIZE = 20;

  private JsonObject fields;
  private JsonObject sort;
  private int limit;
  private int skip;
  private int batchSize;

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
   * @param options  the one to copy
   */
  public FindOptions(FindOptions options) {
    this.fields = options.fields != null ? options.fields.copy() : new JsonObject();
    this.sort = options.sort != null ? options.sort.copy() : new JsonObject();
    this.limit = options.limit;
    this.skip = options.skip;
    this.batchSize = options.batchSize;
  }

  /**
   * Constructor from JSON
   *
   * @param options  the JSON
   */
  public FindOptions(JsonObject options) {
    this();
    FindOptionsConverter.fromJson(options, this);
  }

  /**
   * Convert to JSON
   *
   * @return  the JSON
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
   * @param fields  the fields
   * @return reference to this, for fluency
   */
  public FindOptions setFields(JsonObject fields) {
    this.fields = fields;
    return this;
  }

  /**
   * Get the sort document
   *
   * @return  the sort document
   */
  public JsonObject getSort() {
    return sort;
  }

  /**
   * Set the sort document
   *
   * @param sort  the sort document
   * @return reference to this, for fluency
   */
  public FindOptions setSort(JsonObject sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get the limit - this determines the max number of rows to return
   * @return  the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Set the limit
   *
   * @param limit  the limit
   * @return reference to this, for fluency
   */
  public FindOptions setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get the skip. This determines how many results to skip before returning results.
   *
   * @return  the skip
   */
  public int getSkip() {
    return skip;
  }

  /**
   * Set the skip
   *
   * @param skip  the skip
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FindOptions that = (FindOptions) o;

    if (limit != that.limit) return false;
    if (skip != that.skip) return false;
    if (batchSize != that.batchSize) return false;
    if (fields != null ? !fields.equals(that.fields) : that.fields != null) return false;
    return sort != null ? sort.equals(that.sort) : that.sort == null;
  }

  @Override
  public int hashCode() {
    int result = fields != null ? fields.hashCode() : 0;
    result = 31 * result + (sort != null ? sort.hashCode() : 0);
    result = 31 * result + limit;
    result = 31 * result + skip;
    result = 31 * result + batchSize;
    return result;
  }
}
