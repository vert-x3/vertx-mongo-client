package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SSLSettingsParser {
  private final SslSettings settings;

  public SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    SslSettings.Builder settings = SslSettings.builder();
    Boolean ssl;
    if (connectionString != null) {
      ssl = connectionString.getSslEnabled();
    } else {
      ssl = config.getBoolean("ssl");
    }

    if (ssl != null) {
      settings.enabled(ssl);
    }
    this.settings = settings.build();
  }

  public SslSettings settings() {
    return settings;
  }
}
