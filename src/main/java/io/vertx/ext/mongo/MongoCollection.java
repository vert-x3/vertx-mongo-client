/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@VertxGen
@ProxyGen
public interface MongoCollection {

  void save(JsonObject document, Handler<AsyncResult<String>> handler);

  void insertOne(JsonObject document, Handler<AsyncResult<String>> resultHandler);

  void insertMany(List<JsonObject> document, boolean ordered, Handler<AsyncResult<List<String>>> resultHandler);

  void updateOne(JsonObject query, JsonObject update, boolean upsert, Handler<AsyncResult<Void>> resultHandler);

  void updateMany(JsonObject query, JsonObject update, boolean upsert, Handler<AsyncResult<Void>> resultHandler);

  void replaceOne(JsonObject query, JsonObject replace, boolean upsert, Handler<AsyncResult<Void>> resultHandler);

  void find(JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  void findWithOptions(JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  void findOne(JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler);

  void count(JsonObject query, Handler<AsyncResult<Long>> resultHandler);

  void deleteOne(JsonObject query, Handler<AsyncResult<Void>> resultHandler);

  void deleteMany(JsonObject query, Handler<AsyncResult<Void>> resultHandler);
}
