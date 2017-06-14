package io.vertx.ext.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;

/**
 * @author sschmitt
 */
public class MongoClientBulkWriteResultTest {

  @Test
  public void testMongoClientBulkWriteStatuses() {
    long randomMatched = TestUtils.randomLong();
    long randomModified = TestUtils.randomLong();
    long randomInserted = TestUtils.randomLong();
    long randomDeleted = TestUtils.randomLong();
    List<JsonObject> upserts = randomUpsertIds();

    MongoClientBulkWriteResult mongoClientBulkWriteResult = new MongoClientBulkWriteResult(randomInserted,
        randomMatched, randomDeleted, randomModified, upserts);

    assertEquals(randomMatched, mongoClientBulkWriteResult.getMatchedCount());
    assertEquals(randomModified, mongoClientBulkWriteResult.getModifiedCount());
    assertEquals(randomInserted, mongoClientBulkWriteResult.getInsertedCount());
    assertEquals(randomDeleted, mongoClientBulkWriteResult.getDeletedCount());
    assertEquals(upserts, mongoClientBulkWriteResult.getUpserts());
  }

  @Test
  public void testDefaultMongoClientBulkWriteResult() {
    MongoClientBulkWriteResult mongoClientBulkWriteResult = new MongoClientBulkWriteResult();

    assertEquals(MongoClientBulkWriteResult.DEFAULT_MATCHED_COUNT, mongoClientBulkWriteResult.getMatchedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_MODIFIED_COUNT, mongoClientBulkWriteResult.getModifiedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_INSERTED_COUNT, mongoClientBulkWriteResult.getInsertedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_DELETED_COUNT, mongoClientBulkWriteResult.getDeletedCount());
    assertNull(mongoClientBulkWriteResult.getUpserts());
  }

  @Test
  public void testCopyMongoClientBulkWriteResult() {
    MongoClientBulkWriteResult mongoClientBulkWriteResultOrigin = new MongoClientBulkWriteResult(TestUtils.randomLong(),
        TestUtils.randomLong(), TestUtils.randomLong(), TestUtils.randomLong(), randomUpsertIds());

    MongoClientBulkWriteResult mongoClientBulkWriteResultCopy = new MongoClientBulkWriteResult(
        mongoClientBulkWriteResultOrigin);

    assertEquals(mongoClientBulkWriteResultCopy.getMatchedCount(), mongoClientBulkWriteResultOrigin.getMatchedCount());
    assertEquals(mongoClientBulkWriteResultCopy.getModifiedCount(),
        mongoClientBulkWriteResultOrigin.getModifiedCount());
    assertEquals(mongoClientBulkWriteResultCopy.getInsertedCount(),
        mongoClientBulkWriteResultOrigin.getInsertedCount());
    assertEquals(mongoClientBulkWriteResultCopy.getDeletedCount(), mongoClientBulkWriteResultOrigin.getDeletedCount());
    assertEquals(mongoClientBulkWriteResultCopy.getUpserts(), mongoClientBulkWriteResultOrigin.getUpserts());
  }

  @Test
  public void testJsonMongoClientBulkWriteResult() {
    properJson();

    jsonWithoutRequiredFields();
  }

  private void jsonWithoutRequiredFields() {
    JsonObject mongoClientBulkWriteResultJson = new JsonObject();
    MongoClientBulkWriteResult mongoClientBulkWriteResult = new MongoClientBulkWriteResult(
        mongoClientBulkWriteResultJson);

    assertEquals(MongoClientBulkWriteResult.DEFAULT_MATCHED_COUNT, mongoClientBulkWriteResult.getMatchedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_MODIFIED_COUNT, mongoClientBulkWriteResult.getModifiedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_INSERTED_COUNT, mongoClientBulkWriteResult.getInsertedCount());
    assertEquals(MongoClientBulkWriteResult.DEFAULT_DELETED_COUNT, mongoClientBulkWriteResult.getDeletedCount());
    assertNull(mongoClientBulkWriteResult.getUpserts());
  }

  private void properJson() {
    JsonObject mongoClientBulkWriteResultJson = randomMongoClientBulkWriteResultJson();
    MongoClientBulkWriteResult mongoClientBulkWriteResult = new MongoClientBulkWriteResult(
        mongoClientBulkWriteResultJson);

    assertEquals((long) mongoClientBulkWriteResultJson.getLong(MongoClientBulkWriteResult.DELETED_COUNT,
        MongoClientBulkWriteResult.DEFAULT_DELETED_COUNT), mongoClientBulkWriteResult.getDeletedCount());
    assertEquals((long) mongoClientBulkWriteResultJson.getLong(MongoClientBulkWriteResult.INSERTED_COUNT,
        MongoClientBulkWriteResult.DEFAULT_INSERTED_COUNT), mongoClientBulkWriteResult.getInsertedCount());
    assertEquals((long) mongoClientBulkWriteResultJson.getLong(MongoClientBulkWriteResult.MATCHED_COUNT,
        MongoClientBulkWriteResult.DEFAULT_MATCHED_COUNT), mongoClientBulkWriteResult.getMatchedCount());
    assertEquals((long) mongoClientBulkWriteResultJson.getLong(MongoClientBulkWriteResult.MODIFIED_COUNT,
        MongoClientBulkWriteResult.DEFAULT_MODIFIED_COUNT), mongoClientBulkWriteResult.getModifiedCount());

    JsonArray upserts = mongoClientBulkWriteResultJson.getJsonArray(MongoClientBulkWriteResult.UPSERTS, null);
    assertEquals(upserts != null ? upserts.getList() : null, mongoClientBulkWriteResult.getUpserts());

  }

  @Test
  public void testToJsonMongoClientBulkWriteResult() {
    JsonObject mongoClientBulkWriteResultJson = randomMongoClientBulkWriteResultJson();
    MongoClientBulkWriteResult mongoClientBulkWriteResult = new MongoClientBulkWriteResult(
        mongoClientBulkWriteResultJson);

    assertEquals(mongoClientBulkWriteResultJson, mongoClientBulkWriteResult.toJson());
  }

  @Test
  public void testMongoBulkWriteResultEquality() {
    logicallyUnequal();

    logicallyEqual();
  }

  private void logicallyEqual() {
    long randomMatched = TestUtils.randomLong();
    long randomModified = TestUtils.randomLong();
    long randomInserted = TestUtils.randomLong();
    long randomDeleted = TestUtils.randomLong();
    List<JsonObject> upserts = randomUpsertIds();

    MongoClientBulkWriteResult mongoClientBulkWriteResult1 = new MongoClientBulkWriteResult(randomInserted,
        randomMatched, randomDeleted, randomModified, upserts);
    MongoClientBulkWriteResult mongoClientBulkWriteResult2 = new MongoClientBulkWriteResult(randomInserted,
        randomMatched, randomDeleted, randomModified, upserts);

    assertTrue(mongoClientBulkWriteResult1.equals(mongoClientBulkWriteResult2));
    assertTrue(mongoClientBulkWriteResult2.equals(mongoClientBulkWriteResult1));
  }

  private void logicallyUnequal() {
    MongoClientBulkWriteResult mongoClientBulkWriteResult1 = new MongoClientBulkWriteResult(123, 456, 789, 135,
        randomUpsertIds());
    MongoClientBulkWriteResult mongoClientBulkWriteResult2 = new MongoClientBulkWriteResult(456, 789, 135, 123,
        randomUpsertIds());

    assertFalse(mongoClientBulkWriteResult1.equals(mongoClientBulkWriteResult2));
    assertFalse(mongoClientBulkWriteResult2.equals(mongoClientBulkWriteResult1));
  }

  private JsonObject randomMongoClientBulkWriteResultJson() {
    JsonObject mongoClientBulkWriteResultJson = new JsonObject();

    mongoClientBulkWriteResultJson.put(MongoClientBulkWriteResult.DELETED_COUNT, TestUtils.randomLong());
    mongoClientBulkWriteResultJson.put(MongoClientBulkWriteResult.INSERTED_COUNT, TestUtils.randomLong());
    mongoClientBulkWriteResultJson.put(MongoClientBulkWriteResult.MATCHED_COUNT, TestUtils.randomLong());
    mongoClientBulkWriteResultJson.put(MongoClientBulkWriteResult.MODIFIED_COUNT, TestUtils.randomLong());
    mongoClientBulkWriteResultJson.put(MongoClientBulkWriteResult.UPSERTS, randomUpsertIds());

    return mongoClientBulkWriteResultJson;
  }

  private List<JsonObject> randomUpsertIds() {
    return Arrays.asList(randomUpsertId(), randomUpsertId());
  }

  private JsonObject randomUpsertId() {
    return new JsonObject().put(MongoClientBulkWriteResult.ID, TestUtils.randomAlphaString(23))
        .put(MongoClientBulkWriteResult.INDEX, TestUtils.randomInt());
  }
}
