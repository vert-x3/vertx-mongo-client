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

package io.vertx.ext.mongo;

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
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    if (getConnectionString() == null) {
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
    mongoService.getCollectionNames(onSuccess(list -> {
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
      mongoService.getCollectionNames(onSuccess(list -> {
        List<String> ours = getOurCollections(list);
        assertEquals(1, ours.size());
        assertEquals(collection, ours.get(0));
        String collection2 = randomCollection();
        mongoService.createCollection(collection2, onSuccess(res2 -> {
          mongoService.getCollectionNames(onSuccess(list2 -> {
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
        mongoService.getCollectionNames(onSuccess(list -> {
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
    mongoService.runCommand(ping, onSuccess(reply -> {
      assertTrue(reply.getBoolean("ismaster"));
      testComplete();
    }));
    await();
  }

  @Test
  public void testRunInvalidCommand() throws Exception {
    JsonObject ping = new JsonObject().put("iuhioqwdqhwd", 1);
    mongoService.runCommand(ping, onFailure(ex -> {
      testComplete();
    }));
    await();
  }

  @Test
  public void testInsertNoCollection() {
    String name = randomCollection();
    String random = TestUtils.randomAlphaString(20);
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(new JsonObject().put("foo", random), onSuccess(id -> {
        assertNotNull(id);
        collection.find(new JsonObject(), onSuccess(docs -> {
          assertNotNull(docs);
          assertEquals(1, docs.size());
          assertEquals(random, docs.get(0).getString("foo"));
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testInsertNoPreexistingID() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject doc = createDoc();
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingID() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject doc = createDoc();
      String genID = TestUtils.randomAlphaString(100);
      doc.put("_id", genID);
      collection.insertOne(doc, onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertAlreadyExists() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject doc = createDoc();
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        collection.insertOne(doc, onFailure(t -> {
          testComplete();
        }));
      }));
    }));
    await();
  }

  //TODO: //TODO: Test insert with write options

  @Test
  public void testInsertWithNestedListMap() throws Exception {
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> nestedMap = new HashMap<>();
    nestedMap.put("foo", "bar");
    map.put("nestedMap", nestedMap);
    map.put("nestedList", Arrays.asList(1, 2, 3));

    String name = randomCollection();
    JsonObject doc = new JsonObject(map);
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        collection.findOne(new JsonObject().put("_id", id), null, onSuccess(result -> {
          assertNotNull(result);
          assertNotNull(result.getJsonObject("nestedMap"));
          assertEquals("bar", result.getJsonObject("nestedMap").getString("foo"));
          assertNotNull(result.getJsonArray("nestedList"));
          assertEquals(1, (int) result.getJsonArray("nestedList").getInteger(0));
          assertEquals(2, (int) result.getJsonArray("nestedList").getInteger(1));
          assertEquals(3, (int) result.getJsonArray("nestedList").getInteger(2));
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testSave() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject doc = createDoc();
      collection.save(doc, onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        doc.put("newField", "sheep");
        // Save again - it should update
        collection.save(doc, onSuccess(id2 -> {
          assertNull(id2);
          collection.findOne(new JsonObject(), null, onSuccess(res2 -> {
            assertEquals("sheep", res2.getString("newField"));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testSaveWithNestedListMap() throws Exception {
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> nestedMap = new HashMap<>();
    nestedMap.put("foo", "bar");
    map.put("nestedMap", nestedMap);
    map.put("nestedList", Arrays.asList(1, 2, 3));

    String name = randomCollection();
    JsonObject doc = new JsonObject(map);
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.save(doc, onSuccess(id -> {
        assertNotNull(id);
        collection.findOne(new JsonObject().put("_id", id), null, onSuccess(result -> {
          assertNotNull(result);
          assertNotNull(result.getJsonObject("nestedMap"));
          assertEquals("bar", result.getJsonObject("nestedMap").getString("foo"));
          assertNotNull(result.getJsonArray("nestedList"));
          assertEquals(1, (int) result.getJsonArray("nestedList").getInteger(0));
          assertEquals(2, (int) result.getJsonArray("nestedList").getInteger(1));
          assertEquals(3, (int) result.getJsonArray("nestedList").getInteger(2));
          testComplete();
        }));
      }));
    }));
    await();
  }

  //TODO: Test save with write options

  @Test
  public void testCountNoCollection() {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.count(new JsonObject(), onSuccess(count -> {
        assertEquals((long) 0, (long) count);
        testComplete();
      }));
    }));

    await();
  }

  @Test
  public void testCount() throws Exception {
    int num = 10;
    String name = randomCollection();
    insertDocs(name, num, onSuccess(res -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.count(new JsonObject(), onSuccess(count -> {
          assertNotNull(count);
          assertEquals(num, count.intValue());
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testCountWithQuery() throws Exception {
    int num = 10;
    String name = randomCollection();
    CountDownLatch latch = new CountDownLatch(num);
    for (int i = 0; i < num; i++) {
      JsonObject doc = createDoc();
      if (i % 2 == 0) {
        doc.put("flag", true);
      }
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.insertOne(doc, onSuccess(id -> {
          assertNotNull(id);
          latch.countDown();
        }));
      }));
    }

    awaitLatch(latch);

    JsonObject query = new JsonObject().put("flag", true);
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.count(query, onSuccess(count -> {
        assertNotNull(count);
        assertEquals(num / 2, count.intValue());
        testComplete();
      }));
    }));

    await();
  }

  @Test
  public void testFindOne() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        collection.findOne(new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
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
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      JsonObject doc = createDoc();
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        collection.findOne(new JsonObject().put("foo", "bar"), new JsonObject().put("num", true), onSuccess(obj -> {
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
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.findOne(new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
        assertNull(obj);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testFind() throws Exception {
    int num = 10;
    doTestFind(num, new JsonObject(), new FindOptions(), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(5, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithFields() throws Exception {
    int num = 10;
    doTestFind(num, new JsonObject(), new FindOptions().setFields(new JsonObject().put("num", true)), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(2, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithSort() throws Exception {
    int num = 11;
    doTestFind(num, new JsonObject(), new FindOptions().setSort(new JsonObject().put("foo", 1)), results -> {
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
    doTestFind(num, new JsonObject(), new FindOptions().setLimit(limit), results -> {
      assertEquals(limit, results.size());
    });
  }

  @Test
  public void testFindWithLimitLarger() throws Exception {
    int num = 10;
    int limit = 20;
    doTestFind(num, new JsonObject(), new FindOptions().setLimit(limit), results -> {
      assertEquals(num, results.size());
    });
  }

  @Test
  public void testFindWithSkip() throws Exception {
    int num = 10;
    int skip = 3;
    doTestFind(num, new JsonObject(), new FindOptions().setSkip(skip), results -> {
      assertEquals(num - skip, results.size());
    });
  }

  @Test
  public void testFindWithSkipLarger() throws Exception {
    int num = 10;
    int skip = 20;
    doTestFind(num, new JsonObject(), new FindOptions().setSkip(skip), results -> {
      assertEquals(0, results.size());
    });
  }

  private void doTestFind(int numDocs, JsonObject query, FindOptions options, Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      insertDocs(name, numDocs, onSuccess(v -> {
        collection.findWithOptions(query, options, onSuccess(results -> {
          resultConsumer.accept(results);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testReplace() {
    String name = randomCollection();
    JsonObject doc = createDoc();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        JsonObject replacement = createDoc();
        replacement.put("replacement", true);
        collection.replaceOne(new JsonObject().put("_id", id), replacement, false, onSuccess(v -> {
          collection.find(new JsonObject(), onSuccess(list -> {
            assertNotNull(list);
            assertEquals(1, list.size());
            JsonObject result = list.get(0);
            assertEquals(id, result.getString("_id"));
            result.remove("_id");
            replacement.remove("_id"); // id won't be there for event bus
            assertEquals(replacement, result);
            testComplete();
          }));
        }));
      }));
    }));

    await();
  }

  @Test
  public void testReplaceUpsertNoMatch() {
    String name = randomCollection();
    JsonObject doc = createDoc();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        JsonObject replacement = createDoc();
        replacement.put("replacement", true);
        collection.replaceOne(new JsonObject().put("_id", "foo"), replacement, true, onSuccess(v -> {
          collection.find(new JsonObject(), onSuccess(list -> {
            assertNotNull(list);
            // Should have upserted
            assertEquals(2, list.size());
            JsonObject result = null;
            for (JsonObject o : list) {
              if (o.containsKey("replacement")) {
                result = o;
              }
            }
            assertNotNull(result);
            testComplete();
          }));
        }));
      }));
    }));

    await();
  }

  @Test
  public void testReplaceUpsertWithMatch() {
    String name = randomCollection();
    JsonObject doc = createDoc();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(doc, onSuccess(id -> {
        assertNotNull(id);
        JsonObject replacement = createDoc();
        replacement.put("replacement", true);
        collection.replaceOne(new JsonObject().put("_id", id), replacement, true, onSuccess(v -> {
          collection.find(new JsonObject(), onSuccess(list -> {
            assertNotNull(list);
            // Should not have upserted
            assertEquals(1, list.size());
            assertEquals(id, list.get(0).getString("_id"));
            testComplete();
          }));
        }));
      }));
    }));

    await();
  }

  @Test
  public void testUpdateOne() throws Exception {
    String name = randomCollection();
    mongoService.getCollection(name, onSuccess(collection -> {
      collection.insertOne(createDoc(), onSuccess(id -> {
        collection.updateOne(new JsonObject().put("_id", id), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), false, onSuccess(res -> {
          collection.findOne(new JsonObject().put("_id", id), null, onSuccess(doc -> {
            assertEquals("fooed", doc.getString("foo"));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testUpdateMany() throws Exception {
    int num = 10;
    String name = randomCollection();
    JsonObject query = new JsonObject().put("num", 123);
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("foo", "fooed"));
    insertDocs(name, num, onSuccess(insertResult -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.updateMany(query, update, false, onSuccess(ar -> {
          collection.find(new JsonObject(), onSuccess(results -> {
            assertEquals(num, results.size());
            for (JsonObject doc : results) {
              assertEquals(5, doc.size());
              assertEquals("fooed", doc.getString("foo"));
              assertNotNull(doc.getString("_id"));
              testComplete();
            }
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testUpdateManyUpsert() throws Exception {
    int num = 10;
    String name = randomCollection();
    JsonObject query = new JsonObject().put("num", 111);
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("foo", "fooed").put("yadda", 333));
    insertDocs(name, num, onSuccess(insertResult -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.updateMany(query, update, true, onSuccess(ar -> {
          collection.find(new JsonObject(), onSuccess(results -> {
            assertEquals(num + 1, results.size());
            for (JsonObject doc : results) {
              if (doc.containsKey("yadda")) {
                assertNotNull(doc.getString("_id"));
                assertEquals("fooed", doc.getString("foo"));
                assertEquals(333, (int) doc.getInteger("yadda"));
              } else {
                assertTrue(doc.getString("foo").startsWith("bar"));
              }
            }
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testDeleteOne() throws Exception {
    String name = randomCollection();
    insertDocs(name, 6, onSuccess(res2 -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.deleteOne(new JsonObject().put("num", 123), onSuccess(res3 -> {
          collection.count(new JsonObject(), onSuccess(count -> {
            assertEquals(5, (long) count);
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  //TODO: test delete with write options

  @Test
  public void testDeleteMany() throws Exception {
    String name = randomCollection();
    insertDocs(name, 10, onSuccess(v -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.deleteMany(new JsonObject(), onSuccess(v2 -> {
          collection.find(new JsonObject(), onSuccess(res2 -> {
            assertTrue(res2.isEmpty());
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testDeleteManyWithQuery() throws Exception {
    String name = randomCollection();
    JsonObject query = new JsonObject().put("foo", new JsonObject().put("$gt", "bar4"));
    insertDocs(name, 10, onSuccess(v -> {
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.deleteMany(query, onSuccess(v2 -> {
          collection.find(new JsonObject(), onSuccess(res2 -> {
            assertEquals(5, res2.size());
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
    return new JsonObject().put("foo", "bar" + (num != -1 ? num : "")).put("num", 123).put("big", true).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312)));
  }

  private void insertDocs(String name, int num, Handler<AsyncResult<List<String>>> resultHandler) {
    if (num != 0) {
      List<JsonObject> documents = new ArrayList<>(num);
      for (int i = 0; i < num; i++) {
        documents.add(createDoc(i));
      }
      mongoService.getCollection(name, onSuccess(collection -> {
        collection.insertMany(documents, false, ar -> {
          if (ar.succeeded()) {
            resultHandler.handle(Future.succeededFuture(ar.result()));
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
        });
      }));
    }
  }
}
