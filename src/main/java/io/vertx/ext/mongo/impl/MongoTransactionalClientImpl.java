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

import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.core.Closeable;
import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoTransactionalClient;

import java.util.Objects;

public class MongoTransactionalClientImpl implements MongoTransactionalClient, Closeable {
  private MongoClient delegate;
  private ClientSession session;

  public MongoTransactionalClientImpl(MongoClient delegate, ClientSession session) {
    Objects.requireNonNull(delegate);
    this.delegate = delegate;
    this.session = session;
  }

  @Override
  public void close(Completable<Void> completable) {
    delegate.close();
  }

  @Override
  public Future<MongoClient> getClient() {
    return Future.future(mongoClientPromise -> mongoClientPromise.succeed(delegate));
  }

  @Override
  public Future<Void> commit() {
    final Promise<Void> promise = Promise.promise();
    session.commitTransaction().subscribe(new ClientSessionSubscriber<>(promise, session));
    return promise.future();
  }

  @Override
  public Future<Void> abort() {
    final Promise<Void> promise = Promise.promise();
    session.abortTransaction().subscribe(new ClientSessionSubscriber<>(promise, session));
    return promise.future();
  }

}
