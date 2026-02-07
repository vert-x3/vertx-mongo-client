package io.vertx.ext.mongo.tests;

import static io.vertx.core.transport.Transport.NIO;

import io.vertx.core.transport.Transport;

public class NioTransportTest extends NativeTransportTestBase {

  @Override
  protected Transport vertxTransport() {
    return NIO;
  }
}
