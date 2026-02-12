package io.vertx.ext.mongo.tests.impl.tracing;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.tracing.MongoTracerRequest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MongoTracerRequestTest {

  @Test
  public void shouldRetainMetadata() {
    JsonObject document = new JsonObject().put("field", new JsonObject().put("nested", 1));
    JsonObject options = new JsonObject().put("limit", 10);

    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op")
      .command("document", document)
      .options(options)
      .build();

    assertEquals("db", request.database());
    assertEquals("collection", request.collection());
    assertEquals("op", request.operation());

    assertNotNull(request.command());
    assertNotSame(document, request.command());
    assertEquals(1, request.command().getJsonObject("document").getJsonObject("field").getInteger("nested").intValue());

    assertNotNull(request.options());
    assertNotSame(options, request.options());
    assertEquals(10, request.options().getInteger("limit").intValue());

    document.getJsonObject("field").put("nested", 2);
    options.put("limit", 20);

    assertEquals(1, request.command().getJsonObject("document").getJsonObject("field").getInteger("nested").intValue());
    assertEquals(10, request.options().getInteger("limit").intValue());
  }

  @Test
  public void shouldSanitizeIterables() {
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op")
      .command("values", Arrays.asList("a", new JsonObject().put("key", "value")))
      .option("ids", Arrays.asList(1, 2, 3))
      .build();

    JsonArray values = request.command().getJsonArray("values");
    assertEquals(2, values.size());
    assertEquals("a", values.getString(0));
    assertEquals("value", values.getJsonObject(1).getString("key"));

    JsonArray ids = request.options().getJsonArray("ids");
    assertEquals(JsonArray.of(1, 2, 3), ids);
  }

  @Test
  public void shouldReturnNullWhenEmpty() {
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op").build();

    assertNull(request.command());
    assertNull(request.options());
  }

  @Test
  public void shouldExposeTags() {
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "mongo.find").build();

    assertEquals(4, MongoTracerRequest.TAG_EXTRACTOR.len(request));
    assertEquals("db.system", MongoTracerRequest.TAG_EXTRACTOR.name(request, 0));
    assertEquals("mongodb", MongoTracerRequest.TAG_EXTRACTOR.value(request, 0));
    assertEquals("db.name", MongoTracerRequest.TAG_EXTRACTOR.name(request, 1));
    assertEquals("db", MongoTracerRequest.TAG_EXTRACTOR.value(request, 1));
    assertEquals("db.mongodb.collection", MongoTracerRequest.TAG_EXTRACTOR.name(request, 2));
    assertEquals("collection", MongoTracerRequest.TAG_EXTRACTOR.value(request, 2));
    assertEquals("db.operation", MongoTracerRequest.TAG_EXTRACTOR.name(request, 3));
    assertEquals("mongo.find", MongoTracerRequest.TAG_EXTRACTOR.value(request, 3));
  }
}
