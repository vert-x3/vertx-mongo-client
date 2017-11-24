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

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class DistinctTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
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
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "num", Integer.class.getName(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Integer(123), distincted.getInteger(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", String.class.getName(), onSuccess(distincted -> {
        assertEquals(10, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBoolean() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "big", Boolean.class.getName(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(true, distincted.getBoolean(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctDouble() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "dblval", Double.class.getName(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Double(1.23), distincted.getDouble(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctLong() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "longval", Long.class.getName(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Long(123456789L), distincted.getLong(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Object.class.getName(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryInteger() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "num", Integer.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Integer(123), distincted.getInteger(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", String.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(10, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryBoolean() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "big", Boolean.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(true, distincted.getBoolean(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryDouble() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "dblval", Double.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Double(1.23), distincted.getDouble(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryLong() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "longval", Long.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals(new Long(123456789L), distincted.getLong(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", Object.class.getName(), new JsonObject(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryEmptyResult() {
    String collection = randomCollection();
    JsonObject query = new JsonObject().put("title", "The Hobbit");
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "longval", Long.class.getName(), query, onSuccess(distincted -> {
        assertEquals(0, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBatchBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctBatch(collection, "foo", Object.class.getName(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctStringBadFormat() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Integer.class.getName(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctUnexistentString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
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
    insertDocs(mongoClient, collection, numDocs, onSuccess(inserted -> {
      mongoClient.distinctBatch(collection, "foo", String.class.getName(), onSuccess(distincted -> {
        assertNotNull(distincted);
        latch.countDown();
      }));
    }));
    awaitLatch(latch);
  }

  @Test
  public void testDistinctBatchWithQueryBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", Object.class.getName(), new JsonObject(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryStringBadFormat() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", Integer.class.getName(), new JsonObject(), onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryUnexistentString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10, onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "unexist", String.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertEquals(0, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBatchWithQueryString() throws Exception {
    String collection = randomCollection();
    int numDocs = 10;
    CountDownLatch latch = new CountDownLatch(numDocs);
    insertDocs(mongoClient, collection, numDocs, onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", String.class.getName(), new JsonObject(), onSuccess(distincted -> {
        assertNotNull(distincted);
        latch.countDown();
      }));
    }));
    awaitLatch(latch);
  }
}
