package io.vertx.ext.mongo;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;
import static io.vertx.ext.mongo.WriteOption.UNACKNOWLEDGED;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.junit.Test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import io.vertx.test.core.TestUtils;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class MongoClientTestBase extends MongoTestBase {

  protected MongoClient mongoClient;
  protected boolean useObjectId;//since this class will be inherited by other tests, some tests will toggle useObjectId in their client config. This will keep trakc of it and run the affected test accordingly.

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
  public void testCreateIndexes() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      List<IndexModel> indexes = new ArrayList<>();
      JsonObject key = new JsonObject().put("field", 1);
      IndexModel index = new IndexModel(key);
      indexes.add(index);

      JsonObject key2 = new JsonObject().put("field1", 1);
      IndexModel index2 = new IndexModel(key2);
      indexes.add(index2);

      mongoClient.createIndexes(collection, indexes, onSuccess(res2 -> {
        mongoClient.listIndexes(collection, onSuccess(res3 -> {
          long cnt = res3.stream()
            .filter(o -> ((JsonObject) o).getJsonObject("key").containsKey("field") ||
              ((JsonObject) o).getJsonObject("key").containsKey("field1") )
            .count();
          assertEquals(2, cnt);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testCreateIndex() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject key = new JsonObject().put("field", 1);
      mongoClient.createIndex(collection, key, onSuccess(res2 -> {
        mongoClient.listIndexes(collection, onSuccess(res3 -> {
          long cnt = res3.stream()
                  .filter(o -> ((JsonObject) o).getJsonObject("key").containsKey("field"))
                  .count();
          assertEquals(cnt, 1);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testCreateAndDropIndex() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject key = new JsonObject().put("field", 1);
      mongoClient.createIndex(collection, key, onSuccess(res2 -> {
        mongoClient.dropIndex(collection, "field_1", onSuccess(res3 -> {
          mongoClient.listIndexes(collection, onSuccess(res4 -> {
            long cnt = res4.stream()
                    .filter(o -> ((JsonObject) o).getJsonObject("key").containsKey("field"))
                    .count();
            assertEquals(cnt, 0);
            testComplete();
          }));
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
      .put("pipeline", new JsonArray())
      .put("cursor", new JsonObject());

    mongoClient.runCommand("aggregate", command, onSuccess(resultObj -> {
      JsonArray resArr = resultObj.getJsonObject("cursor").getJsonArray("firstBatch");
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
  public void testSaveWithOptionCanTakeNullWriteOption() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      Long genID = TestUtils.randomLong();
      doc.put("_id", genID);
      mongoClient.saveWithOptions(collection, doc, null, onSuccess(id -> {
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
  public void testInsertWithOptionsCanTakeNullWriteOption() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insertWithOptions(collection, doc, null, onSuccess(id -> {
        assertNotNull(id);
        testComplete();
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
  public void testFindOneAndUpdateDefault() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOneAndUpdate(collection, new JsonObject().put("num", 123), new JsonObject().put("$inc", new JsonObject().put("num", 7)), onSuccess(obj -> {
          assertEquals(123, (long) obj.getLong("num", -1L));
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndUpdateNoRecord() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOneAndUpdate(collection, new JsonObject().put("num", 0), new JsonObject().put("$inc", new JsonObject().put("num", 7)), onSuccess(obj -> {
          assertNull(obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndUpdateWithOptions() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOneAndUpdateWithOptions(collection,
                new JsonObject().put("num", 123),
                new JsonObject().put("$inc", new JsonObject().put("num", 7)),
                new FindOptions().setFields(new JsonObject().put("num", 1)),
                new UpdateOptions().setReturningNewDocument(true),
                onSuccess(obj -> {
                  assertEquals(130, (long) obj.getLong("num", -1L));
                  assertFalse(obj.containsKey("foo"));
                  testComplete();
                }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndReplaceDefault() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        doc.remove("_id");
        mongoClient.findOneAndReplace(collection, new JsonObject().put("num", 123), doc.put("num", 130), onSuccess(obj -> {
          assertEquals(123, (long) obj.getLong("num", -1L));
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndReplaceWithOptions() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        doc.remove("_id");
        mongoClient.findOneAndReplaceWithOptions(collection,
                new JsonObject().put("num", 123),
                doc.put("num", 130),
                new FindOptions().setFields(new JsonObject().put("num", 1)),
                new UpdateOptions().setReturningNewDocument(true),
                onSuccess(obj -> {
                  assertEquals(130, (long) obj.getLong("num", -1L));
                  assertFalse(obj.containsKey("foo"));
                  testComplete();
                }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndDeleteDefault() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOneAndDelete(collection, new JsonObject().put("num", 123), onSuccess(obj -> {
          assertEquals(123, (long) obj.getLong("num", -1L));

          mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
            assertNotNull(count);
            assertEquals(0, count.intValue());
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneAndDeleteWithOptions() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOneAndDeleteWithOptions(collection,
                new JsonObject().put("num", 123),
                new FindOptions().setFields(new JsonObject().put("num", 1)),
                onSuccess(obj -> {
                  assertEquals(123, (long) obj.getLong("num", -1L));
                  assertFalse(obj.containsKey("foo"));

                  mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
                    assertNotNull(count);
                    assertEquals(0, count.intValue());
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
    insertDocs(mongoClient, collection, num, onSuccess(res -> {
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
        assertEquals(12, doc.size()); // Contains _id too
      }
    });
  }

  @Test
  public void testFindWithId() throws Exception {
    int num = 10;
    doTestFind(num, new JsonObject(), new FindOptions().setFields(new JsonObject().put("_id", 1)), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(1, doc.size()); // _id field only
        assertTrue(doc.getValue("_id", null) instanceof String); // and always unwrapped
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
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
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
      mongoClient.replaceDocuments(collection, new JsonObject().put("_id", id), replacement, onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNotNull(list);
          assertEquals(1, list.size());
          JsonObject result = list.get(0);
          Object id_value = result.getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, id_value);
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
  public void testReplaceWithMongoClientUpddateResult() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocuments(collection, new JsonObject().put("_id", id), replacement, onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNull(v.getDocUpsertedId());
          assertEquals(1, v.getDocMatched());
          assertEquals(1, v.getDocModified());

          assertNotNull(list);
          assertEquals(1, list.size());
          JsonObject result = list.get(0);
          Object id_value = result.getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, id_value);
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
  public void testReplaceMongoClientUpdateResultUnacknowledge() throws Exception {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", id), replacement,
        new UpdateOptions().setWriteOption(UNACKNOWLEDGED), onSuccess(v -> {
          assertNull(v);
          testComplete();
      }));
    }));

    await();
  }

  @Test
  public void testReplaceMongoClientUpdateResultAcknowledge() throws Exception {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", id), replacement,
        new UpdateOptions().setWriteOption(ACKNOWLEDGED), onSuccess(v -> {
          assertNull(v.getDocUpsertedId());
          assertEquals(1, v.getDocMatched());
          assertEquals(1, v.getDocModified());
          testComplete();
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
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", "foo"), replacement, new UpdateOptions(true), onSuccess(v -> {
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
  public void testReplaceUpsertWithMongoClientUpdateResult() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    String upsertTestId = "foo";
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", upsertTestId), replacement, new UpdateOptions(true), onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {assertEquals(upsertTestId, v.getDocUpsertedId().getString(MongoClientUpdateResult.ID_FIELD));
          assertEquals(0, v.getDocMatched());
          assertEquals(0, v.getDocModified());

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
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", id), replacement, new UpdateOptions(true), onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNotNull(list);
          assertEquals(1, list.size());
          Object id_value = list.get(0).getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, id_value);
          }
          testComplete();
        }));
      }));
    }));

    await();
  }

  @Test
  public void testReplaceUpsertWithNoNewIdWithMongoClientUpdateResult() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", id), replacement, new UpdateOptions(true), onSuccess(v -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(list -> {
          assertNull(v.getDocUpsertedId());
          assertEquals(1, v.getDocMatched());
          assertEquals(1, v.getDocModified());

          assertNotNull(list);
          assertEquals(1, list.size());
          Object id_value = list.get(0).getValue("_id");
          if (id_value instanceof JsonObject) {
            assertEquals(id, ((JsonObject) id_value).getString("$oid"));
          } else {
            assertEquals(id, id_value);
          }
          testComplete();
        }));
      }));
    }));

    await();
  }
  //TODO: Technical debt. Later may have to re-factor so that the tests that extend this does not cause different execution path because of the conditional tests.
  @Test
  public void testUpdate() throws Exception {
    String collection = randomCollection();
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      if(useObjectId) {
        JsonObject docIdToBeUpdatedObjectId = new JsonObject().put(JsonObjectCodec.OID_FIELD, id);
        updateDataBasedOnId(collection, docIdToBeUpdatedObjectId);
      } else {
        String docIdToBeUpdatedString = id;
        updateDataBasedOnId(collection, docIdToBeUpdatedString);
      }

    }));
    await();
  }

  private <T> void updateDataBasedOnId(String collection, T docIdToBeUpdated) {
    mongoClient.updateCollection(collection, new JsonObject().put("_id", docIdToBeUpdated), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), onSuccess(res -> {
      mongoClient.findOne(collection, new JsonObject().put("_id", docIdToBeUpdated), null, onSuccess(doc -> {
        assertEquals("fooed", doc.getString("foo"));
        testComplete();
      }));
    }));
  }



  @Test
  public void testUpdateWithMongoClientUpdateResult() throws Exception {
    String collection = randomCollection();
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      if(useObjectId) {
        JsonObject docIdToBeUpdatedObjectId = new JsonObject().put(JsonObjectCodec.OID_FIELD, id);
        updateDataBasedOnIdWithMongoClientUpdateResult(collection, docIdToBeUpdatedObjectId);
      } else {
        String docIdToBeUpdatedString = id;
        updateDataBasedOnIdWithMongoClientUpdateResult(collection, docIdToBeUpdatedString);
      }
    }));
    await();
  }

  private <T> void updateDataBasedOnIdWithMongoClientUpdateResult(String collection, T id) {
    mongoClient.updateCollection(collection, new JsonObject().put("_id", id), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), onSuccess(res -> {
      assertEquals(1, res.getDocModified());
      assertEquals(1, res.getDocMatched());
      assertNull(res.getDocUpsertedId());
      mongoClient.findOne(collection, new JsonObject().put("_id", id), null, onSuccess(doc -> {
        assertEquals("fooed", doc.getString("foo"));
        testComplete();
      }));
    }));
  }

  @Test
  public void testUpdateOne() throws Exception {
    int num = 1;
    doTestUpdate(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), new UpdateOptions(), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(12, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getValue("_id"));
      }
    });
  }

  @Test
  public void testUpdateOneWithMongoClientUpdateResult() throws Exception {
    int num = 1;
    doTestUpdateWithMongoClientUpdateResult(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), new UpdateOptions(), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(12, doc.size());
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
        assertEquals(12, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getValue("_id"));
      }
    });
  }

  @Test
  public void testUpdateAllWithMongoClientUpdateResult() throws Exception {
    int num = 10;
    doTestUpdateWithMongoClientUpdateResult(num, new JsonObject().put("num", 123), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")), new UpdateOptions(false, true), results -> {
      assertEquals(num, results.size());
      for (JsonObject doc : results) {
        assertEquals(12, doc.size());
        assertEquals("fooed", doc.getString("foo"));
        assertNotNull(doc.getValue("_id"));
      }
    });
  }

  @Test
  public void testUpdateMongoClientUpdateResultUpsertIdResponse() throws Exception {
    String collection = randomCollection();
    String upsertedId = TestUtils.randomAlphaString(20);
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      mongoClient.updateCollectionWithOptions(collection, new JsonObject().put("_id", upsertedId), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")),
        new UpdateOptions().setUpsert(true),onSuccess(res -> {
          assertEquals(0, res.getDocModified());
          assertEquals(0, res.getDocMatched());
          assertEquals(upsertedId,res.getDocUpsertedId().getString(MongoClientUpdateResult.ID_FIELD));
          testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testMongoClientUpdateResultAcknowledge() throws Exception {
    String collection = randomCollection();
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      if(useObjectId) {
        JsonObject docIdToBeUpdatedObjectId = new JsonObject().put(JsonObjectCodec.OID_FIELD, id);
        updateWithOptionWithMongoClientUpdateResultBasedOnIdAcknowledged(collection, docIdToBeUpdatedObjectId);
      } else {
        String docIdToBeUpdatedString = id;
        updateWithOptionWithMongoClientUpdateResultBasedOnIdAcknowledged(collection, docIdToBeUpdatedString);
      }
    }));
    await();
  }

  private <T> void updateWithOptionWithMongoClientUpdateResultBasedOnIdAcknowledged(String collection, T id) {
    mongoClient.updateCollectionWithOptions(collection, new JsonObject().put("_id", id), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")),
      new UpdateOptions().setWriteOption(ACKNOWLEDGED),onSuccess(res -> {
        assertEquals(1, res.getDocModified());
        assertEquals(1, res.getDocMatched());
        assertNull(res.getDocUpsertedId());
        testComplete();
      }));
  }

  @Test
  public void testMongoClientUpdateResultUnacknowledge() throws Exception {
    String collection = randomCollection();
    String upsertedId = TestUtils.randomAlphaString(20);
    mongoClient.insert(collection, createDoc(), onSuccess(id -> {
      mongoClient.updateCollectionWithOptions(collection, new JsonObject().put("_id", upsertedId), new JsonObject().put("$set", new JsonObject().put("foo", "fooed")),
        new UpdateOptions().setWriteOption(UNACKNOWLEDGED),onSuccess(res -> {
          assertNull(res);
          testComplete();
        }));
    }));
    await();
  }

  private void doTestUpdate(int numDocs, JsonObject query, JsonObject update, UpdateOptions options,
                            Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        mongoClient.updateCollectionWithOptions(collection, query, update, options, onSuccess(res3 -> {
          mongoClient.find(collection, new JsonObject(), onSuccess(res4 -> {
            resultConsumer.accept(res4);
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  private void doTestUpdateWithMongoClientUpdateResult(int numDocs, JsonObject query, JsonObject update, UpdateOptions options,
                            Consumer<List<JsonObject>> resultConsumer) throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      insertDocs(mongoClient, collection, numDocs, onSuccess(res2 -> {
        mongoClient.updateCollectionWithOptions(collection, query, update, options, onSuccess(res3 -> {
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
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocument(collection, new JsonObject().put("num", 123), onSuccess(res3 -> {
        mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
          assertEquals(5, (long) count);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveOneWithMongoClientDeleteResult() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocument(collection, new JsonObject().put("num", 123), onSuccess(res3 -> {
        mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
          assertEquals(1, res3.getRemovedCount());
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
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocumentWithOptions(collection, new JsonObject().put("num", 123), UNACKNOWLEDGED, onSuccess(res3 -> {
        mongoClient.count(collection, new JsonObject(), onSuccess(count -> {
          assertNull(res3);

          assertEquals(5, (long) count);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveDocumentWithOptionsCanHaveNullWriteOption() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocumentWithOptions(collection, new JsonObject().put("num", 123), null, onSuccess(res3 -> {
        assertEquals(1, res3.getRemovedCount());

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRemoveOneWithOptionsWithMongoClientDeleteResultAcknowledged() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocumentWithOptions(collection, new JsonObject().put("num", 123), ACKNOWLEDGED, onSuccess(res3 -> {
        assertEquals(1, res3.getRemovedCount());

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRemoveOneWithOptionsWithMongoClientDeleteResultUnacknowledged() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 6, onSuccess(res2 -> {
      mongoClient.removeDocumentWithOptions(collection, new JsonObject().put("num", 123), UNACKNOWLEDGED, onSuccess(res3 -> {
        assertNull(res3);

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRemoveMultiple() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocuments(collection, new JsonObject(), onSuccess(v2 -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(res2 -> {
          assertTrue(res2.isEmpty());
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveMultipleWithMongoClientDeleteResult() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocuments(collection, new JsonObject(), onSuccess(v2 -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(res2 -> {
          assertEquals(10, v2.getRemovedCount());

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
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocumentsWithOptions(collection, new JsonObject(), ACKNOWLEDGED, onSuccess(v2 -> {
        mongoClient.find(collection, new JsonObject(), onSuccess(res2 -> {
          assertTrue(res2.isEmpty());

          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testRemoveDocumentsWithOptionsCanHaveNullWriteOption() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocumentsWithOptions(collection, new JsonObject(), null, onSuccess(v2 -> {
        assertEquals(10, v2.getRemovedCount());

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRemoveWithOptionsWithMongoClientDeleteResultAcknowledge() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocumentsWithOptions(collection, new JsonObject(), ACKNOWLEDGED, onSuccess(v2 -> {
        assertEquals(10, v2.getRemovedCount());

        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testRemoveWithOptionsWithMongoClientDeleteResultUnacknowledge() throws Exception {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(v -> {
      mongoClient.removeDocumentsWithOptions(collection, new JsonObject(), UNACKNOWLEDGED, onSuccess(v2 -> {
        assertNull(v2);

        testComplete();
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

  @Test
  public void testBulkOperation_insertDocument() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.bulkWrite(collection, Arrays.asList(BulkOperation.createInsert(doc)), onSuccess(bulkResult -> {
      assertEquals(1, bulkResult.getInsertedCount());
      assertEquals(0, bulkResult.getModifiedCount());
      assertEquals(0, bulkResult.getDeletedCount());
      assertEquals(0, bulkResult.getMatchedCount());
      mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {

        assertEquals(1, docs.size());
        JsonObject foundDoc = docs.get(0);
        doc.put("_id", foundDoc.getString("_id"));
        assertEquals(foundDoc, doc);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_insertDocuments() {
    String collection = randomCollection();
    JsonObject doc1 = new JsonObject().put("foo", "bar");
    JsonObject doc2 = new JsonObject().put("foo", "foobar");
    mongoClient.bulkWrite(collection,
        Arrays.asList(BulkOperation.createInsert(doc1), BulkOperation.createInsert(doc2)),
        onSuccess(bulkResult -> {
          assertEquals(2, bulkResult.getInsertedCount());
          assertEquals(0, bulkResult.getModifiedCount());
          assertEquals(0, bulkResult.getDeletedCount());
          assertEquals(0, bulkResult.getMatchedCount());
          testComplete();
        }));
    await();
  }

  @Test
  public void testBulkOperation_updateDocument() {
    String collection = randomCollection();
    JsonObject doc = new JsonObject().put("foo", "bar");
    mongoClient.insert(collection, doc, onSuccess(id -> {
      JsonObject update = new JsonObject().put("$set", new JsonObject().put("foo", "foobar"));
      JsonObject filter = new JsonObject();
      if (useObjectId)
        filter.put("_id", new JsonObject().put(JsonObjectCodec.OID_FIELD, id));
      else
        filter.put("_id", id);
      mongoClient.bulkWrite(collection, Arrays.asList(BulkOperation.createUpdate(filter, update)),
          onSuccess(bulkResult -> {
            assertEquals(1, bulkResult.getModifiedCount());
            assertEquals(0, bulkResult.getDeletedCount());
            assertEquals(0, bulkResult.getInsertedCount());
            assertEquals(1, bulkResult.getMatchedCount());
            mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
              assertEquals(1, docs.size());
              JsonObject foundDoc = docs.get(0);
              assertEquals("foobar", foundDoc.getString("foo"));
              testComplete();
            }));
          }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_updateMultipleDocuments() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 5, onSuccess(v -> {
      JsonObject update = new JsonObject().put("$set", new JsonObject().put("foo", "foobar-bulk"));
      JsonObject filter = new JsonObject();
      BulkOperation bulkUpdate = BulkOperation.createUpdate(filter, update).setMulti(true);
      mongoClient.bulkWrite(collection, Arrays.asList(bulkUpdate), onSuccess(bulkResult -> {
        assertEquals(5, bulkResult.getModifiedCount());
        assertEquals(5, bulkResult.getMatchedCount());
        assertEquals(0, bulkResult.getDeletedCount());
        assertEquals(0, bulkResult.getInsertedCount());
        mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
          assertEquals(5, docs.size());
          for (JsonObject foundDoc : docs) {
            assertEquals("foobar-bulk", foundDoc.getString("foo"));
          }
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_updateMultipleDocuments_multiFalse() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 5, onSuccess(v -> {
      JsonObject update = new JsonObject().put("$set", new JsonObject().put("foo", "foobar-bulk"));
      JsonObject filter = new JsonObject();
      BulkOperation bulkUpdate = BulkOperation.createUpdate(filter, update).setMulti(false);
      mongoClient.bulkWrite(collection, Arrays.asList(bulkUpdate), onSuccess(bulkResult -> {
        assertEquals(1, bulkResult.getModifiedCount());
        assertEquals(0, bulkResult.getDeletedCount());
        assertEquals(0, bulkResult.getInsertedCount());
        assertEquals(1, bulkResult.getMatchedCount());
        mongoClient.find(collection, new JsonObject().put("foo", "foobar-bulk"), onSuccess(docs -> {
          assertEquals(1, docs.size());
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_upsertDocument() {
    String collection = randomCollection();
    JsonObject doc = new JsonObject().put("$set", new JsonObject().put("foo", "bar"));
    BulkOperation bulkUpdate = BulkOperation.createUpdate(new JsonObject().put("foo", "bur"), doc)
        .setUpsert(true);
    mongoClient.bulkWrite(collection, Arrays.asList(bulkUpdate), onSuccess(bulkResult -> {
      // even though one document was created, the MongoDB client returns 0 for all counts
      assertEquals(0, bulkResult.getInsertedCount());
      assertEquals(0, bulkResult.getDeletedCount());
      assertEquals(0, bulkResult.getModifiedCount());
      assertEquals(0, bulkResult.getMatchedCount());
      List<JsonObject> upserts = bulkResult.getUpserts();
      assertNotNull(upserts);
      assertEquals(1, upserts.size());
      JsonObject upsert = upserts.get(0);
      assertEquals(0, upsert.getInteger(MongoClientBulkWriteResult.INDEX).intValue());
      testComplete();
    }));
    await();
  }

  @Test
  public void testBulkOperation_upsertDocument_upsertDisabled() {
    String collection = randomCollection();
    JsonObject doc = new JsonObject().put("$set", new JsonObject().put("foo", "bar"));
    BulkOperation bulkUpdate = BulkOperation.createUpdate(new JsonObject().put("foo", "bur"), doc)
        .setUpsert(false);
    mongoClient.bulkWrite(collection, Arrays.asList(bulkUpdate), onSuccess(bulkResult -> {
      assertEquals(0, bulkResult.getInsertedCount());
      assertEquals(0, bulkResult.getModifiedCount());
      assertEquals(0, bulkResult.getDeletedCount());
      assertEquals(0, bulkResult.getMatchedCount());
      assertEquals(0, bulkResult.getUpserts().size());
      testComplete();
    }));
    await();
  }

  @Test
  public void testBulkOperation_replace() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 1, onSuccess(v -> {
      JsonObject filter = new JsonObject().put("num", 123);
      JsonObject replace = new JsonObject().put("foo", "replaced");
      BulkOperation bulkReplace = BulkOperation.createReplace(filter, replace);
      mongoClient.bulkWrite(collection, Arrays.asList(bulkReplace), onSuccess(bulkResult -> {
        assertEquals(0, bulkResult.getInsertedCount());
        assertEquals(1, bulkResult.getModifiedCount());
        assertEquals(0, bulkResult.getDeletedCount());
        assertEquals(1, bulkResult.getMatchedCount());
        assertEquals(0, bulkResult.getUpserts().size());
        mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
          assertEquals(1, docs.size());
          JsonObject foundDoc = docs.get(0);
          assertEquals("replaced", foundDoc.getString("foo"));
          assertNull(foundDoc.getInteger("num"));
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_replaceById() {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = new JsonObject();
      doc.put("foo", "bar");
      doc.put("num", 123);
      mongoClient.insert(collection, doc, onSuccess(id -> {
        JsonObject filter = new JsonObject().put("_id", id);
        JsonObject replace = new JsonObject().put("foo", "replaced");
        BulkOperation bulkReplace = BulkOperation.createReplace(filter, replace);
        mongoClient.bulkWrite(collection, Arrays.asList(bulkReplace), onSuccess(bulkResult -> {
          assertEquals(0, bulkResult.getInsertedCount());
          assertEquals(0, bulkResult.getDeletedCount());
          assertEquals(1, bulkResult.getMatchedCount());
          assertEquals(0, bulkResult.getUpserts().size());
          mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
            assertEquals(1, docs.size());
            JsonObject foundDoc = docs.get(0);
            assertEquals("replaced", foundDoc.getString("foo"));
            assertNull(foundDoc.getInteger("num"));
            testComplete();
          }));
        }));
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_replaceOne_upsert() {
    String collection = randomCollection();
    JsonObject filter = new JsonObject().put("foo", "bar");
    JsonObject replace = new JsonObject().put("foo", "upsert");
    BulkOperation bulkReplace = BulkOperation.createReplace(filter, replace, true);
    mongoClient.bulkWrite(collection, Arrays.asList(bulkReplace), onSuccess(bulkResult -> {
      assertEquals(0, bulkResult.getInsertedCount());
      assertEquals(0, bulkResult.getDeletedCount());
      assertEquals(0, bulkResult.getModifiedCount());
      assertEquals(0, bulkResult.getMatchedCount());
      assertEquals(1, bulkResult.getUpserts().size());
      assertEquals(0, (int) bulkResult.getUpserts().get(0).getInteger(MongoClientBulkWriteResult.INDEX));
      mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
        assertEquals(1, docs.size());
        JsonObject foundDoc = docs.get(0);
        assertEquals("upsert", foundDoc.getString("foo"));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_delete_multiDisabled() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 5, onSuccess(v -> {
      JsonObject filter = new JsonObject().put("num", 123);
      BulkOperation bulkDelete = BulkOperation.createDelete(filter).setMulti(false);
      mongoClient.bulkWrite(collection, Arrays.asList(bulkDelete), onSuccess(bulkResult -> {
        assertEquals(0, bulkResult.getInsertedCount());
        assertEquals(1, bulkResult.getDeletedCount());
        assertEquals(0, bulkResult.getModifiedCount());
        assertEquals(0, bulkResult.getMatchedCount());
        assertEquals(0, bulkResult.getUpserts().size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_delete_multiEnabled() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 5, onSuccess(v -> {
      JsonObject filter = new JsonObject().put("num", 123);
      BulkOperation bulkDelete = BulkOperation.createDelete(filter).setMulti(true);
      mongoClient.bulkWrite(collection, Arrays.asList(bulkDelete), onSuccess(bulkResult -> {
        assertEquals(0, bulkResult.getInsertedCount());
        assertEquals(5, bulkResult.getDeletedCount());
        assertEquals(0, bulkResult.getModifiedCount());
        assertEquals(0, bulkResult.getMatchedCount());
        assertEquals(0, bulkResult.getUpserts().size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testBulkOperation_completeBulk() {
    testCompleteBulk(null);
  }

  @Test
  public void testBulkOperation_completeBulk_unacknowleged() {
    testCompleteBulk(new BulkWriteOptions().setWriteOption(UNACKNOWLEDGED));
  }

  @Test
  public void testBulkOperationwithOptions_completeBulk_orderedFalse() {
    testCompleteBulk(new BulkWriteOptions(false));
  }

  @Test
  public void testBulkOperationwithOptions_completeBulk_orderedTrue() {
    testCompleteBulk(new BulkWriteOptions(true));
  }

  private void testCompleteBulk(BulkWriteOptions bulkWriteOptions) {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 5, onSuccess(v -> {
      BulkOperation bulkInsert = BulkOperation.createInsert(new JsonObject().put("foo", "insert"));
      BulkOperation bulkUpdate = BulkOperation.createUpdate(new JsonObject().put("foo", "bar1"),
          new JsonObject().put("$set", new JsonObject().put("foo", "update")));
      BulkOperation bulkReplace = BulkOperation.createReplace(new JsonObject().put("foo", "bar2"),
          new JsonObject().put("foo", "replace"));
      BulkOperation bulkDelete = BulkOperation.createDelete(new JsonObject().put("foo", "bar3"));
      Handler<AsyncResult<MongoClientBulkWriteResult>> successHandler = onSuccess(bulkResult -> {
        if (bulkWriteOptions != null && bulkWriteOptions.getWriteOption() == UNACKNOWLEDGED) {
          assertNull(bulkResult);
          testComplete();
        } else {
          assertEquals(1, bulkResult.getInsertedCount());
          assertEquals(1, bulkResult.getDeletedCount());
          assertEquals(2, bulkResult.getModifiedCount());
          assertEquals(2, bulkResult.getMatchedCount());
          assertEquals(0, bulkResult.getUpserts().size());
          mongoClient.find(collection, new JsonObject(), onSuccess(docs -> {
            List<String> values = docs.stream().map(doc -> doc.getString("foo")).collect(Collectors.toList());
            assertTrue(values.contains("insert"));
            assertFalse(values.contains("bar1"));
            assertTrue(values.contains("update"));
            assertFalse(values.contains("bar2"));
            assertTrue(values.contains("replace"));
            assertFalse(values.contains("bar3"));
            testComplete();
          }));
        }
      });
      if (bulkWriteOptions == null)
        mongoClient.bulkWrite(collection, Arrays.asList(bulkInsert, bulkUpdate, bulkReplace, bulkDelete),
            successHandler);
      else
        mongoClient.bulkWriteWithOptions(collection, Arrays.asList(bulkInsert, bulkUpdate, bulkReplace, bulkDelete),
            bulkWriteOptions, successHandler);
    }));
    await();
  }
}
