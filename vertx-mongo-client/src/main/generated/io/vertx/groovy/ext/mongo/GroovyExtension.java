package io.vertx.groovy.ext.mongo;
public class GroovyExtension {
  public static io.vertx.ext.mongo.MongoClient save(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> document, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.save(collection,
      document != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(document) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.String> ar) {
        resultHandler.handle(ar.map(event -> event));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient saveWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> document, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.saveWithOptions(collection,
      document != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(document) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.String> ar) {
        resultHandler.handle(ar.map(event -> event));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient insert(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> document, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.insert(collection,
      document != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(document) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.String> ar) {
        resultHandler.handle(ar.map(event -> event));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient insertWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> document, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.insertWithOptions(collection,
      document != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(document) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.String>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.String> ar) {
        resultHandler.handle(ar.map(event -> event));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient update(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.update(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient updateCollection(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.updateCollection(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient updateWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.updateWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient updateCollectionWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.updateCollectionWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient replace(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.replace(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient replaceDocuments(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.replaceDocuments(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient replaceWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.replaceWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient replaceDocumentsWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.replaceDocumentsWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      options != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientUpdateResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient find(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<java.util.Map<String, Object>>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.find(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
      public void handle(io.vertx.core.AsyncResult<java.util.List<io.vertx.core.json.JsonObject>> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, list -> list.stream().map(elt -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(elt)).collect(java.util.stream.Collectors.toList()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findBatch(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findBatch(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<java.util.Map<String, Object>>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<io.vertx.core.json.JsonObject>>>() {
      public void handle(io.vertx.core.AsyncResult<java.util.List<io.vertx.core.json.JsonObject>> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, list -> list.stream().map(elt -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(elt)).collect(java.util.stream.Collectors.toList()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findBatchWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findBatchWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      options != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOne(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> fields, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOne(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      fields != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(fields) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndUpdate(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndUpdate(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndUpdateWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> update, java.util.Map<String, Object> findOptions, java.util.Map<String, Object> updateOptions, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndUpdateWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      update != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(update) : null,
      findOptions != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(findOptions)) : null,
      updateOptions != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(updateOptions)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndReplace(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndReplace(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndReplaceWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> replace, java.util.Map<String, Object> findOptions, java.util.Map<String, Object> updateOptions, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndReplaceWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      replace != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(replace) : null,
      findOptions != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(findOptions)) : null,
      updateOptions != null ? new io.vertx.ext.mongo.UpdateOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(updateOptions)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndDelete(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndDelete(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient findOneAndDeleteWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, java.util.Map<String, Object> findOptions, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.findOneAndDeleteWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      findOptions != null ? new io.vertx.ext.mongo.FindOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(findOptions)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient count(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Long>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.count(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Long>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Long> ar) {
        resultHandler.handle(ar.map(event -> event));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient remove(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.remove(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeDocuments(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeDocuments(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeDocumentsWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeDocumentsWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeOne(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeOne(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeDocument(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeDocument(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeOneWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeOneWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient removeDocumentWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> query, io.vertx.ext.mongo.WriteOption writeOption, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.removeDocumentWithOptions(collection,
      query != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(query) : null,
      writeOption,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.ext.mongo.MongoClientDeleteResult> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.applyIfNotNull(event, a -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(a.toJson()))));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient createIndex(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> key, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.createIndex(collection,
      key != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(key) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient createIndexWithOptions(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.util.Map<String, Object> key, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.createIndexWithOptions(collection,
      key != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(key) : null,
      options != null ? new io.vertx.ext.mongo.IndexOptions(io.vertx.lang.groovy.ConversionHelper.toJsonObject(options)) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.wrap(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient listIndexes(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.listIndexes(collection,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonArray>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonArray> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonArray(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient runCommand(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String commandName, java.util.Map<String, Object> command, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.runCommand(commandName,
      command != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(command) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient distinct(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.lang.String fieldName, java.lang.String resultClassname, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.distinct(collection,
      fieldName,
      resultClassname,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonArray>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonArray> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonArray(event)));
      }
    } : null));
    return j_receiver;
  }
  public static io.vertx.ext.mongo.MongoClient distinctBatch(io.vertx.ext.mongo.MongoClient j_receiver, java.lang.String collection, java.lang.String fieldName, java.lang.String resultClassname, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    io.vertx.lang.groovy.ConversionHelper.wrap(j_receiver.distinctBatch(collection,
      fieldName,
      resultClassname,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.core.json.JsonObject> ar) {
        resultHandler.handle(ar.map(event -> io.vertx.lang.groovy.ConversionHelper.fromJsonObject(event)));
      }
    } : null));
    return j_receiver;
  }
}
