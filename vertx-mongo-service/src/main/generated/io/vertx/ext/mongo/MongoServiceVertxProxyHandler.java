/*
* Copyright 2014 Red Hat, Inc.
*
* Red Hat licenses this file to you under the Apache License, version 2.0
* (the "License"); you may not use this file except in compliance with the
* License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

package io.vertx.ext.mongo;

import io.vertx.ext.mongo.MongoService;
import io.vertx.core.Vertx;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import io.vertx.serviceproxy.ProxyHelper;
import io.vertx.serviceproxy.ProxyHandler;
import java.util.List;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.UpdateOptions;

/*
  Generated Proxy code - DO NOT EDIT
  @author Roger the Robot
*/
public class MongoServiceVertxProxyHandler extends ProxyHandler {

  public static final long DEFAULT_CONNECTION_TIMEOUT = 5 * 60; // 5 minutes 

  private final Vertx vertx;
  private final MongoService service;
  private final long timerID;
  private long lastAccessed;
  private final long timeoutSeconds;

  public MongoServiceVertxProxyHandler(Vertx vertx, MongoService service) {
    this(vertx, service, DEFAULT_CONNECTION_TIMEOUT);  }

  public MongoServiceVertxProxyHandler(Vertx vertx, MongoService service,
    long timeoutInSecond) {
    this(vertx, service, true, timeoutInSecond);
  }

  public MongoServiceVertxProxyHandler(Vertx vertx, MongoService service, boolean topLevel, long timeoutSeconds) {
    this.vertx = vertx;
    this.service = service;
    this.timeoutSeconds = timeoutSeconds;
    if (timeoutSeconds != -1 && !topLevel) {
      long period = timeoutSeconds * 1000 / 2;
      if (period > 10000) {
        period = 10000;
      }
      this.timerID = vertx.setPeriodic(period, this::checkTimedOut);
    } else {
      this.timerID = -1;
    }
    accessed();
  }

  public MessageConsumer<JsonObject> registerHandler(String address) {
    MessageConsumer<JsonObject> consumer = vertx.eventBus().<JsonObject>consumer(address).handler(this);
    this.setConsumer(consumer);
    return consumer;
  }

  private void checkTimedOut(long id) {
    long now = System.nanoTime();
    if (now - lastAccessed > timeoutSeconds * 1000000000) {
      close();
    }
  }

  @Override
  public void close() {
    if (timerID != -1) {
      vertx.cancelTimer(timerID);
    }
    super.close();
  }

  private void accessed() {
    this.lastAccessed = System.nanoTime();
  }

  public void handle(Message<JsonObject> msg) {
    JsonObject json = msg.body();
    String action = msg.headers().get("action");
    if (action == null) {
      throw new IllegalStateException("action not specified");
    }
    accessed();
    switch (action) {

      case "save": {
        service.save((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("document"), createHandler(msg));
        break;
      }
      case "saveWithOptions": {
        service.saveWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("document"), json.getString("writeOption") == null ? null : io.vertx.ext.mongo.WriteOption.valueOf(json.getString("writeOption")), createHandler(msg));
        break;
      }
      case "insert": {
        service.insert((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("document"), createHandler(msg));
        break;
      }
      case "insertWithOptions": {
        service.insertWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("document"), json.getString("writeOption") == null ? null : io.vertx.ext.mongo.WriteOption.valueOf(json.getString("writeOption")), createHandler(msg));
        break;
      }
      case "update": {
        service.update((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), (io.vertx.core.json.JsonObject)json.getValue("update"), createHandler(msg));
        break;
      }
      case "updateWithOptions": {
        service.updateWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), (io.vertx.core.json.JsonObject)json.getValue("update"), json.getJsonObject("options") == null ? null : new io.vertx.ext.mongo.UpdateOptions(json.getJsonObject("options")), createHandler(msg));
        break;
      }
      case "replace": {
        service.replace((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), (io.vertx.core.json.JsonObject)json.getValue("replace"), createHandler(msg));
        break;
      }
      case "replaceWithOptions": {
        service.replaceWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), (io.vertx.core.json.JsonObject)json.getValue("replace"), json.getJsonObject("options") == null ? null : new io.vertx.ext.mongo.UpdateOptions(json.getJsonObject("options")), createHandler(msg));
        break;
      }
      case "find": {
        service.find((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), createListHandler(msg));
        break;
      }
      case "findWithOptions": {
        service.findWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), json.getJsonObject("options") == null ? null : new io.vertx.ext.mongo.FindOptions(json.getJsonObject("options")), createListHandler(msg));
        break;
      }
      case "findOne": {
        service.findOne((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), (io.vertx.core.json.JsonObject)json.getValue("fields"), createHandler(msg));
        break;
      }
      case "count": {
        service.count((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), createHandler(msg));
        break;
      }
      case "remove": {
        service.remove((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), createHandler(msg));
        break;
      }
      case "removeWithOptions": {
        service.removeWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), json.getString("writeOption") == null ? null : io.vertx.ext.mongo.WriteOption.valueOf(json.getString("writeOption")), createHandler(msg));
        break;
      }
      case "removeOne": {
        service.removeOne((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), createHandler(msg));
        break;
      }
      case "removeOneWithOptions": {
        service.removeOneWithOptions((java.lang.String)json.getValue("collection"), (io.vertx.core.json.JsonObject)json.getValue("query"), json.getString("writeOption") == null ? null : io.vertx.ext.mongo.WriteOption.valueOf(json.getString("writeOption")), createHandler(msg));
        break;
      }
      case "createCollection": {
        service.createCollection((java.lang.String)json.getValue("collectionName"), createHandler(msg));
        break;
      }
      case "getCollections": {
        service.getCollections(createListHandler(msg));
        break;
      }
      case "dropCollection": {
        service.dropCollection((java.lang.String)json.getValue("collection"), createHandler(msg));
        break;
      }
      case "runCommand": {
        service.runCommand((java.lang.String)json.getValue("commandName"), (io.vertx.core.json.JsonObject)json.getValue("command"), createHandler(msg));
        break;
      }
      case "close": {
        service.close();
        break;
      }
      default: {
        throw new IllegalStateException("Invalid action: " + action);
      }
    }
  }
  private <T> Handler<AsyncResult<T>> createHandler(Message msg) {
    return res -> {
      if (res.failed()) {
        msg.fail(-1, res.cause().getMessage());
      } else {
        msg.reply(res.result());
      }
    };
  }
  private <T> Handler<AsyncResult<List<T>>> createListHandler(Message msg) {
    return res -> {
      if (res.failed()) {
        msg.fail(-1, res.cause().getMessage());
      } else {
        msg.reply(new JsonArray(res.result()));
      }
    };
  }
  private <T> Handler<AsyncResult<Set<T>>> createSetHandler(Message msg) {
    return res -> {
      if (res.failed()) {
        msg.fail(-1, res.cause().getMessage());
      } else {
        msg.reply(new JsonArray(new ArrayList<>(res.result())));
      }
    };
  }
  private Handler<AsyncResult<List<Character>>> createListCharHandler(Message msg) {
    return res -> {
      if (res.failed()) {
        msg.fail(-1, res.cause().getMessage());
      } else {
        JsonArray arr = new JsonArray();
        for (Character chr: res.result()) {
          arr.add((int)chr);
        }
        msg.reply(arr);
      }
    };
  }
  private Handler<AsyncResult<Set<Character>>> createSetCharHandler(Message msg) {
    return res -> {
      if (res.failed()) {
        msg.fail(-1, res.cause().getMessage());
      } else {
        JsonArray arr = new JsonArray();
        for (Character chr: res.result()) {
          arr.add((int)chr);
        }
        msg.reply(arr);
      }
    };
  }
  private <T> Map<String, T> convertMap(Map map) {
    return (Map<String, T>)map;
  }
  private <T> List<T> convertList(List list) {
    return (List<T>)list;
  }
  private <T> Set<T> convertSet(List list) {
    return new HashSet<T>((List<T>)list);
  }
}