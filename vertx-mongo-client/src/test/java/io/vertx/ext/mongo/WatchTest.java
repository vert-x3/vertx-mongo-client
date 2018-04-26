package io.vertx.ext.mongo;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class WatchTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig().put("readPreference", "primaryPreferred");
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    // Replica sets take a while to connect to
    longAwaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  @Test
  public void testWatchForInsertsHasCorrectCount() {
    AtomicInteger counter = new AtomicInteger(10);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v -> {
      mongoClient.watch(collection, new JsonArray(), new WatchOptions(), onSuccess(cursor -> {
        cursor.handler(change -> {
          assertEquals(change.getOperationType(), MongoClientChangeOperationType.INSERT);
          if (counter.decrementAndGet() == 0) {
            cursor.close(Future.future());
            testComplete();
          }
        });
        insertDocs(mongoClient, collection, 10, Future.future());
      }));
    }));
    await();
  }

  @Test
  public void testWatchForChangesHasCorrectCount() {
    AtomicInteger counter = new AtomicInteger(10);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v1 -> {
      insertDocs(mongoClient, collection, 10, v2 -> {
        mongoClient.watch(collection, new JsonArray(), new WatchOptions(), onSuccess(cursor -> {
          cursor.handler(change -> {
            assertEquals(change.getOperationType(), MongoClientChangeOperationType.UPDATE);
            assertEquals(change.getUpdatedFields().getString("other.quux"), "newValue");
            assertEquals(change.getUpdatedFields().getString("previouslyNonexistentField"), "newValue");
            assertEquals(change.getRemovedFields(), Collections.singletonList("date"));
            if (counter.decrementAndGet() == 0) {
              cursor.close(Future.future());
              testComplete();
            }
          });
        }));

        mongoClient.updateCollectionWithOptions(collection, new JsonObject(), new JsonObject()
          .put("$unset", new JsonObject()
            .put("date", 1))
          .put("$set", new JsonObject()
            .put("other.quux", "newValue")
            .put("previouslyNonexistentField", "newValue")), new UpdateOptions().setMulti(true), Future.future());
      });
    }));
    await();
  }

  @Test
  public void testChangeStreamStopsCorrectly() {
    AtomicInteger counter = new AtomicInteger(10);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v -> {
      mongoClient.watch(collection, new JsonArray(), new WatchOptions(), onSuccess(cursor -> {
        cursor.handler(change -> {
          assertEquals(change.getOperationType(), MongoClientChangeOperationType.INSERT);
          if (counter.decrementAndGet() == 5) {
            cursor.close(onSuccess(thenInsertMore -> {
              insertDocs(mongoClient, collection, 5, onSuccess(thenCheckCounter -> {
                // Wait until some time has passed, to give time for the stopped cursor to have possibly received more
                // more inserts
                vertx.setTimer(5001L, t -> {
                  assertEquals(counter.get(), 5);
                  testComplete();
                });
              }));
            }));
          }
        });
        insertDocs(mongoClient, collection, 5, Future.future());
      }));
    }));
    await();
  }

  @Test
  public void testPipelineInsertMatchingDoesNotRequireUpdateLookup() {
    AtomicInteger counter = new AtomicInteger(5);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v -> {
      mongoClient.watch(collection, new JsonArray(Collections.singletonList(
        new JsonObject().put("$match", new JsonObject()
          .put("fullDocument.other.i", new JsonObject().put("$gte", 5)))
      )), new WatchOptions(), onSuccess(cursor -> {
        cursor.handler(change -> {
          assertEquals(change.getOperationType(), MongoClientChangeOperationType.INSERT);
          assertFalse(change.getUpdatedFields().containsKey("num"));
          if (counter.decrementAndGet() == 0) {
            cursor.close(Future.future());
            testComplete();
          }
        });
        insertDocs(mongoClient, collection, 10, Future.future());
      }));
    }));
    await();
  }

  @Test
  public void testPipelineInsertDoesNotHaveUpdatedFields() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v1 -> {
      mongoClient.watch(collection, new JsonArray(Collections.singletonList(
        new JsonObject().put("$match", new JsonObject()
          .put("updateDescription.updatedFields.other.i", new JsonObject().put("$gte", 5)))
      )), new WatchOptions(), onSuccess(cursor -> {
        cursor.handler(change -> {
          // No changes are expected because inserts don't have updated fields
          fail();
        });
        insertDocs(mongoClient, collection, 10, Future.future());
        vertx.setTimer(5001L, v2 -> {
          testComplete();
        });
      }));
    }));
    await();
  }

  @Test
  @Ignore(value = "awaiting fix for https://jira.mongodb.org/browse/JAVA-2828")
  public void testPipelineReplacingRootDoesNotThrow() {
    AtomicInteger counter = new AtomicInteger(10);
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v -> {
      mongoClient.watch(collection, new JsonArray(Collections.singletonList(
        new JsonObject().put("$replaceRoot", new JsonObject()
          .put("newRoot", "$fullDocument"))
      )), new WatchOptions(), onSuccess(cursor -> {
        cursor.handler(document -> {
          // TODO Should actually be a change in the root document
          assertTrue(document.getFullDocument().containsKey("foo"));
          if (counter.decrementAndGet() == 0) {
            cursor.close(Future.future());
            testComplete();
          }
        });
        insertDocs(mongoClient, collection, 10, Future.future());
      }));
    }));
    await();
  }

  @Test
  public void testPipelineMatchesExactSubDocumentUpdate() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(v1 -> {
      mongoClient.watch(collection, new JsonArray().add(
        new JsonObject().put("$match", new JsonObject()
          .put("operationType", "update")
          .put("updateDescription.updatedFields", new JsonObject().put("subDocs.0.subVal", 3)))
      ), new WatchOptions().fullDocument(true), onSuccess(cursor -> {
        cursor.handler(change -> {
          assertEquals(3L, (long) change.getFullDocument().getJsonArray("subDocs").getJsonObject(0).getInteger("subVal"));
          testComplete();
        });

        mongoClient.insert(collection, new JsonObject()
          .put("subDocs", new JsonArray()
            .add(new JsonObject().put("subVal", 1))
            .add(new JsonObject().put("subVal", 2))), onSuccess(v2 -> {
          mongoClient.updateCollection(collection, new JsonObject()
              .put("subDocs.subVal", 1),
            new JsonObject()
              .put("$set", new JsonObject().put("subDocs.$.subVal", 3)), onSuccess(v3 -> {
              assertEquals(v3.getDocModified(), 1L);
            }));
        }));
      }));
    }));
    await();
  }
}
