package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoClientTest extends MongoClientTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
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
  public void testFindBatch() throws Exception {
    int numDocs = 200;

    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(numDocs);
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        mongoClient.findBatchWithOptions(collection, new JsonObject(), new FindOptions(), onSuccess(result -> {
          assertNotNull(result);
          latch.countDown();
        }));
      }));
    }));
    awaitLatch(latch);
  }

  @Test
  public void testIndexes() throws Exception {
    String collection = randomCollection();
    JsonObject index = io.vertx.ext.mongo.Indexes.ascending(Collections.singletonList("test"));
    CountDownLatch latch = new CountDownLatch(1);
    mongoClient.createIndex(collection, index, stringAsyncResult -> {
      assertNotNull(stringAsyncResult.result());
      assertEquals("test_1", stringAsyncResult.result());
      latch.countDown();
    });
    latch.await();
  }
}
