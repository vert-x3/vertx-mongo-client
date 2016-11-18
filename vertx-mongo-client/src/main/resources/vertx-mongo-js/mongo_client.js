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

/** @module vertx-mongo-js/mongo_client */
var utils = require('vertx-js/util/utils');
var Vertx = require('vertx-js/vertx');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoClient = io.vertx.ext.mongo.MongoClient;
var MongoClientDeleteResult = io.vertx.ext.mongo.MongoClientDeleteResult;
var IndexOptions = io.vertx.ext.mongo.IndexOptions;
var FindOptions = io.vertx.ext.mongo.FindOptions;
var MongoClientUpdateResult = io.vertx.ext.mongo.MongoClientUpdateResult;
var UpdateOptions = io.vertx.ext.mongo.UpdateOptions;

/**
 A Vert.x service used to interact with MongoDB server instances.
 <p>
 @class
*/
var MongoClient = function(j_val) {

  var j_mongoClient = j_val;
  var that = this;

  /**
   Save a document in the specified collection
   <p>
   This operation might change <i>_id</i> field of <i>document</i> parameter

   @public
   @param collection {string} the collection 
   @param document {Object} the document 
   @param resultHandler {function} result handler will be provided with the id if document didn't already have one 
   @return {MongoClient}
   */
  this.save = function(collection, document, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["save(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Save a document in the specified collection with the specified write option
   <p>
   This operation might change <i>_id</i> field of <i>document</i> parameter

   @public
   @param collection {string} the collection 
   @param document {Object} the document 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} result handler will be provided with the id if document didn't already have one 
   @return {MongoClient}
   */
  this.saveWithOptions = function(collection, document, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["saveWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(document), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Insert a document in the specified collection
   <p>
   This operation might change <i>_id</i> field of <i>document</i> parameter

   @public
   @param collection {string} the collection 
   @param document {Object} the document 
   @param resultHandler {function} result handler will be provided with the id if document didn't already have one 
   @return {MongoClient}
   */
  this.insert = function(collection, document, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["insert(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Insert a document in the specified collection with the specified write option
   <p>
   This operation might change <i>_id</i> field of <i>document</i> parameter

   @public
   @param collection {string} the collection 
   @param document {Object} the document 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} result handler will be provided with the id if document didn't already have one 
   @return {MongoClient}
   */
  this.insertWithOptions = function(collection, document, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["insertWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(document), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Update matching documents in the specified collection

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param update {Object} used to describe how the documents will be updated 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.update = function(collection, query, update, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["update(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param update {Object} used to describe how the documents will be updated 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.updateCollection = function(collection, query, update, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["updateCollection(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Update matching documents in the specified collection, specifying options

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param update {Object} used to describe how the documents will be updated 
   @param options {Object} options to configure the update 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.updateWithOptions = function(collection, query, update, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
      j_mongoClient["updateWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param update {Object} used to describe how the documents will be updated 
   @param options {Object} options to configure the update 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.updateCollectionWithOptions = function(collection, query, update, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
      j_mongoClient["updateCollectionWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Replace matching documents in the specified collection
   <p>
   This operation might change <i>_id</i> field of <i>replace</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param replace {Object} all matching documents will be replaced with this 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.replace = function(collection, query, replace, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["replace(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param replace {Object} all matching documents will be replaced with this 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.replaceDocuments = function(collection, query, replace, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["replaceDocuments(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Replace matching documents in the specified collection, specifying options
   <p>
   This operation might change <i>_id</i> field of <i>replace</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param replace {Object} all matching documents will be replaced with this 
   @param options {Object} options to configure the replace 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.replaceWithOptions = function(collection, query, replace, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
      j_mongoClient["replaceWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match the documents 
   @param replace {Object} all matching documents will be replaced with this 
   @param options {Object} options to configure the replace 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.replaceDocumentsWithOptions = function(collection, query, replace, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
      j_mongoClient["replaceDocumentsWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find matching documents in the specified collection

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param resultHandler {function} will be provided with list of documents 
   @return {MongoClient}
   */
  this.find = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["find(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find matching documents in the specified collection.
   This method use batchCursor for returning each found document.

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param resultHandler {function} will be provided with each found document 
   @return {MongoClient}
   */
  this.findBatch = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["findBatch(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find matching documents in the specified collection, specifying options

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param options {Object} options to configure the find 
   @param resultHandler {function} will be provided with list of documents 
   @return {MongoClient}
   */
  this.findWithOptions = function(collection, query, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.FindOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), options != null ? new FindOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find matching documents in the specified collection, specifying options.
   This method use batchCursor for returning each found document.

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param options {Object} options to configure the find 
   @param resultHandler {function} will be provided with each found document 
   @return {MongoClient}
   */
  this.findBatchWithOptions = function(collection, query, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findBatchWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.FindOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), options != null ? new FindOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param fields {Object} the fields 
   @param resultHandler {function} will be provided with the document, if any 
   @return {MongoClient}
   */
  this.findOne = function(collection, query, fields, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findOne(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(fields), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and update it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param update {Object} used to describe how the documents will be updated 
   @param resultHandler {function} will be provided with the document, if any 
   @return {MongoClient}
   */
  this.findOneAndUpdate = function(collection, query, update, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findOneAndUpdate(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and update it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param update {Object} used to describe how the documents will be updated 
   @param findOptions {Object} options to configure the find 
   @param updateOptions {Object} options to configure the update 
   @param resultHandler {function} will be provided with the document, if any 
   @return {MongoClient}
   */
  this.findOneAndUpdateWithOptions = function(collection, query, update, findOptions, updateOptions, resultHandler) {
    var __args = arguments;
    if (__args.length === 6 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && (typeof __args[4] === 'object' && __args[4] != null) && typeof __args[5] === 'function') {
      j_mongoClient["findOneAndUpdateWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.FindOptions,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(update), findOptions != null ? new FindOptions(new JsonObject(JSON.stringify(findOptions))) : null, updateOptions != null ? new UpdateOptions(new JsonObject(JSON.stringify(updateOptions))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and replace it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param replace {Object} the replacement document 
   @param resultHandler {function} will be provided with the document, if any 
   @return {MongoClient}
   */
  this.findOneAndReplace = function(collection, query, replace, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findOneAndReplace(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and replace it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param replace {Object} the replacement document 
   @param findOptions {Object} options to configure the find 
   @param updateOptions {Object} options to configure the update 
   @param resultHandler {function} will be provided with the document, if any 
   @return {MongoClient}
   */
  this.findOneAndReplaceWithOptions = function(collection, query, replace, findOptions, updateOptions, resultHandler) {
    var __args = arguments;
    if (__args.length === 6 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && (typeof __args[4] === 'object' && __args[4] != null) && typeof __args[5] === 'function') {
      j_mongoClient["findOneAndReplaceWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.FindOptions,io.vertx.ext.mongo.UpdateOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), utils.convParamJsonObject(replace), findOptions != null ? new FindOptions(new JsonObject(JSON.stringify(findOptions))) : null, updateOptions != null ? new UpdateOptions(new JsonObject(JSON.stringify(updateOptions))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and delete it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param resultHandler {function} will be provided with the deleted document, if any 
   @return {MongoClient}
   */
  this.findOneAndDelete = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["findOneAndDelete(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Find a single matching document in the specified collection and delete it.
   <p>
   This operation might change <i>_id</i> field of <i>query</i> parameter

   @public
   @param collection {string} the collection 
   @param query {Object} the query used to match the document 
   @param findOptions {Object} options to configure the find 
   @param resultHandler {function} will be provided with the deleted document, if any 
   @return {MongoClient}
   */
  this.findOneAndDeleteWithOptions = function(collection, query, findOptions, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["findOneAndDeleteWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.FindOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), findOptions != null ? new FindOptions(new JsonObject(JSON.stringify(findOptions))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Count matching documents in a collection.

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param resultHandler {function} will be provided with the number of matching documents 
   @return {MongoClient}
   */
  this.count = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["count(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnLong(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove matching documents from a collection

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.remove = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["remove(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove matching documents from a collection and return the handler with MongoClientDeleteResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeDocuments = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["removeDocuments(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove matching documents from a collection with the specified write option

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["removeWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match documents 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeDocumentsWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["removeDocumentsWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove a single matching document from a collection

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match document 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeOne = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["removeOne(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match document 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeDocument = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["removeDocument(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove a single matching document from a collection with the specified write option

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match document 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeOneWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["removeOneWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result

   @public
   @param collection {string} the collection 
   @param query {Object} query used to match document 
   @param writeOption {Object} the write option to use 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.removeDocumentWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["removeDocumentWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.WriteOption,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(writeOption), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Create a new collection

   @public
   @param collectionName {string} the name of the collection 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.createCollection = function(collectionName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoClient["createCollection(java.lang.String,io.vertx.core.Handler)"](collectionName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Get a list of all collections in the database.

   @public
   @param resultHandler {function} will be called with a list of collections. 
   @return {MongoClient}
   */
  this.getCollections = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_mongoClient["getCollections(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Drop a collection

   @public
   @param collection {string} the collection 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.dropCollection = function(collection, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoClient["dropCollection(java.lang.String,io.vertx.core.Handler)"](collection, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Creates an index.

   @public
   @param collection {string} the collection 
   @param key {Object} A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1. 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.createIndex = function(collection, key, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["createIndex(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(key), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Creates an index.

   @public
   @param collection {string} the collection 
   @param key {Object} A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1. 
   @param options {Object} the options for the index 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.createIndexWithOptions = function(collection, key, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
      j_mongoClient["createIndexWithOptions(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.ext.mongo.IndexOptions,io.vertx.core.Handler)"](collection, utils.convParamJsonObject(key), options != null ? new IndexOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Get all the indexes in this collection.

   @public
   @param collection {string} the collection 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.listIndexes = function(collection, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoClient["listIndexes(java.lang.String,io.vertx.core.Handler)"](collection, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Drops the index given its name.

   @public
   @param collection {string} the collection 
   @param indexName {string} the name of the index to remove 
   @param resultHandler {function} will be called when complete 
   @return {MongoClient}
   */
  this.dropIndex = function(collection, indexName, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_mongoClient["dropIndex(java.lang.String,java.lang.String,io.vertx.core.Handler)"](collection, indexName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Run an arbitrary MongoDB command.

   @public
   @param commandName {string} the name of the command 
   @param command {Object} the command 
   @param resultHandler {function} will be called with the result. 
   @return {MongoClient}
   */
  this.runCommand = function(commandName, command, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoClient["runCommand(java.lang.String,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](commandName, utils.convParamJsonObject(command), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Gets the distinct values of the specified field name.
   Return a JsonArray containing distinct values (eg: [ 1 , 89 ])

   @public
   @param collection {string} the collection 
   @param fieldName {string} the field name 
   @param resultClassname {string} 
   @param resultHandler {function} will be provided with array of values. 
   @return {MongoClient}
   */
  this.distinct = function(collection, fieldName, resultClassname, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["distinct(java.lang.String,java.lang.String,java.lang.String,io.vertx.core.Handler)"](collection, fieldName, resultClassname, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Gets the distinct values of the specified field name.
   This method use batchCursor for returning each found value.
   Each value is a json fragment with fieldName key (eg: {"num": 1}).

   @public
   @param collection {string} the collection 
   @param fieldName {string} the field name 
   @param resultClassname {string} 
   @param resultHandler {function} will be provided with each found value 
   @return {MongoClient}
   */
  this.distinctBatch = function(collection, fieldName, resultClassname, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoClient["distinctBatch(java.lang.String,java.lang.String,java.lang.String,io.vertx.core.Handler)"](collection, fieldName, resultClassname, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Close the client and release its resources

   @public

   */
  this.close = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mongoClient["close()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mongoClient;
};

MongoClient._jclass = utils.getJavaClass("io.vertx.ext.mongo.MongoClient");
MongoClient._jtype = {
  accept: function(obj) {
    return MongoClient._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(MongoClient.prototype, {});
    MongoClient.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
MongoClient._create = function(jdel) {
  var obj = Object.create(MongoClient.prototype, {});
  MongoClient.apply(obj, arguments);
  return obj;
}
/**
 Create a Mongo client which maintains its own data source.

 @memberof module:vertx-mongo-js/mongo_client
 @param vertx {Vertx} the Vert.x instance 
 @param config {Object} the configuration 
 @return {MongoClient} the client
 */
MongoClient.createNonShared = function(vertx, config) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && (typeof __args[1] === 'object' && __args[1] != null)) {
    return utils.convReturnVertxGen(MongoClient, JMongoClient["createNonShared(io.vertx.core.Vertx,io.vertx.core.json.JsonObject)"](vertx._jdel, utils.convParamJsonObject(config)));
  } else throw new TypeError('function invoked with invalid arguments');
};

/**
 Create a Mongo client which shares its data source with any other Mongo clients created with the same
 data source name

 @memberof module:vertx-mongo-js/mongo_client
 @param vertx {Vertx} the Vert.x instance 
 @param config {Object} the configuration 
 @param dataSourceName {string} the data source name 
 @return {MongoClient} the client
 */
MongoClient.createShared = function() {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && (typeof __args[1] === 'object' && __args[1] != null)) {
    return utils.convReturnVertxGen(MongoClient, JMongoClient["createShared(io.vertx.core.Vertx,io.vertx.core.json.JsonObject)"](__args[0]._jdel, utils.convParamJsonObject(__args[1])));
  }else if (__args.length === 3 && typeof __args[0] === 'object' && __args[0]._jdel && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string') {
    return utils.convReturnVertxGen(MongoClient, JMongoClient["createShared(io.vertx.core.Vertx,io.vertx.core.json.JsonObject,java.lang.String)"](__args[0]._jdel, utils.convParamJsonObject(__args[1]), __args[2]));
  } else throw new TypeError('function invoked with invalid arguments');
};

module.exports = MongoClient;