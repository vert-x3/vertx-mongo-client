package io.vertx.ext.mongo;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;
import static io.vertx.ext.mongo.WriteOption.UNACKNOWLEDGED;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class MongoClientTestBase extends MongoTestBase {

  protected MongoClient mongoClient;

  @Test
  public void testCreateAndGetCollection() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      mongoClient.getCollections(onSuccess(list -> {
        List<String> ours = getOurCollections(list);
        assertEquals(1, ours.size());
        assertEquals(collection, ours.get(0));
        String collection2 = randomCollection();
        mongoClient.createCollection(collection2, onSuccess(res2 -> {
          mongoClient.getCollections(onSuccess(list2 -> {
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      mongoClient.createCollection(collection, onFailure(ex -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDropCollection() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      mongoClient.dropCollection(collection, onSuccess(res2 -> {
        mongoClient.getCollections(onSuccess(list -> {
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
    JsonObject command  = new JsonObject().put("isMaster", 1);
    mongoClient.runCommand("isMaster", command, onSuccess(reply -> {
      assertTrue(reply.getBoolean("ismaster"));
      testComplete();
    }));
    await();
  }

  @Test
  public void testRunCommandWithBody() throws Exception {

    JsonObject command = new JsonObject()
      .put("aggregate", "collection_name")
      .put("pipeline", new JsonArray());

    mongoClient.runCommand("aggregate", command, onSuccess(resultObj -> {
      JsonArray resArr = resultObj.getJsonArray("result");
      assertNotNull(resArr);
      assertEquals(0, resArr.size());
      testComplete();
    }));
    await();
  }

  @Test
  public void testRunInvalidCommand() throws Exception {
    JsonObject command  = new JsonObject().put("iuhioqwdqhwd", 1);
    mongoClient.runCommand("iuhioqwdqhwd", command, onFailure(ex -> {
      testComplete();
    }));
    await();
  }

  @Test
  public void testInsertNoCollection() {
    String collection = randomCollection();
    String random = TestUtils.randomAlphaString(20);
    mongoClient.insert(collection, new JsonObject().put("foo", random), onSuccess(id -> {
      assertNotNull(id);
      mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
        assertNotNull(docs);
        assertEquals(1, docs.size());
        assertEquals(random, docs.get(0).getString("foo"));
        testComplete();
      }));
    }));

    await();
  }

  public void assertDocumentWithIdIsPresent(String collection, Object id) {
    mongoClient.find(collection,
            new JsonObject()
                    .put("_id", id),
            onSuccess(result -> {
              assertEquals(1, result.size());
              testComplete();
            }));
  }

  @Test
  public void testInsertNoPreexistingID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      String genID = TestUtils.randomAlphaString(100);
      doc.put("_id", genID);
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingLongID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      Long genID = TestUtils.randomLong();
      doc.put("_id", genID);
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }

  @Test
  public void testSavePreexistingLongID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      Long genID = TestUtils.randomLong();
      doc.put("_id", genID);
      mongoClient.saveWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }

  @Test
  public void testInsertPreexistingObjectID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      JsonObject genID = new JsonObject().put("id", TestUtils.randomAlphaString(100));
      doc.put("_id", genID);
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertDocumentWithIdIsPresent(collection, genID);
      }));
    }));
    await();
  }

  @Test
  public void testInsertDoesntAlterObject() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {

      Map<String, Object> map = new LinkedHashMap<>();
      map.put("nestedMap", new HashMap<>());
      map.put("nestedList", new ArrayList<>());
      JsonObject doc = new JsonObject(map);

      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertNotNull(id);

        // Check the internal types haven't been converted
        assertTrue(map.get("nestedMap") instanceof HashMap);
        assertTrue(map.get("nestedList") instanceof ArrayList);

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testSavePreexistingObjectID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      JsonObject genID  = new JsonObject().put("id", TestUtils.randomAlphaString(100));
      doc.put("_id", genID);
      mongoClient.saveWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertAlreadyExists() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        mongoClient.insert(collection, doc, onFailure(t -> {
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testInsertWithOptions() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insertWithOptions(collection, doc, UNACKNOWLEDGED, onSuccess(id -> {
        assertNotNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertWithNestedListMap() throws Exception {
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> nestedMap = new HashMap<>();
    nestedMap.put("foo", "bar");
    map.put("nestedMap", nestedMap);
    map.put("nestedList", Arrays.asList(1, 2, 3));

    String collection = randomCollection();
    JsonObject doc = new JsonObject(map);
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(result -> {
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
    await();
  }

  @Test
  public void testInsertRetrieve() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      String genID = TestUtils.randomAlphaString(100);
      doc.put("_id", genID);
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNull(id);
        mongoClient.findOne(collection, new JsonObject(), null, onSuccess(retrieved -> {
          assertEquals(doc, retrieved);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testSave() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.save(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        doc.put("newField", "sheep");
        // Save again - it should update
        mongoClient.save(collection, doc, onSuccess(id2 -> {
          assertNull(id2);
          mongoClient.findOne(collection, new JsonObject(), null, onSuccess(res2 -> {
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

    String collection = randomCollection();
    JsonObject doc = new JsonObject(map);
    mongoClient.save(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(result -> {
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
    await();
  }

  @Test
  public void testSaveAndReadBinary() throws Exception {

    String collection = randomCollection();

    Instant now = Instant.now();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(now);
    oos.close();

    JsonObject doc = new JsonObject();
    doc.put("now", new JsonObject().put("$binary", baos.toByteArray()));

    mongoClient.save(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(result -> {
        assertNotNull(result);
        assertNotNull(result.getJsonObject("now"));
        assertNotNull(result.getJsonObject("now").getBinary("$binary"));

        ByteArrayInputStream bais = new ByteArrayInputStream(result.getJsonObject("now").getBinary("$binary"));
        ObjectInputStream ois = null;
        try {
          ois = new ObjectInputStream(bais);
          Instant reconstitutedNow = (Instant) ois.readObject();

          assertEquals(now, reconstitutedNow);
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
          assertTrue(false);
        }
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testSaveAndReadObjectId() throws Exception {

    String collection = randomCollection();
    ObjectId objectId = new ObjectId();

    JsonObject doc = new JsonObject();
    doc.put("otherId", new JsonObject().put("$oid", objectId.toHexString()));

    mongoClient.save(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(result -> {
        assertNotNull(result);
        assertNotNull(result.getJsonObject("otherId").getString("$oid"));
        assertEquals(objectId.toHexString(), result.getJsonObject("otherId").getString("$oid"));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testSaveWithOptions() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.saveWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        doc.put("newField", "sheep");
        // Save again - it should update
        mongoClient.save(collection, doc, onSuccess(id2 -> {
          assertNull(id2);
          mongoClient.findOne(collection, new JsonObject(), null, onSuccess(res2 -> {
            assertEquals("sheep", res2.getString("newField"));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testCountNoCollection() {
    String collection = randomCollection();
    mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
      assertEquals((long) 0, (long) count);
      testComplete();
    }));

    await();
  }

  @Test
  public void testCount() throws Exception {
    int num = 10;
    String collection = randomCollection();
    insertDocs(collection, num, onSuccess(res -> {
      mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
        assertNotNull(count);
        assertEquals(num, count.intValue());
        testComplete();
      }));
    }));

    await();
  }

  @Test
  public void testCountWithQuery() throws Exception {
    int num = 10;
    String collection = randomCollection();
    CountDownLatch latch = new CountDownLatch(num);
    for (int i = 0; i < num; i++) {
      JsonObject doc = createDoc();
      if (i % 2 == 0) {
        doc.put("flag", true);
      }
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        latch.countDown();
      }));
    }

    awaitLatch(latch);

    JsonObject query = new JsonObject().put("flag", true);
    mongoClient.count(collection, query, onSuccess(count -> {
      assertNotNull(count);
      assertEquals(num / 2, count.intValue());
      testComplete();
    }));

    await();
  }

  @Test
  public void testFindOne() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOne(collection, new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOne(collection, new JsonObject().put("foo", "bar"), new JsonObject().put("num", true), onSuccess(obj -> {
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      mongoClient.findOne(collection, new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
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
        assertEquals(9, doc.size()); // Contains _id too
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
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongoClient.findWithOptions(collection, query, options, onSuccess(res3 -> {
          resultConsumer.accept(res3);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testReplace() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replace(collection, new JsonObject().put("_id", id), replacement, onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNotNull(list);
          assertEquals(1, list.size());
          JsonObject result = list.get(0);
          Object id_value = result.getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, (String) id_value);
          }
          result.remove("_id");
          replacement.remove("_id"); // id won't be there for event bus
          assertEquals(replacement, result);
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testReplaceUpsert() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceWithOptions(collection, new JsonObject().put("_id", "foo"), replacement, new UpdateOptions(true), onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNotNull(list);
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

    await();
  }

  @Test
  public void testReplaceUpsert2() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceWithOptions(collection, new JsonObject().put("_id", id), replacement, new UpdateOptions(true), onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNotNull(list);
          assertEquals(1, list.size());
          Object id_value = list.get(0).getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, (String) id_value);
          }
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testUpdate() throws Exception {
    String collection = randomCollection();
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      mongoClient.update(collection, new JsonObject().put("_id", id), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), onSuccess(res -> {
        mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(doc -> {
          assertEquals("fooed", doc.getString("foo"));
          testComplete();
        }));
      }));
    }));
  }

  @Test
  public void testUpdateOne() throws Exception {
    int num = 1;
    doTestUpdate(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), new UpdateOptions(), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(9, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getValue("_id"));
      }
    });
  }

  @Test
  public void testUpdateAll() throws Exception {
    int num = 10;
    doTestUpdate(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), new UpdateOptions(false, true), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(9, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getValue("_id"));
      }
    });
  }

  private void doTestUpdate(int numDocs, JsonObject query, JsonObject update, UpdateOptions options,
                            Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(collection, numDocs, onSuccess(res2 -> {
        mongoClient.updateWithOptions(collection, query, update, options, onSuccess(res3 -> {
          mongoClient.find(collection, new JsonObject(), onSuccess(res4 -> {
            resultConsumer.accept(res4);
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveOne() throws Exception {
    String collection = randomCollection();
    insertDocs(collection, 6, onSuccess(res2 -> {
      mongoClient.removeOne(collection, new JsonObject().put("num", 123), onSuccess(res3 -> {
        mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
          assertEquals(5, (long) count);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveOneWithOptions() throws Exception {
    String collection = randomCollection();
    insertDocs(collection, 6, onSuccess(res2 -> {
      mongoClient.removeOneWithOptions(collection, new JsonObject().put("num", 123), UNACKNOWLEDGED, onSuccess(res3 -> {
        mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
          assertEquals(5, (long) count);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveMultiple() throws Exception {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(v -> {
      mongoClient.remove(collection, new JsonObject(), onSuccess(v2 -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(res2 -> {
          assertTrue(res2.isEmpty());
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveWithOptions() throws Exception {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(v -> {
      mongoClient.removeWithOptions(collection, new JsonObject(), ACKNOWLEDGED, onSuccess(v2 -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(res2 -> {
          assertTrue(res2.isEmpty());
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testNonStringID() {
    String collection = randomCollection();
    JsonObject document = new JsonObject().put("title", "The Hobbit");
    // here it happened
    document.put("_id", 123456);
    document.put("foo", "bar");

    mongoClient.insert(collection, document, onSuccess(id -> {
      mongoClient.findOne(collection, new JsonObject(), null, onSuccess(retrieved -> {
        assertEquals(document, retrieved);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testContexts() {
    vertx.runOnContext(v -> {
      Context currentContext = Vertx.currentContext();
      assertNotNull(currentContext);

      String collection = randomCollection();
      JsonObject document = new JsonObject().put("title", "The Hobbit");
      document.put("_id", 123456);
      document.put("foo", "bar");

      mongoClient.insert(collection, document, onSuccess(id -> {
        Context resultContext = Vertx.currentContext();
        assertSame(currentContext, resultContext);
        testComplete();
      }));

    });
    await();
  }

  protected JsonObject createDoc() {
    return new JsonObject().put("foo", "bar").put("num", 123).put("big", true).putNull("nullentry").
      put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
      put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
      put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312)));
  }

  protected JsonObject createDoc(int num) {
    return new JsonObject().put("foo", "bar" + (num != -1 ? num : "")).put("num", 123).put("big", true).putNull("nullentry").
      put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
      put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
      put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312)));
  }

  protected void insertDocs(String collection, int num, Handler<AsyncResult<Void>> resultHandler) {
    if (num != 0) {
      AtomicInteger cnt = new AtomicInteger();
      for (int i = 0; i < num; i++) {
        JsonObject doc = createDoc(i);
        mongoClient.insert(collection, doc, ar -> {
          if (ar.succeeded()) {
            if (cnt.incrementAndGet() == num) {
              resultHandler.handle(Future.succeededFuture());
            }
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
        });
      }
    } else {
      resultHandler.handle(Future.succeededFuture());
    }
  }

  protected void dropCollections(CountDownLatch latch) {
    // Drop all the collections in the db
    mongoClient.getCollections(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections(list);
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongoClient.dropCollection(collection, onSuccess(v -> {
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

  protected List<String> getOurCollections(List<String> colls) {
    List<String> ours = new ArrayList<>();
    for (String coll : colls) {
      if (coll.startsWith("ext-mongo")) {
        ours.add(coll);
      }
    }
    return ours;
  }


  protected String randomCollection() {
    return "ext-mongo" + TestUtils.randomAlphaString(20);
  }





}
