package io.vertx.ext.mongo;

import static org.junit.Assert.*;

import com.mongodb.client.model.TimeSeriesGranularity;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.Test;

public class TimeSeriesOptionsTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  private static void assertNotEqual(BiConsumer<TimeSeriesOptions, TimeSeriesOptions> f) {
    TimeSeriesOptions a = new TimeSeriesOptions("time");
    TimeSeriesOptions b = new TimeSeriesOptions("time");
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<TimeSeriesOptions> f) {
    TimeSeriesOptions o = new TimeSeriesOptions("time");
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new TimeSeriesOptions("time"), new TimeSeriesOptions("time"));

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
    TimeSeriesOptions a = new TimeSeriesOptions("timeA");
    int hash = a.hashCode();

    assertEquals(hash, new TimeSeriesOptions("timeA").hashCode());

    assertNotEqual(hash, o -> o.setMetaField("metaA"));
    assertNotEqual(hash, o -> o.setGranularity("MINUTES"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeField() {
    String nullTimeField = null;
    new TimeSeriesOptions(nullTimeField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetInvalidTimeField() {
    TimeSeriesOptions options = new TimeSeriesOptions("time");
    String nullTimeField = null;
    options.setTimeField(nullTimeField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetInvalidGranularity() {
    TimeSeriesOptions options = new TimeSeriesOptions("time");
    options.setGranularity("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeFieldFromJson() {
    new TimeSeriesOptions(new JsonObject());
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

    assertEquals(TimeSeriesGranularity.HOURS, options3.getGranularity());
    assertEquals(TimeSeriesGranularity.HOURS, options4.getGranularity());

    assertEquals(
        TimeSeriesGranularity.SECONDS, options4.setGranularity("seconds").getGranularity());
  }

  @Test
  public void testCreateTimeSeriesCollection() {
    final String collectionName = "_testCreateTimeSeriesCollection" + UUID.randomUUID();
    TimeSeriesOptions timeseries =
        new TimeSeriesOptions(
            JsonObject.of(
                TimeSeriesOptions.TIME_FIELD_KEY,
                "timestamp",
                TimeSeriesOptions.META_FIELD_KEY,
                "metadata",
                TimeSeriesOptions.GRANULARITY_KEY,
                "hours"));
    CreateCollectionOptions options = new CreateCollectionOptions();
    options.setTimeseries(timeseries);
    mongoClient
        .createCollectionWithOptions(collectionName, options)
        .onSuccess(
            _void -> {
              mongoClient
                  .runCommand("listCollections", JsonObject.of("listCollections", "1.0"))
                  .onSuccess(
                      json -> {
                        boolean isTimeSeriesCollection = false;
                        for (Object obj : json.getJsonObject("cursor").getJsonArray("firstBatch")) {
                          JsonObject coll = (JsonObject) obj;
                          if (Objects.equals(collectionName, coll.getString("name"))) {
                            if (Objects.equals("timeseries", coll.getString("type"))) {
                              isTimeSeriesCollection = true;
                            }
                          }
                        }
                        assert isTimeSeriesCollection;
                      });
            });
    await();
  }
}
