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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class DistinctTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
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
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "num", Integer.class.getName()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Integer) 123, distincted.getInteger(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", String.class.getName()).onComplete(onSuccess(distincted -> {
        assertEquals(10, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBoolean() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "big", Boolean.class.getName()).onComplete(onSuccess(distincted -> {
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
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "dblval", Double.class.getName()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Double) 1.23, distincted.getDouble(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctLong() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "longval", Long.class.getName()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Long) 123456789L, distincted.getLong(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Object.class.getName()).onComplete(onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryInteger() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "num", Integer.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Integer) 123, distincted.getInteger(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", String.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
        assertEquals(10, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryBoolean() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "big", Boolean.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
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
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "dblval", Double.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Double) 1.23, distincted.getDouble(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryLong() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "longval", Long.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
        assertEquals(1, distincted.size());
        assertEquals((Long) 123456789L, distincted.getLong(0));
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", Object.class.getName(), new JsonObject()).onComplete(onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryEmptyResult() {
    String collection = randomCollection();
    JsonObject query = new JsonObject().put("title", "The Hobbit");
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "longval", Long.class.getName(), query).onComplete(onSuccess(distincted -> {
        assertEquals(0, distincted.size());
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctBatchBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatch(collection, "foo", Object.class.getName())
        .exceptionHandler(t -> testComplete())
        .endHandler(v -> fail("Throwable expected"))
        .handler(v -> fail("Throwable expected"));
    }));
    await();
  }

  @Test
  public void testDistinctStringBadFormat() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "foo", Integer.class.getName()).onComplete(onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctUnexistentString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinct(collection, "unexist", String.class.getName()).onComplete(onSuccess(distincted -> {
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
    List<JsonObject> results = Collections.synchronizedList(new ArrayList<>());
    insertDocs(mongoClient, collection, numDocs).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatch(collection, "foo", String.class.getName())
        .exceptionHandler(this::fail)
        .endHandler(v -> testComplete())
        .handler(results::add);
    }));
    await();
    assertEquals(numDocs, results.size());
  }

  @Test
  public void testDistinctBatchWithQueryBadResultClass() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", Object.class.getName(), new JsonObject())
        .exceptionHandler(t -> testComplete())
        .endHandler(v -> fail("Throwable expected"))
        .handler(v -> fail("Throwable expected"));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryStringBadFormat() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "foo", Integer.class.getName(), new JsonObject()).onComplete(onFailure(failure -> {
        testComplete();
      }));
    }));
    await();
  }

  @Test
  public void testDistinctWithQueryUnexistentString() {
    String collection = randomCollection();
    insertDocs(mongoClient, collection, 10).onComplete(onSuccess(inserted -> {
      mongoClient.distinctWithQuery(collection, "unexist", String.class.getName(), new JsonObject()).onComplete(onSuccess(distincted -> {
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
    List<JsonObject> results = Collections.synchronizedList(new ArrayList<>());
    insertDocs(mongoClient, collection, numDocs).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", String.class.getName(), new JsonObject())
        .exceptionHandler(this::fail)
        .endHandler(v -> testComplete())
        .handler(results::add);
    }));
    await();
    assertEquals(numDocs, results.size());
  }

  @Test
  public void testDistinctOptionsWithCollationSetting() {
    String collection = randomCollection();
    mongoClient.distinctWithQuery(collection, "num", Integer.class.getName(), new JsonObject(), new DistinctOptions().setCollation(new CollationOptions()));
    int numDocs = 10;
    List<JsonObject> results = Collections.synchronizedList(new ArrayList<>());
    insertDocs(mongoClient, collection, numDocs, this::createDocWithAmbiguitiesDependingOnLocale).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", String.class.getName(), new JsonObject(), new DistinctOptions().setCollation(new CollationOptions().setLocale("de_AT")))
        .exceptionHandler(this::fail)
        .endHandler(v -> testComplete())
        .handler(results::add);
    }));
    await();
    assertEquals(2, results.size());
  }

  @Test
  public void testDistinctOptionsSetting() {
    String collection = randomCollection();
    mongoClient.distinctWithQuery(collection, "num", Integer.class.getName(), new JsonObject(), new DistinctOptions());
    int numDocs = 10;
    List<JsonObject> results = Collections.synchronizedList(new ArrayList<>());
    insertDocs(mongoClient, collection, numDocs, this::createDocWithAmbiguitiesDependingOnLocale).onComplete(onSuccess(inserted -> {
      mongoClient.distinctBatchWithQuery(collection, "foo", String.class.getName(), new JsonObject(), new DistinctOptions())
        .exceptionHandler(this::fail)
        .endHandler(v -> testComplete())
        .handler(results::add);
    }));
    await();
    assertEquals(2, results.size());
  }

  @Test
  public void testDistinctOptionsFromJson() {
    JsonObject json = new JsonObject()
      .put("collation", new JsonObject().put("locale", "simple"));

    DistinctOptions options = new DistinctOptions(json);
    assertEquals(new CollationOptions(), options.getCollation());
  }

  @Test
  public void testDistinctOptionsToJson() {
    JsonObject json = new JsonObject()
      .put("collation", new JsonObject().put("locale", "simple"));

    DistinctOptions options = new DistinctOptions().setCollation(new CollationOptions());

    assertEquals(json, options.toJson());
  }
}
