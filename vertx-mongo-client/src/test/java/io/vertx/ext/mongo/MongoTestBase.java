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

package io.vertx.ext.mongo;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;

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

  private static MongodExecutable exe;

  @BeforeClass
  public static void startMongo() throws Exception {
    if (getConnectionString() == null ) {
      IMongodConfig config = new MongodConfigBuilder().
        version(Version.Main.PRODUCTION).
        net(new Net(27018, Network.localhostIsIPv6())).
        build();
      exe = MongodStarter.getDefaultInstance().prepare(config);
      exe.start();
    }
  }

  @AfterClass
  public static void stopMongo() {
    if (exe != null) {
      exe.stop();
    }
  }

  protected JsonObject getConfig() {
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
    mongoClient.getCollections(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections((List) list);
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          mongoClient.dropCollection(collection, onSuccess(v -> {
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

  protected void insertDocs(MongoClient mongoClient, String collection, int num, Handler<AsyncResult<Void>> resultHandler) {
    if (num != 0) {
      AtomicInteger cnt = new AtomicInteger();
      for (int i = 0; i < num; i++) {
        JsonObject doc = createDoc(i);
        mongoClient.insert(collection, doc, ar -> {
          if (ar.succeeded()) {
            if (cnt.incrementAndGet() == num) {
              resultHandler.handle(Future.succeededFuture());
            }
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
        });
      }
    } else {
      resultHandler.handle(Future.succeededFuture());
    }
  }

  protected JsonObject createDoc() {
    return new JsonObject().put("foo", "bar").put("num", 123).put("big", true).putNull("nullentry").
            put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
            put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
            put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
            put("other", new JsonObject().put("quux", "flib").put("myarr",
                    new JsonArray().add("blah").add(true).add(312)));
  }

  protected JsonObject createDoc(int num) {
    return new JsonObject().put("foo", "bar" + (num != -1 ? num : "")).put("num", 123).put("big", true).putNull("nullentry").
            put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
            put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
            put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
            put("other", new JsonObject().put("quux", "flib").put("myarr",
                    new JsonArray().add("blah").add(true).add(312))).
            put("longval", 123456789L).put("dblval", 1.23);
  }

  protected List<JsonObject> createDocs(int num) {
    List<JsonObject> documents = new ArrayList<JsonObject>();
    for (int i = 0; i < num; ++i) {
      documents.add(createDoc(i));
    }
    return documents;
  }
}
