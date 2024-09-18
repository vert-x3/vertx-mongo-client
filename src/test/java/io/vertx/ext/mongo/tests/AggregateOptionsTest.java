package io.vertx.ext.mongo.tests;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.AggregateOptions;
import io.vertx.ext.mongo.CollationOptions;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class AggregateOptionsTest {
  @Test
  public void testOptions() {
    AggregateOptions options = new AggregateOptions();

    long maxTime = TestUtils.randomLong();
    int batchSize = TestUtils.randomInt();
    boolean allowDiskUser = TestUtils.randomBoolean();
    CollationOptions collation = new CollationOptions();

    assertEquals(options, options.setMaxTime(maxTime));
    assertEquals(maxTime, options.getMaxTime());
    assertEquals(options, options.setCollation(collation));
    assertEquals(collation, options.getCollation());
    assertEquals(options, options.setAllowDiskUse(allowDiskUser));
    assertEquals(allowDiskUser, options.getAllowDiskUse());
    assertEquals(options, options.setBatchSize(batchSize));
    assertEquals(batchSize, options.getBatchSize());
  }

  @Test
  public void testDefaultOptions() {
    AggregateOptions options = new AggregateOptions();
    assertEquals(AggregateOptions.DEFAULT_MAX_TIME, options.getMaxTime());
    assertEquals(AggregateOptions.DEFAULT_BATCH_SIZE, options.getBatchSize());
    assertNull(options.getCollation());
  }

  @Test
  public void testOptionsJson() {
    JsonObject json = new JsonObject();

    long maxAwaitTime = TestUtils.randomLong();
    json.put("maxAwaitTime", maxAwaitTime);

    long maxTime = TestUtils.randomLong();
    json.put("maxTime", maxTime);

    int batchSize = TestUtils.randomInt();
    json.put("batchSize", batchSize);

    boolean allowDiskUse = TestUtils.randomBoolean();
    json.put("allowDiskUse", allowDiskUse);

    CollationOptions collation = new CollationOptions();
    json.put("collation", collation.toJson());

    AggregateOptions options = new AggregateOptions(json);
    assertEquals(maxTime, options.getMaxTime());
    assertEquals(batchSize, options.getBatchSize());
    assertEquals(allowDiskUse, options.getAllowDiskUse());
    assertEquals(collation, options.getCollation());
  }

  @Test
  public void testDefaultOptionsJson() {
    AggregateOptions options = new AggregateOptions(new JsonObject());
    AggregateOptions def = new AggregateOptions();
    assertEquals(def.getMaxTime(), options.getMaxTime());
    assertEquals(def.getAllowDiskUse(), options.getAllowDiskUse());
    assertEquals(def.getBatchSize(), options.getBatchSize());
    assertNull(options.getCollation());
  }

  @Test
  public void testCopyOptions() {
    CollationOptions collationOptions = new CollationOptions();
    AggregateOptions options = new AggregateOptions().setCollation(collationOptions);
    options.setMaxTime(TestUtils.randomLong());

    AggregateOptions copy = new AggregateOptions(options);
    assertEquals(options.getMaxTime(), copy.getMaxTime());
    assertEquals(options.getAllowDiskUse(), copy.getAllowDiskUse());
    assertEquals(options.getBatchSize(), copy.getBatchSize());
    assertEquals(options.getCollation(), copy.getCollation());
  }

  @Test
  public void testToJson() {
    CollationOptions collationOptions = new CollationOptions();
    AggregateOptions options = new AggregateOptions().setCollation(collationOptions);
    long maxTime = TestUtils.randomPositiveLong();
    options.setMaxTime(maxTime);

    int batchSize = TestUtils.randomInt();
    options.setBatchSize(batchSize);

    boolean allowDiskUse = TestUtils.randomBoolean();
    options.setAllowDiskUse(allowDiskUse);

    CollationOptions collation = new CollationOptions();
    options.setCollation(collation);

    assertEquals(options, new AggregateOptions(options.toJson()));
  }
}
