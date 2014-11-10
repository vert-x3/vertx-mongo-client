package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SSLSettings;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SSLSettingsParser {
  private final SSLSettings settings;

  public SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    SSLSettings.Builder settings = SSLSettings.builder();
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

  public SSLSettings settings() {
    return settings;
  }
}
