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

/** @module vertx-mongo-js/mongo_grid_fs_upload */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoGridFsUpload = Java.type('io.vertx.ext.mongo.MongoGridFsUpload');

/**
 @class
*/
var MongoGridFsUpload = function(j_val) {

  var j_mongoGridFsUpload = j_val;
  var that = this;

  /**
   Accepts a string of base 64 encoded bytes and adds them to the file to be saved in gridfs.

   @public
   @param base64EncodedBytes {string} string of base 64 encoded bytes 
   @param resultHandler {function} the number of bytes saved to the file. 
   @return {MongoGridFsUpload}
   */
  this.uploadBuffer = function(base64EncodedBytes, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsUpload["uploadBuffer(java.lang.String,io.vertx.core.Handler)"](base64EncodedBytes, function(ar) {
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
   Ends the upload of bytes saved in gridfs.

   @public
   @param resultHandler {function} the ID of the file saved in gridfs. 
   @return {MongoGridFsUpload}
   */
  this.end = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_mongoGridFsUpload["end(io.vertx.core.Handler)"](function(ar) {
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
      j_mongoGridFsUpload["close()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mongoGridFsUpload;
};

MongoGridFsUpload._jclass = utils.getJavaClass("io.vertx.ext.mongo.MongoGridFsUpload");
MongoGridFsUpload._jtype = {
  accept: function(obj) {
    return MongoGridFsUpload._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(MongoGridFsUpload.prototype, {});
    MongoGridFsUpload.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
MongoGridFsUpload._create = function(jdel) {
  var obj = Object.create(MongoGridFsUpload.prototype, {});
  MongoGridFsUpload.apply(obj, arguments);
  return obj;
}
module.exports = MongoGridFsUpload;