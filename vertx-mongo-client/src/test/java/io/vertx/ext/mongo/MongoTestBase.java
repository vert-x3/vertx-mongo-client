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

import com.mongodb.async.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    CountDownLatch latch = new CountDownLatch(1);
    if (getConnectionString() == null) {
      IMongodConfig config = new MongodConfigBuilder().
        version(Version.Main.PRODUCTION).
        net(new Net(27018, Network.localhostIsIPv6())).
        replication(new Storage(null, "testReplSet", 5000)).
        build();
      exe = MongodStarter.getDefaultInstance().prepare(config);
      exe.start();
      final JsonObject replSetInitiateConfig = new JsonObject();
      replSetInitiateConfig.put("replSetInitiate",
        new JsonObject()
          .put("_id", "testReplSet")
          .put("members", new JsonArray(Collections.singletonList(new JsonObject()
            .put("_id", 0).put("host", "localhost:27018")))));
      com.mongodb.async.client.MongoClient client = MongoClients.create("mongodb://localhost:27018/");
      client.getDatabase("admin").runCommand(BsonDocument.parse(replSetInitiateConfig.encode()), (result, t) -> {
        Assert.assertNull(t);
        latch.countDown();
        client.close();
      });
    }
    longAwaitLatch(latch);
  }

  @AfterClass
  public static void stopMongo() throws InterruptedException {
    if (exe != null) {
      CountDownLatch latch = new CountDownLatch(1);
      // Since this is a replica set mongo, force shutdown.
      JsonObject shutdown = new JsonObject()
        .put("shutdown", 1)
        .put("force", true);
      com.mongodb.async.client.MongoClient client = MongoClients.create("mongodb://localhost:27018/");
      client.getDatabase("admin").runCommand(BsonDocument.parse(shutdown.encode()), (result, t) -> {
        latch.countDown();
        client.close();
      });
      longAwaitLatch(latch);
      exe.stop();
    }
  }

  protected JsonObject getConfig() {
    JsonObject config = new JsonObject();
    String connectionString = getConnectionString();
    if (connectionString != null) {
      config.put("connection_string", connectionString);
    } else {
      config.put("connection_string", "mongodb://localhost:27018/?replicaSet=testReplSet");
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
      if (num < 400 /*80% of default maxWaitQueueSize of 500*/) {
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
        // Fixes com.mongodb.MongoWaitQueueFullException: Too many threads are already waiting for a connection. Max
        // number of threads (maxWaitQueueSize) of 500 has been exceeded.
        List<BulkOperation> ops = IntStream.range(0, num)
          .mapToObj(this::createDoc)
          .map(BulkOperation::createInsert)
          .collect(Collectors.toList());

        mongoClient.bulkWrite(collection, ops, ar -> {
          if (ar.succeeded()) {
            if (ar.result() != null
              && ar.result().getInsertedCount() == num) {
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
    return new JsonObject().
      put("foo", "bar" + (num != -1 ? num : "")).
      put("num", 123).put("big", true).putNull("nullentry").
      put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
      put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
      put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
      put("other", new JsonObject().
        put("i", num).
        put("quux", "flib").
        put("myarr", new JsonArray().add("blah").add(true).add(312))).
      put("longval", 123456789L).put("dblval", 1.23);
  }

  protected static boolean longAwaitLatch(CountDownLatch latch) throws InterruptedException {
    // Leave just enough time for a server selection timeout (default of 30 seconds) to trigger
    return latch.await(32L, TimeUnit.SECONDS);
  }
}
