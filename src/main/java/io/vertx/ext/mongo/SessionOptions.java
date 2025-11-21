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

  private boolean closeSession;
  private ClientSessionOptions clientSessionOptions;

  public SessionOptions() {
    init();
  }

  private void init() {
    closeSession = true;
  }

  /**
   * Copy constructor.
   */
  public SessionOptions(SessionOptions options) {
    closeSession = options.closeSession;
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
   * @return the closeSession
   */
  public boolean closeSession() {
    return closeSession;
  }

  /**
   * @param closeSession the closeSession to set
   */
  public SessionOptions setCloseSession(boolean closeSession) {
    this.closeSession = closeSession;
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
      + "closeSession='"
      + closeSession
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
    return closeSession == that.closeSession &&
      Objects.equals(clientSessionOptions, that.clientSessionOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(closeSession, clientSessionOptions);
  }

}
