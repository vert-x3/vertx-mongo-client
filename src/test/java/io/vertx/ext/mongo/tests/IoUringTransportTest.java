package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.IO_URING;

import io.vertx.core.transport.Transport;

public class IoUringTransportTest extends NativeTransportTestBase {

  @Override
  protected Transport vertxTransport() {
    return IO_URING;
  }
}
