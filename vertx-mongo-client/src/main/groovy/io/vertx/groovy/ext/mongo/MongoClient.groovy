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
 * A Vert.x service used to interact with MongoDB server instances.
*/
@CompileStatic
public class MongoClient {
  final def io.vertx.ext.mongo.MongoClient delegate;
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
  public static MongoClient createNonShared(Vertx vertx, Map<String, Object> config) {
    def ret= new io.vertx.groovy.ext.mongo.MongoClient(io.vertx.ext.mongo.MongoClient.createNonShared((io.vertx.core.Vertx)vertx.getDelegate(), config != null ? new io.vertx.core.json.JsonObject(config) : null));
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
    def ret= new io.vertx.groovy.ext.mongo.MongoClient(io.vertx.ext.mongo.MongoClient.createShared((io.vertx.core.Vertx)vertx.getDelegate(), config != null ? new io.vertx.core.json.JsonObject(config) : null, dataSourceName));
    return ret;
  }
  /**
   * Like {@link io.vertx.groovy.ext.mongo.MongoClient#createShared} but with the default data source name
   * @param vertx the Vert.x instance
   * @param config the configuration
   * @return the client
   */
  public static MongoClient createShared(Vertx vertx, Map<String, Object> config) {
    def ret= new io.vertx.groovy.ext.mongo.MongoClient(io.vertx.ext.mongo.MongoClient.createShared((io.vertx.core.Vertx)vertx.getDelegate(), config != null ? new io.vertx.core.json.JsonObject(config) : null));
    return ret;
  }
  /**
   * Save a document in the specified collection
   * @param collection the collection
   * @param document the document
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient save(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.save(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  /**
   * Save a document in the specified collection with the specified write option
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient saveWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.saveWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
  }
  /**
   * Insert a document in the specified collection
   * @param collection the collection
   * @param document the document
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient insert(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.insert(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  /**
   * Insert a document in the specified collection with the specified write option
   * @param collection the collection
   * @param document the document
   * @param writeOption the write option to use
   * @param resultHandler result handler will be provided with the id if document didn't already have one
   * @return 
   */
  public MongoClient insertWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    this.delegate.insertWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
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
    this.delegate.update(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler);
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
    this.delegate.updateWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(new io.vertx.core.json.JsonObject(options)) : null, resultHandler);
    return this;
  }
  /**
   * Replace matching documents in the specified collection
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replace(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.replace(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler);
    return this;
  }
  /**
   * Replace matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match the documents
   * @param replace all matching documents will be replaced with this
   * @param options options to configure the replace (see <a href="../../../../../../../cheatsheet/UpdateOptions.html">UpdateOptions</a>)
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient replaceWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.replaceWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(new io.vertx.core.json.JsonObject(options)) : null, resultHandler);
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
  /**
   * Find matching documents in the specified collection, specifying options
   * @param collection the collection
   * @param query query used to match documents
   * @param options options to configure the find (see <a href="../../../../../../../cheatsheet/FindOptions.html">FindOptions</a>)
   * @param resultHandler will be provided with list of documents
   * @return 
   */
  public MongoClient findWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
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
  /**
   * Find a single matching document in the specified collection
   * @param collection the collection
   * @param query the query used to match the document
   * @param fields the fields
   * @param resultHandler will be provided with the document, if any
   * @return 
   */
  public MongoClient findOne(String collection, Map<String, Object> query, Map<String, Object> fields, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
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
  /**
   * Count matching documents in a collection.
   * @param collection the collection
   * @param query query used to match documents
   * @param resultHandler will be provided with the number of matching documents
   * @return 
   */
  public MongoClient count(String collection, Map<String, Object> query, Handler<AsyncResult<Long>> resultHandler) {
    this.delegate.count(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
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
    this.delegate.remove(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
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
    this.delegate.removeWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
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
    this.delegate.removeOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
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
    this.delegate.removeOneWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  /**
   * Create a new collection
   * @param collectionName the name of the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.createCollection(collectionName, resultHandler);
    return this;
  }
  /**
   * Get a list of all collections in the database.
   * @param resultHandler will be called with a list of collections.
   * @return 
   */
  public MongoClient getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    this.delegate.getCollections(resultHandler);
    return this;
  }
  /**
   * Drop a collection
   * @param collection the collection
   * @param resultHandler will be called when complete
   * @return 
   */
  public MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    this.delegate.dropCollection(collection, resultHandler);
    return this;
  }
  /**
   * Run an arbitrary MongoDB command.
   * @param command the command
   * @param resultHandler will be called with the result.
   * @return 
   */
  public MongoClient runCommand(Map<String, Object> command, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
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
  /**
   * Close the client and release its resources
   */
  public void close() {
    this.delegate.close();
  }
}
