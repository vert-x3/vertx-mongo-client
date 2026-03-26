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
import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Closeable;
import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.internal.ContextInternal;
import io.vertx.ext.mongo.ClientSessionOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoSession;
import io.vertx.ext.mongo.TransactionOptions;

import java.util.Objects;
import java.util.function.Function;

public class MongoSessionImpl implements MongoSession, Closeable {

  private static final long DEFAULT_TRANSACTION_TIMEOUT_MS = 120_000;
  private static final String TRANSIENT_TRANSACTION_ERROR = "TransientTransactionError";
  private static final String UNKNOWN_TRANSACTION_COMMIT_RESULT = "UnknownTransactionCommitResult";

  private final ContextInternal creatingContext;

  private final MongoClient delegate;
  private final com.mongodb.TransactionOptions transactionOptions;
  private final ClientSession session;
  private final boolean autoStartTransaction;
  private final boolean autoClose;
  private volatile boolean inTransaction;
  private volatile boolean isClosed;

  public MongoSessionImpl(ContextInternal creatingContext, MongoClient delegate, ClientSession session, ClientSessionOptions sessionOptions) {
    Objects.requireNonNull(creatingContext);
    Objects.requireNonNull(delegate);
    Objects.requireNonNull(session);
    this.creatingContext = creatingContext;
    this.delegate = delegate;
    this.session = session;
    this.transactionOptions = ((sessionOptions != null) && (sessionOptions.getDefaultTransactionOptions() != null))
      ? sessionOptions.getDefaultTransactionOptions().toMongoDriverObject()
      : null;
    this.autoStartTransaction = (sessionOptions == null) || sessionOptions.isAutoStartTransaction();
    this.autoClose = (sessionOptions == null) || sessionOptions.isAutoClose();

    creatingContext.addCloseHook(this);

    this.inTransaction = false;
    this.isClosed = false;
  }

  @Override
  public <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations) {
    return executeTransaction(operations, this.transactionOptions);
  }

  @Override
  public <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations, TransactionOptions options) {
    return executeTransaction(operations, options != null ? options.toMongoDriverObject() : null);
  }

  private <T> Future<@Nullable T> executeTransaction(Function<MongoClient, Future<@Nullable T>> operations,
                                                     com.mongodb.TransactionOptions options) {
    if (isClosed) {
      return sessionClosed();
    }

    if (inTransaction) {
      return alreadyHasTransaction();
    }

    if (!autoStartTransaction && !inTransaction) {
      return Future.failedFuture(new IllegalStateException(
        "autoStartTransaction is disabled and no transaction has been started. Call start() first."));
    }

    long deadline = System.currentTimeMillis() + DEFAULT_TRANSACTION_TIMEOUT_MS;
    return attemptTransaction(operations, options, deadline);
  }

  private <T> Future<@Nullable T> attemptTransaction(Function<MongoClient, Future<@Nullable T>> operations,
                                                      com.mongodb.TransactionOptions options,
                                                      long deadline) {
    if (autoStartTransaction) {
      if (options != null) {
        session.startTransaction(options);
      } else {
        session.startTransaction();
      }
      this.inTransaction = true;
    }

    return operations.apply(delegate)
      .compose(
        result -> commitWithRetry(deadline).map(result),
        err -> abort().compose(v -> {
          if (hasErrorLabel(err, TRANSIENT_TRANSACTION_ERROR) && System.currentTimeMillis() < deadline) {
            return attemptTransaction(operations, options, deadline);
          }
          return Future.failedFuture(err);
        })
      );
  }

  private Future<Void> commitWithRetry(long deadline) {
    return commit().recover(err -> {
      if (hasErrorLabel(err, UNKNOWN_TRANSACTION_COMMIT_RESULT) && System.currentTimeMillis() < deadline) {
        // Transaction is still active after unknown commit result, so inTransaction must stay true for retry
        this.inTransaction = true;
        return commitWithRetry(deadline);
      }
      return Future.failedFuture(err);
    });
  }

  private boolean hasErrorLabel(Throwable t, String label) {
    return t instanceof MongoException && ((MongoException) t).hasErrorLabel(label);
  }

  @Override
  public Future<Void> start() {
    return start(this.transactionOptions);
  }

  @Override
  public Future<Void> start(TransactionOptions transactionOptions) {
    return start(transactionOptions != null ? transactionOptions.toMongoDriverObject() : null);
  }

  public Future<Void> start(com.mongodb.TransactionOptions transactionOptions) {
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

    final Promise<Void> promise = Promise.promise();
    session.commitTransaction().subscribe(new TransactionSubscriber<>(promise, session, autoClose, () -> {
      this.inTransaction = false;
      if (autoClose) {
        this.isClosed = true;
        creatingContext.removeCloseHook(this);
      }
    }));
    return promise.future();
  }

  @Override
  public Future<Void> abort() {
    if (isClosed || !inTransaction) {
      return Future.succeededFuture();
    }

    final Promise<Void> promise = Promise.promise();
    session.abortTransaction().subscribe(new TransactionSubscriber<>(promise, session, autoClose, () -> {
      this.inTransaction = false;
      if (autoClose) {
        this.isClosed = true;
        creatingContext.removeCloseHook(this);
      }
    }));
    return promise.future();
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
