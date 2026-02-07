package io.vertx.ext.mongo.tests;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.transport.Transport;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import org.junit.Assume;
import org.junit.Test;

/**
 * Verifies a few basic mongo operations with different netty transports.
 */
public abstract class NativeTransportTestBase extends MongoTestBase {

  private MongoClient mongoClient;

  /**
   * Subclasses should use this method to specify which transport to use for the test.
   */
  protected abstract Transport vertxTransport();

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    Transport transport = vertxTransport();
    Assume.assumeTrue("Transport not available", transport != null && transport.available());
    return super.createVertxBuilder(options).withTransport(transport);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    if (mongoClient != null) {
      mongoClient.close();
    }
    super.tearDown();
  }

  @Test
  public void testFind() {
    int num = 10;
    FindOptions options = new FindOptions();
    String collection = randomCollection();
    mongoClient.createCollection(collection).onComplete(onSuccess(res -> {
      insertDocs(mongoClient, collection, num).onComplete(onSuccess(res2 -> {
        mongoClient.findWithOptions(collection, new JsonObject(), options).onComplete(onSuccess(res3 -> {
          this.assertEquals(num, res3.size());
          for (JsonObject doc : res3) {
            assertEquals(12, doc.size());
          }
          testComplete();
        }));
      }));
    }));
    await();
  }
}
