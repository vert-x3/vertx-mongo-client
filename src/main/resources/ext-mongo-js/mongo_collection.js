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

/** @module ext-mongo-js/mongo_collection */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoCollection = io.vertx.ext.mongo.MongoCollection;
var FindOptions = io.vertx.ext.mongo.FindOptions;

/**

 @class
*/
var MongoCollection = function(j_val) {

  var j_mongoCollection = j_val;
  var that = this;

  /**

   @public
   @param document {Object} 
   @param handler {function} 
   */
  this.save = function(document, handler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.save(utils.convParamJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        handler(ar.result(), null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param document {Object} 
   @param resultHandler {function} 
   */
  this.insertOne = function(document, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.insertOne(utils.convParamJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param document {Array.<Object>} 
   @param ordered {boolean} 
   @param resultHandler {function} 
   */
  this.insertMany = function(document, ordered, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'object' && __args[0] instanceof Array && typeof __args[1] ==='boolean' && typeof __args[2] === 'function') {
      j_mongoCollection.insertMany(utils.convParamListJsonObject(document), ordered, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param update {Object} 
   @param upsert {boolean} 
   @param resultHandler {function} 
   */
  this.updateOne = function(query, update, upsert, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'object' && typeof __args[1] === 'object' && typeof __args[2] ==='boolean' && typeof __args[3] === 'function') {
      j_mongoCollection.updateOne(utils.convParamJsonObject(query), utils.convParamJsonObject(update), upsert, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param update {Object} 
   @param upsert {boolean} 
   @param resultHandler {function} 
   */
  this.updateMany = function(query, update, upsert, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'object' && typeof __args[1] === 'object' && typeof __args[2] ==='boolean' && typeof __args[3] === 'function') {
      j_mongoCollection.updateMany(utils.convParamJsonObject(query), utils.convParamJsonObject(update), upsert, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param replace {Object} 
   @param upsert {boolean} 
   @param resultHandler {function} 
   */
  this.replaceOne = function(query, replace, upsert, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'object' && typeof __args[1] === 'object' && typeof __args[2] ==='boolean' && typeof __args[3] === 'function') {
      j_mongoCollection.replaceOne(utils.convParamJsonObject(query), utils.convParamJsonObject(replace), upsert, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param resultHandler {function} 
   */
  this.find = function(query, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.find(utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param options {Object} 
   @param resultHandler {function} 
   */
  this.findWithOptions = function(query, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'object' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoCollection.findWithOptions(utils.convParamJsonObject(query), options != null ? new FindOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param fields {Object} 
   @param resultHandler {function} 
   */
  this.findOne = function(query, fields, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'object' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoCollection.findOne(utils.convParamJsonObject(query), utils.convParamJsonObject(fields), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param resultHandler {function} 
   */
  this.count = function(query, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.count(utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param resultHandler {function} 
   */
  this.deleteOne = function(query, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.deleteOne(utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**

   @public
   @param query {Object} 
   @param resultHandler {function} 
   */
  this.deleteMany = function(query, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoCollection.deleteMany(utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mongoCollection;
};

// We export the Constructor function
module.exports = MongoCollection;