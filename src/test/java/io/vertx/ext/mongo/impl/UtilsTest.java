package io.vertx.ext.mongo.impl;

import com.mongodb.bulk.BulkWriteInsert;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.BulkWriteUpsert;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.test.core.TestUtils;
import org.bson.BsonString;
import org.junit.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:nikolakofistariji@gmail.com">Nikola Kosanovic</a>
 */
public class UtilsTest
{
  @Test
  public void noAcknowledgedTest() {
    BulkWriteResult writeResult = stubBulkWriteResult(false);
    MongoClientBulkWriteResult result = Utils.toMongoClientBulkWriteResult(writeResult);
    assertNull(result);
  }

  @Test
  public void mapperTest() {
    BulkWriteResult writeResult = stubBulkWriteResult(true);
    MongoClientBulkWriteResult result = Utils.toMongoClientBulkWriteResult(writeResult);
    assertEquals(result.getDeletedCount(), writeResult.getDeletedCount());
    assertEquals(result.getInsertedCount(), writeResult.getInsertedCount());
    assertEquals(result.getMatchedCount(), writeResult.getMatchedCount());
    assertEquals(result.getModifiedCount(), writeResult.getModifiedCount());

    assertEquals(result.getUpserts().get(0).getInteger(MongoClientBulkWriteResult.INDEX), Integer.valueOf(writeResult.getUpserts().get(0).getIndex()));
    assertEquals(result.getUpserts().get(0).getString(MongoClientBulkWriteResult.ID), writeResult.getUpserts().get(0).getId().asString().getValue());

    assertEquals(result.getInserts().get(0).getInteger(MongoClientBulkWriteResult.INDEX), Integer.valueOf(writeResult.getInserts().get(0).getIndex()));
    assertEquals(result.getInserts().get(0).getString(MongoClientBulkWriteResult.ID), writeResult.getInserts().get(0).getId().asString().getValue());
  }

  BulkWriteResult stubBulkWriteResult(boolean wasAcknowledged) {
    int counter = TestUtils.randomInt();
    String randomInsertId = TestUtils.randomAlphaString(32);
    String randomUpsertId = TestUtils.randomAlphaString(32);

    return new BulkWriteResult()
    {
      @Override
      public boolean wasAcknowledged() {
        return wasAcknowledged;
      }

      @Override
      public int getInsertedCount() {
        return counter;
      }

      @Override
      public int getMatchedCount() {
        return counter;
      }

      @Override
      public int getDeletedCount() {
        return counter;
      }

      @Override
      public int getModifiedCount() {
        return counter;
      }

      @Override
      public List<BulkWriteInsert> getInserts() {
        return Collections.singletonList(new BulkWriteInsert(0, new BsonString(randomInsertId)));
      }

      @Override
      public List<BulkWriteUpsert> getUpserts() {
        return Collections.singletonList(new BulkWriteUpsert(0, new BsonString(randomUpsertId)));
      }
    };
  }
}
