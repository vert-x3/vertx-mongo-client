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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class DistinctTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  @Test
  public void testDistinctInteger() {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "num", Integer.class.getName(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Integer(123), (Integer) distincted.get(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctString() {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", String.class.getName(), onSuccess(distincted -> {
        assertEquals(10, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBadResultClass() {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Object.class.getName(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctStringBadFormat() {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Integer.class.getName(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctUnexistentString() {
    String collection = randomCollection();
    insertDocs(collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "unexist", String.class.getName(), onSuccess(distincted -> {
        assertEquals(0, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBatchString() throws Exception {
    String collection = randomCollection();
    int numDocs = 10;
    CountDownLatch latch = new CountDownLatch(numDocs);
    insertDocs(collection, numDocs, onSuccess(inserted -> {
      mongoClient.distinctBatch(collection, "foo", String.class.getName(), onSuccess(distincted -> {
        assertNotNull(distincted);
        latch.countDown();
      }));
    }));
    awaitLatch(latch);
  }

  protected void dropCollections(CountDownLatch latch) {
    // Drop all the collections in the db
    mongoClient.getCollections(onSuccess(list -> {
      AtomicInteger collCount = new AtomicInteger();
      List<String> toDrop = getOurCollections(list);
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

  protected void insertDocs(String collection, int num, Handler<AsyncResult<Void>> resultHandler) {
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

  protected JsonObject createDoc(int num) {
    return new JsonObject().put("foo", "bar" + (num != -1 ? num : "")).put("num", 123).put("big", true).putNull("nullentry").
            put("arr", new JsonArray().add("x").add(true).add(12).add(1.23).addNull().add(new JsonObject().put("wib", "wob"))).
            put("date", new JsonObject().put("$date", "2015-05-30T22:50:02Z")).
            put("object_id", new JsonObject().put("$oid", new ObjectId().toHexString())).
            put("other", new JsonObject().put("quux", "flib").put("myarr",
                    new JsonArray().add("blah").add(true).add(312)));
  }
}
