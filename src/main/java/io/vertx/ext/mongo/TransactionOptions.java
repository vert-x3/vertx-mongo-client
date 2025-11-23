package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Options used to configure transactions.
 *
 * <p>Added in MongoDB 4.2 https://www.mongodb.com/docs/manual/core/transactions/</p>
 */
@DataObject
@JsonGen(publicConverter = false)
public class TransactionOptions {

  private com.mongodb.ReadConcern readConcern;
  private com.mongodb.WriteConcern writeConcern;
  private com.mongodb.ReadPreference readPreference;
  private Long maxCommitTimeMillis;
  private Long timeoutMillis;

  public TransactionOptions() {
    init();
  }

  private void init() {
  }

  /**
   * Copy constructor.
   */
  public TransactionOptions(TransactionOptions options) {
    readConcern = options.readConcern;
    writeConcern = options.writeConcern;
    readPreference = options.readPreference;
    maxCommitTimeMillis = options.maxCommitTimeMillis;
    timeoutMillis = options.timeoutMillis;
  }

  public TransactionOptions(JsonObject json) {
    init();
    TransactionOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TransactionOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * @return the readConcern
   */
  public com.mongodb.ReadConcern getReadConcern() {
    return readConcern;
  }

  /**
   * @param readConcern the readConcern to set
   */
  public TransactionOptions setReadConcern(com.mongodb.ReadConcern readConcern) {
    this.readConcern = readConcern;
    return this;
  }

  /**
   * @return the writeConcern
   */
  public com.mongodb.WriteConcern getWriteConcern() {
    return writeConcern;
  }


  /**
   * @param writeConcern the writeConcern to set
   */
  public TransactionOptions setWriteConcern(com.mongodb.WriteConcern writeConcern) {
    this.writeConcern = writeConcern;
    return this;
  }

  /**
   * @return the readPreference
   */
  public com.mongodb.ReadPreference getReadPreference() {
    return readPreference;
  }

  /**
   * @param readPreference the readPreference to set
   */
  public TransactionOptions setReadPreference(com.mongodb.ReadPreference readPreference) {
    this.readPreference = readPreference;
    return this;
  }

  /**
   * @return the maxCommitTimeMillis
   */
  public Long getMaxCommitTimeMillis() {
    return maxCommitTimeMillis;
  }

  /**
   * @param maxCommitTimeMillis the maxCommitTimeMillis to set
   */
  public TransactionOptions setMaxCommitTimeMillis(Long maxCommitTimeMillis) {
    this.maxCommitTimeMillis = maxCommitTimeMillis;
    return this;
  }

  /**
   * @param maxCommitTime the maxCommitTime to set
   * @param timeUnit the timeUnit of maxCommitTime
   */
  public TransactionOptions setMaxCommitTime(long maxCommitTime, TimeUnit timeUnit) {
    this.maxCommitTimeMillis = timeUnit.toMillis(maxCommitTime);
    return this;
  }

  /**
   * @return the timeoutMillis
   */
  public Long getTimeoutMillis() {
    return timeoutMillis;
  }

  /**
   * @param timeoutMillis the timeoutMillis to set
   */
  public TransactionOptions setTimeoutMillis(Long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
    return this;
  }

  /**
   * @param timeout the timeout to set
   * @param timeUnit the timeUnit of timeout
   */
  public TransactionOptions setTimeout(long timeout, TimeUnit timeUnit) {
    this.timeoutMillis = timeUnit.toMillis(timeout);
    return this;
  }

  public com.mongodb.TransactionOptions toMongoDriverObject() {
    final com.mongodb.TransactionOptions.Builder builder = com.mongodb.TransactionOptions.builder();
    if (readConcern != null) builder.readConcern(readConcern);
    if (writeConcern != null) builder.writeConcern(writeConcern);
    if (readPreference != null) builder.readPreference(readPreference);
    if (maxCommitTimeMillis != null) builder.maxCommitTime(maxCommitTimeMillis, TimeUnit.MILLISECONDS);
    if (timeoutMillis != null) builder.timeout(timeoutMillis, TimeUnit.MILLISECONDS);
    return builder.build();
  }

  @Override
  public String toString() {
    return "TransactionOptions{" +
      "readConcern=" + readConcern +
      ", writeConcern=" + writeConcern +
      ", readPreference=" + readPreference +
      ", maxCommitTimeMillis=" + maxCommitTimeMillis +
      ", timeoutMS=" + timeoutMillis +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TransactionOptions)) return false;
    TransactionOptions that = (TransactionOptions) o;
    return Objects.equals(readConcern, that.readConcern)
      && Objects.equals(writeConcern, that.writeConcern)
      && Objects.equals(readPreference, that.readPreference)
      && Objects.equals(maxCommitTimeMillis, that.maxCommitTimeMillis)
      && Objects.equals(timeoutMillis, that.timeoutMillis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(readConcern, writeConcern, readPreference, maxCommitTimeMillis, timeoutMillis);
  }

}
