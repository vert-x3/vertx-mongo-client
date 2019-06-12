package io.vertx.ext.mongo;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author <a href="mailto:kostya05983@mail.ru">Konstantin Volivach</a>
 */
public class CloseTest extends MongoClientTestBase {
  private static final JsonObject theConfig = getConfig();

  @Override
  public void setUp() throws Exception{
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  public static class SharedVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startFuture) {
      MongoClient client = MongoClient.createNonShared(vertx, theConfig);
      startFuture.complete();
    }
  }

  @Test
  public void testCloseWhenVerticleUndeployed() throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<String> id = new CompletableFuture<>();
    vertx.deployVerticle(SharedVerticle.class.getName(), new DeploymentOptions().setInstances(1), onSuccess(id::complete));

    close(id.get(10, TimeUnit.SECONDS));
  }

  private void close(String deploymentId) throws InterruptedException {
    CountDownLatch closeLatch = new CountDownLatch(1);
    vertx.undeploy(deploymentId, onSuccess(v -> {
      closeLatch.countDown();
    }));
    awaitLatch(closeLatch);
  }
}
