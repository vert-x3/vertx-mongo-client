package io.vertx.ext.mongo.tests;

import static org.junit.Assert.*;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:maziz.esa@gmail.com">Maziz Esa</a>
 */
public class MongoClientDeleteResultTest {

  @Test
  public void testMongoClientDeleteResultWithRemovedCountSpecified() {
    long randomRemoved = TestUtils.randomLong();
    MongoClientDeleteResult mongoClientDeleteResult = new MongoClientDeleteResult(randomRemoved);

    assertEquals(randomRemoved, mongoClientDeleteResult.getRemovedCount());
  }

  @Test
  public void testDefaultMongoClientDeleteResult() {
    MongoClientDeleteResult mongoClientDeleteResult = new MongoClientDeleteResult();
    assertEquals(MongoClientDeleteResult.DEFAULT_REMOVEDCOUNT, mongoClientDeleteResult.getRemovedCount());
  }

  @Test
  public void testCopyMongoClientDeleteResult() {
    MongoClientDeleteResult origin = new MongoClientDeleteResult(TestUtils.randomLong());
    MongoClientDeleteResult copy = new MongoClientDeleteResult(origin);

    assertEquals(origin.getRemovedCount(), copy.getRemovedCount());
  }

  @Test
  public void testJsonMongoClientDeleteResult() {
    properJson();

    jsonWithoutRequiredField();
  }

  private void jsonWithoutRequiredField() {
    JsonObject jsonObject = new JsonObject();
    MongoClientDeleteResult mongoClientDeleteResult = new MongoClientDeleteResult(jsonObject);

    assertEquals(MongoClientDeleteResult.DEFAULT_REMOVEDCOUNT, mongoClientDeleteResult.getRemovedCount());
  }

  private void properJson() {
    JsonObject randomMongoClientDeleteResultJson = randomMongoClientDeleteResultJson();
    MongoClientDeleteResult mongoClientDeleteResult = new MongoClientDeleteResult(randomMongoClientDeleteResultJson);

    assertEquals((long)randomMongoClientDeleteResultJson.getLong(MongoClientDeleteResult.REMOVED_COUNT,0l), mongoClientDeleteResult.getRemovedCount());
  }

  @Test
  public void testToJsonMongoClientDeleteResult() {
    JsonObject randomMongoClientDeleteJson = randomMongoClientDeleteResultJson();
    MongoClientDeleteResult mongoClientDeleteResult = new MongoClientDeleteResult(randomMongoClientDeleteJson);

    assertEquals(randomMongoClientDeleteJson, mongoClientDeleteResult.toJson());
  }


  @Test
  public void testMongoClientDeleteResultEquality() {

    logicallyEqual();

    logicallyUnequal();

  }

  private void logicallyUnequal() {
    MongoClientDeleteResult mongoClientDeleteResult1 = new MongoClientDeleteResult(23);
    MongoClientDeleteResult mongoClientDeleteResult2 = new MongoClientDeleteResult(55);
    assertFalse(mongoClientDeleteResult1.equals(mongoClientDeleteResult2));
    assertFalse(mongoClientDeleteResult2.equals(mongoClientDeleteResult1));
  }

  private void logicallyEqual() {
    long randomRemoved = TestUtils.randomLong();
    MongoClientDeleteResult mongoClientDeleteResult1 = new MongoClientDeleteResult(randomRemoved);
    MongoClientDeleteResult mongoClientDeleteResult2 = new MongoClientDeleteResult(randomRemoved);
    assertTrue(mongoClientDeleteResult1.equals(mongoClientDeleteResult2));
    assertTrue(mongoClientDeleteResult2.equals(mongoClientDeleteResult1));
  }

  private static JsonObject randomMongoClientDeleteResultJson(){
    JsonObject jsonObject = new JsonObject();
    jsonObject.put(MongoClientDeleteResult.REMOVED_COUNT, TestUtils.randomLong());

    return jsonObject;
  }
}
