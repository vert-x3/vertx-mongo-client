package io.vertx.ext.mongo.tests.impl.config;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.IO_URING;
import static io.vertx.core.transport.Transport.KQUEUE;
import static io.vertx.core.transport.Transport.NIO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.NettyTransportSettings;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.internal.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.transport.Transport;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import java.util.concurrent.CompletableFuture;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class MongoClientOptionsParserTransportTest {
  private static final String NIO_SOCKET_CHANNEL_CLASS = "io.netty.channel.socket.nio.NioSocketChannel";
  private static final String EPOLL_SOCKET_CHANNEL_CLASS = "io.netty.channel.epoll.EpollSocketChannel";
  private static final String IO_URING_SOCKET_CHANNEL_CLASS = "io.netty.channel.uring.IoUringSocketChannel";
  private static final String KQUEUE_SOCKET_CHANNEL_CLASS = "io.netty.channel.kqueue.KQueueSocketChannel";

  private Vertx vertx;

  void setUpWithTransport(String vertxTransportName, String mongoTransportName) throws Exception {
    Transport vertxTransport = transportByName(vertxTransportName);
    Assume.assumeTrue(vertxTransportName + " vertx transport is not available, skipping test",
      vertxTransport != null && vertxTransport.available());
    if (mongoTransportName != null && !mongoTransportName.equals("auto")) {
      Transport mongoTransport = transportByName(mongoTransportName);
      Assume.assumeTrue(mongoTransportName + " mongo transport is not available, skipping test",
        mongoTransport != null && mongoTransport.available());
    }
    vertx = Vertx.builder().withTransport(vertxTransport).build();
  }

  @After
  public void tearDown() {
    if (vertx != null) {
      CompletableFuture<Void> closeFuture = new CompletableFuture<>();
      vertx.close().onComplete(ar -> {
        if (ar.succeeded()) {
          closeFuture.complete(null);
        } else {
          closeFuture.completeExceptionally(ar.cause());
        }
      });
      closeFuture.join();
    }
  }

  Object[] transportCombinations() {
    return new Object[] {
      //     [vertxTransport, mongoTransport, expectedSocketChannelClass]
      new Object[] {"nio", "nio", NIO_SOCKET_CHANNEL_CLASS},
      new Object[] {"nio", "epoll", EPOLL_SOCKET_CHANNEL_CLASS},
      new Object[] {"nio", "io_uring", IO_URING_SOCKET_CHANNEL_CLASS},
      new Object[] {"nio", "kqueue", KQUEUE_SOCKET_CHANNEL_CLASS},
      new Object[] {"nio", "auto", NIO_SOCKET_CHANNEL_CLASS},
      new Object[] {"nio", null, NIO_SOCKET_CHANNEL_CLASS},

      new Object[] {"epoll", "nio", NIO_SOCKET_CHANNEL_CLASS},
      new Object[] {"epoll", "epoll", EPOLL_SOCKET_CHANNEL_CLASS},
      new Object[] {"epoll", "io_uring", IO_URING_SOCKET_CHANNEL_CLASS},
      // new Object[] {"epoll", "kqueue"}, // not possible
      new Object[] {"epoll", "auto", EPOLL_SOCKET_CHANNEL_CLASS},
      new Object[] {"epoll", null, EPOLL_SOCKET_CHANNEL_CLASS},

      new Object[] {"io_uring", "nio", NIO_SOCKET_CHANNEL_CLASS},
      new Object[] {"io_uring", "epoll", EPOLL_SOCKET_CHANNEL_CLASS},
      new Object[] {"io_uring", "io_uring", IO_URING_SOCKET_CHANNEL_CLASS},
      // new Object[] {"io_uring", "kqueue"}, // not possible
      new Object[] {"io_uring", "auto", IO_URING_SOCKET_CHANNEL_CLASS},
      new Object[] {"io_uring", null, IO_URING_SOCKET_CHANNEL_CLASS},

      new Object[] {"kqueue", "nio", NIO_SOCKET_CHANNEL_CLASS},
      // new Object[] {"kqueue", "epoll"}, // not possible
      // new Object[] {"kqueue", "io_uring"}, // not possible
      new Object[] {"kqueue", "kqueue", KQUEUE_SOCKET_CHANNEL_CLASS},
      new Object[] {"kqueue", "auto", KQUEUE_SOCKET_CHANNEL_CLASS},
      new Object[] {"kqueue", null, KQUEUE_SOCKET_CHANNEL_CLASS},
    };
  }

  @Test
  @Parameters(method = "transportCombinations")
  public void testTransportSettings(String vertxTransport, String mongoTransport,
    String expectedSocketChannelClass) throws Exception {
    // given: mongo client settings json configured to use mongoTransport
    setUpWithTransport(vertxTransport, mongoTransport);
    String connectionString = "mongodb://localhost:27017/";
    JsonObject config = new JsonObject()
      .put("connection_string", connectionString)
      .put("db_name", "my_db");
    if (mongoTransport != null) {
      config.put("transport", mongoTransport);
    }

    // when: parse the mongo settings
    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    MongoClientSettings parsedSettings = parser.settings();

    // then: mongo settings are configured with the channel class for the requested transport
    NettyTransportSettings parsedTransportSettings =
      (NettyTransportSettings) parsedSettings.getTransportSettings();
    assertNotNull(parsedTransportSettings);
    assertNotNull(parsedTransportSettings.getSocketChannelClass());
    String parsedSocketChannelClass = parsedTransportSettings.getSocketChannelClass().getCanonicalName();
    assertEquals(expectedSocketChannelClass, parsedSocketChannelClass);

    // and: mongo settings are configured to reuse vertx event-loop if transport matches,
    //  otherwise a new event-loop is created
    boolean shouldReuseEventLoop = (vertxTransport.equals(mongoTransport)) ||
      (mongoTransport == null) || (mongoTransport.equals("auto"));
    EventLoopGroup vertxEventLoop = ((VertxInternal) vertx).nettyEventLoopGroup();
    if (shouldReuseEventLoop) {
      assertEquals(vertxEventLoop, parsedTransportSettings.getEventLoopGroup());
    } else {
      assertNotNull(parsedTransportSettings.getEventLoopGroup());
      assertNotEquals(vertxEventLoop, parsedTransportSettings.getEventLoopGroup());

      // and: the newly created event-loop has the same size as the vertx event-loop
      int vertxEventLoopSize = 0;
      for (EventExecutor el : vertxEventLoop) {
        vertxEventLoopSize++;
      }
      int mongoEventLoopSize = 0;
      for (EventExecutor el : parsedTransportSettings.getEventLoopGroup()) {
        mongoEventLoopSize++;
      }
      assertEquals(vertxEventLoopSize, mongoEventLoopSize);
    }
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
