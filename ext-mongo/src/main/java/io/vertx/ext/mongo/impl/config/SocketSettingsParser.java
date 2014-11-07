package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class SocketSettingsParser {
  private final SocketSettings settings;

  public SocketSettingsParser(ConnectionString connectionString, JsonObject config) {
    SocketSettings.Builder settings = SocketSettings.builder();
    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    } else {
      Integer connectTimeoutMS = config.getInteger("connectTimeoutMS");
      if (connectTimeoutMS != null) {
        settings.connectTimeout(connectTimeoutMS, MILLISECONDS);
      }
      Integer socketTimeoutMS = config.getInteger("socketTimeoutMS");
      if (socketTimeoutMS != null) {
        settings.readTimeout(socketTimeoutMS, MILLISECONDS);
      }
      Boolean keepAlive = config.getBoolean("keepAlive");
      if (keepAlive != null) {
        settings.keepAlive(keepAlive);
      }
      Integer receiveBufferSize = config.getInteger("receiveBufferSize");
      if (receiveBufferSize != null) {
        settings.receiveBufferSize(receiveBufferSize);
      }
      Integer sendBufferSize = config.getInteger("sendBufferSize");
      if (sendBufferSize != null) {
        settings.sendBufferSize(sendBufferSize);
      }
    }

    this.settings = settings.build();
  }

  public SocketSettings settings() {
    return settings;
  }
}
