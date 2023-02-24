package io.vertx.ext.mongo;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;

public class CompressorsConnectivityTest extends MongoTestBase {

  private MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  @Test
  public void testQueryWithZLibCompressOptions() {
    JsonObject config = getConfig()
      .put("compressors", new JsonArray().add("zlib"))
      .put("zlibCompressionLevel", 6);

    MongoClient mongoClient = MongoClient.create(vertx, config);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = new JsonObject().put("compressor", "zlib");
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(r -> {
          assertEquals("zlib", r.get(0).getString("compressor"));
          testComplete();
        }));
      }));
    }));
    await();
  }
}
