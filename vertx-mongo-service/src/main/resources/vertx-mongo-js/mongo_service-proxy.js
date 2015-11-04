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

/** @module vertx-mongo-js/mongo_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-mongo-js/mongo_service-proxy', [], factory);
  } else {
    // plain old include
    MongoService = factory();
  }
}(function () {

  /**

 @class
  */
  var MongoService = function(eb, address) {

    var j_eb = eb;
    var j_address = address;
    var closed = false;
    var that = this;
    var convCharCollection = function(coll) {
      var ret = [];
      for (var i = 0;i < coll.length;i++) {
        ret.push(String.fromCharCode(coll[i]));
      }
      return ret;
    };
  MongoClient.call(this, j_val);

    /**

     @public
     @param collection {string} 
     @param document {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.save = function(collection, document, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "document":__args[1]}, {"action":"save"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param document {Object} 
     @param writeOption {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.saveWithOptions = function(collection, document, writeOption, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "document":__args[1], "writeOption":__args[2]}, {"action":"saveWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param document {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.insert = function(collection, document, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "document":__args[1]}, {"action":"insert"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param document {Object} 
     @param writeOption {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.insertWithOptions = function(collection, document, writeOption, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "document":__args[1], "writeOption":__args[2]}, {"action":"insertWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param update {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.update = function(collection, query, update, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "update":__args[2]}, {"action":"update"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param update {Object} 
     @param options {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.updateWithOptions = function(collection, query, update, options, resultHandler) {
      var __args = arguments;
      if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "update":__args[2], "options":__args[3]}, {"action":"updateWithOptions"}, function(err, result) { __args[4](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param replace {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.replace = function(collection, query, replace, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "replace":__args[2]}, {"action":"replace"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param replace {Object} 
     @param options {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.replaceWithOptions = function(collection, query, replace, options, resultHandler) {
      var __args = arguments;
      if (__args.length === 5 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && (typeof __args[3] === 'object' && __args[3] != null) && typeof __args[4] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "replace":__args[2], "options":__args[3]}, {"action":"replaceWithOptions"}, function(err, result) { __args[4](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.find = function(collection, query, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1]}, {"action":"find"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.findBatch = function(collection, query, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1]}, {"action":"findBatch"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param options {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.findWithOptions = function(collection, query, options, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "options":__args[2]}, {"action":"findWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param options {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.findBatchWithOptions = function(collection, query, options, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "options":__args[2]}, {"action":"findBatchWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param fields {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.findOne = function(collection, query, fields, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && (typeof __args[2] === 'object' && __args[2] != null) && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "fields":__args[2]}, {"action":"findOne"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.count = function(collection, query, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1]}, {"action":"count"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.remove = function(collection, query, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1]}, {"action":"remove"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param writeOption {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.removeWithOptions = function(collection, query, writeOption, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "writeOption":__args[2]}, {"action":"removeWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.removeOne = function(collection, query, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1]}, {"action":"removeOne"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param query {Object} 
     @param writeOption {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.removeOneWithOptions = function(collection, query, writeOption, resultHandler) {
      var __args = arguments;
      if (__args.length === 4 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0], "query":__args[1], "writeOption":__args[2]}, {"action":"removeOneWithOptions"}, function(err, result) { __args[3](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collectionName {string} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.createCollection = function(collectionName, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collectionName":__args[0]}, {"action":"createCollection"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.getCollections = function(resultHandler) {
      var __args = arguments;
      if (__args.length === 1 && typeof __args[0] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action":"getCollections"}, function(err, result) { __args[0](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param collection {string} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.dropCollection = function(collection, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"collection":__args[0]}, {"action":"dropCollection"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param commandName {string} 
     @param command {Object} 
     @param resultHandler {function} 
     @return {MongoService}
     */
    this.runCommand = function(commandName, command, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"commandName":__args[0], "command":__args[1]}, {"action":"runCommand"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  /**
   Create a proxy to a service that is deployed somewhere on the event bus

   @memberof module:vertx-mongo-js/mongo_service
   @param vertx {Vertx} the Vert.x instance 
   @param address {string} the address the service is listening on on the event bus 
   @return {MongoService} the service
   */
  MongoService.createEventBusProxy = function(vertx, address) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'string') {
      if (closed) {
        throw new Error('Proxy is closed');
      }
      j_eb.send(j_address, {"vertx":__args[0], "address":__args[1]}, {"action":"createEventBusProxy"});
      return;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = MongoService;
    } else {
      exports.MongoService = MongoService;
    }
  } else {
    return MongoService;
  }
});