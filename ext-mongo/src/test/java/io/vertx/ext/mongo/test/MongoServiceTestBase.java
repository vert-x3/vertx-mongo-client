/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

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
import io.vertx.ext.mongo.InsertOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOptions;
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
public abstract class MongoServiceTestBase extends VertxTestBase {

  private static MongodExecutable exe;

  protected static String getConnectionString() {
    return getProperty("connection_string");
  }

  protected static String getDatabaseName() {
    return getProperty("db_name");
  }

  protected static String getProperty(String name) {
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
        net(new Net(27018, Network.localhostIsIPv6())).
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


  protected MongoService mongoService;

  private String randomCollection() {
    return "ext-mongo" + TestUtils.randomAlphaString(20);
  }

  protected JsonObject getConfig() {
    JsonObject config = new JsonObject();
    String connectionString = getConnectionString();
    if (connectionString != null) {
      config.put("connection_string", connectionString);
    } else {
      config.put("connection_string", "mongodb://localhost:27018");
    }
    String databaseName = getDatabaseName();
    if (databaseName != null) {
      config.put("db_name", databaseName);
    }
    return config;
  }

  protected List<String> getOurCollections(List<String> colls) {
    List<String> ours = new ArrayList<>();
    for (String coll : colls) {
      if (coll.startsWith("ext-mongo")) {
        ours.add(coll);
      }
    }
    return ours;
  }

  protected void dropCollections(CountDownLatch latch) {
    // Drop all the collections in the db
    mongoService.getCollections(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections(list);
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongoService.dropCollection(collection, onSuccess(v -> {
            if (collCount.incrementAndGet() == count) {
              latch.countDown();
            }
          }));
        }
      } else {
        latch.countDown();
      }
    }));
  }

  @Test
  public void testCreateAndGetCollection() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.getCollections(onSuccess(list -> {
        List<String> ours = getOurCollections(list);
        assertEquals(1, ours.size());
        assertEquals(collection, ours.get(0));
        String collection2 = randomCollection();
        mongoService.createCollection(collection2, onSuccess(res2 -> {
          mongoService.getCollections(onSuccess(list2 -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.createCollection(collection, onFailure(ex -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDropCollection() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.dropCollection(collection, onSuccess(res2 -> {
        mongoService.getCollections(onSuccess(list -> {
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
    JsonObject ping = new JsonObject().put("isMaster", 1);
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.runCommand(collection, ping, onSuccess(reply -> {
        assertTrue(reply.getBoolean("ismaster"));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRunInvalidCommand() throws Exception {
    JsonObject ping = new JsonObject().put("iuhioqwdqhwd", 1);
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.runCommand(collection, ping, onFailure(ex -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertNoPreexistingID() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNotNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingID() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      String genID  = TestUtils.randomAlphaString(100);
      doc.put("_id", genID);
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertAlreadyExists() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        mongoService.insert(collection, doc, new InsertOptions(), onFailure(t -> {
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testSave() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoService.save(collection, doc, new WriteOptions(), onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        doc.put("newField", "sheep");
        // Save again - it should update
        mongoService.save(collection, doc, new WriteOptions(), onSuccess(id2 -> {
          assertNull(id2);
          mongoService.findOne(collection, new JsonObject(), null, onSuccess(res2 -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNotNull(id);
        mongoService.findOne(collection, new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
          assertTrue(obj.containsKey("_id"));
          obj.remove("_id");
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneWithKeys() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNotNull(id);
        mongoService.findOne(collection, new JsonObject().put("foo", "bar"), new JsonObject().put("num", true), onSuccess(obj -> {
          assertEquals(2, obj.size());
          assertEquals(123, obj.getInteger("num").intValue());
          assertTrue(obj.containsKey("_id"));
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneNotFound() throws Exception {
    String collection = randomCollection();
    mongoService.createCollection(collection, onSuccess(res -> {
      mongoService.findOne(collection, new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
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
    doTestFind(num, new JsonObject(), new JsonObject().put("num", true), null, -1, -1, results -> {
      assertEquals(num, results.size());
      for (JsonObject doc: results) {
        assertEquals(2, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithSort() throws Exception {
    int num = 11;
    doTestFind(num, new JsonObject(), null, new JsonObject().put("foo", 1), -1, -1, results -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongoService.find(collection, query, fields, sort, limit, skip, onSuccess(res3 -> {
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
    doTestUpdate(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), false, false, results -> {
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
//    doTestUpdate(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), false, true, results -> {
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
//    doTestUpdate(0, new JsonObject().put("num", 123), new JsonObject().put("_id", "someid").put("foo", "bar"), true, false, results -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongoService.update(collection, query, update, new UpdateOptions().setUpsert(upsert).setMulti(multi), onSuccess(res3 -> {
          mongoService.find(collection, new JsonObject(), null, null, -1, -1, onSuccess(res4 -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoService.insert(collection, doc, new InsertOptions(), onSuccess(id -> {
        assertNotNull(id);
        mongoService.delete(collection, new JsonObject().put("_id", id), new WriteOptions(), onSuccess(v -> {
          mongoService.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(res2 -> {
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
    mongoService.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, 10, onSuccess(v -> {
        mongoService.delete(collection, new JsonObject(), new WriteOptions(), onSuccess(v2 -> {
          mongoService.find(collection, new JsonObject(), null, null, -1, -1, onSuccess(res2 -> {
            assertTrue(res2.isEmpty());
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }


  private JsonObject createDoc() {
    return new JsonObject().put("foo", "bar").put("num", 123).put("big", true).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312)));
  }

  private JsonObject createDoc(int num) {
    return new JsonObject().put("foo", "bar" + (num != -1 ? num: "")).put("num", 123).put("big", true).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312)));
  }

  private void insertDocs(String collection, int num, Handler<AsyncResult<Void>> resultHandler) {
    if (num != 0) {
      AtomicInteger cnt = new AtomicInteger();
      for (int i = 0; i < num; i++) {
        JsonObject doc = createDoc(i);
        mongoService.insert(collection, doc, new InsertOptions(), ar -> {
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
