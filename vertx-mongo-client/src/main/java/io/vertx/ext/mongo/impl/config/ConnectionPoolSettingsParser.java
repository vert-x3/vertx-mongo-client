package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.ConnectionPoolSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class ConnectionPoolSettingsParser {

  private final ConnectionPoolSettings settings;

  /*
  * The Default Mongo Driver maxWaitQueueSize @see <a href="https://github.com/mongodb/mongo-java-driver/blob/master/driver-core/src/main/com/mongodb/connection/ConnectionPoolSettings.java#L61">maxWaitQueueSize</a>
  */
  private final Integer DEFAULT_MONGO_DRIVER_WAIT_Q_SIZE = 500;

  public ConnectionPoolSettingsParser(ConnectionString connectionString, JsonObject config) {
    ConnectionPoolSettings.Builder settings = ConnectionPoolSettings.builder();
    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    } else {
      Integer maxPoolSize = config.getInteger("maxPoolSize");
      if (maxPoolSize != null) {
        settings.maxSize(maxPoolSize);
      }
      Integer minPoolSize = config.getInteger("minPoolSize");
      if (minPoolSize != null) {
        settings.minSize(minPoolSize);
      }
      Long maxIdleTimeMS = config.getLong("maxIdleTimeMS");
      if (maxIdleTimeMS != null) {
        settings.maxConnectionIdleTime(maxIdleTimeMS, MILLISECONDS);
      }
      Long maxLifeTimeMS = config.getLong("maxLifeTimeMS");
      if (maxLifeTimeMS != null) {
        settings.maxConnectionLifeTime(maxLifeTimeMS, MILLISECONDS);
      }
      Integer waitQueueMultiple = config.getInteger("waitQueueMultiple");
      if (waitQueueMultiple != null) {
	      Integer waitQueueSize = waitQueueMultiple * DEFAULT_MONGO_DRIVER_WAIT_Q_SIZE;
        settings.maxWaitQueueSize(waitQueueSize);
      }
      Long waitQueueTimeoutMS = config.getLong("waitQueueTimeoutMS");
      if (waitQueueTimeoutMS != null) {
        settings.maxWaitTime(waitQueueTimeoutMS, MILLISECONDS);
      }
      Long maintenanceInitialDelayMS = config.getLong("maintenanceInitialDelayMS");
      if (maintenanceInitialDelayMS != null) {
        settings.maintenanceInitialDelay(maintenanceInitialDelayMS, MILLISECONDS);
      }
      Long maintenanceFrequencyMS = config.getLong("maintenanceFrequencyMS");
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
