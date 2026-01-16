package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.NIO;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore
public class DeleteMeNativeTransportTest extends MongoTestBase {
  private MongoClient mongoClient;

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    return super.createVertxBuilder(options)
      .withTransport(EPOLL);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    config.put("transport", NIO.name());
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
    mongoClient.createCollection(collection).onComplete(ar -> {
      System.out.println("createCollection complete: " + ar.succeeded());
      System.out.println("err: " + ar.cause());
      testComplete();
    });
    await();
  }
}
