package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static io.vertx.ext.mongo.WriteOption.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:tomasz.groch@gmail.com">Tomasz Groch</a>
 */
public class InsertManyOptionsTest {
  @Test
  public void testOptions() {
    InsertManyOptions options = new InsertManyOptions();

    boolean ordered = TestUtils.randomBoolean();
    assertEquals(options, options.setOrdered(ordered));
    assertEquals(ordered, options.isOrdered());
  }

  @Test
  public void testDefaultOptions() {
    InsertManyOptions options = new InsertManyOptions();
    assertTrue(options.isOrdered());
  }

  @Test
  public void testOptionsJson() {
    JsonObject json = new JsonObject();

    boolean ordered = TestUtils.randomBoolean();
    json.put("ordered", ordered);

    InsertManyOptions options = new InsertManyOptions(json);
    assertEquals(ordered, options.isOrdered());
  }

  @Test
  public void testDefaultOptionsJson() {
    InsertManyOptions options = new InsertManyOptions(new JsonObject());
    InsertManyOptions def = new InsertManyOptions();
    assertEquals(def.isOrdered(), options.isOrdered());
  }

  @Test
  public void testCopyOptions() {
    InsertManyOptions options = new InsertManyOptions();
    boolean ordered = TestUtils.randomBoolean();

    options.setOrdered(ordered);

    InsertManyOptions copy = new InsertManyOptions(options);
    assertEquals(options.isOrdered(), copy.isOrdered());
  }

  @Test
  public void testToJson() {
    InsertManyOptions options = new InsertManyOptions();
    boolean ordered = TestUtils.randomBoolean();
    options.setOrdered(ordered);
    assertEquals(options, new InsertManyOptions(options.toJson()));
  }
}
