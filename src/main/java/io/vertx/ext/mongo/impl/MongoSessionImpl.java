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

import com.mongodb.TransactionOptions;
import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Closeable;
import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.internal.ContextInternal;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoSession;
import io.vertx.ext.mongo.SessionOptions;

import java.util.Objects;
import java.util.function.Function;

public class MongoSessionImpl implements MongoSession, Closeable {

  private final ContextInternal creatingContext;

  private final MongoClient delegate;
  private final TransactionOptions transactionOptions;
  private final ClientSession session;
  private final boolean autoStart;
  private final boolean autoClose;
  private boolean inTransaction;
  private boolean isClosed;

  public MongoSessionImpl(ContextInternal creatingContext, MongoClient delegate, ClientSession session, SessionOptions sessionOptions) {
    Objects.requireNonNull(creatingContext);
    Objects.requireNonNull(delegate);
    Objects.requireNonNull(session);
    this.creatingContext = creatingContext;
    this.delegate = delegate;
    this.session = session;
    this.transactionOptions = (sessionOptions != null && sessionOptions.getClientSessionOptions() != null)
      ? sessionOptions.getClientSessionOptions().getDefaultTransactionOptions()
      : null;
    this.autoStart = (sessionOptions == null) || sessionOptions.isAutoStart();
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
    if (isClosed) {
      return sessionClosed();
    }

    if (inTransaction) {
      return alreadyHasTransaction();
    }

    if (autoStart) {
      if (options != null) {
        session.startTransaction(options);
      } else {
        session.startTransaction();
      }
      this.inTransaction = true;
    }

    return operations.apply(delegate)
      .compose(
        result -> commit().map(result),
        err -> abort().compose(v2 -> Future.failedFuture(err))
      );
  }

  @Override
  public Future<Void> start() {
    if (inTransaction) {
      return alreadyHasTransaction();
    }

    try {
      session.startTransaction();
      inTransaction = true;
      return Future.succeededFuture();
    } catch (Exception e) {
      return Future.failedFuture(e);
    }
  }

  @Override
  public Future<Void> start(TransactionOptions transactionOptions) {
    if (inTransaction) {
      return alreadyHasTransaction();
    }

    try {
      session.startTransaction(transactionOptions);
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
    session.commitTransaction().subscribe(new TransactionSubscriber<>(promise, session, autoClose, () -> this.inTransaction = false));
    return promise.future();
  }

  @Override
  public Future<Void> abort() {
    if (isClosed || !inTransaction) {
      return Future.succeededFuture();
    }

    final Promise<Void> promise = Promise.promise();
    session.abortTransaction().subscribe(new TransactionSubscriber<>(promise, session, autoClose, () -> this.inTransaction = false));
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
    try {
      session.close();
    } catch (Exception e) {
      completionHandler.fail(e);
    }

    inTransaction = false;
    isClosed = true;

    creatingContext.removeCloseHook(this);
    completionHandler.succeed();
  }

}
