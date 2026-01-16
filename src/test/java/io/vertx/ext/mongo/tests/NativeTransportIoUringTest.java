package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.IO_URING;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import org.junit.Assume;
import org.junit.Test;

/**
 * Verifies operation when Vertx and MongoDB client are both using IO_URING
 * native transport and the same netty event loop group.
 */
public class NativeTransportIoUringTest extends MongoTestBase {

  private MongoClient mongoClient;

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    VertxBuilder builder = super.createVertxBuilder(options);
    if (IO_URING != null && IO_URING.available()) {
      builder.withTransport(IO_URING);
    }
    return builder;
  }

  @Override
  public void setUp() throws Exception {
    Assume.assumeTrue("IO_URING Transport not available, skipping test", IO_URING != null && IO_URING.available());
    super.setUp();
    JsonObject config = getConfig();
    config.put("transport", IO_URING.name());
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
