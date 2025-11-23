package io.vertx.ext.mongo.tests;

import com.mongodb.ClientSessionOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoQueryException;
import com.mongodb.TransactionOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.SessionOptions;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MongoClientWithTransactionTest extends MongoClientTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    config.put("useObjectId", true);
    useObjectId = true;
    final MongoClientSettings settings = new MongoClientOptionsParser(vertx, config).settings();
    mongoClient = MongoClient.createWithMongoSettings(vertx, config, UUID.randomUUID().toString(), settings);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  protected static JsonObject getConfig() {
    JsonObject config = MongoClientTestBase.getConfig();
    config.put("useObjectId", true);
    return config;
  }

  @Test
  public void testReplaceUpsertCommit() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    AtomicReference<String> id1 = new AtomicReference<>();
    AtomicReference<String> id2 = new AtomicReference<>();

    mongoClient.startSession()
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        session.executeTransaction(client ->
            Future.join(
              client.insert(collection, doc).onComplete(onSuccess(insertedId -> {
                assertTrue(ObjectId.isValid(insertedId));
                id1.set(insertedId);
              })),
              client.insert(collection2, doc2).onComplete(onSuccess(insertedId -> {
                assertTrue(ObjectId.isValid(insertedId));
                id2.set(insertedId);
              }))
            )
          )
          .onComplete(onSuccess(id -> {
            mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
              assertIdOfFirstRecord(id1.get(), coll);

              mongoClient.find(collection2, new JsonObject()).onComplete(onSuccess(coll2 -> {
                assertIdOfFirstRecord(id2.get(), coll2);

                testComplete();
              }));
            }));
          }));
      }));

    await();
  }

  @Test
  public void testTwoSequentialTransactions() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    AtomicReference<String> id1 = new AtomicReference<>();
    AtomicReference<String> id2 = new AtomicReference<>();

    JsonObject doc = createDoc();
    JsonObject doc2 = createDoc();

    Future.join(
      mongoClient.executeTransaction(client -> client.insert(collection, doc).onComplete(onSuccess(insertedId -> {
        assertTrue(ObjectId.isValid(insertedId));
        id1.set(insertedId);
      }))),
      mongoClient.executeTransaction(client -> client.insert(collection2, doc2).onComplete(onSuccess(insertedId -> {
        assertTrue(ObjectId.isValid(insertedId));
        id2.set(insertedId);
      })))
    ).onComplete(onSuccess(id -> {
      mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
        assertIdOfFirstRecord(id1.get(), coll);

        mongoClient.find(collection2, new JsonObject()).onComplete(onSuccess(coll2 -> {
          assertIdOfFirstRecord(id2.get(), coll2);

          testComplete();
        }));
      }));
    }));

    await();
  }

  //TODO review and rewrite
  @Ignore("rewrite")
  @Test
  public void testAbort() {
    String collection = randomCollection();

    mongoClient.startSession(new SessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        session.executeTransaction(client ->
          Future.join(
            session.abort(),
            client.insert(collection, doc).onComplete(onSuccess(id -> assertTrue(ObjectId.isValid(id))))
          )
        ).onFailure(ex -> {
          assertNotNull(ex);

          session.close().result();

          mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
            assertEquals(0, coll.size());

            testComplete();
          }));
        }).onSuccess(onSuccess(id -> session.close().result()));
      }));

    await();
  }

  @Test
  public void testAbortWithError() {
    String collection = randomCollection();

    mongoClient.startSession()
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        session.executeTransaction(client ->
          Future.join(
            client.insert(collection, doc).onComplete(onSuccess(id -> assertTrue(ObjectId.isValid(id)))),
            client.updateCollection("wrongcollection", new JsonObject(), new JsonObject().put("$noSuchOperator", new JsonObject().put("x", 1)))
          )
        ).onFailure(ex -> {
          assertNotNull(ex);

          mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
            assertEquals(0, coll.size());

            testComplete();
          }));
        });
      }));

    await();
  }

  @Test
  public void testExecuteTransactionCommit() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    mongoClient.executeTransaction(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();
        return Future.join(
          tx.insert(collection, doc),
          tx.insert(collection2, doc2));
      })
      .onComplete(onSuccess(cf -> {
        assertTrue(ObjectId.isValid(cf.resultAt(0)));
        assertTrue(ObjectId.isValid(cf.resultAt(1)));
        testComplete();
      }));

    await();
  }

  @Test
  public void testExecuteTransactionCommitForMultipleTransactions() {
    String collection = randomCollection();
    String collection2 = randomCollection();
    String collection3 = randomCollection();

    mongoClient.executeTransaction(client -> {
      JsonObject doc = createDoc();
      JsonObject doc2 = createDoc();
      return Future.join(
          client.insert(collection, doc),
          client.insert(collection2, doc2))
        .onComplete(onSuccess(cf -> {
          assertTrue(ObjectId.isValid(cf.resultAt(0)));
          assertTrue(ObjectId.isValid(cf.resultAt(1)));

          JsonObject doc3 = createDoc();
          client.insert(collection3, doc3)
            .onComplete(onSuccess(id3 -> {
              assertTrue(ObjectId.isValid(id3));
              testComplete();
            }));
        }));
    }, new SessionOptions()
      .setClientSessionOptions(
        ClientSessionOptions.builder()
          .defaultTransactionOptions(
            TransactionOptions.builder()
              .maxCommitTime(100L, TimeUnit.SECONDS)
              .build())
          .build()));

    await();
  }

  @Test
  public void testExecuteTransactionAbortByException() {
    String collection = randomCollection();

    mongoClient.executeTransaction(client -> {
        JsonObject doc = createDoc();
        return Future.join(
          client.insert(collection, doc),
          client.find("wrongcollection",
            new JsonObject().put("$notARealOperator", 1)));
      })
      .onFailure(ex -> {
        assertTrue(ex instanceof MongoQueryException);

        mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
          assertEquals(0, coll.size());
          testComplete();
        }));
      });

    await();
  }

  private void assertIdOfFirstRecord(String id, List<JsonObject> coll) {
    assertEquals(1, coll.size());
    final JsonObject actual = coll.get(0);
    assertTrue(actual.containsKey("_id"));
    assertTrue(actual.getValue("_id") instanceof String);
    assertEquals(id, actual.getString("_id"));
  }
}
