package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Options used to configure the client sessions and its transactions.
 *
 * <p>Added in MongoDB 4.2 https://www.mongodb.com/docs/manual/core/transactions/</p>
 */
@DataObject
@JsonGen(publicConverter = false)
public class ClientSessionOptions {

  private Boolean causallyConsistent;
  private Boolean snapshot;
  private Long defaultTimeoutMillis;
  private TransactionOptions defaultTransactionOptions;

  public ClientSessionOptions() {
  }

  /**
   * Copy constructor.
   */
  public ClientSessionOptions(ClientSessionOptions options) {
    causallyConsistent = options.causallyConsistent;
    snapshot = options.snapshot;
    defaultTimeoutMillis = options.defaultTimeoutMillis;
    defaultTransactionOptions = options.defaultTransactionOptions;
  }

  public ClientSessionOptions(JsonObject json) {
    ClientSessionOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ClientSessionOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * @return the causallyConsistent flag
   */
  public Boolean getCausallyConsistent() {
    return causallyConsistent;
  }

  /**
   * @param causallyConsistent the causallyConsistent flag to set
   */
  public ClientSessionOptions setCausallyConsistent(Boolean causallyConsistent) {
    this.causallyConsistent = causallyConsistent;
    return this;
  }

  /**
   * @return the snapshot flag
   */
  public Boolean getSnapshot() {
    return snapshot;
  }

  /**
   * @param snapshot the snapshot flag to set
   */
  public ClientSessionOptions setSnapshot(Boolean snapshot) {
    this.snapshot = snapshot;
    return this;
  }

  /**
   * @return the defaultTimeoutMillis
   */
  public Long getDefaultTimeoutMillis() {
    return defaultTimeoutMillis;
  }

  /**
   * @param defaultTimeoutMillis the defaultTimeoutMillis to set
   */
  public ClientSessionOptions setDefaultTimeoutMillis(Long defaultTimeoutMillis) {
    this.defaultTimeoutMillis = defaultTimeoutMillis;
    return this;
  }

  /**
   * @param defaultTimeout the defaultTimeout to set
   * @param timeUnit the timeUnit of defaultTimeout
   */
  public ClientSessionOptions setDefaultTimeout(long defaultTimeout, TimeUnit timeUnit) {
    this.defaultTimeoutMillis = timeUnit.toMillis(defaultTimeout);
    return this;
  }

  /**
   * @return the defaultTransactionOptions
   */
  public TransactionOptions getDefaultTransactionOptions() {
    return defaultTransactionOptions;
  }

  /**
   * @param defaultTransactionOptions the defaultTransactionOptions to set
   */
  public ClientSessionOptions setDefaultTransactionOptions(TransactionOptions defaultTransactionOptions) {
    this.defaultTransactionOptions = defaultTransactionOptions;
    return this;
  }

  public com.mongodb.ClientSessionOptions toMongoDriverObject() {
    final com.mongodb.ClientSessionOptions.Builder builder = com.mongodb.ClientSessionOptions.builder();
    if (causallyConsistent != null) builder.causallyConsistent(causallyConsistent);
    if (snapshot != null) builder.snapshot(snapshot);
    if (defaultTimeoutMillis != null) builder.defaultTimeout(defaultTimeoutMillis, TimeUnit.MILLISECONDS);
    if (defaultTransactionOptions != null) builder.defaultTransactionOptions(defaultTransactionOptions.toMongoDriverObject());
    return builder.build();
  }

  @Override
  public String toString() {
    return "ClientSessionOptions{" +
      "causallyConsistent=" + causallyConsistent +
      ", snapshot=" + snapshot +
      ", defaultTimeoutMillis=" + defaultTimeoutMillis +
      ", defaultTransactionOptions=" + defaultTransactionOptions +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ClientSessionOptions)) return false;
    ClientSessionOptions that = (ClientSessionOptions) o;
    return Objects.equals(causallyConsistent, that.causallyConsistent)
      && Objects.equals(snapshot, that.snapshot)
      && Objects.equals(defaultTimeoutMillis, that.defaultTimeoutMillis)
      && Objects.equals(defaultTransactionOptions, that.defaultTransactionOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(causallyConsistent, snapshot, defaultTimeoutMillis, defaultTransactionOptions);
  }

}
