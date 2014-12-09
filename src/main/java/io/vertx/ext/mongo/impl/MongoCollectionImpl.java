/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.FindFluent;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoCollectionImpl extends AbstractMongo implements MongoCollection {
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();
  private static final String ID_FIELD = "_id";

  private final com.mongodb.async.client.MongoCollection<JsonObject> collection;

  public MongoCollectionImpl(Vertx vertx, com.mongodb.async.client.MongoCollection<JsonObject> collection) {
    super(vertx);
    this.collection = collection;
  }

  @Override
  public void save(JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    String id = document.getString(ID_FIELD);
    if (id == null) {
      this.collection.insertOne(document, convertCallback(resultHandler, wr -> document.getString(ID_FIELD)));
    } else {
      this.collection.replaceOne(new JsonObject().put(ID_FIELD, document.getString(ID_FIELD)), document, convertCallback(resultHandler, result -> null));
    }
  }

  @Override
  public void insertOne(JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    boolean id = document.containsKey(ID_FIELD);
    this.collection.insertOne(document, convertCallback(resultHandler, wr -> {
      if (id) {
        return null;
      } else {
        return document.getString(ID_FIELD);
      }
    }));
  }

  @Override
  public void insertMany(List<JsonObject> documents, boolean ordered, Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(documents, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    collection.insertMany(documents, new InsertManyOptions().ordered(ordered), convertCallback(resultHandler, v ->
        documents.stream()
          .map(doc -> doc.getString(ID_FIELD))
          .collect(Collectors.toList())
    ));
  }

  @Override
  public void updateOne(JsonObject query, JsonObject update, boolean upsert, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    UpdateOptions updateOptions = new UpdateOptions().upsert(upsert);
    collection.updateOne(query, update, updateOptions, convertCallback(resultHandler, result -> null));
  }

  @Override
  public void updateMany(JsonObject query, JsonObject update, boolean upsert, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    UpdateOptions updateOptions = new UpdateOptions().upsert(upsert);
    collection.updateMany(query, update, updateOptions, convertCallback(resultHandler, result -> null));
  }

  @Override
  public void replaceOne(JsonObject query, JsonObject replace, boolean upsert, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(replace, "update cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");


    this.collection.replaceOne(query, replace, new UpdateOptions().upsert(upsert), convertCallback(resultHandler, result -> null));
  }

  @Override
  public void find(JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(query, DEFAULT_FIND_OPTIONS, resultHandler);
  }

  @Override
  public void findWithOptions(JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindFluent<JsonObject> view = doFind(query, options);
    List<JsonObject> results = new ArrayList<>();
    view.into(results, wrapCallback(resultHandler));
  }

  @Override
  public void findOne(JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    collection.find(query).projection(fields).first(wrapCallback(resultHandler));
  }

  @Override
  public void count(JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    collection.count(query, wrapCallback(resultHandler));
  }

  @Override
  public void deleteMany(JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    collection.deleteMany(query, convertCallback(resultHandler, result -> null));
  }

  @Override
  public void deleteOne(JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    this.collection.deleteOne(query, convertCallback(resultHandler, result -> null));
  }

  protected FindFluent<JsonObject> doFind(JsonObject query, FindOptions options) {
    FindFluent<JsonObject> find = collection.find(query, JsonObject.class);
    if (options.getLimit() != -1) {
      find.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      find.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      find.sort(options.getSort());
    }
    if (options.getFields() != null) {
      find.projection(options.getFields());
    }
    return find;
  }
}
