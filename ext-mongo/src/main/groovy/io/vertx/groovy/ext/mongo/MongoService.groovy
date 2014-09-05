/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.groovy.ext.mongo;
import groovy.transform.CompileStatic
import io.vertx.lang.groovy.InternalHelper
import java.util.List
import io.vertx.groovy.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@CompileStatic
public class MongoService {
  final def io.vertx.ext.mongo.MongoService delegate;
  public MongoService(io.vertx.ext.mongo.MongoService delegate) {
    this.delegate = delegate;
  }
  public io.vertx.ext.mongo.MongoService getDelegate() {
    return delegate;
  }
  public static MongoService create(Vertx vertx, Map<String, Object> config) {
    def ret= new MongoService(io.vertx.ext.mongo.MongoService.create(vertx.getDelegate(), config != null ? new io.vertx.core.json.JsonObject(config) : null));
    return ret;
  }
  public static MongoService createEventBusProxy(Vertx vertx, String address) {
    def ret= new MongoService(io.vertx.ext.mongo.MongoService.createEventBusProxy(vertx.getDelegate(), address));
    return ret;
  }
  public void save(String collection, Map<String, Object> document, String writeConcern, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.save(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeConcern, resultHandler);
  }
  public void insert(String collection, Map<String, Object> document, String writeConcern, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.insert(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeConcern, resultHandler);
  }
  public void update(String collection, Map<String, Object> query, Map<String, Object> update, String writeConcern, boolean upsert, boolean multi, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.update(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, writeConcern, upsert, multi, resultHandler);
  }
  public void find(String collection, Map<String, Object> query, Map<String, Object> fields, Map<String, Object> sort, int limit, int skip, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    this.delegate.find(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, fields != null ? new io.vertx.core.json.JsonObject(fields) : null, sort != null ? new io.vertx.core.json.JsonObject(sort) : null, limit, skip, new Handler<AsyncResult<List<JsonObject>>>() {
      public void handle(AsyncResult<List<JsonObject>> event) {
        AsyncResult<List<Map<String, Object>>> f
        if (event.succeeded()) {
          f = InternalHelper.<List<Map<String, Object>>>result(event.result().collect({
            io.vertx.core.json.JsonObject element ->
            element?.toMap()
          }) as List)
        } else {
          f = InternalHelper.<List<Map<String, Object>>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
  }
  public void findOne(String collection, Map<String, Object> query, Map<String, Object> fields, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    this.delegate.findOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, fields != null ? new io.vertx.core.json.JsonObject(fields) : null, new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> event) {
        AsyncResult<Map<String, Object>> f
        if (event.succeeded()) {
          f = InternalHelper.<Map<String, Object>>result(event.result()?.toMap())
        } else {
          f = InternalHelper.<Map<String, Object>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
  }
  public void delete(String collection, Map<String, Object> query, String writeConcern, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.delete(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeConcern, resultHandler);
  }
  public void createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.createCollection(collectionName, resultHandler);
  }
  public void getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    this.delegate.getCollections(resultHandler);
  }
  public void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.dropCollection(collection, resultHandler);
  }
  public void runCommand(String collection, Map<String, Object> command, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    this.delegate.runCommand(collection, command != null ? new io.vertx.core.json.JsonObject(command) : null, new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> event) {
        AsyncResult<Map<String, Object>> f
        if (event.succeeded()) {
          f = InternalHelper.<Map<String, Object>>result(event.result()?.toMap())
        } else {
          f = InternalHelper.<Map<String, Object>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
  }
  public void start() {
    this.delegate.start();
  }
  public void stop() {
    this.delegate.stop();
  }
}
