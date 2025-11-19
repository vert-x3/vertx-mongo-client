package io.vertx.ext.mongo.tests;

import com.mongodb.MongoClientSettings;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoTransaction;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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

    mongoClient.createTransaction()
      .flatMap(MongoTransaction::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertNotNull(id);

          tx.insert(collection2, doc2).onComplete(onSuccess(id2 -> {
            assertNotNull(id2);

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

    mongoClient.createTransaction()
      .flatMap(MongoTransaction::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertNotNull(id);

          tx.insert(collection2, doc2).onComplete(onSuccess(id2 -> {
            assertNotNull(id2);

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

    mongoClient.createTransaction()
      .flatMap(MongoTransaction::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertNotNull(id);

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

  private void assertIdOfFirstRecord(String id, List<JsonObject> coll) {
    assertEquals(1, coll.size());
    final JsonObject actual = coll.get(0);
    assertTrue(actual.containsKey("_id"));
    assertTrue(actual.getValue("_id") instanceof String);
    assertEquals(id, actual.getString("_id"));
  }
}
