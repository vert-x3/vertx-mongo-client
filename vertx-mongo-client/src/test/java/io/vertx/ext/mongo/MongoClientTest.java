package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

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
    dropCollections(latch);
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
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongoClient.findBatchWithOptions(collection, new JsonObject(), new FindOptions(), onSuccess(result -> {
          assertNotNull(result);
          latch.countDown();
        }));
      }));
    }));
    awaitLatch(latch);
  }
}
