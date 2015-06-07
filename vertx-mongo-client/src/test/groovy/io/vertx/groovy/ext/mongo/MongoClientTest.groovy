package io.vertx.groovy.ext.mongo

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoTestBase
import io.vertx.groovy.core.Vertx


/**
 * Tests to confirm Groovy specific behavior.
 * 
 * @author sfitts
 *
 */
class MongoClientTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(new Vertx(vertx), config.getMap());
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
  void testNestedObject() {
    String collection = randomCollection();
    
    // Construct object with nested object and array
    def nested = [
        nestedObject: [key: 'value'],
        nestedArray: ['v1', 'v2']
      ]
    
    // Insert object
    waitFor(1)
    mongoClient.insert(collection, nested) { insert ->
      if (!insert.succeeded()) {
        fail('Failed to insert nested object.')
      }
      // Now fetch it back
      String id = insert.result()
      mongoClient.findOne(collection, [_id: id], null) { findRes ->
        if (!findRes.succeeded()) {
          fail('Failed to fetch nested object.')
        }
        
        // Confirm that the nested properties have been converted
        def found = findRes.result()
        assertTrue(found.nestedObject instanceof Map)
        assertTrue(found.nestedArray instanceof List)
        testComplete();
      }
    }
    await()
  }

  protected void dropCollections(CountDownLatch latch) {
    // Drop all the collections in the db
    mongoClient.getCollections() { res ->
      if (!res.succeeded()) {
        fail('Unable to get collections in order to remove them.')
      }
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections(res.result());
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongoClient.dropCollection(collection) { dropRes ->
            if (!res.succeeded()) {
              fail("Unable to drop collection: $collection")
              latch.countDown();
              return
            }
            if (collCount.incrementAndGet() == count) {
              latch.countDown();
            }
          }
        }
      } else {
        latch.countDown();
      }
    }
  }
}
