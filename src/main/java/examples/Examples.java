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

package examples;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Examples {

  public void example0_1(Vertx vertx, JsonObject config) {

    // Deploy service - can be anywhere on your network
    DeploymentOptions options = new DeploymentOptions().setConfig(config);

    vertx.deployVerticle("service:io.vertx:mongo-service", options, res -> {

      if (res.succeeded()) {
        // Deployed ok
      } else {
        // Failed to deploy
      }

    });
  }

  public void example0_1_1(Vertx vertx, JsonObject config) {

    JsonObject save = new JsonObject().put("collection", "books")
      .put("document", new JsonObject().put("title", "The Hobbit"));

    vertx.eventBus().send("vertx.mongo", save,
      new DeliveryOptions().addHeader("action", "save"), saveResult -> {

      if (saveResult.succeeded()) {

        String id = (String) saveResult.result().body();

        System.out.println("Saved book with id " + id);

      } else {
        saveResult.cause().printStackTrace();
      }

    });

  }

  public void example0_2(Vertx vertx, JsonObject credentials) {

    MongoService proxy = MongoService.createEventBusProxy(vertx, "vertx.mongo");

    // Now do stuff with it:

    proxy.count("books", new JsonObject(), res -> {

      // ...

    });
  }

  public void example0_3(Vertx vertx, JsonObject credentials) {

    JsonObject config = new JsonObject();

    // Set your config properties

    MongoService mongoService = MongoService.create(vertx, config);

    mongoService.start();

    // Now do stuff with it:

    mongoService.count("books", new JsonObject(), res -> {

      // ...

    });

  }

  public void example1(MongoService mongoService) {

    // Document has no id

    JsonObject document = new JsonObject().put("title", "The Hobbit");

    mongoService.save("books", document, res -> {

      if (res.succeeded()) {

        String id = res.result();
        System.out.println("Saved book with id " + id);

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example2(MongoService mongoService) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit").put("_id", "123244");

    mongoService.save("books", document, res -> {

      if (res.succeeded()) {

        // ...

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example3(MongoService mongoService) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit");

    mongoService.insert("books", document, res -> {

      if (res.succeeded()) {

        String id = res.result();
        System.out.println("Inserted book with id " + id);

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example4(MongoService mongoService) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit").put("_id", "123244");

    mongoService.insert("books", document, res -> {

      if (res.succeeded()) {

        //...

      } else {

        // Will fail if the book with that id already exists.
      }

    });

  }

  public void example5(MongoService mongoService) {

    // Match any documents with title=The Hobbit
    JsonObject query = new JsonObject().put("title", "The Hobbit");

    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("author", "J. R. R. Tolkien"));

    mongoService.update("books", query, update, res -> {

      if (res.succeeded()) {

        System.out.println("Book updated !");

      } else {

        res.cause().printStackTrace();
      }

    });

  }

  public void example6(MongoService mongoService) {

    // Match any documents with title=The Hobbit
    JsonObject query = new JsonObject().put("title", "The Hobbit");

    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("author", "J. R. R. Tolkien"));

    UpdateOptions options = new UpdateOptions().setMulti(true);

    mongoService.updateWithOptions("books", query, update, options, res -> {

      if (res.succeeded()) {

        System.out.println("Book updated !");

      } else {

        res.cause().printStackTrace();
      }

    });

  }

  public void example7(MongoService mongoService) {

    JsonObject query = new JsonObject().put("title", "The Hobbit");

    JsonObject replace = new JsonObject().put("title", "The Lord of the Rings").put("author", "J. R. R. Tolkien");

    mongoService.replace("books", query, replace, res -> {

      if (res.succeeded()) {

        System.out.println("Book replaced !");

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example8(MongoService mongoService) {

    // empty query = match any
    JsonObject query = new JsonObject();

    mongoService.find("books", query, res -> {

      if (res.succeeded()) {

        for (JsonObject json : res.result()) {

          System.out.println(json.encodePrettily());

        }

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example9(MongoService mongoService) {

    // will match all Tolkien books
    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoService.find("books", query, res -> {

      if (res.succeeded()) {

        for (JsonObject json : res.result()) {

          System.out.println(json.encodePrettily());

        }

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example10(MongoService mongoService) {

    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoService.remove("books", query, res -> {

      if (res.succeeded()) {

        System.out.println("Never much liked Tolkien stuff!");

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11(MongoService mongoService) {

    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoService.count("books", query, res -> {

      if (res.succeeded()) {

        long num = res.result();

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_1(MongoService mongoService) {

    mongoService.getCollections(res -> {

      if (res.succeeded()) {

        List<String> collections = res.result();

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_2(MongoService mongoService) {

    mongoService.createCollection("mynewcollectionr", res -> {

      if (res.succeeded()) {

        // Created ok!

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_3(MongoService mongoService) {

    mongoService.dropCollection("mynewcollectionr", res -> {

      if (res.succeeded()) {

        // Dropped ok!

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example12(MongoService mongoService) {

    mongoService.runCommand(new JsonObject().put("ping", 1), res -> {

      if (res.succeeded()) {

        System.out.println("Result: " + res.result().encodePrettily());

      } else {

        res.cause().printStackTrace();

      }
    });

  }

}
