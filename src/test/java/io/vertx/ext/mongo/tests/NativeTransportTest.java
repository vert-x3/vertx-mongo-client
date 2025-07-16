package io.vertx.ext.mongo.tests;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.junit.Assume;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static io.vertx.core.transport.Transport.EPOLL;

public class NativeTransportTest extends MongoTestBase {

  private MongoClient mongoClient;

  @Override
  protected VertxOptions getOptions() {
    return super.getOptions().setPreferNativeTransport(true);
  }

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    Assume.assumeTrue("EPOLL Transport not available", options.getPreferNativeTransport() && EPOLL.available());
    return super.createVertxBuilder(options).withTransport(EPOLL);
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
    mongoClient.close();
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
