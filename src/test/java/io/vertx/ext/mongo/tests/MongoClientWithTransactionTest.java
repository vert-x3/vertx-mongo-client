package io.vertx.ext.mongo.tests;

import com.mongodb.MongoClientSettings;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoTransaction;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class MongoClientWithTransactionTest extends MongoClientTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    config.put("useObjectId", true);
    useObjectId = true;
    final MongoClientSettings settings = new MongoClientOptionsParser(vertx, config).settings();
    mongoClient = MongoClient.createWithMongoSettings(vertx, config, UUID.randomUUID().toString(), settings);
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
    JsonObject config = MongoClientTestBase.getConfig();
    config.put("useObjectId", true);
    return config;
  }

  @Test
  public void testReplaceUpsert() {
    String collection = randomCollection();
    String collection2 = randomCollection();

    mongoClient.createTransaction()
      .flatMap(MongoTransaction::start)
      .onComplete(onSuccess(tx -> {
        JsonObject doc = createDoc();
        JsonObject doc2 = createDoc();

        tx.insert(collection, doc).onComplete(onSuccess(id -> {
          assertNotNull(id);

          tx.insert(collection2, doc2).onComplete(onSuccess(id2 -> {
            assertNotNull(id2);
            tx.commit()
              .onComplete(onSuccess(v -> {
                assertNull(v);
                testComplete();
              }));
          }));
        }));
      }));

    await();
  }
}
