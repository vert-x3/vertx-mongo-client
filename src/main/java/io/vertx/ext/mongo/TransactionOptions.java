package io.vertx.ext.mongo;

import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
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

  private String readConcernLevel;
  private String writeConcern;
  private String readPreference;
  private Long maxCommitTimeMillis;
  private Long timeoutMillis;

  public TransactionOptions() {
    // default constructor
  }

  /**
   * Copy constructor.
   */
  public TransactionOptions(TransactionOptions options) {
    readConcernLevel = options.readConcernLevel;
    writeConcern = options.writeConcern;
    readPreference = options.readPreference;
    maxCommitTimeMillis = options.maxCommitTimeMillis;
    timeoutMillis = options.timeoutMillis;
  }

  public TransactionOptions(JsonObject json) {
    TransactionOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TransactionOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * @return the read concern level, e.g. {@code "local"} or {@code "majority"}
   */
  public String getReadConcernLevel() {
    return readConcernLevel;
  }

  /**
   * @param readConcernLevel the read concern level to set, e.g. {@code "local"} or {@code "majority"}
   */
  public TransactionOptions setReadConcernLevel(String readConcernLevel) {
    this.readConcernLevel = readConcernLevel;
    return this;
  }

  /**
   * @return the write concern, either a named one such as {@code "majority"} or a number of nodes
   */
  public String getWriteConcern() {
    return writeConcern;
  }

  /**
   * @param writeConcern the write concern to set, either a named one such as {@code "majority"} or a number of nodes
   */
  public TransactionOptions setWriteConcern(String writeConcern) {
    this.writeConcern = writeConcern;
    return this;
  }

  /**
   * @return the read preference, e.g. {@code "primary"}
   */
  public String getReadPreference() {
    return readPreference;
  }

  /**
   * @param readPreference the read preference to set, e.g. {@code "primary"}
   */
  public TransactionOptions setReadPreference(String readPreference) {
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
   * @param timeoutMillis the timeoutMillis to set, also bounds the transaction retry loop
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
    com.mongodb.TransactionOptions.Builder builder = com.mongodb.TransactionOptions.builder();
    if (readConcernLevel != null) {
      builder.readConcern(new ReadConcern(ReadConcernLevel.fromString(readConcernLevel)));
    }
    if (writeConcern != null) {
      builder.writeConcern(toWriteConcern(writeConcern));
    }
    if (readPreference != null) {
      builder.readPreference(ReadPreference.valueOf(readPreference));
    }
    if (maxCommitTimeMillis != null) {
      builder.maxCommitTime(maxCommitTimeMillis, TimeUnit.MILLISECONDS);
    }
    if (timeoutMillis != null) {
      builder.timeout(timeoutMillis, TimeUnit.MILLISECONDS);
    }
    return builder.build();
  }

  private static WriteConcern toWriteConcern(String value) {
    try {
      return new WriteConcern(Integer.parseInt(value));
    } catch (NumberFormatException ignored) {
      // do nothing
    }
    return WriteConcern.valueOf(value);
  }

  @Override
  public String toString() {
    return "TransactionOptions{" +
      "readConcernLevel=" + readConcernLevel +
      ", writeConcern=" + writeConcern +
      ", readPreference=" + readPreference +
      ", maxCommitTimeMillis=" + maxCommitTimeMillis +
      ", timeoutMillis=" + timeoutMillis +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TransactionOptions)) return false;
    TransactionOptions that = (TransactionOptions) o;
    return Objects.equals(readConcernLevel, that.readConcernLevel)
      && Objects.equals(writeConcern, that.writeConcern)
      && Objects.equals(readPreference, that.readPreference)
      && Objects.equals(maxCommitTimeMillis, that.maxCommitTimeMillis)
      && Objects.equals(timeoutMillis, that.timeoutMillis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(readConcernLevel, writeConcern, readPreference, maxCommitTimeMillis, timeoutMillis);
  }

}
