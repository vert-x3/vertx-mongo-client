package io.vertx.ext.mongo.test;

import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoClientConfig;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoClientConfigTest {

  @Test
  public void testHost() {
    JsonObject config = new JsonObject();
    config.put("host", "example.org");
    config.put("port", 1234);

    List<ServerAddress> hosts = new MongoClientConfig(config).hosts();
    assertNotNull(hosts);
    assertEquals(1, hosts.size());
    assertEquals(new ServerAddress("example.org", 1234), hosts.get(0));
  }

  @Test
  public void testHosts() {
    JsonObject config = new JsonObject();
    JsonArray array = new JsonArray();
    config.put("hosts", array);

    JsonObject h = new JsonObject();
    h.put("host", "example.org");
    h.put("port", 1234);
    array.add(h);
    h = new JsonObject();
    h.put("host", "foo.org");
    h.put("port", 3000);
    array.add(h);

    List<ServerAddress> hosts = new MongoClientConfig(config).hosts();
    assertNotNull(hosts);
    assertEquals(2, hosts.size());
    assertEquals(new ServerAddress("example.org", 1234), hosts.get(0));
    assertEquals(new ServerAddress("foo.org", 3000), hosts.get(1));
  }

  @Test
  public void testHostsOverrideHost() {
    JsonObject config = new JsonObject();
    JsonArray array = new JsonArray();
    config.put("hosts", array);

    JsonObject h = new JsonObject();
    h.put("host", "example.org");
    h.put("port", 1234);
    array.add(h);
    h = new JsonObject();
    h.put("host", "foo.org");
    h.put("port", 3000);
    array.add(h);

    config.put("host", "localhost");
    config.put("port", 8080);

    List<ServerAddress> hosts = new MongoClientConfig(config).hosts();
    assertNotNull(hosts);
    assertEquals(2, hosts.size());
    assertEquals(new ServerAddress("example.org", 1234), hosts.get(0));
    assertEquals(new ServerAddress("foo.org", 3000), hosts.get(1));
  }

  @Test
  public void testWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "SAFE");

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.SAFE, wc);
  }

  @Test
  public void testWriteConcernCaseInsensitive() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "safe");

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.SAFE, wc);
  }

  @Test
  public void testInvalidWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "foo");

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNull(wc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTypeWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", 123);

    new MongoClientConfig(config).writeConcern();
  }

  @Test
  public void testAdvancedWriteConcern_w_int() {
    WriteConcern expected = new WriteConcern(3, 25, true, true);
    JsonObject config = new JsonObject();
    config.put("w", 3);
    config.put("wtimeoutMS", 25);
    config.put("fsync", true);
    config.put("j", true);

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_string() {
    WriteConcern expected = new WriteConcern("majority", 1, false, true);
    JsonObject config = new JsonObject();
    config.put("w", "majority");
    config.put("wtimeoutMS", 1);
    config.put("fsync", false);
    config.put("j", true);

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_int_only() {
    WriteConcern expected = new WriteConcern(123);
    JsonObject config = new JsonObject();
    config.put("w", 123);

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_string_only() {
    WriteConcern expected = new WriteConcern("foo");
    JsonObject config = new JsonObject();
    config.put("w", "foo");

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testSimpleAndAdvancedWriteConcern() {
    WriteConcern expected = WriteConcern.JOURNALED;
    JsonObject config = new JsonObject();
    config.put("w", "majority");
    config.put("wtimeoutMS", 1);
    config.put("fsync", false);
    config.put("j", true);
    // this overwrites the other options
    config.put("writeConcern", "journaled");

    WriteConcern wc = new MongoClientConfig(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "primary");

    ReadPreference rp = new MongoClientConfig(config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test
  public void testReadPreferenceCaseInsenitive() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "PRIMARY");

    ReadPreference rp = new MongoClientConfig(config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test
  public void testInvalidReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "foo");

    ReadPreference rp = new MongoClientConfig(config).readPreference();
    assertNull(rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTypeReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", 123);

    new MongoClientConfig(config).readPreference();
  }
}
