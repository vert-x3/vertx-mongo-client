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
import io.vertx.ext.mongo.WriteOption
import io.vertx.groovy.core.Vertx
import io.vertx.ext.mongo.FindOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.mongo.UpdateOptions
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
*/
@CompileStatic
public class MongoService extends MongoClient {
  final def io.vertx.ext.mongo.MongoService delegate;
  public MongoService(io.vertx.ext.mongo.MongoService delegate) {
    super(delegate);
    this.delegate = delegate;
  }
  public Object getDelegate() {
    return delegate;
  }
  /**
   * Create a proxy to a service that is deployed somewhere on the event bus
   * @param vertx the Vert.x instance
   * @param address the address the service is listening on on the event bus
   * @return the service
   */
  public static MongoService createEventBusProxy(Vertx vertx, String address) {
    def ret= new io.vertx.groovy.ext.mongo.MongoService(io.vertx.ext.mongo.MongoService.createEventBusProxy((io.vertx.core.Vertx)vertx.getDelegate(), address));
    return ret;
  }
  public MongoService save(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.save(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  public MongoService saveWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.saveWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService insert(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.insert(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  public MongoService insertWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.insertWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService update(String collection, Map<String, Object> query, Map<String, Object> update, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.update(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler);
    return this;
  }
  public MongoService updateWithOptions(String collection, Map<String, Object> query, Map<String, Object> update, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.updateWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(new io.vertx.core.json.JsonObject(options)) : null, resultHandler);
    return this;
  }
  public MongoService replace(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.replace(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler);
    return this;
  }
  public MongoService replaceWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.replaceWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(new io.vertx.core.json.JsonObject(options)) : null, resultHandler);
    return this;
  }
  public MongoService find(String collection, Map<String, Object> query, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    this.delegate.find(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, new Handler<AsyncResult<List<JsonObject>>>() {
      public void handle(AsyncResult<List<JsonObject>> event) {
        AsyncResult<List<Map<String, Object>>> f
        if (event.succeeded()) {
          f = InternalHelper.<List<Map<String, Object>>>result(event.result().collect({
            io.vertx.core.json.JsonObject element ->
            element?.getMap()
          }) as List)
        } else {
          f = InternalHelper.<List<Map<String, Object>>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
    return this;
  }
  public MongoService findWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    this.delegate.findWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, options != null ? new io.vertx.ext.mongo.FindOptions(new io.vertx.core.json.JsonObject(options)) : null, new Handler<AsyncResult<List<JsonObject>>>() {
      public void handle(AsyncResult<List<JsonObject>> event) {
        AsyncResult<List<Map<String, Object>>> f
        if (event.succeeded()) {
          f = InternalHelper.<List<Map<String, Object>>>result(event.result().collect({
            io.vertx.core.json.JsonObject element ->
            element?.getMap()
          }) as List)
        } else {
          f = InternalHelper.<List<Map<String, Object>>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
    return this;
  }
  public MongoService findOne(String collection, Map<String, Object> query, Map<String, Object> fields, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    this.delegate.findOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, fields != null ? new io.vertx.core.json.JsonObject(fields) : null, new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> event) {
        AsyncResult<Map<String, Object>> f
        if (event.succeeded()) {
          f = InternalHelper.<Map<String, Object>>result(event.result()?.getMap())
        } else {
          f = InternalHelper.<Map<String, Object>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
    return this;
  }
  public MongoService count(String collection, Map<String, Object> query, Handler<AsyncResult<Long>> resultHandler) {
    this.delegate.count(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService remove(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.remove(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService removeWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.removeWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService removeOne(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.removeOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService removeOneWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.removeOneWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.createCollection(collectionName, resultHandler);
    return this;
  }
  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    this.delegate.getCollections(resultHandler);
    return this;
  }
  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.dropCollection(collection, resultHandler);
    return this;
  }
  public MongoService runCommand(Map<String, Object> command, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    this.delegate.runCommand(command != null ? new io.vertx.core.json.JsonObject(command) : null, new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> event) {
        AsyncResult<Map<String, Object>> f
        if (event.succeeded()) {
          f = InternalHelper.<Map<String, Object>>result(event.result()?.getMap())
        } else {
          f = InternalHelper.<Map<String, Object>>failure(event.cause())
        }
        resultHandler.handle(f)
      }
    });
    return this;
  }
  public void close() {
    this.delegate.close();
  }
}
