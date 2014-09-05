package io.vertx.ext.mongo.impl;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.MongoServiceFactory;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceFactoryImpl implements MongoServiceFactory {

  @Override
  public MongoService create(Vertx vertx, JsonObject config) {
    return new MongoServiceImpl(vertx, config);
  }

  @Override
  public MongoService createEventBusProxy(Vertx vertx, String address) {
    return vertx.eventBus().createProxy(MongoService.class, address);
  }
}
