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

package io.vertx.ext.mongo.tests;

import io.vertx.core.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class MongoTestBase extends VertxTestBase {

  protected static String getConnectionString() {
    return getProperty("connection_string");
  }

  protected static String getDatabaseName() {
    return getProperty("db_name");
  }

  protected static String getProperty(String name) {
    String s = System.getProperty(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }

    return null;
  }

  private static MongoDBContainer mongoDBContainer;

  @BeforeClass
  public static void startMongo() {
    int port = 27018;
    mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.15"));
    mongoDBContainer.setPortBindings(Collections.singletonList(port + ":27017"));
    mongoDBContainer.start();
  }

  @AfterClass
  public static void stopMongo() {
    mongoDBContainer.stop();
  }

  protected static JsonObject getConfig() {
    JsonObject config = new JsonObject();
    String connectionString = getConnectionString();
    if (connectionString != null) {
      config.put("connection_string", connectionString);
    } else {
      config.put("connection_string", "mongodb://localhost:27018");
    }
    String databaseName = getDatabaseName();
    if (databaseName != null) {
      config.put("db_name", databaseName);
    }
    return config;
  }

  protected void dropCollections(MongoClient mongoClient, CountDownLatch latch) {
    // Drop all the collections in the db
    mongoClient.getCollections().onComplete(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections((List) list);
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongoClient.dropCollection(collection).onComplete(onSuccess(v -> {
            if (collCount.incrementAndGet() == count) {
              latch.countDown();
            }
          }));
        }
      } else {
        latch.countDown();
      }
    }));
  }

  protected List<String> getOurCollections(List<String> colls) {
    List<String> ours = new ArrayList<>();
    for (String coll : colls) {
      if (coll.startsWith("ext-mongo")) {
        ours.add(coll);
      }
    }
    return ours;
  }

  protected String randomCollection() {
    return "ext-mongo" + TestUtils.randomAlphaString(20);
  }

  protected void insertDocs(MongoClient mongoClient, String collection, int num, Completable<Void> resultHandler) {
    insertDocs(mongoClient, collection, num, this::createDoc, resultHandler);
  }

  protected Future<Void> insertDocs(MongoClient mongoClient, String collection, int num) {
    return Future.future(h -> insertDocs(mongoClient, collection, num, h));
  }

  protected Future<Void> insertDocs(MongoClient mongoClient, String collection, int num, Function<Integer, JsonObject> docSupplier) {
    return Future.future(h -> insertDocs(mongoClient, collection, num, docSupplier, h));
  }

  protected void insertDocs(MongoClient mongoClient, String collection, int num, Function<Integer, JsonObject> docSupplier, Completable<Void> resultHandler) {
    if (num != 0) {
      AtomicInteger cnt = new AtomicInteger();
      for (int i = 0; i < num; i++) {
        mongoClient.insert(collection, docSupplier.apply(i)).onComplete(ar -> {
          if (ar.succeeded()) {
            if (cnt.incrementAndGet() == num) {
              resultHandler.succeed();
            }
          } else {
            resultHandler.fail(ar.cause());
          }
        });
      }
    } else {
      resultHandler.succeed();
    }
  }

  protected JsonObject createDoc() {
    return new JsonObject()
      .put("foo", "bar")
      .put("num", 123)
      .put("big", true)
      .putNull("nullentry")
      .put("bigDec", BigDecimal.ONE)
      .put("arr", new JsonArray()
        .add("x")
        .add(true)
        .add(12)
        .add(1.23)
        .addNull()
        .add(BigDecimal.ONE)
        .add(new JsonObject()
          .put("wib", "wob")))
      .put("date", new JsonObject()
        .put("$date", "2015-05-30T22:50:02Z"))
      .put("object_id", new JsonObject()
        .put("$oid", new ObjectId().toHexString()))
      .put("other", new JsonObject()
        .put("quux", "flib")
        .put("myarr", new JsonArray()
          .add("blah")
          .add(true)
          .add(312)))
      .put("nested_id1", new JsonObject()
        .put("_id", new ObjectId().toHexString()))
      .put("nested_id2", new JsonArray()
        .add(new JsonObject()
          .put("_id", new ObjectId().toHexString()))
        .add(new JsonObject()
          .put("_id", new ObjectId().toHexString())));
  }

  protected JsonObject createDoc(int num) {
    return new JsonObject().put("foo", "bar" + (num != -1 ? num : "")).put("num", 123).put("big", true).putNull("nullentry").
      put("counter", num).
      put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
      put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
      put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
      put("other", new JsonObject().put("quux", "flib").put("myarr",
        new JsonArray().add("blah").add(true).add(312))).
      put("longval", 123456789L).put("dblval", 1.23);
  }

  protected JsonObject createDocWithAmbiguitiesDependingOnLocale(int num) {
    return new JsonObject()
      .put("foo", num % 2 == 0 ? "bar" : "bär")
      .put("num", 123).put("big", true)
      .putNull("nullentry")
      .put("counter", num)
      .put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob")))
      .put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z"))
      .put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString()))
      .put("other", new JsonObject().put("quux", "flib")
      .put("myarr", new JsonArray().add("blah").add(true).add(312)))
      .put("longval", 123456789L).put("dblval", 1.23);
  }

  // WARN: try to getObjectId from doc will generate new objectId on doc if not exists
  protected String getObjectId(JsonObject doc) {
    Object idVal = doc.getValue("_id");

    // auto generate when not exists
    if(idVal == null) {
      String _id = new ObjectId().toHexString();
      doc.put("_id", JsonObject.of("$oid", _id));
      return _id;
    }

    // return string
    if(idVal instanceof String) {
      return (String) idVal;
    }

    // return $oid from ObjectId object
    if(idVal instanceof JsonObject) {
      return ((JsonObject) idVal).getString("$oid");
    }

    return null;
  }

}
