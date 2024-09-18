package io.vertx.ext.mongo.tests.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.config.WriteConcernParser;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class WriteConcernParserTest {

  @Test
  public void testNoWriteConcern() {
    WriteConcern wc = new WriteConcernParser(null, new JsonObject()).writeConcern();
    assertNull(wc);
  }

  @Test
  public void testWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "ACKNOWLEDGED");

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.ACKNOWLEDGED, wc);
  }

  @Test
  public void testWriteConcernCaseInsensitive() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "acknowledged");

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.ACKNOWLEDGED, wc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "foo");

    new WriteConcernParser(null, config).writeConcern();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTypeWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", 123);

    new WriteConcernParser(null, config);
  }

  @Test
  public void testAdvancedWriteConcern_w_int() {
    WriteConcern expected = new WriteConcern(3).withWTimeout(25, TimeUnit.MILLISECONDS).withJournal(true);
    JsonObject config = new JsonObject();
    config.put("w", 3);
    config.put("wtimeoutMS", 25);
    config.put("j", true);

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_string() {
    WriteConcern expected = WriteConcern.MAJORITY.withWTimeout(1, TimeUnit.MILLISECONDS).withJournal(true);
    JsonObject config = new JsonObject();
    config.put("w", "majority");
    config.put("wtimeoutMS", 1);
    config.put("j", true);

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_int_only() {
    WriteConcern expected = new WriteConcern(123);
    JsonObject config = new JsonObject();
    config.put("w", 123);

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_string_only() {
    WriteConcern expected = new WriteConcern("foo");
    JsonObject config = new JsonObject();
    config.put("w", "foo");

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testSimpleAndAdvancedWriteConcern() {
    WriteConcern expected = WriteConcern.JOURNALED;
    JsonObject config = new JsonObject();
    config.put("w", "majority");
    config.put("wtimeoutMS", 1);
    config.put("j", true);
    // this overwrites the other options
    config.put("writeConcern", "journaled");

    WriteConcern wc = new WriteConcernParser(null, config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWriteConcern_w_boolean() {
    JsonObject config = new JsonObject();
    config.put("w", true);

    new WriteConcernParser(null, config).writeConcern();
  }

  @Test
  public void testConnStringNoWriteConcern() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp");
    WriteConcern rp = new WriteConcernParser(connString, new JsonObject()).writeConcern();
    assertNull(rp);
  }

  @Test
  public void testConnStringWriteConcern() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp&safe=true");
    WriteConcern wc = new WriteConcernParser(connString, new JsonObject()).writeConcern();

    assertNotNull(wc);
    assertEquals(WriteConcern.ACKNOWLEDGED, wc);
  }

  @Test
  public void testConnStringSimpleAndAdvancedWriteConcern() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp" +
      "&w=majority&wtimeoutms=20&journal=false");
    WriteConcern expected = new WriteConcern("majority").withWTimeout(20, TimeUnit.MILLISECONDS).withJournal(false);
    WriteConcern wc = new WriteConcernParser(connString, new JsonObject()).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }
}
