/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo.impl;

import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Closeable;
import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.internal.ContextInternal;
import io.vertx.core.internal.VertxInternal;
import io.vertx.ext.mongo.ClientSessionOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoSession;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MongoSessionImpl implements MongoSession, Closeable {

  private static final long DEFAULT_TRANSACTION_TIMEOUT_MS = 120_000;
  private static final String TRANSIENT_TRANSACTION_ERROR = "TransientTransactionError";
  private static final String UNKNOWN_TRANSACTION_COMMIT_RESULT = "UnknownTransactionCommitResult";

  private final ContextInternal creatingContext;
  private final VertxInternal vertx;

  private final MongoClient delegate;
  private final TransactionOptions defaultTransactionOptions;
  private final Long defaultTimeoutMillis;
  private final boolean snapshot;
  private final ClientSession session;
  private volatile boolean inTransaction;
  private volatile boolean isClosed;

  public MongoSessionImpl(ContextInternal creatingContext, MongoClient delegate, ClientSession session, ClientSessionOptions sessionOptions) {
    Objects.requireNonNull(creatingContext);
    Objects.requireNonNull(delegate);
    Objects.requireNonNull(session);
    this.creatingContext = creatingContext;
    this.vertx = creatingContext.owner();
    this.delegate = delegate;
    this.session = session;
    this.defaultTransactionOptions = ((sessionOptions != null) && (sessionOptions.getDefaultTransactionOptions() != null))
      ? sessionOptions.getDefaultTransactionOptions().toMongoDriverObject()
      : null;
    this.defaultTimeoutMillis = sessionOptions != null ? sessionOptions.getDefaultTimeoutMillis() : null;
    this.snapshot = sessionOptions != null && Boolean.TRUE.equals(sessionOptions.getSnapshot());

    creatingContext.addCloseHook(this);

    this.inTransaction = false;
    this.isClosed = false;
  }

  @Override
  public MongoClient client() {
    return delegate;
  }

  @Override
  public <T> Future<@Nullable T> withTransaction(Function<MongoClient, Future<@Nullable T>> operations) {
    return withTransaction(operations, defaultTransactionOptions);
  }

  @Override
  public <T> Future<@Nullable T> withTransaction(Function<MongoClient, Future<@Nullable T>> operations, io.vertx.ext.mongo.TransactionOptions options) {
    return withTransaction(operations, options != null ? options.toMongoDriverObject() : defaultTransactionOptions);
  }

  private <T> Future<@Nullable T> withTransaction(Function<MongoClient, Future<@Nullable T>> operations, TransactionOptions options) {
    Objects.requireNonNull(operations, "operations cannot be null");
    if (isClosed) {
      return sessionClosed();
    }

    if (inTransaction) {
      return Future.failedFuture(new IllegalStateException(
        "A transaction is already active on this session. Use client() with startTransaction()/commit()/abort() for manual control."));
    }

    long deadline = System.currentTimeMillis() + retryTimeoutMillis(options);
    return attemptTransaction(operations, options, deadline);
  }

  private long retryTimeoutMillis(TransactionOptions options) {
    Long timeout = options != null ? options.getTimeout(TimeUnit.MILLISECONDS) : null;
    if (timeout == null) {
      timeout = defaultTimeoutMillis;
    }
    return timeout != null && timeout > 0 ? timeout : DEFAULT_TRANSACTION_TIMEOUT_MS;
  }

  private <T> Future<@Nullable T> attemptTransaction(Function<MongoClient, Future<@Nullable T>> operations,
                                                     TransactionOptions options,
                                                     long deadline) {
    return startTransaction(options).compose(started -> {
      Future<T> opsFuture = getOperationsFuture(operations);
      if (opsFuture == null) {
        opsFuture = Future.failedFuture(new IllegalStateException("The operations function returned a null Future"));
      }
      return opsFuture.compose(
        result -> commitWithRetry(deadline).compose(
          committed -> Future.succeededFuture(result),
          commitErr -> retryOrFail(operations, options, deadline, commitErr)),
        err -> abort().transform(abortResult -> retryOrFail(operations, options, deadline, err)));
    });
  }

  private <T> Future<@Nullable T> getOperationsFuture(Function<MongoClient, Future<@Nullable T>> operations) {
    try {
      return operations.apply(delegate);
    } catch (Throwable t) {
      return Future.failedFuture(t);
    }
  }

  private <T> Future<@Nullable T> retryOrFail(Function<MongoClient, Future<@Nullable T>> operations,
                                              TransactionOptions options,
                                              long deadline,
                                              Throwable err) {
    if (hasErrorLabel(err, TRANSIENT_TRANSACTION_ERROR) && System.currentTimeMillis() < deadline) {
      return attemptTransaction(operations, options, deadline);
    }
    return Future.failedFuture(err);
  }

  private Future<Void> commitWithRetry(long deadline) {
    return commit().recover(err -> {
      if (hasErrorLabel(err, UNKNOWN_TRANSACTION_COMMIT_RESULT) && System.currentTimeMillis() < deadline) {
        return commitWithRetry(deadline);
      }
      return Future.failedFuture(err);
    });
  }

  private boolean hasErrorLabel(Throwable t, String label) {
    return t instanceof MongoException && ((MongoException) t).hasErrorLabel(label);
  }

  @Override
  public Future<Void> startTransaction() {
    return startTransaction((TransactionOptions) null);
  }

  @Override
  public Future<Void> startTransaction(io.vertx.ext.mongo.TransactionOptions transactionOptions) {
    return startTransaction(transactionOptions != null ? transactionOptions.toMongoDriverObject() : null);
  }

  private Future<Void> startTransaction(TransactionOptions transactionOptions) {
    if (isClosed) {
      return sessionClosed();
    }
    if (snapshot) {
      return Future.failedFuture(new IllegalStateException("Snapshot sessions are read-only and do not support transactions"));
    }
    if (inTransaction) {
      return alreadyHasTransaction();
    }

    try {
      if (transactionOptions != null) {
        session.startTransaction(transactionOptions);
      } else {
        session.startTransaction();
      }
      inTransaction = true;
      return Future.succeededFuture();
    } catch (Exception e) {
      return Future.failedFuture(e);
    }
  }

  @Override
  public Future<Void> commit() {
    if (isClosed) {
      return sessionClosed();
    }
    if (!inTransaction) {
      return noTransaction();
    }

    Promise<Void> promise = vertx.promise();
    session.commitTransaction().subscribe(new CompletionSubscriber<>(promise));
    return promise.future().andThen(ar -> {
      // after an unknown commit result the transaction is still open on the server and commit may be retried
      inTransaction = ar.failed() && hasErrorLabel(ar.cause(), UNKNOWN_TRANSACTION_COMMIT_RESULT);
    });
  }

  @Override
  public Future<Void> abort() {
    if (isClosed || !inTransaction) {
      return Future.succeededFuture();
    }

    Promise<Void> promise = vertx.promise();
    session.abortTransaction().subscribe(new CompletionSubscriber<>(promise));
    return promise.future().andThen(ar -> inTransaction = false);
  }

  private <T> Future<T> sessionClosed() {
    return Future.failedFuture(new IllegalStateException("Session is closed"));
  }

  private <T> Future<T> noTransaction() {
    return Future.failedFuture(new IllegalStateException("Session is not in transaction"));
  }

  private <T> Future<T> alreadyHasTransaction() {
    return Future.failedFuture(new IllegalStateException("Session is already in transaction"));
  }

  @Override
  public Future<Void> close() {
    final Promise<Void> promise = Promise.promise();

    try {
      session.close();
      promise.complete();
    } catch (Exception e) {
      promise.fail(e);
    }

    inTransaction = false;
    isClosed = true;

    creatingContext.removeCloseHook(this);

    return promise.future();
  }

  @Override
  public void close(Completable<Void> completionHandler) {
    inTransaction = false;
    isClosed = true;
    creatingContext.removeCloseHook(this);

    try {
      session.close();
      completionHandler.succeed();
    } catch (Exception e) {
      completionHandler.fail(e);
    }
  }

}
