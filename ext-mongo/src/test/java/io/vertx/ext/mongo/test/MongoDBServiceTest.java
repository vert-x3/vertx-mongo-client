package io.vertx.ext.mongo.test;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoDBServiceTest extends VertxTestBase {

  private static MongodExecutable exe;

  private static String getConnectionString() {
    return getProperty("connection_string");
  }

  private static String getDatabaseName() {
    return getProperty("db_name");
  }

  private static String getProperty(String name) {
    String s = System.getProperty(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }

    return null;
  }

  @BeforeClass
  public static void startMongo() throws Exception {
    if (getConnectionString() == null ) {
      IMongodConfig config = new MongodConfigBuilder().
          version(Version.Main.PRODUCTION).
          net(new Net(27017, Network.localhostIsIPv6())).
          build();
      exe = MongodStarter.getDefaultInstance().prepare(config);
      exe.start();
    }
  }

  @AfterClass
  public static void stopMongo() {
    if (exe != null) {
      exe.stop();
    }
  }

  MongoService mongo;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = new JsonObject();
    String connectionString = getConnectionString();
    if (connectionString != null) {
      config.putString("connection_string", connectionString);
    }
    String databaseName = getDatabaseName();
    if (databaseName != null) {
      config.putString("db_name", databaseName);
    }
    mongo = MongoService.create(vertx, config);
    mongo.start();
    CountDownLatch latch = new CountDownLatch(1);
    // Drop all the collections in the db
    mongo.getCollections(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections(list);
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongo.dropCollection(collection, onSuccess(v -> {
            if (collCount.incrementAndGet() == count) {
              latch.countDown();
            }
          }));
        }
      } else {
        latch.countDown();
      }
    }));
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongo.stop();
    super.tearDown();
  }

  private String randomCollection() {
    return "ext-mongo" + TestUtils.randomAlphaString(20);
  }

  private List<String> getOurCollections(List<String> colls) {
    List<String> ours = new ArrayList<>();
    for (String coll : colls) {
      if (coll.startsWith("ext-mongo")) {
        ours.add(coll);
      }
    }
    return ours;
  }

  @Test
  public void testCreateAndGetCollection() throws Exception {
    String collection = randomCollection();
      mongo.createCollection(collection, onSuccess(res -> {
      mongo.getCollections(onSuccess(list -> {
        List<String> ours = getOurCollections(list);
        assertEquals(1, ours.size());
        assertEquals(collection, ours.get(0));
        String collection2 = randomCollection();
        mongo.createCollection(collection2, onSuccess(res2 -> {
          mongo.getCollections(onSuccess(list2 -> {
            List<String> ours2 = getOurCollections(list2);
            assertEquals(2, ours2.size());
            assertTrue(ours2.contains(collection));
            assertTrue(ours2.contains(collection2));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testCreateCollectionAlreadyExists() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      mongo.createCollection(collection, onFailure(ex -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDropCollection() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      mongo.dropCollection(collection, onSuccess(res2 -> {
        mongo.getCollections(onSuccess(list -> {
          List<String> ours = getOurCollections(list);
          assertTrue(ours.isEmpty());
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRunCommand() throws Exception {
    JsonObject ping = new JsonObject().putNumber("isMaster", 1);
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      mongo.runCommand(collection, ping, onSuccess(reply -> {
        assertTrue(reply.getBoolean("ismaster"));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRunInvalidCommand() throws Exception {
    JsonObject ping = new JsonObject().putNumber("iuhioqwdqhwd", 1);
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      mongo.runCommand(collection, ping, onFailure(ex -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertNoPreexistingID() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingID() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      String genID  = TestUtils.randomAlphaString(100);
      doc.putString("_id", genID);
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertAlreadyExists() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        doc.putString("_id", id);
        mongo.insert(collection, doc, "NORMAL", onFailure(t -> {
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testSave() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.save(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        doc.putString("_id", id);
        doc.putString("newField", "sheep");
        // Save again - it should update
        mongo.save(collection, doc, "NORMAL", onSuccess(id2 -> {
          assertNull(id2);
          mongo.findOne(collection, null, null, onSuccess(res2 -> {
            assertEquals("sheep", res2.getString("newField"));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOne() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        mongo.findOne(collection, new JsonObject().putString("foo", "bar"), null, onSuccess(obj -> {
          assertEquals(doc, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneWithKeys() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        mongo.findOne(collection, new JsonObject().putString("foo", "bar"), new JsonObject().putBoolean("num", true), onSuccess(obj -> {
          assertEquals(2, obj.size());
          assertEquals(123, obj.getInteger("num").intValue());
          assertTrue(obj.containsField("_id"));
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneNotFound() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      mongo.findOne(collection, new JsonObject().putString("foo", "bar"), null, onSuccess(obj -> {
        assertNull(obj);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testFind() throws Exception {
    int num = 10;
    doTestFind(num, new JsonObject(), null, null, -1, -1, results -> {
      assertEquals(num, results.size());
      for (JsonObject doc: results) {
        assertEquals(5, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithFields() throws Exception {
    int num = 10;
    doTestFind(num, new JsonObject(), new JsonObject().putBoolean("num", true), null, -1, -1, results -> {
      assertEquals(num, results.size());
      for (JsonObject doc: results) {
        assertEquals(2, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithSort() throws Exception {
    int num = 11;
    doTestFind(num, new JsonObject(), null, new JsonObject().putNumber("foo", 1), -1, -1, results -> {
      assertEquals(num, results.size());
      assertEquals("bar0", results.get(0).getString("foo"));
      assertEquals("bar1", results.get(1).getString("foo"));
      assertEquals("bar10", results.get(2).getString("foo"));
    });
  }

  @Test
  public void testFindWithLimit() throws Exception {
    int num = 10;
    int limit = 3;
    doTestFind(num, new JsonObject(), null, null, -1, limit, results -> {
      assertEquals(limit, results.size());
    });
  }

  @Test
  public void testFindWithSkip() throws Exception {
    int num = 10;
    int skip = 3;
    doTestFind(num, new JsonObject(), null, null, skip, -1, results -> {
      assertEquals(num - skip, results.size());
    });
  }

  private void doTestFind(int numDocs, JsonObject query, JsonObject fields, JsonObject sort, int skip, int limit,
                          Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongo.find(collection, query, fields, sort, limit, skip, onSuccess(res3 -> {
          resultConsumer.accept(res3);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testUpdateOne() throws Exception {
    int num = 1;
    doTestUpdate(num, new JsonObject().putNumber("num", 123), new JsonObject().putObject("$set", new JsonObject().putString("foo", "fooed")), false, false, results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(5, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getString("_id"));
      }
    });
  }

  // FIXME - update of document doesn't seem to work well with async client

//  @Test
//  public void testUpdateAll() throws Exception {
//    int num = 10;
//    doTestUpdate(num, new JsonObject().putNumber("num", 123), new JsonObject().putObject("$set", new JsonObject().putString("foo", "fooed")), false, true, results -> {
//      assertEquals(num, results.size());
//      for (JsonObject doc : results) {
//        assertEquals(5, doc.size());
//        assertEquals("fooed", doc.getString("foo"));
//        assertNotNull(doc.getString("_id"));
//      }
//    });
//  }

//  @Test
//  public void testUpsert() throws Exception {
//    doTestUpdate(0, new JsonObject().putNumber("num", 123), new JsonObject().putString("_id", "someid").putString("foo", "bar"), true, false, results -> {
//      assertEquals(1, results.size());
//      for (JsonObject doc : results) {
//        assertEquals(2, doc.size());
//        assertEquals("bar", doc.getString("foo"));
//        assertNotNull(doc.getString("_id"));
//      }
//    });
//  }

  private void doTestUpdate(int numDocs, JsonObject query, JsonObject update, boolean upsert, boolean multi,
                            Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongo.update(collection, query, update, "NORMAL", upsert, multi, onSuccess(res3 -> {
          mongo.find(collection, null, null, null, -1, -1, onSuccess(res4 -> {
            resultConsumer.accept(res4);
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testDeleteOne() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongo.insert(collection, doc, "NORMAL", onSuccess(id -> {
        assertNotNull(id);
        mongo.delete(collection, new JsonObject().putString("_id", id), "NORMAL", onSuccess(v -> {
          mongo.findOne(collection, new JsonObject().putString("_id", id), null, onSuccess(res2 -> {
            assertNull(res2);
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testDeleteMultiple() throws Exception {
    String collection = randomCollection();
    mongo.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, 10, onSuccess(v -> {
        mongo.delete(collection, new JsonObject(), "NORMAL", onSuccess(v2 -> {
          mongo.find(collection, new JsonObject(), null, null, -1, -1, onSuccess(res2 -> {
            assertTrue(res2.isEmpty());
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }


  private JsonObject createDoc() {
    return new JsonObject().putString("foo", "bar").putNumber("num", 123).putBoolean("big", true).
      putObject("other", new JsonObject().putString("quux", "flib").putArray("myarr",
        new JsonArray().addString("blah").addBoolean(true).addNumber(312)));
  }

  private JsonObject createDoc(int num) {
    return new JsonObject().putString("foo", "bar" + (num != -1 ? num: "")).putNumber("num", 123).putBoolean("big", true).
      putObject("other", new JsonObject().putString("quux", "flib").putArray("myarr",
        new JsonArray().addString("blah").addBoolean(true).addNumber(312)));
  }

  private void insertDocs(String collection, int num, Handler<AsyncResult<Void>> resultHandler) {
    if (num != 0) {
      AtomicInteger cnt = new AtomicInteger();
      for (int i = 0; i < num; i++) {
        JsonObject doc = createDoc(i);
        mongo.insert(collection, doc, "NORMAL", ar -> {
          if (ar.succeeded()) {
            if (cnt.incrementAndGet() == num) {
              resultHandler.handle(Future.completedFuture());
            }
          } else {
            resultHandler.handle(Future.completedFuture(ar.cause()));
          }
        });
      }
    } else {
      resultHandler.handle(Future.completedFuture());
    }
  }

//  @Test
//  public void testCreateConnectionViaProxy() throws Exception {
//    Vertx vertx = Vertx.vertx();
//
//    vertx.deployVerticle("java:" + MongoDBServiceVerticle.class.getName(), DeploymentOptions.options(), ar -> {
//
//    });
//
//
//    MongoDBService mongo = MongoDBService.createEventBusProxy(vertx, "vertx.mongodb");
//
//    String collection = TestUtils.randomAlphaString(100);
//    mongo.createCollection(collection, onSuccess(res -> {
//      testComplete();
//    }));
//    await();
//  }
}
