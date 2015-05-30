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

package io.vertx.rxjava.ext.mongo;

import java.util.Map;
import io.vertx.lang.rxjava.InternalHelper;
import rx.Observable;
import java.util.List;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rxjava.core.Vertx;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.MongoService original} non RX-ified interface using Vert.x codegen.
 */

public class MongoService extends MongoClient {

  final io.vertx.ext.mongo.MongoService delegate;

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
    MongoService ret= MongoService.newInstance(io.vertx.ext.mongo.MongoService.createEventBusProxy((io.vertx.core.Vertx) vertx.getDelegate(), address));
    return ret;
  }

  public MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) { 
    this.delegate.save(collection, document, resultHandler);
    return this;
  }

  public Observable<String> saveObservable(String collection, JsonObject document) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    save(collection, document, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) { 
    this.delegate.saveWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  public Observable<String> saveWithOptionsObservable(String collection, JsonObject document, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    saveWithOptions(collection, document, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) { 
    this.delegate.insert(collection, document, resultHandler);
    return this;
  }

  public Observable<String> insertObservable(String collection, JsonObject document) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insert(collection, document, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) { 
    this.delegate.insertWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  public Observable<String> insertWithOptionsObservable(String collection, JsonObject document, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insertWithOptions(collection, document, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.update(collection, query, update, resultHandler);
    return this;
  }

  public Observable<Void> updateObservable(String collection, JsonObject query, JsonObject update) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    update(collection, query, update, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.updateWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  public Observable<Void> updateWithOptionsObservable(String collection, JsonObject query, JsonObject update, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    updateWithOptions(collection, query, update, options, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.replace(collection, query, replace, resultHandler);
    return this;
  }

  public Observable<Void> replaceObservable(String collection, JsonObject query, JsonObject replace) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replace(collection, query, replace, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.replaceWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  public Observable<Void> replaceWithOptionsObservable(String collection, JsonObject query, JsonObject replace, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replaceWithOptions(collection, query, replace, options, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    this.delegate.find(collection, query, resultHandler);
    return this;
  }

  public Observable<List<JsonObject>> findObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<List<JsonObject>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    find(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    this.delegate.findWithOptions(collection, query, options, resultHandler);
    return this;
  }

  public Observable<List<JsonObject>> findWithOptionsObservable(String collection, JsonObject query, FindOptions options) { 
    io.vertx.rx.java.ObservableFuture<List<JsonObject>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findWithOptions(collection, query, options, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) { 
    this.delegate.findOne(collection, query, fields, resultHandler);
    return this;
  }

  public Observable<JsonObject> findOneObservable(String collection, JsonObject query, JsonObject fields) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findOne(collection, query, fields, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) { 
    this.delegate.count(collection, query, resultHandler);
    return this;
  }

  public Observable<Long> countObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Long> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    count(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.remove(collection, query, resultHandler);
    return this;
  }

  public Observable<Void> removeObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    remove(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.removeWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  public Observable<Void> removeWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.removeOne(collection, query, resultHandler);
    return this;
  }

  public Observable<Void> removeOneObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeOne(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.removeOneWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  public Observable<Void> removeOneWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeOneWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.createCollection(collectionName, resultHandler);
    return this;
  }

  public Observable<Void> createCollectionObservable(String collectionName) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    createCollection(collectionName, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) { 
    this.delegate.getCollections(resultHandler);
    return this;
  }

  public Observable<List<String>> getCollectionsObservable() { 
    io.vertx.rx.java.ObservableFuture<List<String>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    getCollections(resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) { 
    this.delegate.dropCollection(collection, resultHandler);
    return this;
  }

  public Observable<Void> dropCollectionObservable(String collection) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    dropCollection(collection, resultHandler.toHandler());
    return resultHandler;
  }

  public MongoService runCommand(String commandName, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) { 
    this.delegate.runCommand(commandName, command, resultHandler);
    return this;
  }

  public Observable<JsonObject> runCommandObservable(String commandName, JsonObject command) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    runCommand(commandName, command, resultHandler.toHandler());
    return resultHandler;
  }

  public void close() { 
    this.delegate.close();
  }


  public static MongoService newInstance(io.vertx.ext.mongo.MongoService arg) {
    return new MongoService(arg);
  }
}
