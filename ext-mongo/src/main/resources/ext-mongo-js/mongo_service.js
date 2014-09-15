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

var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoService = io.vertx.ext.mongo.MongoService;

/**

  @class
*/
var MongoService = function(j_val) {

  var j_mongoService = j_val;
  var that = this;

  this.save = function(collection, document, writeConcern, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.save(collection, utils.convJSObjectToJsonObject(document), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.insert = function(collection, document, writeConcern, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.insert(collection, utils.convJSObjectToJsonObject(document), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.update = function(collection, query, update, writeConcern, upsert, multi, resultHandler) {
    var __args = arguments;
    if (__args.length === 7 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'string' && typeof __args[4] ==='boolean' && typeof __args[5] ==='boolean' && typeof __args[6] === 'function') {
      j_mongoService.update(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(update), writeConcern, upsert, multi, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.find = function(collection, query, fields, sort, limit, skip, resultHandler) {
    var __args = arguments;
    if (__args.length === 7 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'object' && typeof __args[4] ==='number' && typeof __args[5] ==='number' && typeof __args[6] === 'function') {
      j_mongoService.find(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(fields), utils.convJSObjectToJsonObject(sort), limit, skip, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.findOne = function(collection, query, fields, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'function') {
      j_mongoService.findOne(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(fields), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convJsonToJS(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.delete = function(collection, query, writeConcern, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.delete(collection, utils.convJSObjectToJsonObject(query), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.createCollection = function(collectionName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoService.createCollection(collectionName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.getCollections = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_mongoService.getCollections(function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.dropCollection = function(collection, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoService.dropCollection(collection, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.runCommand = function(collection, command, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.runCommand(collection, utils.convJSObjectToJsonObject(command), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convJsonToJS(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.start = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mongoService.start();
    } else utils.invalidArgs();
  };

  this.stop = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mongoService.stop();
    } else utils.invalidArgs();
  };

  this._vertxgen = true;

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_mongoService;
  }

};

MongoService.create = function(vertx, config) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._vertxgen && typeof __args[1] === 'object') {
    return new MongoService(JMongoService.create(vertx._jdel(), utils.convJSObjectToJsonObject(config)));
  } else utils.invalidArgs();
};

MongoService.createEventBusProxy = function(vertx, address) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._vertxgen && typeof __args[1] === 'string') {
    return new MongoService(JMongoService.createEventBusProxy(vertx._jdel(), address));
  } else utils.invalidArgs();
};

// We export the Constructor function
module.exports = MongoService;