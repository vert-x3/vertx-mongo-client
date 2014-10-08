package io.vertx.ext.mongo.spi;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface MongoServiceFactory {

  MongoService create(Vertx vertx, JsonObject config);

  MongoService createEventBusProxy(Vertx vertx, String address);
}
