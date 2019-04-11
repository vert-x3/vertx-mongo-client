package io.vertx.ext.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import org.junit.Test;

import static java.util.stream.Collectors.joining;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoClientTest extends MongoClientTestBase {

  private com.mongodb.async.client.MongoClient actualMongo;
  private MongoDatabase db;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);

    actualMongo = MongoClients.create("mongodb://localhost:27018");
    db = actualMongo.getDatabase(io.vertx.ext.mongo.MongoClient.DEFAULT_DB_NAME);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    actualMongo.close();
    super.tearDown();
  }


  @Test
  public void testFindBatch() throws Exception {
    testFindBatch((latch, stream) -> {
      List<String> dummyData = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> dummyData.add(result.getString("foo")));
      return dummyData;
    });
  }

  @Test
  public void testFindBatchResumePause() throws Exception {
    testFindBatch((latch, stream) -> {
      List<String> dummyData = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          dummyData.add(result.getString("foo"));
          if (dummyData.size() % 100 == 0) {
            stream.pause();
            vertx.setTimer(10, id -> {
              stream.resume();
            });
          }
        });
      return dummyData;
    });
  }

  @Test
  public void findBatchResumePause() throws Exception {
    int numberOfDocs = 111;
    testFindBatch((latch, stream) -> {
      List<String> dummyData = new ArrayList<>();
      CountDownLatch handlerCallsAfterPause = new CountDownLatch(numberOfDocs);
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> {
          latch.countDown();
          assertEquals(0, handlerCallsAfterPause.getCount());
        }).handler(result -> {
          dummyData.add(result.getString("foo"));
          stream.pause();
          vertx.setTimer(10, id -> {
            handlerCallsAfterPause.countDown();
            stream.resume();
          });
      });
      return dummyData;
    }, numberOfDocs);
  }

  @Test
  public void testFindBatchFetch() throws Exception {
    testFindBatch((latch, stream) -> {
      List<String> dummyData = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          dummyData.add(result.getString("foo"));
          if (dummyData.size() % 100 == 0) {
            vertx.setTimer(10, id -> {
              stream.fetch(100);
            });
          }
        });
      stream.fetch(100);
      return dummyData;
    });
  }

  private void testFindBatch(BiFunction<CountDownLatch, ReadStream<JsonObject>, List<String>> checker) throws Exception {
    int numberOfDocs = 3000;
    testFindBatch(checker, numberOfDocs);
  }

  private void testFindBatch(BiFunction<CountDownLatch, ReadStream<JsonObject>, List<String>> checker, int numberOfDocs) throws Exception {
    AtomicReference<ReadStream<JsonObject>> streamReference = new AtomicReference<>();
    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<List<String>> dummyData = new AtomicReference<>();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numberOfDocs, onSuccess(res2 -> {
        FindOptions findOptions = new FindOptions().setSort(new JsonObject().put("foo", 1));
        ReadStream<JsonObject> stream = mongoClient.findBatchWithOptions(collection, new JsonObject(), findOptions);
        streamReference.set(stream);
        dummyData.set(checker.apply(latch, stream));
      }));
    }));
    awaitLatch(latch);
    assertEquals(numberOfDocs, dummyData.get().size());

    // check first element received
    assertEquals("bar0", dummyData.get().get(0));

    // check last element received
    String expectedLastDummyValue = "bar" + Stream.generate(() -> "9").limit(String.valueOf(numberOfDocs).length() -1).collect(joining());
    assertEquals(expectedLastDummyValue, dummyData.get().get(numberOfDocs - 1));

    // Make sure stream handlers can be set to null after closing
    streamReference.get().handler(null).exceptionHandler(null).endHandler(null);
  }

  @Test
  public void testUpsertCreatesHexIfRecordDoesNotExist() {
    upsertDoc(randomCollection(), createDoc(), null, IGNORE -> {
      testComplete();
    });

    await();
  }

  @Test
  public void testUpsertWithASetOnInsertIsNotOverWritten() {
    String collection = randomCollection();
    JsonObject docToInsert = createDoc();
    JsonObject insertStatement = new JsonObject()
      .put("$set", docToInsert)
      .put("$setOnInsert", new JsonObject().put("a-field", "an-entry"));

    upsertDoc(collection, docToInsert, insertStatement, null, saved -> {
      assertEquals("an-entry", saved.getString("a-field"));
      testComplete();
    });
    await();
  }

  @Test
  public void testUpsertDoesNotChangeIdIfRecordExist() {
    String collection = randomCollection();
    JsonObject docToInsert = createDoc();
    mongoClient
      .insert(collection, docToInsert, onSuccess(id -> {
        upsertDoc(collection, docToInsert, id, IGNORE -> {
          testComplete();
        });
      }));
    await();
  }

  @Test
  public void testAggregate() throws Exception {
    final int numDocs = 1000;

    final String collection = randomCollection();
    final CountDownLatch latch = new CountDownLatch(1);
    final AtomicLong count = new AtomicLong();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        mongoClient.aggregate(collection,
                              new JsonArray().add(new JsonObject().put("$match", new JsonObject().put("foo", new JsonObject().put("$regex", "bar1"))))
                                             .add(new JsonObject().put("$count", "foo_starting_with_bar1")))
                   .exceptionHandler(this::fail)
                   .endHandler(v -> latch.countDown())
                   .handler(result -> count.set(result.getLong("foo_starting_with_bar1")));
      }));
    }));
    awaitLatch(latch);
    assertEquals(111, count.longValue());
  }

  private void upsertDoc(String collection, JsonObject docToInsert, String expectedId, Consumer<JsonObject> doneFunction) {
    JsonObject insertStatement = new JsonObject()
      .put("$set", docToInsert);

    upsertDoc(collection, docToInsert, insertStatement, expectedId, doneFunction);
  }

  private void upsertDoc(String collection, JsonObject docToInsert, JsonObject insertStatement, String expectedId, Consumer<JsonObject> doneFunction) {
    mongoClient.updateCollectionWithOptions(collection,
      new JsonObject()
        .put("foo", docToInsert.getString("foo")),
      insertStatement,
      new UpdateOptions()
        .setUpsert(true),
      onSuccess(res -> {
        assertEquals(0, res.getDocModified());

        if (expectedId == null) {
          assertEquals(0, res.getDocMatched());
          assertNotNull(res.getDocUpsertedId());
        } else {
          assertEquals(1, res.getDocMatched());
          assertNull(res.getDocUpsertedId());
        }

        //need to check actual DB, not through the Vertx client, in order to make sure the id is a string
        db
          .getCollection(collection)
          .find()

          .first((savedDoc, error) -> {
            vertx.runOnContext(IGNORE -> {
              if (expectedId != null) {
                assertEquals(expectedId, savedDoc.getString(MongoClientUpdateResult.ID_FIELD));
              } else {
                assertEquals(res.getDocUpsertedId().getString(MongoClientUpdateResult.ID_FIELD), savedDoc.getString(MongoClientUpdateResult.ID_FIELD));
              }
              doneFunction.accept(new JsonObject(savedDoc.toJson()));
            });
          });
      }));
  }


}
