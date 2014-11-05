package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.ConnectionPoolSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class ConnectionPoolSettingsParser extends AbstractParser {

  private final ConnectionPoolSettings settings;

  public ConnectionPoolSettingsParser(ConnectionString connectionString, JsonObject config) {
    ConnectionPoolSettings.Builder settings = ConnectionPoolSettings.builder();
    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    } else {
      Integer maxPoolSize = get(config, "maxPoolSize", Integer.class);
      if (maxPoolSize != null) {
        settings.maxSize(maxPoolSize);
      }
      Integer minPoolSize = get(config, "minPoolSize", Integer.class);
      if (minPoolSize != null) {
        settings.minSize(minPoolSize);
      }
      Long maxIdleTimeMS = get(config, "maxIdleTimeMS", Long.class);
      if (maxIdleTimeMS != null) {
        settings.maxConnectionIdleTime(maxIdleTimeMS, MILLISECONDS);
      }
      Long maxLifeTimeMS = get(config, "maxLifeTimeMS", Long.class);
      if (maxLifeTimeMS != null) {
        settings.maxConnectionLifeTime(maxLifeTimeMS, MILLISECONDS);
      }
      Integer waitQueueMultiple = get(config, "waitQueueMultiple", Integer.class);
      if (waitQueueMultiple != null) {
        settings.maxWaitQueueSize(waitQueueMultiple);
      }
      Long waitQueueTimeoutMS = get(config, "waitQueueTimeoutMS", Long.class);
      if (waitQueueTimeoutMS != null) {
        settings.maxWaitTime(waitQueueTimeoutMS, MILLISECONDS);
      }
      Long maintenanceInitialDelayMS = get(config, "maintenanceInitialDelayMS", Long.class);
      if (maintenanceInitialDelayMS != null) {
        settings.maintenanceInitialDelay(maintenanceInitialDelayMS, MILLISECONDS);
      }
      Long maintenanceFrequencyMS = get(config, "maintenanceFrequencyMS", Long.class);
      if (maintenanceFrequencyMS != null) {
        settings.maintenanceFrequency(maintenanceFrequencyMS, MILLISECONDS);
      }
    }

    this.settings = settings.build();
  }

  public ConnectionPoolSettings settings() {
    return settings;
  }
}
