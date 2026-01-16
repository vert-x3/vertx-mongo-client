package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.NIO;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Verifies operation when Vertx uses NIO java transport and the MongoDB driver
 * uses EPOLL native transport and a separate netty event loop group
 * managed by the driver.
 */
@Ignore
public class NativeTransportSeparateELEpollTest extends MongoTestBase {
  private MongoClient mongoClient;

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    return super.createVertxBuilder(options)
      .withTransport(NIO);
  }

  @Override
  public void setUp() throws Exception {
    Assume.assumeTrue("EPOLL Transport not available, skipping test", EPOLL != null && EPOLL.available());
    super.setUp();
    JsonObject config = getConfig();
    config.put("transport", EPOLL.name());
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
