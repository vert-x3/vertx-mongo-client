package io.vertx.ext.mongo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceVerticle extends AbstractVerticle {

  private MongoService service;

  @Override
  public void start() throws Exception {
    // Create the service
    JsonObject config = vertx.currentContext().config();
    service = MongoService.create(vertx, config);
    service.start();
    String address = config.getString("address", "vertx.mongodb");
    vertx.eventBus().registerService(service, address);
  }

}
