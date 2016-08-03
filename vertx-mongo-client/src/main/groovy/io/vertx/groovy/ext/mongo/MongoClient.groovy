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
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.InsertOptions
import io.vertx.ext.mongo.MongoClientDeleteResult
import io.vertx.ext.mongo.WriteOption
import io.vertx.groovy.core.Vertx
import io.vertx.core.json.JsonArray
import java.util.List
import io.vertx.ext.mongo.IndexOptions
import io.vertx.ext.mongo.FindOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.mongo.MongoClientUpdateResult
import io.vertx.ext.mongo.UpdateOptions
/**
 * A Vert.x service used to interact with MongoDB server instances.
 * <p>
 * Some of the operations might change <i>_id</i> field of passed  document.
*/
@CompileStatic
public class MongoClient {
  private final def io.vertx.ext.mongo.MongoClient delegate;
  public MongoClient(Object delegate) {
    this.delegate = (io.vertx.ext.mongo.MongoClient) delegate;
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
  public static MongoClient createNonShared(Vertx vertx, Map<String, Object> config) {
    def ret = InternalHelper.safeCreate(io.vertx.ext.mongo.MongoClient.createNonShared(vertx != null ? (io.vertx.core.Vertx)vertx.getDelegate() : null, config != null ? new io.vertx.core.json.JsonObject(config) : null), io.vertx.groovy.ext.mongo.MongoClient.class);
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
  public static MongoClient createShared(Vertx vertx, Map<String, Object> config, String dataSourceName) {
    def ret = InternalHelper.safeCreate(io.vertx.ext.mongo.MongoClient.createShared(vertx != null ? (io.vertx.core.Vertx)vertx.getDelegate() : null, config != null ? new io.vertx.core.json.JsonObject(config) : null, dataSourceName), io.vertx.groovy.ext.mongo.MongoClient.class);
    return ret;
  }
  /**
   * Like {@link io.vertx.groovy.ext.mongo.MongoClient#createShared} but with the default data source name
   * @param vertx the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  public static MongoClient createShared(Vertx vertx, Map<String, Object> config) {
    def ret = InternalHelper.safeCreate(io.vertx.ext.mongo.MongoClient.createShared(vertx != null ? (io.vertx.core.Vertx)vertx.getDelegate() : null, config != null ? new io.vertx.core.json.JsonObject(config) : null), io.vertx.groovy.ext.mongo.MongoClient.class);
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
  public MongoClient save(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    delegate.save(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
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
  public MongoClient saveWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    delegate.saveWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
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
  public MongoClient insert(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    delegate.insert(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
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
  public MongoClient insertWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    delegate.insertWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
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
  public MongoClient insertMany(String collection, List<Map<String, Object>> documents, Handler<AsyncResult<Void>> resultHandler) {
    delegate.insertMany(collection, documents != null ? (List)documents.collect({new io.vertx.core.json.JsonObject(it)}) : null, resultHandler);
    return this;
  }
  /**
   * Insert documents in the specified collection with the specified write option
   * <p>
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param collection the collection
   * @param documents the documents
   * @param insertOptions the options for insertion to use (see <a href="../../../../../../../cheatsheet/InsertOptions.html">InsertOptions</a>)
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient insertManyWithOptions(String collection, List<Map<String, Object>> documents, Map<String, Object> insertOptions, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    delegate.insertManyWithOptions(collection, documents != null ? (List)documents.collect({new io.vertx.core.json.JsonObject(it)}) : null, insertOptions != null ? new io.vertx.ext.mongo.InsertOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(insertOptions)) : null, writeOption, resultHandler);
    return this;
  }
  /**
   * Update matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient update(String collection, Map<String, Object> query, Map<String, Object> update, Handler<AsyncResult<Void>> resultHandler) {
    delegate.update(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler);
    return this;
  }
  /**
   * Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateCollection(String collection, Map<String, Object> query, Map<String, Object> update, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.updateCollection(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Update matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update (see <a href="../../../../../../../cheatsheet/UpdateOptions.html">UpdateOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateWithOptions(String collection, Map<String, Object> query, Map<String, Object> update, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    delegate.updateWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler);
    return this;
  }
  /**
   * Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param update used to describe how the documents will be updated
   * @param options options to configure the update (see <a href="../../../../../../../cheatsheet/UpdateOptions.html">UpdateOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient updateCollectionWithOptions(String collection, Map<String, Object> query, Map<String, Object> update, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.updateCollectionWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
  public MongoClient replace(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Void>> resultHandler) {
    delegate.replace(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler);
    return this;
  }
  /**
   * Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceDocuments(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.replaceDocuments(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Replace matching documents in the specified collection, specifying options
   * <p>
   * This operation might change <i>_id</i> field of <i>replace</i> parameter
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace (see <a href="../../../../../../../cheatsheet/UpdateOptions.html">UpdateOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    delegate.replaceWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler);
    return this;
  }
  /**
   * Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace (see <a href="../../../../../../../cheatsheet/UpdateOptions.html">UpdateOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceDocumentsWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.replaceDocumentsWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Find matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with list of documents
   * @return 
   */
  public MongoClient find(String collection, Map<String, Object> query, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    delegate.find(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
      public void handle(AsyncResult<java.util.List<io.vertx.core.json.JsonObject>> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((List)ar.result()?.collect({(Map<String, Object>)InternalHelper.wrapObject(it)})));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Find matching documents in the specified collection.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with each found document
   * @return 
   */
  public MongoClient findBatch(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.findBatch(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Find matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find (see <a href="../../../../../../../cheatsheet/FindOptions.html">FindOptions</a>)
   * @param resultHandler will be provided with list of documents
   * @return 
   */
  public MongoClient findWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    delegate.findWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
      public void handle(AsyncResult<java.util.List<io.vertx.core.json.JsonObject>> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((List)ar.result()?.collect({(Map<String, Object>)InternalHelper.wrapObject(it)})));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Find matching documents in the specified collection, specifying options.
   * This method use batchCursor for returning each found document.
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find (see <a href="../../../../../../../cheatsheet/FindOptions.html">FindOptions</a>)
   * @param resultHandler will be provided with each found document
   * @return 
   */
  public MongoClient findBatchWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.findBatchWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
  public MongoClient findOne(String collection, Map<String, Object> query, Map<String, Object> fields, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.findOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, fields != null ? new io.vertx.core.json.JsonObject(fields) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Count matching documents in a collection.
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with the number of matching documents
   * @return 
   */
  public MongoClient count(String collection, Map<String, Object> query, Handler<AsyncResult<Long>> resultHandler) {
    delegate.count(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  /**
   * Remove matching documents from a collection
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient remove(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    delegate.remove(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  /**
   * Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocuments(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.removeDocuments(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Remove matching documents from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    delegate.removeWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  /**
   * Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match documents
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocumentsWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.removeDocumentsWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Remove a single matching document from a collection
   * @param collection the collection
   * @param query query used to match document
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeOne(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    delegate.removeOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  /**
   * Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocument(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.removeDocument(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Remove a single matching document from a collection with the specified write option
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeOneWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    delegate.removeOneWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  /**
   * Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
   * @param collection the collection
   * @param query query used to match document
   * @param writeOption the write option to use
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient removeDocumentWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.removeDocumentWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result()?.toJson())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
   * Get a list of all collections in the database.
   * @param resultHandler will be called with a list of collections.
   * @return 
   */
  public MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    delegate.getCollections(resultHandler != null ? new Handler<AsyncResult<java.util.List<java.lang.String>>>() {
      public void handle(AsyncResult<java.util.List<java.lang.String>> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture(ar.result()));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createIndex(String collection, Map<String, Object> key, Handler<AsyncResult<Void>> resultHandler) {
    delegate.createIndex(collection, key != null ? new io.vertx.core.json.JsonObject(key) : null, resultHandler);
    return this;
  }
  /**
   * Creates an index.
   * @param collection the collection
   * @param key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
   * @param options the options for the index (see <a href="../../../../../../../cheatsheet/IndexOptions.html">IndexOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createIndexWithOptions(String collection, Map<String, Object> key, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    delegate.createIndexWithOptions(collection, key != null ? new io.vertx.core.json.JsonObject(key) : null, options != null ? new io.vertx.ext.mongo.IndexOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler);
    return this;
  }
  /**
   * Get all the indexes in this collection.
   * @param collection the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient listIndexes(String collection, Handler<AsyncResult<List<Object>>> resultHandler) {
    delegate.listIndexes(collection, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonArray>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonArray> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((List<Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
   * Run an arbitrary MongoDB command.
   * @param commandName the name of the command
   * @param command the command
   * @param resultHandler will be called with the result.
   * @return 
   */
  public MongoClient runCommand(String commandName, Map<String, Object> command, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.runCommand(commandName, command != null ? new io.vertx.core.json.JsonObject(command) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
  public MongoClient distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<List<Object>>> resultHandler) {
    delegate.distinct(collection, fieldName, resultClassname, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonArray>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonArray> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((List<Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
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
  public MongoClient distinctBatch(String collection, String fieldName, String resultClassname, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    delegate.distinctBatch(collection, fieldName, resultClassname, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(AsyncResult<io.vertx.core.json.JsonObject> ar) {
        if (ar.succeeded()) {
          resultHandler.handle(io.vertx.core.Future.succeededFuture((Map<String, Object>)InternalHelper.wrapObject(ar.result())));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
        }
      }
    } : null);
    return this;
  }
  /**
   * Close the client and release its resources
   */
  public void close() {
    delegate.close();
  }
}
