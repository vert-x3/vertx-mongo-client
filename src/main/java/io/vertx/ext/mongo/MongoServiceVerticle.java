package io.vertx.ext.mongo;

import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle which starts a MongoDB service and registers it to listen on the event bus.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceVerticle extends AbstractVerticle {

  MongoService service;

  @Override
  public void start() throws Exception {

    // Create the service object
    service = MongoService.create(vertx, config());

    // And register it on the event bus against the configured address
    String address = config().getString("address");
    if (address == null) {
      throw new IllegalStateException("address field must be specified in config for service verticle");
    }
    ProxyHelper.registerService(MongoService.class, vertx, service, address);

    // Start it
    service.start();
  }

  @Override
  public void stop() throws Exception {
    service.stop();
  }
}
