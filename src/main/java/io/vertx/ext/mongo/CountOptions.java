package io.vertx.ext.mongo;


import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.JsonObjectBsonAdapter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@DataObject
@JsonGen(publicConverter = false)
public class CountOptions {
  private JsonObject hint;
  private String hintString;
  private Integer limit;
  private Integer skip;
  private Long maxTime;
  private CollationOptions collation;

  public CountOptions() {
    this.hint = null;
    this.hintString = null;
    this.limit = null;
    this.skip = null;
    this.maxTime = null;
    this.collation = null;
  }

  public CountOptions(CountOptions countOptions) {
    this.hint = countOptions.getHint();
    this.hintString = countOptions.getHintString();
    this.limit = countOptions.getLimit();
    this.skip = countOptions.getSkip();
    this.maxTime = countOptions.getMaxTime();
    this.collation = countOptions.getCollation();
  }

  public CountOptions(JsonObject json) {
    CountOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CountOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * Returns the mongo-java-driver specific object.
   *
   * @return com.mongodb.client.model.CountOptions
   */
  public com.mongodb.client.model.CountOptions toMongoDriverObject() {
    com.mongodb.client.model.CountOptions options = new com.mongodb.client.model.CountOptions();
    if (limit != null) {
      options.limit(limit);
    }
    if (skip != null) {
      options.skip(skip);
    }
    if (maxTime != null) {
      options.maxTime(maxTime, TimeUnit.MILLISECONDS);
    }
    if (collation != null) {
      options.collation(collation.toMongoDriverObject());
    }
    if (hint != null) {
      options.hint(new JsonObjectBsonAdapter(hint));
    }
    if (hintString != null) {
      options.hintString(hintString);
    }
    return options;
  }

  /**
   * Gets the hint to apply.
   *
   * @return the hint, which should describe an existing
   */
  public JsonObject getHint() {
    return this.hint;
  }

  /**
   * Optional. The index to use. Specify either the index name as a string or the index specification document.
   *
   * @param hint
   * @return CountOptions
   */
  public CountOptions setHint(JsonObject hint) {
    this.hint = hint;
    return this;
  }

  /**
   * Gets the hint string to apply.
   *
   * @return the hint string, which should be the name of an existing index
   */
  public String getHintString() {
    return this.hintString;
  }

  /**
   * Sets the hint to apply.
   * Note: If hint is set, that will be used instead of any hint string.
   *
   * @param hint the name of the index which should be used for the operation
   * @return CountOptions
   */
  public CountOptions setHintString(String hint) {
    this.hintString = hint;
    return this;
  }

  /**
   * Gets the limit to apply. The default is 0, which means there is no limit.
   *
   * @return the limit
   */
  public Integer getLimit() {
    return this.limit;
  }

  /**
   * Sets the limit to apply.
   *
   * @param limit the limit
   * @return CountOptions
   */
  public CountOptions setLimit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Gets the number of documents to skip. The default is 0.
   *
   * @return the number of documents to skip
   */
  public Integer getSkip() {
    return this.skip;
  }

  /**
   * Optional. The number of matching documents to skip before returning results.
   *
   * @param skip
   * @return
   */
  public CountOptions setSkip(Integer skip) {
    this.skip = skip;
    return this;
  }

  /**
   * Gets the maximum execution time (in ms) on the server for this operation.
   * The default is 0, which places no limit on the execution time.
   *
   * @return the maximum execution time in milliseconds
   */
  public Long getMaxTime() {
    return maxTime;
  }

  /**
   * Sets the maximum execution time (in ms) on the server for this operation.
   *
   * @param maxTime the max time (in ms)
   * @return CountOptions
   */
  public CountOptions setMaxTime(Long maxTime) {
    this.maxTime = maxTime;
    return this;
  }

  public CollationOptions getCollation() {
    return this.collation;
  }

  /**
   * Sets the collation options
   *
   * @return CollationOptions
   */
  public CountOptions setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CountOptions that = (CountOptions) o;
    return Objects.equals(hint, that.hint) && Objects.equals(hintString, that.hintString) && Objects.equals(limit, that.limit) && Objects.equals(skip, that.skip) && Objects.equals(maxTime, that.maxTime) && Objects.equals(collation, that.collation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hint, hintString, limit, skip, maxTime, collation);
  }

  @Override
  public String toString() {
    return "CountOptions{" +
      "hint=" + hint +
      ", hintString='" + hintString + '\'' +
      ", limit=" + limit +
      ", skip=" + skip +
      ", maxTime=" + maxTime +
      ", collation=" + collation +
      '}';
  }
}
