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
    j_mongoService.save(collection, utils.convJSObjectToJsonObject(document), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.insert = function(collection, document, writeConcern, resultHandler) {
    j_mongoService.insert(collection, utils.convJSObjectToJsonObject(document), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.update = function(collection, query, update, writeConcern, upsert, multi, resultHandler) {
    j_mongoService.update(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(update), writeConcern, upsert, multi, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.find = function(collection, query, fields, sort, limit, skip, resultHandler) {
    j_mongoService.find(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(fields), utils.convJSObjectToJsonObject(sort), limit, skip, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.findOne = function(collection, query, fields, resultHandler) {
    j_mongoService.findOne(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(fields), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convJsonToJS(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.delete = function(collection, query, writeConcern, resultHandler) {
    j_mongoService.delete(collection, utils.convJSObjectToJsonObject(query), writeConcern, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.createCollection = function(collectionName, resultHandler) {
    j_mongoService.createCollection(collectionName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.getCollections = function(resultHandler) {
    j_mongoService.getCollections(function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.dropCollection = function(collection, resultHandler) {
    j_mongoService.dropCollection(collection, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.runCommand = function(collection, command, resultHandler) {
    j_mongoService.runCommand(collection, utils.convJSObjectToJsonObject(command), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convJsonToJS(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
  };

  this.start = function() {
    j_mongoService.start();
  };

  this.stop = function() {
    j_mongoService.stop();
  };

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_mongoService;
  }

};

MongoService.create = function(vertx, config) {
  return new MongoService(JMongoService.create(vertx._jdel(), utils.convJSObjectToJsonObject(config)));
};

MongoService.createEventBusProxy = function(vertx, address) {
  return new MongoService(JMongoService.createEventBusProxy(vertx._jdel(), address));
};

// We export the Constructor function
module.exports = MongoService;