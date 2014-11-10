package io.vertx.ext.mongo.impl.config;

import com.mongodb.connection.ServerSettings;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ServerSettingsParserTest {

  @Test
  public void testServerSettings() {
    long heartbeatFrequencyMS = 1234;
    long minHeartbeatFrequencyMS = heartbeatFrequencyMS / 2;
    JsonObject config = new JsonObject();
    config.put("heartbeatFrequencyMS", heartbeatFrequencyMS);
    config.put("minHeartbeatFrequencyMS", minHeartbeatFrequencyMS);

    ServerSettings settings = new ServerSettingsParser(config).settings();
    assertEquals(heartbeatFrequencyMS, settings.getHeartbeatFrequency(TimeUnit.MILLISECONDS));
    assertEquals(minHeartbeatFrequencyMS, settings.getMinHeartbeatFrequency(TimeUnit.MILLISECONDS));
  }
}
