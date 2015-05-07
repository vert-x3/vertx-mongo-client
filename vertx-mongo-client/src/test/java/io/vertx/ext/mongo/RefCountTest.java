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
import io.vertx.core.shareddata.LocalMap;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class RefCountTest extends MongoTestBase {

  private LocalMap<String, Object> getLocalMap() {
    return vertx.sharedData().getLocalMap("__vertx.MongoClient.datasources");
  }

  @Test
  public void testNonShared() {
    LocalMap<String, Object> map = getLocalMap();
    JsonObject config = getConfig();
    MongoClient client1 = MongoClient.createNonShared(vertx, config);
    assertEquals(1, map.size());
    MongoClient client2 = MongoClient.createNonShared(vertx, config);
    assertEquals(2, map.size());
    MongoClient client3 = MongoClient.createNonShared(vertx, config);
    assertEquals(3, map.size());
    client1.close();
    assertEquals(2, map.size());
    client2.close();
    assertEquals(1, map.size());
    client3.close();
    waitUntil(() -> map.size() == 0);
    waitUntil(() -> getLocalMap().size() == 0);
    waitUntil(() -> map != getLocalMap()); // Map has been closed
  }

  @Test
  public void testSharedDefault() throws Exception {
    LocalMap<String, Object> map = getLocalMap();
    JsonObject config = getConfig();
    MongoClient client1 = MongoClient.createShared(vertx, config);
    assertEquals(1, map.size());
    MongoClient client2 = MongoClient.createShared(vertx, config);
    assertEquals(1, map.size());
    MongoClient client3 = MongoClient.createShared(vertx, config);
    assertEquals(1, map.size());
    client1.close();
    assertEquals(1, map.size());
    client2.close();
    assertEquals(1, map.size());
    client3.close();
    assertEquals(0, map.size());
    assertNotSame(map, getLocalMap());
  }

  @Test
  public void testSharedNamed() throws Exception {
    LocalMap<String, Object> map = getLocalMap();
    JsonObject config = getConfig();
    MongoClient client1 = MongoClient.createShared(vertx, config, "ds1");
    assertEquals(1, map.size());
    MongoClient client2 = MongoClient.createShared(vertx, config, "ds1");
    assertEquals(1, map.size());
    MongoClient client3 = MongoClient.createShared(vertx, config, "ds1");
    assertEquals(1, map.size());

    MongoClient client4 = MongoClient.createShared(vertx, config, "ds2");
    assertEquals(2, map.size());
    MongoClient client5 = MongoClient.createShared(vertx, config, "ds2");
    assertEquals(2, map.size());
    MongoClient client6 = MongoClient.createShared(vertx, config, "ds2");
    assertEquals(2, map.size());

    client1.close();
    assertEquals(2, map.size());
    client2.close();
    assertEquals(2, map.size());
    client3.close();
    assertEquals(1, map.size());

    client4.close();
    assertEquals(1, map.size());
    client5.close();
    assertEquals(1, map.size());
    client6.close();
    assertEquals(0, map.size());
    assertNotSame(map, getLocalMap());
  }
}
