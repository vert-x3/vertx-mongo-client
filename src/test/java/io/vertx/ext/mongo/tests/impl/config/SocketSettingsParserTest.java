package io.vertx.ext.mongo.tests.impl.config;

import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.config.SocketSettingsParser;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SocketSettingsParserTest {
  @Test
  public void testSocketSettings() {
    int connectTimeoutMS = Math.abs(TestUtils.randomInt());
    int socketTimeoutMS = Math.abs(TestUtils.randomInt());
    int receiveBufferSize = Math.abs(TestUtils.randomInt());
    int sendBufferSize = Math.abs(TestUtils.randomInt());

    JsonObject config = new JsonObject();
    config.put("connectTimeoutMS", connectTimeoutMS);
    config.put("socketTimeoutMS", socketTimeoutMS);
    config.put("receiveBufferSize", receiveBufferSize);
    config.put("sendBufferSize", sendBufferSize);

    SocketSettings settings = new SocketSettingsParser(null, config).settings();
    assertEquals(connectTimeoutMS, settings.getConnectTimeout(TimeUnit.MILLISECONDS));
    assertEquals(socketTimeoutMS, settings.getReadTimeout(TimeUnit.MILLISECONDS));
    assertEquals(receiveBufferSize, settings.getReceiveBufferSize());
    assertEquals(sendBufferSize, settings.getSendBufferSize());
  }
}
