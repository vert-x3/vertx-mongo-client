package io.vertx.ext.mongo.tests;

import io.vertx.core.Vertx;
import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.transport.Transport;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.Arrays;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assume;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import org.junit.runner.RunWith;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.IO_URING;
import static io.vertx.core.transport.Transport.KQUEUE;
import static io.vertx.core.transport.Transport.NIO;

/**
 * Verifies a few basic mongo operations with different netty transports.
 */
@RunWith(JUnitParamsRunner.class)
public class NativeTransportTest extends MongoTestBase {

  MongoClient mongoClient;
  Transport vertxTransport;

  void setUpWithTransport(Transport vertxTransport) throws Exception {
    Assume.assumeTrue("Specific native transport is not available, skipping test",
      vertxTransport != null && vertxTransport.available());
    this.vertxTransport = vertxTransport;
    super.setUp(); // triggers Vertx instance creation
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void setUp() throws Exception {
    // do not create the Vertx instance before knowing which transport to use,
    // each test will set it up with the desired transport
  }

  @Override
  protected VertxBuilder createVertxBuilder(VertxOptions options) {
    // explicitly set transport in builder has a higher priority than
    // options.preferNativeTransport
    return super.createVertxBuilder(options)
      .withTransport(vertxTransport);
  }

  @Override
  protected Vertx createVertx(VertxOptions options) {
    // Parent's "createVertx" performs additional check on transport to prevent
    // accidental usage of transport that is not available. But this check
    // implementation relies on the global "vertx.transport" system property,
    // which is intentionally overridden in this test.
    return createVertxBuilder(options).build();
  }

  @Override
  public void tearDown() throws Exception {
    if (mongoClient != null) {
      mongoClient.close();
    }
    super.tearDown();
  }

  Iterable<?> allTransports() {
    return Arrays.asList(NIO, EPOLL, IO_URING, KQUEUE);
  }

  @Test
  @Parameters(method = "allTransports")
  public void testFind(Transport vertxTransport) throws Exception {
    setUpWithTransport(vertxTransport);
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
