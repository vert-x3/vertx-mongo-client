package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

@ProxyGen
@VertxGen
public interface MongoGridFsClient {

  /**
   * Deletes a file by it's ID
   *
   * @param id  the identifier of the file
   * @param resultHandler  will be called when the file is deleted
   */
  @Fluent
  MongoGridFsClient delete(String id, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Downloads a file into a buffer.
   *
   * @param fileName  the name of the file to download
   * @param resultHandler  called when a {@link MongoGridFsDownload} is ready that can be used to accept the buffer
   */
  @Fluent
  MongoGridFsClient downloadBuffer(String fileName, Handler<AsyncResult<MongoGridFsDownload>> resultHandler);

  /**
   * Downloads a file.
   *
   * @param fileName  the name of the file to download
   * @param resultHandler  called when the file is downloaded and returns the length in bytes
   */
  @Fluent
  MongoGridFsClient downloadFile(String fileName, Handler<AsyncResult<Long>> resultHandler);

  /**
   * Downloads a file and gives it a new name.
   *
   * @param fileName  the name of the file to download
   * @param newFileName  the name the file should be saved as
   * @param resultHandler  called when the file is downloaded and returns the length in bytes
   */
  @Fluent
  MongoGridFsClient downloadFileAs(String fileName, String newFileName, Handler<AsyncResult<Long>> resultHandler);

  /**
   * Drops the entire file bucket with all of its contents
   *
   * @param resultHandler  called when the bucket is dropped
   */
  @Fluent
  MongoGridFsClient drop(Handler<AsyncResult<Void>> resultHandler);

  /**
   * Finds all file ids in the bucket
   *
   * @param resultHandler  called when the list of file ids is available
   */
  @Fluent
  MongoGridFsClient findAllIds(Handler<AsyncResult<List<String>>> resultHandler);

  /**
   * Finds all file ids that match a query.
   *
   * @param query a bson query expressed as json that will be used to match files
   * @param resultHandler  called when the list of file ids is available
   */
  @Fluent
  MongoGridFsClient findIds(JsonObject query, Handler<AsyncResult<List<String>>> resultHandler);

  /**
   * Upload a file using a buffer
   *
   * @param fileName the name of the file to store in gridfs
   * @param resultHandler  a {@link MongoGridFsUpload} to interact with with whilst uploaded contents via buffer
   */
  @Fluent
  MongoGridFsClient uploadBuffer(String fileName, Handler<AsyncResult<MongoGridFsUpload>> resultHandler);

  /**
   * Upload a file using a buffer with options
   *
   * @param fileName the name of the file to store in gridfs
   * @param options {@link UploadOptions} for specifying metadata and chunk size
   * @param resultHandler  a {@link MongoGridFsUpload} to interact with with whilst uploaded contents via buffer
   */
  @Fluent
  MongoGridFsClient uploadBufferWithOptions(String fileName, UploadOptions options, Handler<AsyncResult<MongoGridFsUpload>> resultHandler);

  /**
   * Upload a file to gridfs
   *
   * @param fileName the name of the file to store in gridfs
   * @param resultHandler  the id of the file that was uploaded
   */
  @Fluent
  MongoGridFsClient uploadFile(String fileName, Handler<AsyncResult<String>> resultHandler);

  /**
   * Upload a file to gridfs with options
   *
   * @param fileName the name of the file to store in gridfs
   * @param options {@link UploadOptions} for specifying metadata and chunk size
   * @param resultHandler  the id of the file that was uploaded
   */
  @Fluent
  MongoGridFsClient uploadFileWithOptions(String fileName, UploadOptions options, Handler<AsyncResult<String>> resultHandler);

  /**
   * Close the client and release its resources
   */
  void close();
}
