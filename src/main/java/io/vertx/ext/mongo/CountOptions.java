package io.vertx.ext.mongo;


import com.mongodb.assertions.Assertions;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.JsonObjectBsonAdapter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@DataObject(generateConverter = true)
public class CountOptions {
  private static final TimeUnit DEFAULT_MAX_TIME_TIMEUNIT = TimeUnit.MILLISECONDS;
  private JsonObject hint;
  private String hintString;
  private int limit;
  private int skip;
  private long maxTime;
  private CollationOptions collation;

  public CountOptions() {
  }

  public CountOptions(CountOptions countOptions) {
    this.hint = countOptions.getHint();
    this.hintString = countOptions.getHintString();
    this.limit = countOptions.getLimit();
    this.skip = countOptions.getSkip();
    this.maxTime = countOptions.getMaxTime(DEFAULT_MAX_TIME_TIMEUNIT);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CountOptions that = (CountOptions) o;
    return getLimit() == that.getLimit() && getSkip() == that.getSkip() && maxTime == that.maxTime && Objects.equals(getHint(), that.getHint()) && Objects.equals(getHintString(), that.getHintString()) && Objects.equals(getCollation(), that.getCollation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getHint(), getHintString(), getLimit(), getSkip(), maxTime, getCollation());
  }

  /**
   * Returns the mongo-java-driver specific object.
   * @return com.mongodb.client.model.CountOptions
   */
  public com.mongodb.client.model.CountOptions toMongoDriverObject() {
    com.mongodb.client.model.CountOptions options = new com.mongodb.client.model.CountOptions();
    if (collation != null) {
      options.collation(collation.toMongoDriverObject());
    }

    return options
      .hint(hint == null ? null : new JsonObjectBsonAdapter(hint))
      .hintString(hintString)
      .limit(limit)
      .skip(skip)
      .maxTime(maxTime, DEFAULT_MAX_TIME_TIMEUNIT);
  }

  /**
   * Gets the hint to apply.
   * @return the hint, which should describe an existing
   */
  public JsonObject getHint() {
    return this.hint;
  }

  /**
   * Gets the hint string to apply.
   * @return the hint string, which should be the name of an existing index
   */
  public String getHintString() {
    return this.hintString;
  }

  /**
   * Optional. The index to use. Specify either the index name as a string or the index specification document.
   * @param hint
   * @return CountOptions
   */
  public CountOptions setHint(JsonObject hint) {
    this.hint = hint;
    return this;
  }

  /**
   * Sets the hint to apply.
   * Note: If hint is set, that will be used instead of any hint string.
   * @param hint the name of the index which should be used for the operation
   * @return CountOptions
   */
  public CountOptions setHintString(String hint) {
    this.hintString = hint;
    return this;
  }

  /**
   * Gets the limit to apply. The default is 0, which means there is no limit.
   * @return the limit
   */
  public int getLimit() {
    return this.limit;
  }

  /**
   * Sets the limit to apply.
   * @param limit the limit
   * @return CountOptions
   */
  public CountOptions setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Gets the number of documents to skip. The default is 0.
   * @return the number of documents to skip
   */
  public int getSkip() {
    return this.skip;
  }

  /**
   * Optional. The number of matching documents to skip before returning results.
   * @param skip
   * @return
   */
  public CountOptions setSkip(int skip) {
    this.skip = skip;
    return this;
  }

  /**
   * Gets the maximum execution time in milliseconds on the server for this operation.
   * The default is 0, which places no limit on the execution time.
   * @return the maximum execution time in milliseconds in the given time unit
   */
  public long getMaxTime() {
    return getMaxTime(DEFAULT_MAX_TIME_TIMEUNIT);
  }

  /**
   * Sets the maximum execution time in milliseconds on the server for this operation.
   * @param maxTimeMS the max time in milliseconds
   * @return CountOptions
   */
  public CountOptions setMaxTime(long maxTimeMS) {
    return setMaxTime(maxTimeMS, DEFAULT_MAX_TIME_TIMEUNIT);
  }

  /**
   * Gets the maximum execution time on the server for this operation. The default is 0, which places no limit on the execution time.
   * @param timeUnit the time unit to return the result in
   * @return the maximum execution time in the given time unit
   */
  @GenIgnore
  public long getMaxTime(TimeUnit timeUnit) {
    Assertions.notNull("timeUnit", timeUnit);
    return timeUnit.convert(this.maxTime, DEFAULT_MAX_TIME_TIMEUNIT);
  }

  /**
   * Sets the maximum execution time on the server for this operation.
   * @param maxTime the max time
   * @param timeUnit the time unit, which may not be null
   * @return CountOptions
   */
  @GenIgnore
  public CountOptions setMaxTime(long maxTime, TimeUnit timeUnit) {
    Assertions.notNull("timeUnit", timeUnit);
    this.maxTime = DEFAULT_MAX_TIME_TIMEUNIT.convert(maxTime, timeUnit);
    return this;
  }

  public CollationOptions getCollation() {
    return this.collation;
  }

  /**
   * Sets the collation options
   * @return CollationOptions
   */
  public CountOptions setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  public String toString() {
    return "CountOptions{hint=" + this.hint + ", hintString='" + this.hintString + '\'' + ", limit=" + this.limit + ", skip=" + this.skip + ", maxTimeMS=" + this.maxTime + ", collation=" + this.collation + '}';
  }
}
