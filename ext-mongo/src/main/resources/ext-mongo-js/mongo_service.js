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
var FindOptions = io.vertx.ext.mongo.FindOptions;
var UpdateOptions = io.vertx.ext.mongo.UpdateOptions;

/**

  @class
*/
var MongoService = function(j_val) {

  var j_mongoService = j_val;
  var that = this;

  this.save = function(collection, document, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.save(collection, utils.convJSObjectToJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.saveWithOptions = function(collection, document, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.saveWithOptions(collection, utils.convJSObjectToJsonObject(document), io.vertx.ext.mongo.WriteOption.valueOf(__args[2]), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.insert = function(collection, document, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.insert(collection, utils.convJSObjectToJsonObject(document), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.insertWithOptions = function(collection, document, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.insertWithOptions(collection, utils.convJSObjectToJsonObject(document), io.vertx.ext.mongo.WriteOption.valueOf(__args[2]), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.update = function(collection, query, update, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'function') {
      j_mongoService.update(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(update), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.updateWithOptions = function(collection, query, update, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'object' && typeof __args[4] === 'function') {
      j_mongoService.updateWithOptions(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(update), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.replace = function(collection, query, replace, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'function') {
      j_mongoService.replace(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(replace), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.replaceWithOptions = function(collection, query, replace, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 5 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'object' && typeof __args[4] === 'function') {
      j_mongoService.replaceWithOptions(collection, utils.convJSObjectToJsonObject(query), utils.convJSObjectToJsonObject(replace), options != null ? new UpdateOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.find = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.find(collection, utils.convJSObjectToJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.findWithOptions = function(collection, query, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'object' && typeof __args[3] === 'function') {
      j_mongoService.findWithOptions(collection, utils.convJSObjectToJsonObject(query), options != null ? new FindOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
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

  this.count = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.count(collection, utils.convJSObjectToJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.remove = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.remove(collection, utils.convJSObjectToJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.removeWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.removeWithOptions(collection, utils.convJSObjectToJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(__args[2]), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.removeOne = function(collection, query, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'function') {
      j_mongoService.removeOne(collection, utils.convJSObjectToJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  this.removeOneWithOptions = function(collection, query, writeOption, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_mongoService.removeOneWithOptions(collection, utils.convJSObjectToJsonObject(query), io.vertx.ext.mongo.WriteOption.valueOf(__args[2]), function(ar) {
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

  this.runCommand = function(command, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_mongoService.runCommand(utils.convJSObjectToJsonObject(command), function(ar) {
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