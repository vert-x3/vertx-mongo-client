package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.IO_URING;
import static io.vertx.core.transport.Transport.KQUEUE;
import static io.vertx.core.transport.Transport.NIO;

import io.vertx.core.VertxBuilder;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.transport.Transport;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies a few basic mongo operations with various combinations of transport
 * used for Vertx and MongoDB-client.
 */
@RunWith(JUnitParamsRunner.class)
public class NativeTransportTest extends MongoTestBase {

  MongoClient mongoClient;
  Transport vertxTransport;

  void setUpWithTransport(String vertxTransportName, String mongoTransportName) throws Exception {
    Transport vertxTransport = transportByName(vertxTransportName);
    Assume.assumeTrue(vertxTransportName + " vertx transport is not available, skipping test",
      vertxTransport != null && vertxTransport.available());
    Transport mongoTransport = transportByName(mongoTransportName);
    Assume.assumeTrue(mongoTransportName + " mongo transport is not available, skipping test",
      mongoTransport != null && mongoTransport.available());
    this.vertxTransport = vertxTransport;
    super.setUp(); // triggers Vertx instance creation
    JsonObject config = getConfig();
    config.put("transport", mongoTransportName);
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
  public void tearDown() throws Exception {
    if (mongoClient != null) {
      mongoClient.close();
    }
    super.tearDown();
  }

  Object[] transportCombinations() {
    return new Object[] {
      //     [vertxTransport, mongoTransport]
      new Object[] {"nio", "nio"},
      new Object[] {"nio", "epoll"},
      new Object[] {"nio", "io_uring"},
      new Object[] {"nio", "kqueue"},

      new Object[] {"epoll", "nio"},
      new Object[] {"epoll", "epoll"},
      new Object[] {"epoll", "io_uring"},
      // new Object[] {"epoll", "kqueue"}, // not possible

      new Object[] {"io_uring", "nio"},
      new Object[] {"io_uring", "epoll"},
      new Object[] {"io_uring", "io_uring"},
      // new Object[] {"io_uring", "kqueue"}, // not possible

      new Object[] {"kqueue", "nio"},
      // new Object[] {"kqueue", "epoll"}, // not possible
      // new Object[] {"kqueue", "io_uring"}, // not possible
      new Object[] {"kqueue", "kqueue"},
    };
  }

  @Test
  @Parameters(method = "transportCombinations")
  public void testFind(String vertxTransport, String mongoTransport) throws Exception {
    setUpWithTransport(vertxTransport, mongoTransport);
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

  private Transport transportByName(String transportName) {
    if (transportName.equals("epoll")) {
      return EPOLL;
    } else if (transportName.equals("nio")) {
      return NIO;
    } else if (transportName.equals("kqueue")) {
      return KQUEUE;
    } else if (transportName.equals("io_uring")) {
      return IO_URING;
    } else {
      throw new IllegalArgumentException("Unknown transport name: " + transportName);
    }
  }
}
