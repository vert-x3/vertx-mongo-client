package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

@VertxGen
public interface MongoTransaction {

  Future<Void> commit();
  Future<Void> abort();

}
