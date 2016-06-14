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
import io.vertx.ext.mongo.MongoClientDeleteResult
import io.vertx.ext.mongo.WriteOption
import io.vertx.groovy.core.Vertx
import io.vertx.core.json.JsonArray
import java.util.List
import io.vertx.ext.mongo.FindOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.mongo.MongoClientUpdateResult
import io.vertx.ext.mongo.UpdateOptions
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
*/
@CompileStatic
public class MongoService extends MongoClient {
  private final def io.vertx.ext.mongo.MongoService delegate;
  public MongoService(Object delegate) {
    super((io.vertx.ext.mongo.MongoService) delegate);
    this.delegate = (io.vertx.ext.mongo.MongoService) delegate;
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
    def ret = InternalHelper.safeCreate(io.vertx.ext.mongo.MongoService.createEventBusProxy(vertx != null ? (io.vertx.core.Vertx)vertx.getDelegate() : null, address), io.vertx.groovy.ext.mongo.MongoService.class);
    return ret;
  }
  public MongoService save(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).save(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  public MongoService saveWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).saveWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService insert(String collection, Map<String, Object> document, Handler<AsyncResult<String>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).insert(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, resultHandler);
    return this;
  }
  public MongoService insertWithOptions(String collection, Map<String, Object> document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).insertWithOptions(collection, document != null ? new io.vertx.core.json.JsonObject(document) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService update(String collection, Map<String, Object> query, Map<String, Object> update, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).update(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler);
    return this;
  }
  public MongoService updateCollection(String collection, Map<String, Object> query, Map<String, Object> update, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).updateCollection(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
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
  public MongoService updateWithOptions(String collection, Map<String, Object> query, Map<String, Object> update, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).updateWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler);
    return this;
  }
  public MongoService updateCollectionWithOptions(String collection, Map<String, Object> query, Map<String, Object> update, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).updateCollectionWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, update != null ? new io.vertx.core.json.JsonObject(update) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
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
  public MongoService replace(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).replace(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler);
    return this;
  }
  public MongoService replaceDocuments(String collection, Map<String, Object> query, Map<String, Object> replace, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).replaceDocuments(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
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
  public MongoService replaceWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).replaceWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler);
    return this;
  }
  public MongoService replaceDocumentsWithOptions(String collection, Map<String, Object> query, Map<String, Object> replace, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).replaceDocumentsWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, replace != null ? new io.vertx.core.json.JsonObject(replace) : null, options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
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
  public MongoService find(String collection, Map<String, Object> query, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).find(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
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
  public MongoService findBatch(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).findBatch(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
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
  public MongoService findWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<List<Map<String, Object>>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).findWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
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
  public MongoService findBatchWithOptions(String collection, Map<String, Object> query, Map<String, Object> options, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).findBatchWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.InternalHelper.toJsonObject(options)) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
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
  public MongoService findOne(String collection, Map<String, Object> query, Map<String, Object> fields, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).findOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, fields != null ? new io.vertx.core.json.JsonObject(fields) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
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
  public MongoService count(String collection, Map<String, Object> query, Handler<AsyncResult<Long>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).count(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService remove(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).remove(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService removeDocuments(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeDocuments(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
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
  public MongoService removeWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService removeDocumentsWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeDocumentsWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
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
  public MongoService removeOne(String collection, Map<String, Object> query, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeOne(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler);
    return this;
  }
  public MongoService removeDocument(String collection, Map<String, Object> query, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeDocument(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
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
  public MongoService removeOneWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeOneWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler);
    return this;
  }
  public MongoService removeDocumentWithOptions(String collection, Map<String, Object> query, WriteOption writeOption, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).removeDocumentWithOptions(collection, query != null ? new io.vertx.core.json.JsonObject(query) : null, writeOption, resultHandler != null ? new Handler<AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
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
  public MongoService createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).createCollection(collectionName, resultHandler);
    return this;
  }
  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).getCollections(resultHandler != null ? new Handler<AsyncResult<java.util.List<java.lang.String>>>() {
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
  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).dropCollection(collection, resultHandler);
    return this;
  }
  public MongoService runCommand(String commandName, Map<String, Object> command, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).runCommand(commandName, command != null ? new io.vertx.core.json.JsonObject(command) : null, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
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
  public MongoService distinct(String collection, String fieldName, String resultClassname, Handler<AsyncResult<List<Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).distinct(collection, fieldName, resultClassname, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonArray>>() {
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
  public MongoService distinctBatch(String collection, String fieldName, String resultClassname, Handler<AsyncResult<Map<String, Object>>> resultHandler) {
    ((io.vertx.ext.mongo.MongoClient) delegate).distinctBatch(collection, fieldName, resultClassname, resultHandler != null ? new Handler<AsyncResult<io.vertx.core.json.JsonObject>>() {
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
  public void close() {
    ((io.vertx.ext.mongo.MongoClient) delegate).close();
  }
}
