package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class AggregateOptionsTest {
  @Test
  public void testOptions() {
    final AggregateOptions options = new AggregateOptions();

    final long maxAwaitTime = TestUtils.randomLong();
    assertEquals(options, options.setMaxAwaitTime(maxAwaitTime));
    assertEquals(maxAwaitTime, options.getMaxAwaitTime());

    final long maxTime = TestUtils.randomLong();
    assertEquals(options, options.setMaxTime(maxTime));
    assertEquals(maxTime, options.getMaxTime());

    final boolean useDisk = TestUtils.randomBoolean();
    assertEquals(options, options.setAllowDiskUse(useDisk));
    assertEquals(useDisk, options.getAllowDiskUse());
  }

  @Test
  public void testDefaultOptions() {
    final AggregateOptions options = new AggregateOptions();
    assertEquals(AggregateOptions.DEFAULT_MAX_AWAIT_TIME, options.getMaxAwaitTime());
    assertEquals(AggregateOptions.DEFAULT_MAX_TIME, options.getMaxTime());
    assertNull(options.getAllowDiskUse());
  }

  @Test
  public void testOptionsJson() {
    final JsonObject json = new JsonObject();

    long maxAwaitTime = TestUtils.randomLong();
    json.put("maxAwaitTime", maxAwaitTime);

    long maxTime = TestUtils.randomLong();
    json.put("maxTime", maxTime);

    boolean useDisk = TestUtils.randomBoolean();
    json.put("allowDiskUse", useDisk);

    final AggregateOptions options = new AggregateOptions(json);
    assertEquals(maxAwaitTime, options.getMaxAwaitTime());
    assertEquals(maxTime, options.getMaxTime());
    assertEquals(useDisk, options.getAllowDiskUse());
  }

  @Test
  public void testDefaultOptionsJson() {
    final AggregateOptions options = new AggregateOptions(new JsonObject());
    final AggregateOptions def = new AggregateOptions();
    assertEquals(def.getMaxAwaitTime(), options.getMaxAwaitTime());
    assertEquals(def.getMaxTime(), options.getMaxTime());
    assertNull(options.getAllowDiskUse());
  }

  @Test
  public void testCopyOptions() {
    final AggregateOptions options = new AggregateOptions();
    options.setMaxAwaitTime(TestUtils.randomLong());
    options.setMaxTime(TestUtils.randomLong());
    options.setAllowDiskUse(TestUtils.randomBoolean());

    final AggregateOptions copy = new AggregateOptions(options);
    assertEquals(options.getMaxAwaitTime(), copy.getMaxAwaitTime());
    assertEquals(options.getMaxTime(), copy.getMaxTime());
    assertEquals(options.getAllowDiskUse(), copy.getAllowDiskUse());
  }

  @Test
  public void testToJson() {
    final AggregateOptions options = new AggregateOptions();
    options.setMaxAwaitTime(TestUtils.randomPositiveLong());
    options.setMaxTime(TestUtils.randomPositiveLong());
    options.setAllowDiskUse(TestUtils.randomBoolean());

    assertEquals(options, new AggregateOptions(options.toJson()));
  }
}
