package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;

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
  public void testNonStringID() {
    String collection = randomCollection();
    JsonObject document = new JsonObject().put("title", "The Hobbit");
    // here it happened
    document.put("_id", 123456);
    document.put("foo", "bar");

    mongoClient.insert(collection, document, onSuccess(id -> {
      mongoClient.findOne(collection, new JsonObject(), null, onSuccess(retrieved -> {
        assertEquals(document, retrieved);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingLongID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      Long genID = TestUtils.randomLong();
      doc.put("_id", genID);
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }

  @Test
  public void testSavePreexistingLongID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      Long genID = TestUtils.randomLong();
      doc.put("_id", genID);
      mongoClient.saveWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }


  @Test
  public void testFindBatch() throws Exception {
    int numDocs = 3000;

    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(1);
    List<String> foos = new ArrayList<>();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        mongoClient.findBatchWithOptions(collection, new JsonObject(), new FindOptions().setSort(new JsonObject().put("foo", 1)))
          .exceptionHandler(this::fail)
          .endHandler(v -> latch.countDown())
          .handler(result -> foos.add(result.getString("foo")));
      }));
    }));
    awaitLatch(latch);
    assertEquals(numDocs, foos.size());
    assertEquals("bar0", foos.get(0));
    assertEquals("bar999", foos.get(numDocs - 1));
  }

}
