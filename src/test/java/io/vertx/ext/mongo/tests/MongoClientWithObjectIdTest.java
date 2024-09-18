package io.vertx.ext.mongo.tests;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;

public class MongoClientWithObjectIdTest extends MongoClientTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    config.put("useObjectId", true);
    useObjectId = true;
    mongoClient = MongoClient.create(vertx, config);
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
    JsonObject config  = MongoClientTestBase.getConfig();
    config.put("useObjectId", true);
    return config;
  }

  protected void assertEquals(JsonObject expected, JsonObject actual) {

    //Test cases will fail unless we map the $oid first. This is because the original document is
    //transformed with an object ID. Probably shouldn't do that.
    if (actual.containsKey("_id")) {
      if (actual.getValue("_id") instanceof String) {
        actual.put("_id", new JsonObject().put("$oid", actual.getString("_id")));
      }
    }
    super.assertEquals(expected, actual);

  }


  @Test
  @Override
  public void testSavePreexistingLongID() throws Exception {
    //Override this test as it does not make sense for useObjectId = true
    assertTrue(true);
    testComplete();
    await();
  }

  @Test
  public void testFindOneReturnsStringId() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOne(collection, new JsonObject().put("foo", "bar"), null).onComplete(onSuccess(obj -> {
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof String);
          obj.remove("_id");
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneWithNestedQueryReturnsStringId() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      String objectId = getObjectId(doc);
      JsonObject query = JsonObject.of("$and", JsonArray.of(
        JsonObject.of("foo", "bar"),
        JsonObject.of("_id", objectId)));
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        // no auto-generated objectId from mongo
        assertNull(id);
        mongoClient.findOne(collection, query, null).onComplete(onSuccess(obj -> {
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof String);
          obj.remove("_id");
          // nested "_id" will not be modified when insert
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindOneReturnsNothing() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOne(collection, new JsonObject().put("nothing", "xxrandomxx"), null).onComplete(onSuccess(obj -> {
          assertNull(obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindReturnsStringId() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNotNull(id);
        mongoClient.find(collection, new JsonObject().put("foo", "bar")).onComplete(onSuccess(list -> {
          assertTrue(list.size() == 1);
          JsonObject obj = list.get(0);
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof String);
          obj.remove("_id");
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }


  @Test
  public void testFindWithNestedQueryReturnsStringId() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      String objectId = getObjectId(doc);
      JsonObject query = JsonObject.of("$and", JsonArray.of(
          JsonObject.of("foo", "bar"),
          JsonObject.of("_id", objectId)));
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        // no auto-generated objectId from mongo
        assertNull(id);
        mongoClient.find(collection, query).onComplete(onSuccess(list -> {
          assertTrue(list.size() == 1);
          JsonObject obj = list.get(0);
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof String);
          obj.remove("_id");
          // nested "_id" will not be modified when insert
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  public void testFindWithNestedQueryWithListMapReturnsStringId() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      String objectId = getObjectId(doc);
      Map<String, String> m1 = new HashMap<>();
      m1.put("foo", "bar");
      Map<String, String> m2 = new HashMap<>();
      m2.put("_id", objectId);
      JsonObject query = JsonObject.of("$and", Arrays.asList(m1, m2));
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        // no auto-generated objectId from mongo
        assertNull(id);
        mongoClient.find(collection, query).onComplete(onSuccess(list -> {
          assertTrue(list.size() == 1);
          JsonObject obj = list.get(0);
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof String);
          obj.remove("_id");
          // nested "_id" will not be modified when insert
          assertEquals(orig, obj);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  @Override
  public void testInsertPreexistingObjectID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED).onComplete(onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  @Override
  public void testInsertPreexistingID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  @Override
  public void testInsertRetrieve() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject doc = createDoc();
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNull(id);
        mongoClient.findOne(collection, new JsonObject(), null).onComplete(onSuccess(retrieved -> {
          assertEquals(doc, retrieved);
          testComplete();
        }));
      }));
    }));
    await();
  }

  @Test
  @Override
  public void testSavePreexistingObjectID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.saveWithOptions(collection, doc, ACKNOWLEDGED).onComplete(onSuccess(id -> {
        assertNull(id);
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testInsertAlreadyExists() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      JsonObject doc = createDoc();
      mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
        assertNotNull(id);
        doc.put("_id", id);
        mongoClient.insert(collection, doc).onComplete(response -> {
          assertFalse(response.succeeded());
          testComplete();
        });
      }));
    }));
    await();
  }

  @Test
  public void testReplaceUpsert() {
    String collection = randomCollection();
    JsonObject doc = createDoc();
    mongoClient.insert(collection, doc).onComplete(onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceDocumentsWithOptions(collection, new JsonObject().put("_id", new ObjectId().toHexString()), replacement, new UpdateOptions(true)).onComplete(onSuccess(v -> {
        mongoClient.find(collection, new JsonObject()).onComplete(onSuccess(list -> {
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
}
