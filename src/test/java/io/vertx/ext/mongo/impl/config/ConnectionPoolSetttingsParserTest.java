package io.vertx.ext.mongo.impl.config;

import com.mongodb.connection.ConnectionPoolSettings;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static io.vertx.test.core.TestUtils.*;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ConnectionPoolSetttingsParserTest {
  @Test
  public void testConnectionPoolSettings() {
    int maxPoolSize = 42;
    int minPoolSize = maxPoolSize / 2; // min needs to be less then max
    long maxIdleTimeMS = Math.abs(randomLong());
    long maxLifeTimeMS = Math.abs(randomLong());
    int waitQueueMultiple = Math.abs(randomInt());
    long waitQueueTimeoutMS = Math.abs(randomLong());
    long maintenanceInitialDelayMS = Math.abs(randomLong());
    long maintenanceFrequencyMS = Math.abs(randomLong());

    JsonObject config = new JsonObject();
    config.put("maxPoolSize", maxPoolSize);
    config.put("minPoolSize", minPoolSize);
    config.put("maxIdleTimeMS", maxIdleTimeMS);
    config.put("maxLifeTimeMS", maxLifeTimeMS);
    config.put("waitQueueMultiple", waitQueueMultiple);
    config.put("waitQueueTimeoutMS", waitQueueTimeoutMS);
    config.put("maintenanceInitialDelayMS", maintenanceInitialDelayMS);
    config.put("maintenanceFrequencyMS", maintenanceFrequencyMS);

    ConnectionPoolSettings settings = new ConnectionPoolSettingsParser(null, config).settings();
    assertEquals(maxPoolSize, settings.getMaxSize());
    assertEquals(minPoolSize, settings.getMinSize());
    assertEquals(maxIdleTimeMS, settings.getMaxConnectionIdleTime(MILLISECONDS));
    assertEquals(maxLifeTimeMS, settings.getMaxConnectionLifeTime(MILLISECONDS));
    assertEquals(waitQueueMultiple, settings.getMaxWaitQueueSize());
    assertEquals(waitQueueTimeoutMS, settings.getMaxWaitTime(MILLISECONDS));
    assertEquals(maintenanceInitialDelayMS, settings.getMaintenanceInitialDelay(MILLISECONDS));
    assertEquals(maintenanceFrequencyMS, settings.getMaintenanceFrequency(MILLISECONDS));
  }
}
