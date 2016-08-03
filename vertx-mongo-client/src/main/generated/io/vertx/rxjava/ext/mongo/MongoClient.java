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
import rx.Observable;
import io.vertx.ext.mongo.InsertOptions;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rxjava.core.Vertx;
import io.vertx.core.json.JsonArray;
import java.util.List;
import io.vertx.ext.mongo.IndexOptions;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * A Vert.x service used to interact with MongoDB server instances.
 * <p>
 * Some of the operations might change <i>_id</i> field of passed  document.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.MongoClient original} non RX-ified interface using Vert.x codegen.
 */

public class MongoClient {

  final io.vertx.ext.mongo.MongoClient delegate;

  public MongoClient(io.vertx.ext.mongo.MongoClient delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Create a Mongo client which maintains its own data source.
   * @param vertx the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  public static MongoClient createNonShared(Vertx vertx, JsonObject config) { 
    MongoClient ret = MongoClient.newInstance(io.vertx.ext.mongo.MongoClient.createNonShared((io.vertx.core.Vertx)vertx.getDelegate(), config));
    return ret;
  }

  /**
   * Create a Mongo client which shares its data source with any other Mongo clients created with the same
   * data source name
   * @param vertx the Vert.x instance
   * @param config the configuration
   * @param dataSourceName the data source name
   * @return the client
   */
  public static MongoClient createShared(Vertx vertx, JsonObject config, String dataSourceName) { 
    MongoClient ret = MongoClient.newInstance(io.vertx.ext.mongo.MongoClient.createShared((io.vertx.core.Vertx)vertx.getDelegate(), config, dataSourceName));
    return ret;
  }

  /**
   * Like {@link io.vertx.rxjava.ext.mongo.MongoClient#createShared} but with the default data source name
   * @param vertx the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  public static MongoClient createShared(Vertx vertx, JsonObject config) { 
    MongoClient ret = MongoClient.newInstance(io.vertx.ext.mongo.MongoClient.createShared((io.vertx.core.Vertx)vertx.getDelegate(), config));
    return ret;
  }

  /**
   * Save a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) { 
    delegate.save(collection, document, resultHandler);
    return this;
  }

  /**
   * Save a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @return 
   */
  public Observable<String> saveObservable(String collection, JsonObject document) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    save(collection, document, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Save a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) { 
    delegate.saveWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  /**
   * Save a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<String> saveWithOptionsObservable(String collection, JsonObject document, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    saveWithOptions(collection, document, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Insert a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) { 
    delegate.insert(collection, document, resultHandler);
    return this;
  }

  /**
   * Insert a document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @return 
   */
  public Observable<String> insertObservable(String collection, JsonObject document) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insert(collection, document, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Insert a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) { 
    delegate.insertWithOptions(collection, document, writeOption, resultHandler);
    return this;
  }

  /**
   * Insert a document in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<String> insertWithOptionsObservable(String collection, JsonObject document, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<String> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insertWithOptions(collection, document, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Insert documents in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param documents the documents
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient insertMany(String collection, List<JsonObject> documents, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.insertMany(collection, documents, resultHandler);
    return this;
  }

  /**
   * Insert documents in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param documents the documents
   * @return 
   */
  public Observable<Void> insertManyObservable(String collection, List<JsonObject> documents) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insertMany(collection, documents, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Insert documents in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param documents the documents
   * @param insertOptions the options for insertion to use
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient insertManyWithOptions(String collection, List<JsonObject> documents, InsertOptions insertOptions, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.insertManyWithOptions(collection, documents, insertOptions, writeOption, resultHandler);
    return this;
  }

  /**
   * Insert documents in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param documents the documents
   * @param insertOptions the options for insertion to use
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<Void> insertManyWithOptionsObservable(String collection, List<JsonObject> documents, InsertOptions insertOptions, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    insertManyWithOptions(collection, documents, insertOptions, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Update matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.update(collection, query, update, resultHandler);
    return this;
  }

  /**
   * Update matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @return 
   */
  public Observable<Void> updateObservable(String collection, JsonObject query, JsonObject update) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    update(collection, query, update, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateCollection(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) { 
    delegate.updateCollection(collection, query, update, resultHandler);
    return this;
  }

  /**
   * Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @return 
   */
  public Observable<MongoClientUpdateResult> updateCollectionObservable(String collection, JsonObject query, JsonObject update) { 
    io.vertx.rx.java.ObservableFuture<MongoClientUpdateResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    updateCollection(collection, query, update, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Update matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.updateWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  /**
   * Update matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @return 
   */
  public Observable<Void> updateWithOptionsObservable(String collection, JsonObject query, JsonObject update, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    updateWithOptions(collection, query, update, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) { 
    delegate.updateCollectionWithOptions(collection, query, update, options, resultHandler);
    return this;
  }

  /**
   * Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update
   * @return 
   */
  public Observable<MongoClientUpdateResult> updateCollectionWithOptionsObservable(String collection, JsonObject query, JsonObject update, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<MongoClientUpdateResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    updateCollectionWithOptions(collection, query, update, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Replace matching documents in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>replace</i> parameter
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.replace(collection, query, replace, resultHandler);
    return this;
  }

  /**
   * Replace matching documents in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>replace</i> parameter
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @return 
   */
  public Observable<Void> replaceObservable(String collection, JsonObject query, JsonObject replace) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replace(collection, query, replace, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) { 
    delegate.replaceDocuments(collection, query, replace, resultHandler);
    return this;
  }

  /**
   * Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @return 
   */
  public Observable<MongoClientUpdateResult> replaceDocumentsObservable(String collection, JsonObject query, JsonObject replace) { 
    io.vertx.rx.java.ObservableFuture<MongoClientUpdateResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replaceDocuments(collection, query, replace, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Replace matching documents in the specified collection, specifying options
   * <p>
   * This operation might change <i>_id</i> field of <i>replace</i> parameter
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.replaceWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  /**
   * Replace matching documents in the specified collection, specifying options
   * <p>
   * This operation might change <i>_id</i> field of <i>replace</i> parameter
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace
   * @return 
   */
  public Observable<Void> replaceWithOptionsObservable(String collection, JsonObject query, JsonObject replace, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replaceWithOptions(collection, query, replace, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) { 
    delegate.replaceDocumentsWithOptions(collection, query, replace, options, resultHandler);
    return this;
  }

  /**
   * Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace
   * @return 
   */
  public Observable<MongoClientUpdateResult> replaceDocumentsWithOptionsObservable(String collection, JsonObject query, JsonObject replace, UpdateOptions options) { 
    io.vertx.rx.java.ObservableFuture<MongoClientUpdateResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    replaceDocumentsWithOptions(collection, query, replace, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Find matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with list of documents
   * @return 
   */
  public MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    delegate.find(collection, query, resultHandler);
    return this;
  }

  /**
   * Find matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match documents
   * @return 
   */
  public Observable<List<JsonObject>> findObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<List<JsonObject>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    find(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Find matching documents in the specified collection.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with each found document
   * @return 
   */
  public MongoClient findBatch(String collection, JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.findBatch(collection, query, resultHandler);
    return this;
  }

  /**
   * Find matching documents in the specified collection.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @return 
   */
  public Observable<JsonObject> findBatchObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findBatch(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Find matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find
   * @param resultHandler will be provided with list of documents
   * @return 
   */
  public MongoClient findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) { 
    delegate.findWithOptions(collection, query, options, resultHandler);
    return this;
  }

  /**
   * Find matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find
   * @return 
   */
  public Observable<List<JsonObject>> findWithOptionsObservable(String collection, JsonObject query, FindOptions options) { 
    io.vertx.rx.java.ObservableFuture<List<JsonObject>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findWithOptions(collection, query, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Find matching documents in the specified collection, specifying options.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find
   * @param resultHandler will be provided with each found document
   * @return 
   */
  public MongoClient findBatchWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.findBatchWithOptions(collection, query, options, resultHandler);
    return this;
  }

  /**
   * Find matching documents in the specified collection, specifying options.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find
   * @return 
   */
  public Observable<JsonObject> findBatchWithOptionsObservable(String collection, JsonObject query, FindOptions options) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findBatchWithOptions(collection, query, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Find a single matching document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   * @param collection the collection
   * @param query the query used to match the document
   * @param fields the fields
   * @param resultHandler will be provided with the document, if any
   * @return 
   */
  public MongoClient findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.findOne(collection, query, fields, resultHandler);
    return this;
  }

  /**
   * Find a single matching document in the specified collection
   * <p>
   * This operation might change <i>_id</i> field of <i>query</i> parameter
   * @param collection the collection
   * @param query the query used to match the document
   * @param fields the fields
   * @return 
   */
  public Observable<JsonObject> findOneObservable(String collection, JsonObject query, JsonObject fields) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    findOne(collection, query, fields, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Count matching documents in a collection.
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with the number of matching documents
   * @return 
   */
  public MongoClient count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) { 
    delegate.count(collection, query, resultHandler);
    return this;
  }

  /**
   * Count matching documents in a collection.
   * @param collection the collection
   * @param query query used to match documents
   * @return 
   */
  public Observable<Long> countObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Long> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    count(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove matching documents from a collection
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.remove(collection, query, resultHandler);
    return this;
  }

  /**
   * Remove matching documents from a collection
   * @param collection the collection
   * @param query query used to match documents
   * @return 
   */
  public Observable<Void> removeObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    remove(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) { 
    delegate.removeDocuments(collection, query, resultHandler);
    return this;
  }

  /**
   * Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @return 
   */
  public Observable<MongoClientDeleteResult> removeDocumentsObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<MongoClientDeleteResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeDocuments(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove matching documents from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  /**
   * Remove matching documents from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<Void> removeWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocumentsWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) { 
    delegate.removeDocumentsWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  /**
   * Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<MongoClientDeleteResult> removeDocumentsWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<MongoClientDeleteResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeDocumentsWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove a single matching document from a collection
   * @param collection the collection
   * @param query query used to match document
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeOne(collection, query, resultHandler);
    return this;
  }

  /**
   * Remove a single matching document from a collection
   * @param collection the collection
   * @param query query used to match document
   * @return 
   */
  public Observable<Void> removeOneObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeOne(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocument(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) { 
    delegate.removeDocument(collection, query, resultHandler);
    return this;
  }

  /**
   * Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @return 
   */
  public Observable<MongoClientDeleteResult> removeDocumentObservable(String collection, JsonObject query) { 
    io.vertx.rx.java.ObservableFuture<MongoClientDeleteResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeDocument(collection, query, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove a single matching document from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeOneWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  /**
   * Remove a single matching document from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<Void> removeOneWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeOneWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocumentWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) { 
    delegate.removeDocumentWithOptions(collection, query, writeOption, resultHandler);
    return this;
  }

  /**
   * Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @return 
   */
  public Observable<MongoClientDeleteResult> removeDocumentWithOptionsObservable(String collection, JsonObject query, WriteOption writeOption) { 
    io.vertx.rx.java.ObservableFuture<MongoClientDeleteResult> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeDocumentWithOptions(collection, query, writeOption, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Create a new collection
   * @param collectionName the name of the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createCollection(collectionName, resultHandler);
    return this;
  }

  /**
   * Create a new collection
   * @param collectionName the name of the collection
   * @return 
   */
  public Observable<Void> createCollectionObservable(String collectionName) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    createCollection(collectionName, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Get a list of all collections in the database.
   * @param resultHandler will be called with a list of collections.
   * @return 
   */
  public MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) { 
    delegate.getCollections(resultHandler);
    return this;
  }

  /**
   * Get a list of all collections in the database.
   * @return 
   */
  public Observable<List<String>> getCollectionsObservable() { 
    io.vertx.rx.java.ObservableFuture<List<String>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    getCollections(resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Drop a collection
   * @param collection the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.dropCollection(collection, resultHandler);
    return this;
  }

  /**
   * Drop a collection
   * @param collection the collection
   * @return 
   */
  public Observable<Void> dropCollectionObservable(String collection) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    dropCollection(collection, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createIndex(String collection, JsonObject key, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createIndex(collection, key, resultHandler);
    return this;
  }

  /**
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @return 
   */
  public Observable<Void> createIndexObservable(String collection, JsonObject key) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    createIndex(collection, key, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param options the options for the index
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createIndexWithOptions(String collection, JsonObject key, IndexOptions options, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createIndexWithOptions(collection, key, options, resultHandler);
    return this;
  }

  /**
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param options the options for the index
   * @return 
   */
  public Observable<Void> createIndexWithOptionsObservable(String collection, JsonObject key, IndexOptions options) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    createIndexWithOptions(collection, key, options, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Get all the indexes in this collection.
   * @param collection the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient listIndexes(String collection, Handler<AsyncResult<JsonArray>> resultHandler) { 
    delegate.listIndexes(collection, resultHandler);
    return this;
  }

  /**
   * Get all the indexes in this collection.
   * @param collection the collection
   * @return 
   */
  public Observable<JsonArray> listIndexesObservable(String collection) { 
    io.vertx.rx.java.ObservableFuture<JsonArray> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    listIndexes(collection, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Drops the index given its name.
   * @param collection the collection
   * @param indexName the name of the index to remove
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient dropIndex(String collection, String indexName, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.dropIndex(collection, indexName, resultHandler);
    return this;
  }

  /**
   * Drops the index given its name.
   * @param collection the collection
   * @param indexName the name of the index to remove
   * @return 
   */
  public Observable<Void> dropIndexObservable(String collection, String indexName) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    dropIndex(collection, indexName, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Run an arbitrary MongoDB command.
   * @param commandName the name of the command
   * @param command the command
   * @param resultHandler will be called with the result.
   * @return 
   */
  public MongoClient runCommand(String commandName, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.runCommand(commandName, command, resultHandler);
    return this;
  }

  /**
   * Run an arbitrary MongoDB command.
   * @param commandName the name of the command
   * @param command the command
   * @return 
   */
  public Observable<JsonObject> runCommandObservable(String commandName, JsonObject command) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    runCommand(commandName, command, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Gets the distinct values of the specified field name.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   * @param collection the collection
   * @param fieldName the field name
   * @param resultClassname 
   * @param resultHandler will be provided with array of values.
   * @return 
   */
  public MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonArray>> resultHandler) { 
    delegate.distinct(collection, fieldName, resultClassname, resultHandler);
    return this;
  }

  /**
   * Gets the distinct values of the specified field name.
   * Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
   * @param collection the collection
   * @param fieldName the field name
   * @param resultClassname 
   * @return 
   */
  public Observable<JsonArray> distinctObservable(String collection, String fieldName, String resultClassname) { 
    io.vertx.rx.java.ObservableFuture<JsonArray> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    distinct(collection, fieldName, resultClassname, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Gets the distinct values of the specified field name.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   * @param collection the collection
   * @param fieldName the field name
   * @param resultClassname 
   * @param resultHandler will be provided with each found value
   * @return 
   */
  public MongoClient distinctBatch(String collection, String fieldName, String resultClassname, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.distinctBatch(collection, fieldName, resultClassname, resultHandler);
    return this;
  }

  /**
   * Gets the distinct values of the specified field name.
   * This method use batchCursor for returning each found value.
   * Each value is a json fragment with fieldName key (eg: {"num": 1}).
   * @param collection the collection
   * @param fieldName the field name
   * @param resultClassname 
   * @return 
   */
  public Observable<JsonObject> distinctBatchObservable(String collection, String fieldName, String resultClassname) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    distinctBatch(collection, fieldName, resultClassname, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Close the client and release its resources
   */
  public void close() { 
    delegate.close();
  }


  public static MongoClient newInstance(io.vertx.ext.mongo.MongoClient arg) {
    return arg != null ? new MongoClient(arg) : null;
  }
}
