package io.vertx.ext.mongo.impl.config;

import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class WriteConcernParserTest {

  @Test
  public void testNoWriteConcern() {
    WriteConcern wc = new WriteConcernParser(new JsonObject()).writeConcern();
    assertNull(wc);
  }

  @Test
  public void testWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "SAFE");

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.SAFE, wc);
  }

  @Test
  public void testWriteConcernCaseInsensitive() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "safe");

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
    assertNotNull(wc);
    assertEquals(WriteConcern.SAFE, wc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", "foo");

    new WriteConcernParser(config).writeConcern();
  }

  @Test(expected = ClassCastException.class)
  public void testInvalidTypeWriteConcern() {
    JsonObject config = new JsonObject();
    config.put("writeConcern", 123);

    new WriteConcernParser(config).writeConcern();
  }

  @Test
  public void testAdvancedWriteConcern_w_int() {
    WriteConcern expected = new WriteConcern(3, 25, true, true);
    JsonObject config = new JsonObject();
    config.put("w", 3);
    config.put("wtimeoutMS", 25);
    config.put("fsync", true);
    config.put("j", true);

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
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

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_int_only() {
    WriteConcern expected = new WriteConcern(123);
    JsonObject config = new JsonObject();
    config.put("w", 123);

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test
  public void testAdvancedWriteConcern_w_string_only() {
    WriteConcern expected = new WriteConcern("foo");
    JsonObject config = new JsonObject();
    config.put("w", "foo");

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
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

    WriteConcern wc = new WriteConcernParser(config).writeConcern();
    assertNotNull(wc);
    assertEquals(expected, wc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWriteConcern_w_boolean() {
    JsonObject config = new JsonObject();
    config.put("w", true);

    new WriteConcernParser(config).writeConcern();
  }
}
