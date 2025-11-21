package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

@VertxGen
public interface MongoSession extends MongoClient {

  /**
   * Starts a transaction on this session instance.
   *
   * @return a future notified with the current session
   */
  Future<MongoSession> start();

  /**
   * Commits the current transaction.
   *
   * @return a future notified once complete
   */
  Future<Void> commit();

  /**
   * Aborts the current transaction.
   *
   * @return a future notified once complete
   */
  Future<Void> abort();

}
