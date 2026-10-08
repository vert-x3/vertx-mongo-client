package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

import java.util.function.Function;

/**
 * A client session, used to execute multi-document transactions.
 * <p>
 * A session can run several sequential transactions and stays open until {@link #close()} is called.
 * Operations on a session must be issued sequentially, and only with the {@link MongoClient} that
 * created the session.
 */
@VertxGen
public interface MongoSession {

  /**
   * @return a {@link MongoClient} bound to this session; operations invoked on it execute in the scope
   *         of this session and of its active transaction, if any. Closing the returned client is a no-op.
   */
  MongoClient client();

  /**
   * Executes the operations in a transaction: commits on success, aborts on failure, and retries
   * transient errors and unknown commit results as recommended by MongoDB.
   * Fails if a transaction is already active on this session.
   *
   * @param operations the operations to execute inside the transaction
   * @param <T>        the return type of the operations function
   * @return a future notified with the result of operations
   */
  <T> Future<@Nullable T> withTransaction(Function<MongoClient, Future<@Nullable T>> operations);

  /**
   * Like {@link #withTransaction(Function)} with the specified {@link TransactionOptions}.
   */
  <T> Future<@Nullable T> withTransaction(Function<MongoClient, Future<@Nullable T>> operations, TransactionOptions options);

  /**
   * Manually starts a transaction, scoping all subsequent {@link #client()} operations to it until
   * {@link #commit()} or {@link #abort()} is called.
   *
   * @return a future notified once complete
   */
  Future<Void> startTransaction();

  /**
   * Like {@link #startTransaction()} with the specified {@link TransactionOptions}.
   */
  Future<Void> startTransaction(TransactionOptions transactionOptions);

  /**
   * Commits the active transaction.
   *
   * @return a future notified once complete
   */
  Future<Void> commit();

  /**
   * Aborts the active transaction, if any.
   *
   * @return a future notified once complete
   */
  Future<Void> abort();

  /**
   * Closes the session and releases its resources. An active transaction is aborted by the server.
   */
  Future<Void> close();

}
