package io.vertx.ext.mongo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface MongoServiceFactory {

  MongoService create(Vertx vertx, JsonObject config);

  MongoService createEventBusProxy(Vertx vertx, String address);
}
