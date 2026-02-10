package io.vertx.ext.mongo.tests.impl.config;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.IO_URING;
import static io.vertx.core.transport.Transport.KQUEUE;
import static io.vertx.core.transport.Transport.NIO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.NettyTransportSettings;
import io.netty.channel.EventLoopGroup;
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

  private Vertx vertx;

  void setUpWithTransport(Transport vertxTransport) {
    Assume.assumeTrue("Specific native transport is not available, skipping test",
      vertxTransport != null && vertxTransport.available());
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

  Object[] allTransports() {
    return new Object[] {
      //     [vertxTransport, expectedSocketChannelClass]
      new Object[] {NIO, "io.netty.channel.socket.nio.NioSocketChannel"},
      new Object[] {EPOLL, "io.netty.channel.epoll.EpollSocketChannel"},
      new Object[] {IO_URING, "io.netty.channel.uring.IoUringSocketChannel"},
      new Object[] {KQUEUE, "io.netty.channel.kqueue.KQueueSocketChannel"},
    };
  }

  @Test
  @Parameters(method = "allTransports")
  public void testTransportSettings(Transport vertxTransport, String expectedSocketChannelClass) throws Exception {
    // given: vertx configured to use specific mongoTransport
    setUpWithTransport(vertxTransport);
    JsonObject config = new JsonObject()
      .put("connection_string", "mongodb://localhost:27017/")
      .put("db_name", "my_db");

    // when: parse the mongo settings
    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    MongoClientSettings parsedSettings = parser.settings();

    // then: parsed settings have the corresponding transport channel class
    NettyTransportSettings parsedTransportSettings =
      (NettyTransportSettings) parsedSettings.getTransportSettings();
    assertNotNull(parsedTransportSettings);
    assertNotNull(parsedTransportSettings.getSocketChannelClass());
    String parsedSocketChannelClass = parsedTransportSettings.getSocketChannelClass().getCanonicalName();
    assertEquals(expectedSocketChannelClass, parsedSocketChannelClass);

    // and: parsed settings have the same event loop group instance as vertx
    EventLoopGroup vertxEventLoop = ((VertxInternal) vertx).nettyEventLoopGroup();
    assertEquals(vertxEventLoop, parsedTransportSettings.getEventLoopGroup());
  }
}
