package io.vertx.ext.mongo;

import com.mongodb.ClientSessionOptions;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options used to configure the client sessions and its transactions.
 *
 * <p>Added in MongoDB 4.2 https://www.mongodb.com/docs/manual/core/transactions/</p>
 */
@DataObject
@JsonGen(publicConverter = false)
public class SessionOptions {

  private boolean autoClose;
  private boolean autoStart;
  private ClientSessionOptions clientSessionOptions;

  public SessionOptions() {
    init();
  }

  private void init() {
    autoStart = true;
    autoClose = true;
  }

  /**
   * Copy constructor.
   */
  public SessionOptions(SessionOptions options) {
    autoClose = options.autoClose;
    clientSessionOptions = options.clientSessionOptions;
  }

  public SessionOptions(JsonObject json) {
    init();
    SessionOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    SessionOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * @return the autoStart flag
   */
  public boolean isAutoStart() {
    return autoStart;
  }

  /**
   * @param autoStart the autoStart flag to set
   */
  public SessionOptions setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
    return this;
  }

  /**
   * @return the autoClose flag
   */
  public boolean isAutoClose() {
    return autoClose;
  }

  /**
   * @param autoClose the autoClose flag to set
   */
  public SessionOptions setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
    return this;
  }

  /**
   * @return the clientSessionOptions
   */
  public ClientSessionOptions getClientSessionOptions() {
    return clientSessionOptions;
  }

  /**
   * @param clientSessionOptions the clientSessionOptions to set
   */
  public SessionOptions setClientSessionOptions(ClientSessionOptions clientSessionOptions) {
    this.clientSessionOptions = clientSessionOptions;
    return this;
  }

  @Override
  public String toString() {
    return "SessionOptions{"
      + "autoClose='"
      + autoClose
      + '\''
      + ", clientSessionOptions="
      + clientSessionOptions
      + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SessionOptions that = (SessionOptions) o;
    return autoClose == that.autoClose &&
      Objects.equals(clientSessionOptions, that.clientSessionOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(autoClose, clientSessionOptions);
  }

}
