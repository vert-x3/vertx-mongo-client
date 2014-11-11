package io.vertx.ext.mongo.impl.config;

import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ClusterSettingsParserTest {

  @Test
  public void testSingleHost() {
    assertSingleHost(ClusterConnectionMode.SINGLE, settings(singleHost()));
  }

  @Test
  public void testMultipleHosts() {
    assertMultipleHosts(settings(multipleHosts()));
  }

  @Test
  public void testMultipleAndSingleHost() {
    assertMultipleHosts(settings(singleHost(multipleHosts())));
  }

  @Test
  public void testReplicaSetSingleHost() {
    ClusterSettings settings = settings(singleHost().put("replicaSet", "foo"));
    assertSingleHost(ClusterConnectionMode.MULTIPLE, settings);
    assertEquals("foo", settings.getRequiredReplicaSetName());
  }

  @Test
  public void testReplicaSetMultipleHosts() {
    ClusterSettings settings = settings(multipleHosts().put("replicaSet", "foobar"));
    assertMultipleHosts(settings);
    assertEquals("foobar", settings.getRequiredReplicaSetName());
  }

  private static void assertSingleHost(ClusterConnectionMode mode, ClusterSettings settings) {
    List<ServerAddress> hosts = settings.getHosts();
    assertNotNull(hosts);
    assertEquals(1, hosts.size());
    assertEquals(new ServerAddress("single.host", 1111), hosts.get(0));
    assertEquals(mode, settings.getMode());
  }

  private static void assertMultipleHosts(ClusterSettings settings) {
    List<ServerAddress> hosts = settings.getHosts();
    assertNotNull(hosts);
    assertEquals(2, hosts.size());
    assertEquals(new ServerAddress("multiple.1", 2222), hosts.get(0));
    assertEquals(new ServerAddress("multiple.2", 3333), hosts.get(1));
    assertEquals(ClusterConnectionMode.MULTIPLE, settings.getMode());
  }

  private static ClusterSettings settings(JsonObject config) {
    return new ClusterSettingsParser(null, config).settings();
  }

  private static JsonObject singleHost() {
    return singleHost(new JsonObject().put("host", "single.host").put("port", 1111));
  }

  private static JsonObject singleHost(JsonObject config) {
    return config.put("host", "single.host").put("port", 1111);
  }

  private static JsonObject multipleHosts() {
    JsonObject config = new JsonObject();
    JsonArray array = new JsonArray();
    config.put("hosts", array);

    JsonObject h = new JsonObject();
    h.put("host", "multiple.1");
    h.put("port", 2222);
    array.add(h);
    h = new JsonObject();
    h.put("host", "multiple.2");
    h.put("port", 3333);
    array.add(h);

    return config;
  }
}
