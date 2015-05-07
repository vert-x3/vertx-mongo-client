package io.vertx.ext.mongo;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.mongo.impl.MongoServiceImpl;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle which starts a MongoDB client and registers it to listen on the event bus.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceVerticle extends AbstractVerticle {

  MongoService service;

  @Override
  public void start() throws Exception {

    // Create the client object
    service = new MongoServiceImpl(MongoClient.createNonShared(vertx, config()));

    // And register it on the event bus against the configured address
    String address = config().getString("address");
    if (address == null) {
      throw new IllegalStateException("address field must be specified in config for client verticle");
    }
    ProxyHelper.registerService(MongoService.class, vertx, service, address);
  }

  @Override
  public void stop() throws Exception {
    service.close();
  }
}
