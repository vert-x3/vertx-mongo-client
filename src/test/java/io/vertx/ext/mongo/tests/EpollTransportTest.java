package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.EPOLL;

import io.vertx.core.transport.Transport;

public class EpollTransportTest extends NativeTransportTestBase {

  @Override
  protected Transport vertxTransport() {
    return EPOLL;
  }
}
