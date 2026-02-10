package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.KQUEUE;

import io.vertx.core.transport.Transport;

public class KqueueTransportTest extends NativeTransportTestBase {

  @Override
  protected Transport vertxTransport() {
    return KQUEUE;
  }
}
