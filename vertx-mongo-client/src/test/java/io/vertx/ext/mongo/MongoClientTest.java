package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
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
    int numDocs = 3000;

    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(1);
    List<String> foos = new ArrayList<>();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
          mongoClient.findBatchWithOptions(collection, new JsonObject(), new FindOptions().setSort(new JsonObject().put("foo", 1)), onSuccess(result -> {
            if (result == null) {
              latch.countDown();
            } else {
              foos.add(result.getString("foo"));
            }
          }));
      }));
    }));
    awaitLatch(latch);
    assertEquals(numDocs, foos.size());
    assertEquals("bar0", foos.get(0));
    assertEquals("bar999", foos.get(numDocs - 1));
  }

}
