package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options used to configure aggregate operations.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@DataObject
@JsonGen(publicConverter = false)
public class AggregateOptions {
  /**
   * The default value of batchSize = 20.
   */
  public static final int DEFAULT_BATCH_SIZE = 20;
  /**
   * The default value of maxTime = 0.
   */
  public static final long DEFAULT_MAX_TIME = 0L;
  /**
   * The default value of maxAwaitTime = 1000.
   */
  public static final long DEFAULT_MAX_AWAIT_TIME = 1000L;

  private int batchSize;
  private long maxTime;
  private long maxAwaitTime;
  private Boolean allowDiskUse;
  private CollationOptions collation;
  /**
   * Default constructor
   */
  public AggregateOptions() {
    this.batchSize = DEFAULT_BATCH_SIZE;
    this.maxTime = DEFAULT_MAX_TIME;
    this.maxAwaitTime = DEFAULT_MAX_AWAIT_TIME;
    this.collation = null;
  }

  /**
   * Copy constructor
   *
   * @param options the one to copy
   */
  public AggregateOptions(AggregateOptions options) {
    this.batchSize = options.batchSize;
    this.maxTime = options.maxTime;
    this.maxAwaitTime = options.maxAwaitTime;
    this.allowDiskUse = options.allowDiskUse;
    this.collation = options.collation;
  }

  /**
   * Constructor from JSON
   *
   * @param options the JSON
   */
  public AggregateOptions(JsonObject options) {
    this();
    AggregateOptionsConverter.fromJson(options, this);
  }

  /**
   * @return Configured collationOptions
   */
  public CollationOptions getCollation() {
    return collation;
  }

  /**
   * Optional.
   * <p>
   * Specifies the collation to use for the operation.
   * <p>
   * Collation allows users to specify language-specific rules for string comparison, such as rules for lettercase and accent marks.
   *
   * @param collation
   * @return reference to this, for fluency
   */
  public AggregateOptions setCollation(CollationOptions collation) {
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
    AggregateOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * Get the specified time limit in milliseconds for processing operations on a cursor.
   * If you do not specify a value for maxTime, operations will not time out.
   * A value of 0 explicitly specifies the default unbounded behavior.
   *
   * @return the specified time limit in milliseconds for processing operations on a cursor
   */
  public long getMaxTime() {
    return maxTime;
  }

  /**
   * Set the time limit in milliseconds for processing operations on a cursor.
   *
   * @param maxTime the time limit in milliseconds for processing operations on a cursor
   * @return reference to this, for fluency
   */
  public AggregateOptions setMaxTime(long maxTime) {
    this.maxTime = maxTime;
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
  public AggregateOptions setBatchSize(int batchSize) {
    this.batchSize = batchSize;
    return this;
  }

  /**
   * Get the flag if writing to temporary files is enabled.
   * When set to true, aggregation operations can write data to the _tmp subdirectory in the dbPath directory.
   *
   * @return true if writing to temporary files is enabled.
   */
  public Boolean getAllowDiskUse() {
    return allowDiskUse;
  }

  /**
   * Set the flag if writing to temporary files is enabled.
   *
   * @param allowDiskUse the flag indicating disk usage on aggregate or not.
   * @return reference to this, for fluency
   */
  public AggregateOptions setAllowDiskUse(final Boolean allowDiskUse) {
    this.allowDiskUse = allowDiskUse;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AggregateOptions that = (AggregateOptions) o;
    return batchSize == that.batchSize && maxTime == that.maxTime && Objects.equals(allowDiskUse, that.allowDiskUse) && Objects.equals(collation, that.collation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchSize, maxTime, allowDiskUse, collation);
  }

  @Override
  public String toString() {
    return "AggregateOptions{" +
      "batchSize=" + batchSize +
      ", maxTime=" + maxTime +
      ", allowDiskUse=" + allowDiskUse +
      ", collation=" + collation +
      '}';
  }
}
