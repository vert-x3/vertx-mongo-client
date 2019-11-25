package io.vertx.ext.mongo;

import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoClientTest extends MongoClientTestBase {

  private com.mongodb.reactivestreams.client.MongoClient actualMongo;
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
    testFindBatch(3000, (latch, stream) -> {
      List<String> foos = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> foos.add(result.getString("foo")));
      return foos;
    });
  }

  @Test
  public void testFindBatchResumePause() throws Exception {
    testFindBatch(3000, (latch, stream) -> {
      List<String> foos = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          foos.add(result.getString("foo"));
          if (foos.size() % 100 == 0) {
            stream.pause();
            vertx.setTimer(10, id -> {
              stream.resume();
            });
          }
        });
      return foos;
    });
  }

  @Test
  public void testFindBatchFetch() throws Exception {
    testFindBatch(3000, (latch, stream) -> {
      List<String> foos = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          foos.add(result.getString("foo"));
          if (foos.size() % 100 == 0) {
            vertx.setTimer(10, id -> {
              stream.fetch(100);
            });
          }
        });
      stream.pause();
      stream.fetch(100);
      return foos;
    });
  }

  @Test
  public void testFindSmallBatchResumePauseOneByOne() throws Exception {
    testFindBatch(10, (latch, stream) -> {
      List<String> foos = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          foos.add(result.getString("foo"));
           stream.pause();
           vertx.setTimer(10, id -> {
             stream.resume();
           });
        });
      return foos;
    });
  }

  @Test
  public void testFindSmallBatchFetchOneByOne() throws Exception {
    testFindBatch(10, (latch, stream) -> {
      List<String> foos = new ArrayList<>();
      stream
        .exceptionHandler(this::fail)
        .endHandler(v -> latch.countDown())
        .handler(result -> {
          foos.add(result.getString("foo"));
          vertx.setTimer(10, id -> {
            stream.fetch(1);
          });
        });
      stream.pause();
      stream.fetch(1);
      return foos;
    });
  }

  private void testFindBatch(int numDocs, BiFunction<CountDownLatch, ReadStream<JsonObject>, List<String>> checker) throws Exception {
    AtomicReference<ReadStream<JsonObject>> streamReference = new AtomicReference<>();

    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<List<String>> foos = new AtomicReference<>();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        FindOptions findOptions = new FindOptions().setSort(new JsonObject().put("counter", 1)).setBatchSize(1);
        ReadStream<JsonObject> stream = mongoClient.findBatchWithOptions(collection, new JsonObject(), findOptions);
        streamReference.set(stream);
        foos.set(checker.apply(latch, stream));
      }));
    }));
    awaitLatch(latch);
    assertEquals(numDocs, foos.get().size());
    assertEquals("bar0", foos.get().get(0));
    assertEquals("bar" + (numDocs - 1), foos.get().get(numDocs - 1));

    // Make sure stream handlers can be set to null after closing
    streamReference.get().handler(null).exceptionHandler(null).endHandler(null);
  }


  @Test
  public void testUpsertCreatesHexIfRecordDoesNotExist() throws Exception {
    upsertDoc(randomCollection(), createDoc(), null, IGNORE -> {
      testComplete();
    });

    await();
  }

  @Test
  public void testUpsertWithASetOnInsertIsNotOverWritten() throws Exception {
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
  public void testUpsertDoesNotChangeIdIfRecordExist() throws Exception {
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

  @Test
  public void testAggregateWithOptions() throws Exception {
    AggregateOptions aggregateOptions = new AggregateOptions();
    aggregateOptions.setAllowDiskUse(true);

    JsonArray pipeline = new JsonArray();
    pipeline.add(new JsonObject().put("$addFields", new JsonObject().put("field", "test")));
    int numDocs = 25;
    final CountDownLatch latch = new CountDownLatch(1);
    final String collection = randomCollection();

    insertDocs(mongoClient, collection, numDocs, onSuccess(res -> {
      mongoClient.aggregateWithOptions(collection, pipeline, aggregateOptions).exceptionHandler(e -> {
      }).handler(item -> {
        System.out.println(item.encodePrettily());
      }).fetch(25).endHandler(v -> latch.countDown());
    }));

    awaitLatch(latch);
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
          .first()
          .subscribe(new Utils.ObservableSubscriber<Document>(vertx, Utils.toSingleResult(new Handler<AsyncResult<Document>>() {
            @Override
            public void handle(AsyncResult<Document> savedDocResult) {
              if (savedDocResult.failed())
                throw new RuntimeException(savedDocResult.cause());

              Document savedDoc = savedDocResult.result();
              if (expectedId != null) {
                assertEquals(expectedId, savedDoc.getString(MongoClientUpdateResult.ID_FIELD));
              } else {
                assertEquals(res.getDocUpsertedId().getString(MongoClientUpdateResult.ID_FIELD), savedDoc.getString(MongoClientUpdateResult.ID_FIELD));
              }
              doneFunction.accept(new JsonObject(savedDoc.toJson()));
            }
          })));
      }));
  }


}
