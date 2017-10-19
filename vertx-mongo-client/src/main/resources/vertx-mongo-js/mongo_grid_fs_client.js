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

/** @module vertx-mongo-js/mongo_grid_fs_client */
var utils = require('vertx-js/util/utils');
var MongoGridFsDownload = require('vertx-mongo-js/mongo_grid_fs_download');
var MongoGridFsUpload = require('vertx-mongo-js/mongo_grid_fs_upload');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMongoGridFsClient = Java.type('io.vertx.ext.mongo.MongoGridFsClient');
var UploadOptions = Java.type('io.vertx.ext.mongo.UploadOptions');

/**
 @class
*/
var MongoGridFsClient = function(j_val) {

  var j_mongoGridFsClient = j_val;
  var that = this;

  /**
   Deletes a file by it's ID

   @public
   @param id {string} the identifier of the file 
   @param resultHandler {function} will be called when the file is deleted 
   @return {MongoGridFsClient}
   */
  this.delete = function(id, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsClient["delete(java.lang.String,io.vertx.core.Handler)"](id, function(ar) {
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
   Downloads a file into a buffer.

   @public
   @param fileName {string} the name of the file to download 
   @param resultHandler {function} called when a {@link MongoGridFsDownload} is ready that can be used to accept the buffer 
   @return {MongoGridFsClient}
   */
  this.downloadBuffer = function(fileName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsClient["downloadBuffer(java.lang.String,io.vertx.core.Handler)"](fileName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnVertxGen(MongoGridFsDownload, ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Downloads a file.

   @public
   @param fileName {string} the name of the file to download 
   @param resultHandler {function} called when the file is downloaded and returns the length in bytes 
   @return {MongoGridFsClient}
   */
  this.downloadFile = function(fileName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsClient["downloadFile(java.lang.String,io.vertx.core.Handler)"](fileName, function(ar) {
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
   Downloads a file and gives it a new name.

   @public
   @param fileName {string} the name of the file to download 
   @param newFileName {string} the name the file should be saved as 
   @param resultHandler {function} called when the file is downloaded and returns the length in bytes 
   @return {MongoGridFsClient}
   */
  this.downloadFileAs = function(fileName, newFileName, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_mongoGridFsClient["downloadFileAs(java.lang.String,java.lang.String,io.vertx.core.Handler)"](fileName, newFileName, function(ar) {
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
   Drops the entire file bucket with all of its contents

   @public
   @param resultHandler {function} called when the bucket is dropped 
   @return {MongoGridFsClient}
   */
  this.drop = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_mongoGridFsClient["drop(io.vertx.core.Handler)"](function(ar) {
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
   Finds all file ids in the bucket

   @public
   @param resultHandler {function} called when the list of file ids is available 
   @return {MongoGridFsClient}
   */
  this.findAllIds = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_mongoGridFsClient["findAllIds(io.vertx.core.Handler)"](function(ar) {
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
   Finds all file ids that match a query.

   @public
   @param query {Object} a bson query expressed as json that will be used to match files 
   @param resultHandler {function} called when the list of file ids is available 
   @return {MongoGridFsClient}
   */
  this.findIds = function(query, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_mongoGridFsClient["findIds(io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(query), function(ar) {
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
   Upload a file using a buffer

   @public
   @param fileName {string} the name of the file to store in gridfs 
   @param resultHandler {function} a {@link MongoGridFsUpload} to interact with with whilst uploaded contents via buffer 
   @return {MongoGridFsClient}
   */
  this.uploadBuffer = function(fileName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsClient["uploadBuffer(java.lang.String,io.vertx.core.Handler)"](fileName, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnVertxGen(MongoGridFsUpload, ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Upload a file using a buffer with options

   @public
   @param fileName {string} the name of the file to store in gridfs 
   @param options {Object} <a href="../../dataobjects.html#UploadOptions">UploadOptions</a> for specifying metadata and chunk size 
   @param resultHandler {function} a {@link MongoGridFsUpload} to interact with with whilst uploaded contents via buffer 
   @return {MongoGridFsClient}
   */
  this.uploadBufferWithOptions = function(fileName, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoGridFsClient["uploadBufferWithOptions(java.lang.String,io.vertx.ext.mongo.UploadOptions,io.vertx.core.Handler)"](fileName, options != null ? new UploadOptions(new JsonObject(Java.asJSONCompatible(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnVertxGen(MongoGridFsUpload, ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Upload a file to gridfs

   @public
   @param fileName {string} the name of the file to store in gridfs 
   @param resultHandler {function} the id of the file that was uploaded 
   @return {MongoGridFsClient}
   */
  this.uploadFile = function(fileName, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_mongoGridFsClient["uploadFile(java.lang.String,io.vertx.core.Handler)"](fileName, function(ar) {
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
   Upload a file to gridfs with options

   @public
   @param fileName {string} the name of the file to store in gridfs 
   @param options {Object} <a href="../../dataobjects.html#UploadOptions">UploadOptions</a> for specifying metadata and chunk size 
   @param resultHandler {function} the id of the file that was uploaded 
   @return {MongoGridFsClient}
   */
  this.uploadFileWithOptions = function(fileName, options, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_mongoGridFsClient["uploadFileWithOptions(java.lang.String,io.vertx.ext.mongo.UploadOptions,io.vertx.core.Handler)"](fileName, options != null ? new UploadOptions(new JsonObject(Java.asJSONCompatible(options))) : null, function(ar) {
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
      j_mongoGridFsClient["close()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mongoGridFsClient;
};

MongoGridFsClient._jclass = utils.getJavaClass("io.vertx.ext.mongo.MongoGridFsClient");
MongoGridFsClient._jtype = {
  accept: function(obj) {
    return MongoGridFsClient._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(MongoGridFsClient.prototype, {});
    MongoGridFsClient.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
MongoGridFsClient._create = function(jdel) {
  var obj = Object.create(MongoGridFsClient.prototype, {});
  MongoGridFsClient.apply(obj, arguments);
  return obj;
}
module.exports = MongoGridFsClient;