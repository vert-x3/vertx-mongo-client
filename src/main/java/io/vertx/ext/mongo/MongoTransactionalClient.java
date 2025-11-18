package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

@VertxGen
public interface MongoTransactionalClient extends MongoClient {

  void start();
  Future<Void> commit();
  Future<Void> abort();

}
