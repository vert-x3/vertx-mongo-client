package io.vertx.ext.mongo.tests;

import com.mongodb.ClientSessionOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoQueryException;
import com.mongodb.TransactionOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoSession;
import io.vertx.ext.mongo.SessionOptions;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    mongoClient.createSession()
      .flatMap(MongoSession::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertTrue(ObjectId.isValid(id));

          tx.insert(collection2, doc2).onComplete(onSuccess(id2 -> {
            assertTrue(ObjectId.isValid(id2));

            tx.commit().onComplete(onSuccess(v -> {
              assertNull(v);

              mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
                assertIdOfFirstRecord(id, coll);

                mongoClient.find(collection2, new JsonObject()).onComplete(onSuccess(coll2 -> {
                  assertIdOfFirstRecord(id2, coll2);

                  testComplete();
                }));
              }));
            }));
          }));
        }));
      }));

    await();
  }

  @Test
  public void testReplaceUpsertAbort() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    mongoClient.createSession()
      .flatMap(MongoSession::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertTrue(ObjectId.isValid(id));

          tx.insert(collection2, doc2).onComplete(onSuccess(id2 -> {
            assertTrue(ObjectId.isValid(id2));

            tx.abort().onComplete(onSuccess(v -> {
              assertNull(v);

              mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
                assertEquals(0, coll.size());

                mongoClient.find(collection2, new JsonObject()).onComplete(onSuccess(coll2 -> {
                  assertEquals(0, coll2.size());

                  testComplete();
                }));
              }));
            }));
          }));
        }));
      }));

    await();
  }

  @Test
  public void testReplaceUpsertAbortWithError() {
    String collection = randomCollection();

    mongoClient.createSession()
      .flatMap(MongoSession::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertTrue(ObjectId.isValid(id));

          tx.find("wrongcollection", new JsonObject().put("$eq", new JsonObject().put("$notARealOperator", 1)))
            .onFailure(ex -> {
              assertNotNull(ex);

              tx.abort();

              mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
                assertEquals(0, coll.size());

                testComplete();
              }));
            })
          ;
        }));
      }));

    await();
  }

  @Test
  public void testInTransactionCommit() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    mongoClient.inTransaction(tx -> {
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
  public void testInTransactionCommitForMultipleTransactions() {
    String collection = randomCollection();
    String collection2 = randomCollection();
    String collection3 = randomCollection();

    mongoClient.inTransaction(tx -> {
      JsonObject doc = createDoc();
      JsonObject doc2 = createDoc();
      return Future.join(
          tx.insert(collection, doc),
          tx.insert(collection2, doc2))
        .onComplete(onSuccess(cf -> {
          assertTrue(ObjectId.isValid(cf.resultAt(0)));
          assertTrue(ObjectId.isValid(cf.resultAt(1)));

          JsonObject doc3 = createDoc();
          tx.insert(collection3, doc3)
            .onComplete(onSuccess(id3 -> {
              assertTrue(ObjectId.isValid(id3));
              testComplete();
            }));
        }));
    }, new SessionOptions()
      .setCloseSession(false)
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
  public void testInTransactionAbortByException() {
    String collection = randomCollection();

    mongoClient.inTransaction(tx -> {
        JsonObject doc = createDoc();
        return Future.join(
          tx.insert(collection, doc),
          tx.find("wrongcollection",
            new JsonObject().put("$eq", new JsonObject().put("$notARealOperator", 1))));
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
