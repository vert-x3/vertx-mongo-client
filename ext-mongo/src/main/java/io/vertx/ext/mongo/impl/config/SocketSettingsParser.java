package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class SocketSettingsParser extends AbstractParser {
  private final SocketSettings settings;

  public SocketSettingsParser(ConnectionString connectionString, JsonObject config) {
    SocketSettings.Builder settings = SocketSettings.builder();
    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    } else {
      Integer connectTimeoutMS = get(config, "connectTimeoutMS", Integer.class);
      if (connectTimeoutMS != null) {
        settings.connectTimeout(connectTimeoutMS, MILLISECONDS);
      }
      Integer socketTimeoutMS = get(config, "socketTimeoutMS", Integer.class);
      if (socketTimeoutMS != null) {
        settings.readTimeout(socketTimeoutMS, MILLISECONDS);
      }
      Boolean keepAlive = get(config, "keepAlive", Boolean.class);
      if (keepAlive != null) {
        settings.keepAlive(keepAlive);
      }
      Integer receiveBufferSize = get(config, "receiveBufferSize", Integer.class);
      if (receiveBufferSize != null) {
        settings.receiveBufferSize(receiveBufferSize);
      }
      Integer sendBufferSize = get(config, "sendBufferSize", Integer.class);
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
