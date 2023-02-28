package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.vertx.ext.mongo.TimeSeriesGranularity.*;
import static io.vertx.ext.mongo.TimeSeriesOptions.*;
import static org.junit.Assert.*;

public class TimeSeriesOptionsTest {

  private static void assertNotEqual(BiConsumer<TimeSeriesOptions, TimeSeriesOptions> f) {
    TimeSeriesOptions a = new TimeSeriesOptions().setTimeField("time");
    TimeSeriesOptions b = new TimeSeriesOptions().setTimeField("time");
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<TimeSeriesOptions> f) {
    TimeSeriesOptions o = new TimeSeriesOptions().setTimeField("time");
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new TimeSeriesOptions().setTimeField("time"), new TimeSeriesOptions().setTimeField("time"));

    assertNotEqual(
        (a, b) -> {
          a.setMetaField("metaA");
          b.setMetaField("metaB");
        });

    assertNotEqual(
        (a, b) -> {
          a.setMetaField("SECONDS");
          b.setMetaField("HOURS");
        });

    assertNotEquals(new CollationOptions(), null);
  }

  @Test
  public void testHashCode() {
    TimeSeriesOptions a = new TimeSeriesOptions().setTimeField("timeA");
    int hash = a.hashCode();

    assertEquals(hash, new TimeSeriesOptions().setTimeField("timeA").hashCode());

    assertNotEqual(hash, o -> o.setMetaField("metaA"));
    assertNotEqual(hash, o -> o.setGranularity(MINUTES));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeField() {
    new TimeSeriesOptions().setTimeField(null).toMongoDriverObject();
  }

  @Test
  public void testNullTimeFieldFromJson() {
    assertEquals(DEFAULT_TIME_FIELD, new TimeSeriesOptions(new JsonObject()).getTimeField());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGranularityFromJson() {
    new TimeSeriesOptions(JsonObject.of("timeField", "time", "granularity", "invalid"));
  }

  @Test
  public void testTimeSeriesOptionsFromJson() {
    TimeSeriesOptions options1 = new TimeSeriesOptions(JsonObject.of("timeField", "time"));
    TimeSeriesOptions options2 = new TimeSeriesOptions(JsonObject.of("timeField", "time"));
    assertEquals(options1, options2);

    TimeSeriesOptions options3 =
      new TimeSeriesOptions(
        JsonObject.of("timeField", "time", "metaField", "meta", "granularity", "HOURS"));
    TimeSeriesOptions options4 =
      new TimeSeriesOptions(
        JsonObject.of("timeField", "time", "metaField", "meta", "granularity", "HOURS"));
    assertEquals(options3, options4);

    assertEquals(HOURS, options3.getGranularity());
    assertEquals(HOURS, options4.getGranularity());

    assertEquals(SECONDS, options4.setGranularity(SECONDS).getGranularity());
  }
}
