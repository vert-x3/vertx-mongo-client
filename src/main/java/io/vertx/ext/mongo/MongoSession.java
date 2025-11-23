package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

import java.util.function.Function;

@VertxGen
public interface MongoSession {

  /**
   * Executes the specified function in a distributed transaction.
   *
   * @param operations     the operations to execute inside the transaction
   * @param <T>            the return type from the operations function
   *
   * @return a future notified with the result of operations
   */
  <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations);

  /**
   * Executes the specified function in a distributed transaction.
   * The specified {@link TransactionOptions} will be applied to the session and all transactions.
   *
   * @param options     options to use for the transaction
   * @param operations  the operations to execute inside the transaction
   * @param <T>         the return type from the operations function
   *
   * @return a future notified with the result of operations
   */
  <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations, TransactionOptions options);

  /**
   * Starts a transaction.
   *
   * @return a future notified once complete
   */
  Future<Void> start();

  /**
   * Starts a transaction.
   *
   * @return a future notified once complete
   */
  Future<Void> start(TransactionOptions transactionOptions);

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

  /**
   * Close the session and release its resources
   */
  Future<Void> close();

}
