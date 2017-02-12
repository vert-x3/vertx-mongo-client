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

/** @module vertx-mongo-js/mongo_grid_fs_download */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoGridFsDownload = Java.type('io.vertx.ext.mongo.MongoGridFsDownload');

/**
 @class
*/
var MongoGridFsDownload = function(j_val) {

  var j_mongoGridFsDownload = j_val;
  var that = this;

  /**
   Read bytes and returns them as a base 64 encoded string. Use a decoder to turn
   them back into an array of bytes.

   @public
   @param bufferSize {number} the maximum number of bytes to return 
   @param resultHandler {function} a string of base 64 encoded bytes. 
   @return {MongoGridFsDownload}
   */
  this.read = function(bufferSize, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] ==='number' && typeof __args[1] === 'function') {
      j_mongoGridFsDownload["read(java.lang.Integer,io.vertx.core.Handler)"](utils.convParamInteger(bufferSize), function(ar) {
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
   Close the client and release its resources

   @public

   */
  this.close = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mongoGridFsDownload["close()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mongoGridFsDownload;
};

MongoGridFsDownload._jclass = utils.getJavaClass("io.vertx.ext.mongo.MongoGridFsDownload");
MongoGridFsDownload._jtype = {
  accept: function(obj) {
    return MongoGridFsDownload._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(MongoGridFsDownload.prototype, {});
    MongoGridFsDownload.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
MongoGridFsDownload._create = function(jdel) {
  var obj = Object.create(MongoGridFsDownload.prototype, {});
  MongoGridFsDownload.apply(obj, arguments);
  return obj;
}
module.exports = MongoGridFsDownload;