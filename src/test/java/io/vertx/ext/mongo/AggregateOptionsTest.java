package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class AggregateOptionsTest {
  @Test
  public void testOptions() {
    AggregateOptions options = new AggregateOptions();

    long maxTime = TestUtils.randomLong();
    assertEquals(options, options.setMaxTime(maxTime));
    assertEquals(maxTime, options.getMaxTime());
  }

  @Test
  public void testDefaultOptions() {
    AggregateOptions options = new AggregateOptions();
    assertEquals(AggregateOptions.DEFAULT_MAX_TIME, options.getMaxTime());
  }

  @Test
  public void testOptionsJson() {
    JsonObject json = new JsonObject();

    long maxAwaitTime = TestUtils.randomLong();
    json.put("maxAwaitTime", maxAwaitTime);

    long maxTime = TestUtils.randomLong();
    json.put("maxTime", maxTime);

    AggregateOptions options = new AggregateOptions(json);
    assertEquals(maxTime, options.getMaxTime());
  }

  @Test
  public void testDefaultOptionsJson() {
    AggregateOptions options = new AggregateOptions(new JsonObject());
    AggregateOptions def = new AggregateOptions();
    assertEquals(def.getMaxTime(), options.getMaxTime());
  }

  @Test
  public void testCopyOptions() {
    AggregateOptions options = new AggregateOptions();
    options.setMaxTime(TestUtils.randomLong());

    AggregateOptions copy = new AggregateOptions(options);
    assertEquals(options.getMaxTime(), copy.getMaxTime());
  }

  @Test
  public void testToJson() {
    AggregateOptions options = new AggregateOptions();
    long maxAwaitTime = TestUtils.randomPositiveLong();
    long maxTime = TestUtils.randomPositiveLong();
    options.setMaxTime(maxTime);

    assertEquals(options, new AggregateOptions(options.toJson()));
  }
}
