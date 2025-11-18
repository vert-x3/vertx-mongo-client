/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo.impl;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Closeable;
import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.mongo.*;

import java.util.List;
import java.util.Objects;

public class MongoTransactionalClientImpl implements MongoTransactionalClient, Closeable {
  private final MongoClient delegate;
  private final ClientSession session;

  public MongoTransactionalClientImpl(MongoClient delegate, ClientSession session) {
    Objects.requireNonNull(delegate);
    Objects.requireNonNull(session);
    this.delegate = delegate;
    this.session = session;
  }

  @Override
  public Future<@Nullable String> save(String collection, JsonObject document) {
    return delegate.save(collection, document);
  }

  @Override
  public Future<@Nullable String> saveWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption) {
    return delegate.saveWithOptions(collection, document, writeOption);
  }

  @Override
  public Future<@Nullable String> insert(String collection, JsonObject document) {
    return delegate.insert(collection, document);
  }

  @Override
  public Future<@Nullable String> insertWithOptions(String collection, JsonObject document, @Nullable WriteOption writeOption) {
    return delegate.insertWithOptions(collection, document, writeOption);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonObject update) {
    return delegate.updateCollection(collection, query, update);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollection(String collection, JsonObject query, JsonArray update) {
    return delegate.updateCollection(collection, query, update);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options) {
    return delegate.updateCollectionWithOptions(collection, query, update, options);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> updateCollectionWithOptions(String collection, JsonObject query, JsonArray update, UpdateOptions options) {
    return delegate.updateCollectionWithOptions(collection, query, update, options);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> replaceDocuments(String collection, JsonObject query, JsonObject replace) {
    return delegate.replaceDocuments(collection, query, replace);
  }

  @Override
  public Future<@Nullable MongoClientUpdateResult> replaceDocumentsWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options) {
    return delegate.replaceDocumentsWithOptions(collection, query, replace, options);
  }

  @Override
  public Future<@Nullable MongoClientBulkWriteResult> bulkWrite(String collection, List<BulkOperation> operations) {
    return delegate.bulkWrite(collection, operations);
  }

  @Override
  public Future<@Nullable MongoClientBulkWriteResult> bulkWriteWithOptions(String collection, List<BulkOperation> operations, BulkWriteOptions bulkWriteOptions) {
    return delegate.bulkWriteWithOptions(collection, operations, bulkWriteOptions);
  }

  @Override
  public Future<List<JsonObject>> find(String collection, JsonObject query) {
    return delegate.find(collection, query);
  }

  @Override
  public ReadStream<JsonObject> findBatch(String collection, JsonObject query) {
    return delegate.findBatch(collection, query);
  }

  @Override
  public Future<List<JsonObject>> findWithOptions(String collection, JsonObject query, FindOptions options) {
    return delegate.findWithOptions(collection, query, options);
  }

  @Override
  public ReadStream<JsonObject> findBatchWithOptions(String collection, JsonObject query, FindOptions options) {
    return delegate.findBatchWithOptions(collection, query, options);
  }

  @Override
  public Future<@Nullable JsonObject> findOne(String collection, JsonObject query, @Nullable JsonObject fields) {
    return delegate.findOne(collection, query, fields);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndUpdate(String collection, JsonObject query, JsonObject update) {
    return delegate.findOneAndUpdate(collection, query, update);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndUpdateWithOptions(String collection, JsonObject query, JsonObject update, FindOptions findOptions, UpdateOptions updateOptions) {
    return delegate.findOneAndUpdateWithOptions(collection, query, update, findOptions, updateOptions);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndReplace(String collection, JsonObject query, JsonObject replace) {
    return delegate.findOneAndReplace(collection, query, replace);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndReplaceWithOptions(String collection, JsonObject query, JsonObject replace, FindOptions findOptions, UpdateOptions updateOptions) {
    return delegate.findOneAndReplaceWithOptions(collection, query, replace, findOptions, updateOptions);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndDelete(String collection, JsonObject query) {
    return delegate.findOneAndDelete(collection, query);
  }

  @Override
  public Future<@Nullable JsonObject> findOneAndDeleteWithOptions(String collection, JsonObject query, FindOptions findOptions) {
    return delegate.findOneAndDeleteWithOptions(collection, query, findOptions);
  }

  @Override
  public Future<Long> count(String collection, JsonObject query) {
    return delegate.count(collection, query);
  }

  @Override
  public Future<Long> countWithOptions(String collection, JsonObject query, CountOptions countOptions) {
    return delegate.countWithOptions(collection, query, countOptions);
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocuments(String collection, JsonObject query) {
    return delegate.removeDocuments(collection, query);
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocumentsWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption) {
    return delegate.removeDocumentsWithOptions(collection, query, writeOption);
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocument(String collection, JsonObject query) {
    return delegate.removeDocument(collection, query);
  }

  @Override
  public Future<@Nullable MongoClientDeleteResult> removeDocumentWithOptions(String collection, JsonObject query, @Nullable WriteOption writeOption) {
    return delegate.removeDocumentWithOptions(collection, query, writeOption);
  }

  @Override
  public Future<Void> createCollection(String collectionName) {
    return delegate.createCollection(collectionName);
  }

  @Override
  public Future<Void> createCollectionWithOptions(String collectionName, CreateCollectionOptions collectionOptions) {
    return delegate.createCollectionWithOptions(collectionName, collectionOptions);
  }

  @Override
  public Future<List<String>> getCollections() {
    return delegate.getCollections();
  }

  @Override
  public Future<Void> dropCollection(String collection) {
    return delegate.dropCollection(collection);
  }

  @Override
  public Future<Void> renameCollection(String oldCollectionName, String newCollectionName) {
    return delegate.renameCollection(oldCollectionName, newCollectionName);
  }

  @Override
  public Future<Void> renameCollectionWithOptions(String oldCollectionName, String newCollectionName, RenameCollectionOptions collectionOptions) {
    return delegate.renameCollectionWithOptions(oldCollectionName, newCollectionName, collectionOptions);
  }

  @Override
  public Future<Void> createIndex(String collection, JsonObject key) {
    return delegate.createIndex(collection, key);
  }

  @Override
  public Future<Void> createIndexWithOptions(String collection, JsonObject key, IndexOptions options) {
    return delegate.createIndexWithOptions(collection, key, options);
  }

  @Override
  public Future<Void> createIndexes(String collection, List<IndexModel> indexes) {
    return delegate.createIndexes(collection, indexes);
  }

  @Override
  public Future<JsonArray> listIndexes(String collection) {
    return delegate.listIndexes(collection);
  }

  @Override
  public Future<Void> dropIndex(String collection, String indexName) {
    return delegate.dropIndex(collection, indexName);
  }

  @Override
  public Future<Void> dropIndex(String collection, JsonObject key) {
    return delegate.dropIndex(collection, key);
  }

  @Override
  public Future<@Nullable JsonObject> runCommand(String commandName, JsonObject command) {
    return delegate.runCommand(commandName, command);
  }

  @Override
  public Future<JsonArray> distinct(String collection, String fieldName, String resultClassname) {
    return delegate.distinct(collection, fieldName, resultClassname);
  }

  @Override
  public Future<JsonArray> distinct(String collection, String fieldName, String resultClassname, DistinctOptions distinctOptions) {
    return delegate.distinct(collection, fieldName, resultClassname, distinctOptions);
  }

  @Override
  public Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) {
    return delegate.distinctWithQuery(collection, fieldName, resultClassname, query);
  }

  @Override
  public Future<JsonArray> distinctWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, DistinctOptions distinctOptions) {
    return delegate.distinctWithQuery(collection, fieldName, resultClassname, query, distinctOptions);
  }

  @Override
  public ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname) {
    return delegate.distinctBatch(collection, fieldName, resultClassname);
  }

  @Override
  public ReadStream<JsonObject> distinctBatch(String collection, String fieldName, String resultClassname, DistinctOptions distinctOptions) {
    return delegate.distinctBatch(collection, fieldName, resultClassname, distinctOptions);
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query) {
    return delegate.distinctBatchWithQuery(collection, fieldName, resultClassname, query);
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, DistinctOptions distinctOptions) {
    return delegate.distinctBatchWithQuery(collection, fieldName, resultClassname, query, distinctOptions);
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize) {
    return delegate.distinctBatchWithQuery(collection, fieldName, resultClassname, query, batchSize);
  }

  @Override
  public ReadStream<JsonObject> distinctBatchWithQuery(String collection, String fieldName, String resultClassname, JsonObject query, int batchSize, DistinctOptions distinctOptions) {
    return delegate.distinctBatchWithQuery(collection, fieldName, resultClassname, query, batchSize, distinctOptions);
  }

  @Override
  public ReadStream<JsonObject> aggregate(String collection, JsonArray pipeline) {
    return delegate.aggregate(collection, pipeline);
  }

  @Override
  public ReadStream<JsonObject> aggregateWithOptions(String collection, JsonArray pipeline, AggregateOptions options) {
    return delegate.aggregateWithOptions(collection, pipeline, options);
  }

  @Override
  public ReadStream<ChangeStreamDocument<JsonObject>> watch(String collection, JsonArray pipeline, boolean withUpdatedDoc, int batchSize) {
    return delegate.watch(collection, pipeline, withUpdatedDoc, batchSize);
  }

  @Override
  public Future<MongoGridFsClient> createDefaultGridFsBucketService() {
    return delegate.createDefaultGridFsBucketService();
  }

  @Override
  public Future<MongoGridFsClient> createGridFsBucketService(String bucketName) {
    return delegate.createGridFsBucketService(bucketName);
  }

  @Override
  public Future<MongoTransactionalClient> createTransactionContext() {
    return Future.failedFuture(new IllegalStateException("Cmon bruh"));
  }

  @Override
  public Future<Void> close() {
    return delegate.close();
  }

  @Override
  public void close(Completable<Void> completionHandler) {
    delegate.close();
    completionHandler.succeed();
  }

  @Override
  public Future<Void> commit() {
    final Promise<Void> promise = Promise.promise();
    session.commitTransaction().subscribe(new ClientSessionSubscriber<>(promise, session));
    return promise.future();
  }

  @Override
  public Future<Void> abort() {
    final Promise<Void> promise = Promise.promise();
    session.abortTransaction().subscribe(new ClientSessionSubscriber<>(promise, session));
    return promise.future();
  }

}
