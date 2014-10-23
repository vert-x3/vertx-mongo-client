package io.vertx.ext.mongo.test;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceTest extends MongoServiceTestBase {


  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoService = MongoService.create(vertx, config);
    mongoService.start();
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoService.stop();
    super.tearDown();
  }


}
