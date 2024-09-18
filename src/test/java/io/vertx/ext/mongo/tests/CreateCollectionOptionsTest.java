package io.vertx.ext.mongo.tests;

import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.TimeSeriesGranularity;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.CollationOptions;
import io.vertx.ext.mongo.CreateCollectionOptions;
import io.vertx.ext.mongo.TimeSeriesOptions;
import io.vertx.ext.mongo.ValidationOptions;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.vertx.ext.mongo.TimeSeriesGranularity.*;
import static org.junit.Assert.*;

public class CreateCollectionOptionsTest {


  private static void assertNotEqual(BiConsumer<CreateCollectionOptions, CreateCollectionOptions> f) {
    CreateCollectionOptions a = new CreateCollectionOptions();
    CreateCollectionOptions b = new CreateCollectionOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<CreateCollectionOptions> f) {
    CreateCollectionOptions o = new CreateCollectionOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new CreateCollectionOptions(), new CreateCollectionOptions());

    assertNotEqual((a, b) -> {
      a.setCapped(true);
      b.setCapped(false);
    });
    assertNotEqual((a, b) -> {
      a.setCollation(new CollationOptions().setLocale("de_AT"));
      b.setCollation(new CollationOptions().setLocale("en_US"));
    });
    assertNotEqual((a, b) -> {
      a.setIndexOptionDefaults(new JsonObject().put("some", "option"));
      b.setIndexOptionDefaults(new JsonObject());
    });
    assertNotEqual((a, b) -> {
      a.setValidationOptions(new ValidationOptions().setValidationAction(ValidationAction.WARN));
      b.setValidationOptions(new ValidationOptions().setValidationAction(ValidationAction.ERROR));
    });
    assertNotEqual((a, b) -> {
      a.setMaxDocuments(12345L);
      b.setMaxDocuments(10L);
    });
    assertNotEqual((a, b) -> {
      a.setSizeInBytes(1024L);
      b.setSizeInBytes(2048L);
    });
    assertNotEqual((a, b) -> {
      a.setStorageEngineOptions(new JsonObject().put("some", "option"));
      b.setStorageEngineOptions(new JsonObject());
    });

    assertNotEquals(new CreateCollectionOptions(), null);
  }

  @Test
  public void testHashCode() {
    CreateCollectionOptions a = new CreateCollectionOptions();
    int hash = a.hashCode();

    assertEquals(hash, new CreateCollectionOptions().hashCode());

    assertNotEqual(hash, o -> o.setMaxDocuments(12345L));
    assertNotEqual(hash, o -> o.setSizeInBytes(4096L));
    assertNotEqual(hash, o -> o.setCapped(true));
    assertNotEqual(hash, o -> o.setValidationOptions(new ValidationOptions().setValidationLevel(ValidationLevel.MODERATE)));
    assertNotEqual(hash, o -> o.setIndexOptionDefaults(new JsonObject().put("some", "option")));
    assertNotEqual(hash, o -> o.setCollation(new CollationOptions().setLocale("de_AT").setStrength(CollationStrength.IDENTICAL)));
    assertNotEqual(hash, o -> o.setStorageEngineOptions(new JsonObject().put("some", "option")));
  }

  @Test
  public void testCreateCollectionOptionsFromJson() {
    JsonObject json = new JsonObject()
      .put("maxDocuments", 10L)
      .put("sizeInBytes", 20L)
      .put("capped", true)
      .put("validationOptions", new JsonObject().put("validationLevel", "STRICT").put("validationAction", "ERROR"))
      .put("indexOptionDefaults", new JsonObject().put("some", "value"))
      .put("storageEngineOptions", new JsonObject().put("some", "otherValue"))
      .put("collation", new JsonObject().put("locale", "simple"));

    CreateCollectionOptions options = new CreateCollectionOptions(json);
    assertEquals(new ValidationOptions().setValidationLevel(ValidationLevel.STRICT).setValidationAction(ValidationAction.ERROR), options.getValidationOptions());
    assertEquals(new JsonObject().put("some", "value"), options.getIndexOptionDefaults());
    assertEquals(new JsonObject().put("some", "otherValue"), options.getStorageEngineOptions());
    assertEquals(true, options.getCapped());
    assertEquals((Long) 10L, options.getMaxDocuments());
    assertEquals((Long) 20L, options.getSizeInBytes());
    assertEquals(new CollationOptions(), options.getCollation());
  }

  @Test
  public void testCreateCollectionOptionsToJson() {
    JsonObject json = new JsonObject()
      .put("maxDocuments", 10L)
      .put("sizeInBytes", 20L)
      .put("capped", true)
      .put("validationOptions", new JsonObject().put("validationLevel", "STRICT").put("validationAction", "ERROR").put("validator", new JsonObject()))
      .put("indexOptionDefaults", new JsonObject().put("some", "value"))
      .put("storageEngineOptions", new JsonObject().put("some", "otherValue"))
      .put("collation", new JsonObject().put("locale", "simple"));


    CreateCollectionOptions options = new CreateCollectionOptions()
      .setCollation(new CollationOptions())
      .setMaxDocuments(10L)
      .setSizeInBytes(20L)
      .setValidationOptions(new ValidationOptions().setValidationLevel(ValidationLevel.STRICT).setValidationAction(ValidationAction.ERROR))
      .setStorageEngineOptions(new JsonObject().put("some", "otherValue"))
      .setIndexOptionDefaults(new JsonObject().put("some", "value"))
      .setCapped(true);

    assertEquals(json, options.toJson());
  }

  @Test
  public void testSetTimeSeries() {
    TimeSeriesOptions timeseries = new TimeSeriesOptions().setTimeField("time");
    CreateCollectionOptions options = new CreateCollectionOptions();
    options.setTimeSeriesOptions(timeseries);
    assertEquals("time", options.toMongoDriverObject().getTimeSeriesOptions().getTimeField());

    timeseries.setMetaField("meta");
    options.setTimeSeriesOptions(timeseries);
    assertEquals("meta", options.toMongoDriverObject().getTimeSeriesOptions().getMetaField());

    timeseries.setGranularity(MINUTES);
    options.setTimeSeriesOptions(timeseries);
    assertEquals(TimeSeriesGranularity.MINUTES, options.toMongoDriverObject().getTimeSeriesOptions().getGranularity());

    timeseries.setGranularity(SECONDS);
    options.setTimeSeriesOptions(timeseries);
    assertEquals(TimeSeriesGranularity.SECONDS, options.toMongoDriverObject().getTimeSeriesOptions().getGranularity());
  }
}
