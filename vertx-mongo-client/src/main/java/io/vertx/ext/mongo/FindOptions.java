package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options used to configure find operations.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@DataObject
public class FindOptions {

  /**
   * The default value of limit = -1, signifying no limit
   */
  public static final int DEFAULT_LIMIT = -1;

  /**
   * The default value of skip = 0
   */
  public static final int DEFAULT_SKIP = 0;

  private JsonObject fields;
  private JsonObject sort;
  private int limit;
  private int skip;

  /**
   * Default constructor
   */
  public FindOptions() {
    this.limit = DEFAULT_LIMIT;
    this.skip = DEFAULT_SKIP;
  }

  /**
   * Copy constructor
   *
   * @param other  the one to copy
   */
  public FindOptions(FindOptions other) {
    this.fields = other.fields;
    this.sort = other.sort;
    this.limit = other.limit;
    this.skip = other.skip;
  }

  /**
   * Constructor from JSON
   *
   * @param json  the JSON
   */
  public FindOptions(JsonObject json) {
    this.fields = json.getJsonObject("fields");
    this.sort = json.getJsonObject("sort");
    this.limit = json.getInteger("limit", DEFAULT_LIMIT);
    this.skip = json.getInteger("skip", DEFAULT_SKIP);
  }

  /**
   * Convert to JSON
   *
   * @return  the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (fields != null) {
      json.put("fields", fields);
    }
    if (sort != null) {
      json.put("sort", sort);
    }
    if (limit != DEFAULT_LIMIT) {
      json.put("limit", limit);
    }
    if (skip != DEFAULT_SKIP) {
      json.put("skip", skip);
    }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FindOptions options = (FindOptions) o;

    if (limit != options.limit) return false;
    if (skip != options.skip) return false;
    if (fields != null ? !fields.equals(options.fields) : options.fields != null) return false;
    if (sort != null ? !sort.equals(options.sort) : options.sort != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = fields != null ? fields.hashCode() : 0;
    result = 31 * result + (sort != null ? sort.hashCode() : 0);
    result = 31 * result + limit;
    result = 31 * result + skip;
    return result;
  }
}
