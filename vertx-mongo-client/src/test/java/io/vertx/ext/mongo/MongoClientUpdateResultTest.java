package io.vertx.ext.mongo;

import static org.junit.Assert.*;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:maziz.esa@gmail.com">Maziz Esa</a>
 */
public class MongoClientUpdateResultTest {

  @Test
  public void testMongoClientUpdateResultStatuses() {
    long randomMatched = TestUtils.randomLong();
    JsonObject randomUpsertedId = randomUpsertId();
    long randomModified = TestUtils.randomLong();

    MongoClientUpdateResult mongoClientUpdateResult = new MongoClientUpdateResult(randomMatched, randomUpsertedId, randomModified);

    assertEquals(randomMatched, mongoClientUpdateResult.getDocMatched());
    assertEquals(randomUpsertedId, mongoClientUpdateResult.getDocUpsertedId());
    assertEquals(randomModified, mongoClientUpdateResult.getDocModified());
  }

  @Test
  public void testDefaultMongoClientUpdateResult() {
    MongoClientUpdateResult mongoClientUpdateResult = new MongoClientUpdateResult();

    assertEquals(MongoClientUpdateResult.DEFAULT_DOCMATCHED, mongoClientUpdateResult.getDocMatched());
    assertNull(mongoClientUpdateResult.getDocUpsertedId());
    assertEquals(MongoClientUpdateResult.DEFAULT_DOCMODIFIED, mongoClientUpdateResult.getDocModified());
  }

  @Test
  public void testCopyMongoClientUpdateResult() {
    MongoClientUpdateResult mongoClientUpdateResultOrigin = new MongoClientUpdateResult(TestUtils.randomLong(),
      randomUpsertId(), TestUtils.randomLong());
    MongoClientUpdateResult mongoClientUpdateResultCopy = new MongoClientUpdateResult(mongoClientUpdateResultOrigin);

    assertEquals(mongoClientUpdateResultOrigin.getDocMatched(), mongoClientUpdateResultCopy.getDocMatched());
    assertEquals(mongoClientUpdateResultOrigin.getDocUpsertedId(), mongoClientUpdateResultCopy.getDocUpsertedId());
    assertEquals(mongoClientUpdateResultOrigin.getDocModified(), mongoClientUpdateResultCopy.getDocModified());
  }

  @Test
  public void testJsonMongoClientUpdateResult() {
    properJson();

    jsonWithoutRequiredFields();
  }

  private void jsonWithoutRequiredFields() {
    JsonObject mongoClientUpdateResultJson = new JsonObject();
    MongoClientUpdateResult mongoClientUpdateResult = new MongoClientUpdateResult(mongoClientUpdateResultJson);

    assertEquals(MongoClientUpdateResult.DEFAULT_DOCMATCHED, mongoClientUpdateResult.getDocMatched());
    assertNull(mongoClientUpdateResult.getDocUpsertedId());
    assertEquals(MongoClientUpdateResult.DEFAULT_DOCMODIFIED, mongoClientUpdateResult.getDocModified());
  }

  private void properJson() {
    JsonObject mongoClientUpdateResultJson = randomMongoClientUpdateResultJson();
    MongoClientUpdateResult mongoClientUpdateResult = new MongoClientUpdateResult(mongoClientUpdateResultJson);

    assertEquals((long)mongoClientUpdateResultJson.getLong(MongoClientUpdateResult.DOC_MATCHED, MongoClientUpdateResult.DEFAULT_DOCMATCHED), mongoClientUpdateResult.getDocMatched());
    assertEquals(mongoClientUpdateResultJson.getJsonObject(MongoClientUpdateResult.UPSERTED_ID), mongoClientUpdateResult.getDocUpsertedId());
    assertEquals((long)mongoClientUpdateResultJson.getLong(MongoClientUpdateResult.DOC_MODIFIED, MongoClientUpdateResult.DEFAULT_DOCMODIFIED), mongoClientUpdateResult.getDocModified());
  }

  @Test
  public void testToJsonMongoClientUpdateResult() {
    JsonObject mongoClientUpdateResultJson = randomMongoClientUpdateResultJson();
    MongoClientUpdateResult mongoClientUpdateResult = new MongoClientUpdateResult(mongoClientUpdateResultJson);

    assertEquals(mongoClientUpdateResultJson, mongoClientUpdateResult.toJson());
  }
  
  @Test
  public void testMongoUpdateResultEquality() {
    logicallyUnequal();

    logicallyEqual();
  }

  private void logicallyEqual() {
    long randomMatched = TestUtils.randomLong();
    JsonObject randomUpsertedId = randomUpsertId();
    long randomModified = TestUtils.randomLong();
    MongoClientUpdateResult mongoClientUpdateResult1 = new MongoClientUpdateResult(randomMatched, randomUpsertedId, randomModified);
    MongoClientUpdateResult mongoClientUpdateResult2 = new MongoClientUpdateResult(randomMatched, randomUpsertedId, randomModified);
    assertTrue(mongoClientUpdateResult1.equals(mongoClientUpdateResult2));
    assertTrue(mongoClientUpdateResult2.equals(mongoClientUpdateResult1));
  }

  private void logicallyUnequal() {
    MongoClientUpdateResult mongoClientUpdateResult1 = new MongoClientUpdateResult(2333333,randomUpsertId(),66554545);
    MongoClientUpdateResult mongoClientUpdateResult2 = new MongoClientUpdateResult(52325,randomUpsertId(),6021323);
    assertFalse(mongoClientUpdateResult1.equals(mongoClientUpdateResult2));
    assertFalse(mongoClientUpdateResult2.equals(mongoClientUpdateResult1));
  }

  private JsonObject randomMongoClientUpdateResultJson() {
    JsonObject mongoClientUpdateResultJson = new JsonObject();

    mongoClientUpdateResultJson.put(MongoClientUpdateResult.DOC_MATCHED, TestUtils.randomLong());
    mongoClientUpdateResultJson.put(MongoClientUpdateResult.UPSERTED_ID, randomUpsertId());
    mongoClientUpdateResultJson.put(MongoClientUpdateResult.DOC_MODIFIED, TestUtils.randomLong());

    return mongoClientUpdateResultJson;
  }

  private JsonObject randomUpsertId() {
    return new JsonObject().put(MongoClientUpdateResult.ID_FIELD, TestUtils.randomAlphaString(23));
  }
}
