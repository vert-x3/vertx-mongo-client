package io.vertx.ext.mongo.tests;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoQueryException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.ClientSessionOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.TransactionOptions;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.types.ObjectId;
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

  @Test
  public void testAbort() {
    String collection = randomCollection();

    mongoClient.startSession(new ClientSessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        session.executeTransaction(client ->
          client.insert(collection, doc)
            .onComplete(onSuccess(id -> assertTrue(ObjectId.isValid(id))))
            .compose(id -> session.abort())
        ).onFailure(ex -> {
          assertNotNull(ex);

          session.close()
            .onComplete(onSuccess(closed ->
              mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
                assertEquals(0, coll.size());

                testComplete();
              }))));
        });
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
    }, new ClientSessionOptions().setDefaultTransactionOptions(
      new TransactionOptions().setMaxCommitTime(100L, TimeUnit.SECONDS)
    ));

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

  @Test
  public void testManualStartCommit() {
    String collection = randomCollection();

    mongoClient.startSession(new ClientSessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        session.executeTransaction(client ->
          client.insert(collection, doc)
        ).onComplete(onSuccess(id -> {
          assertTrue(ObjectId.isValid(id));

          mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(coll -> {
            assertEquals(1, coll.size());
            session.close().onComplete(onSuccess(closed -> testComplete()));
          }));
        }));
      }));

    await();
  }

  @Test
  public void testCommitOnClosedSession() {
    mongoClient.startSession(new ClientSessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        session.close()
          .onComplete(onSuccess(v -> {
            session.commit()
              .onComplete(onFailure(err -> {
                assertTrue(err instanceof IllegalStateException);
                assertTrue(err.getMessage().contains("closed"));
                testComplete();
              }));
          }));
      }));

    await();
  }

  @Test
  public void testCommitWithoutTransaction() {
    mongoClient.startSession(new ClientSessionOptions().setAutoStartTransaction(false).setAutoClose(false))
      .onComplete(onSuccess(session -> {
        session.commit()
          .onComplete(onFailure(err -> {
            assertTrue(err instanceof IllegalStateException);
            assertTrue(err.getMessage().contains("not in transaction"));
            session.close().onComplete(onSuccess(v -> testComplete()));
          }));
      }));

    await();
  }

  @Test
  public void testDoubleStart() {
    mongoClient.startSession(new ClientSessionOptions().setAutoStartTransaction(false).setAutoClose(false))
      .onComplete(onSuccess(session -> session.start()
        .onComplete(onSuccess(v -> session.start()
          .onComplete(onFailure(err -> {
            assertTrue(err instanceof IllegalStateException);
            assertTrue(err.getMessage().contains("already in transaction"));
            session.abort().onComplete(onSuccess(v2 ->
              session.close().onComplete(onSuccess(v3 -> testComplete()))
            ));
          }))
        ))
      ));

    await();
  }

  @Test
  public void testExecuteTransactionOnClosedSession() {
    String collection = randomCollection();

    mongoClient.startSession(new ClientSessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        session.close()
          .onComplete(onSuccess(v -> session.executeTransaction(client ->
            client.insert(collection, createDoc())
          ).onComplete(onFailure(err -> {
            assertTrue(err instanceof IllegalStateException);
            assertTrue(err.getMessage().contains("closed"));
            testComplete();
          }))));
      }));

    await();
  }

  @Test
  public void testExecuteTransactionWithoutAutoStartAndNoManualStart() {
    String collection = randomCollection();

    mongoClient.startSession(new ClientSessionOptions().setAutoStartTransaction(false).setAutoClose(false))
      .onComplete(onSuccess(session -> session.executeTransaction(client ->
        client.insert(collection, createDoc())
      ).onComplete(onFailure(err -> {
        assertTrue(err instanceof IllegalStateException);
        assertTrue(err.getMessage().contains("autoStartTransaction is disabled"));
        session.close().onComplete(onSuccess(v -> testComplete()));
      }))));

    await();
  }

  @Test
  public void testNestedSessionPrevented() {
    mongoClient.executeTransaction(client ->
      client.startSession().compose(nestedSession ->
        Future.failedFuture("Should not reach here"))
    ).onComplete(onFailure(err -> {
      assertTrue(err instanceof IllegalStateException);
      assertTrue(err.getMessage().contains("nested session"));
      testComplete();
    }));

    await();
  }

  @Test
  public void testFindOneInsideTransaction() {
    String collection = randomCollection();
    JsonObject doc = createDoc();

    mongoClient.executeTransaction(client ->
      client.insert(collection, doc)
        .compose(id -> client.findOne(collection, JsonObject.of("_id", id), null)
          .map(found -> {
            assertNotNull(found);
            assertEquals("bar", found.getString("foo"));
            return id;
          }))
    ).onComplete(onSuccess(id -> {
      assertTrue(ObjectId.isValid(id));
      testComplete();
    }));

    await();
  }

  @Test
  public void testUncommittedWriteNotVisibleOutsideTransaction() {
    String collection = randomCollection();

    mongoClient.startSession(new ClientSessionOptions().setAutoClose(false))
      .onComplete(onSuccess(session -> {
        JsonObject doc = createDoc();
        session.executeTransaction(client ->
          client.insert(collection, doc)
            .compose(id -> {
              // Read from OUTSIDE the transaction — should not see the uncommitted insert
              return mongoClient.findOne(collection, new JsonObject().put("_id", id), null)
                .map(found -> {
                  assertNull(found);
                  return id;
                });
            })
        ).onComplete(onSuccess(id -> {
          // After commit, the doc should now be visible outside the transaction
          mongoClient.findOne(collection, new JsonObject().put("_id", id), null)
            .onComplete(onSuccess(found -> {
              assertNotNull(found);
              assertEquals("bar", found.getString("foo"));
              session.close().onComplete(onSuccess(v -> testComplete()));
            }));
        }));
      }));

    await();
  }

  @Test
  public void testCountInsideTransaction() {
    String collection = randomCollection();

    mongoClient.executeTransaction(client ->
      client.insert(collection, createDoc())
        .compose(id -> client.count(collection, new JsonObject()))
        .map(count -> {
          assertEquals(1L, count.longValue());
          return count;
        })
    ).onComplete(onSuccess(count -> {
      assertEquals(1L, count.longValue());
      testComplete();
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
