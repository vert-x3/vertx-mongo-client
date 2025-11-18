package io.vertx.ext.mongo.tests;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.UUID;

@Deprecated
public class TxExample {

  public void whateva() {
    MongoClient baseClient = null;
    final UUID slipId = UUID.randomUUID();

    baseClient.createTransactionContext().flatMap(txClient ->
        txClient.insert("betslip_event", JsonObject.of("slipId", slipId))
        .compose(s -> txClient.updateCollection("betslips", JsonObject.of("slipId", slipId), JsonObject.of("story", "cool")))
        .map(r -> txClient)
        .onFailure(throwable -> txClient.abort()))
      .onSuccess(txClient -> txClient.commit())
      .onFailure(throwable -> System.err.println("BRUH " + throwable.getMessage()));
  }

}
