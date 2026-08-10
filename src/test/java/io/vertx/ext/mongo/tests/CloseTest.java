package io.vertx.ext.mongo.tests;

import io.vertx.core.*;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author <a href="mailto:kostya05983@mail.ru">Konstantin Volivach</a>
 */
public class CloseTest extends MongoTestBase {
  private static final JsonObject theConfig = getConfig();

  public static class SharedVerticle extends VerticleBase {

    private static volatile MongoClient clientRef;

    @Override
    public Future<?> start() {
      MongoClient client = MongoClient.create(vertx, theConfig);
      clientRef = client;
      return client.ping();
    }
  }

  @Test
  public void testCloseWhenVerticleUndeployed() throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<String> id = new CompletableFuture<>();
    vertx.deployVerticle(SharedVerticle.class.getName(), new DeploymentOptions().setInstances(1)).onComplete(onSuccess(id::complete));
    vertx.undeploy(id.get(10, TimeUnit.SECONDS)).await();
    try {
      SharedVerticle.clientRef.ping().await();
      fail();
    } catch (Exception expected) {
    }
  }
}
