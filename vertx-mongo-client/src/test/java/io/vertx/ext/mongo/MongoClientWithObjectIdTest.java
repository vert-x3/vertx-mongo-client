package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static io.vertx.ext.mongo.WriteOption.ACKNOWLEDGED;

public class MongoClientWithObjectIdTest extends MongoClientTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    config.put("useObjectId", true);
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  protected void assertEquals(JsonObject expected, JsonObject actual) {

    //Test cases will fail unless we map the $oid first
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject orig = createDoc();
      JsonObject doc = orig.copy();
      mongoClient.insert(collection, doc, onSuccess(id -> {
        assertNotNull(id);
        mongoClient.findOne(collection, new JsonObject().put("foo", "bar"), null, onSuccess(obj -> {
          assertTrue(obj.containsKey("_id"));
          assertTrue(obj.getValue("_id") instanceof JsonObject);
          assertTrue(((JsonObject) obj.getValue("_id")).containsKey("$oid"));
          obj.remove("_id");
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.insertWithOptions(collection, doc, ACKNOWLEDGED, onSuccess(id -> {
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
      mongoClient.insert(collection, doc, onSuccess(id -> {
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
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
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
  @Override
  public void testSavePreexistingObjectID() throws Exception {
    String collection = randomCollection();
    mongoClient.createCollection(collection, onSuccess(res -> {
      JsonObject doc = createDoc();
      //Changed to hex string as a random string will not be valid for useObjectId = true
      doc.put("_id", new ObjectId().toHexString());
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
        mongoClient.insert(collection, doc, response -> {
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
    mongoClient.insert(collection, doc, onSuccess(id -> {
      assertNotNull(id);
      JsonObject replacement = createDoc();
      replacement.put("replacement", true);
      mongoClient.replaceWithOptions(collection, new JsonObject().put("_id", new ObjectId().toHexString()), replacement, new UpdateOptions(true), onSuccess(v -> {
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
}
