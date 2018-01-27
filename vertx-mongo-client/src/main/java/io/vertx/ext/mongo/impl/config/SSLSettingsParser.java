package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SSLSettingsParser {
  private final ConnectionString connectionString;
  private final JsonObject config;

  SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;
  }

  public SslSettings settings() {
    return fromConnectionString().orElseGet(this::fromConfiguration);
  }

  private Optional<SslSettings> fromConnectionString() {
    return Optional.ofNullable(connectionString).map(cs ->
      SslSettings.builder()
        .applyConnectionString(cs)
        .build()
    );
  }

  private SslSettings fromConfiguration() {
    return SslSettings.builder()
      .enabled(config.getBoolean("ssl", false))
      .invalidHostNameAllowed(config.getBoolean("sslInvalidHostNameAllowed", false))
      .build();
  }
}
