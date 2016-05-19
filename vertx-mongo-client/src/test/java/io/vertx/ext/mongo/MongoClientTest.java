package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
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
    CountDownLatch latch = new CountDownLatch(3);
    IndexOptions testOptions = new IndexOptions().unique(true).name("testName");
    mongoClient.createIndexWithOptions(collection, index, testOptions,  stringAsyncResult -> {
      assertNotNull(stringAsyncResult.result());
      assertEquals("testName", stringAsyncResult.result());
      latch.countDown();
      mongoClient.listIndexes(collection, result -> {
          assertTrue(result.succeeded());
          List<JsonObject> indexes = result.result();
          assertTrue(
                  indexes
                    .stream()
                    .filter( p -> p.getString("name").equals("testName"))
                    .count() == 1
          );
          latch.countDown();
          mongoClient.dropIndex(collection, "testName",  result2 -> {
              assertTrue(result2.succeeded());
              latch.countDown();
          });
        });
    });
    latch.await();
  }
}
