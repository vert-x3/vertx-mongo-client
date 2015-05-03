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

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Examples {


  public void exampleCreateDefault(Vertx vertx, JsonObject config) {

    MongoClient client = MongoClient.createShared(vertx, config);

  }

  public void exampleCreatePoolName(Vertx vertx, JsonObject config) {

    MongoClient client = MongoClient.createShared(vertx, config, "MyPoolName");

  }

  public void exampleCreateNonShared(Vertx vertx, JsonObject config) {

    MongoClient client = MongoClient.createNonShared(vertx, config);

  }


  public void example1(MongoClient mongoClient) {

    // Document has no id

    JsonObject document = new JsonObject().put("title", "The Hobbit");

    mongoClient.save("books", document, res -> {

      if (res.succeeded()) {

        String id = res.result();
        System.out.println("Saved book with id " + id);

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example2(MongoClient mongoClient) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit").put("_id", "123244");

    mongoClient.save("books", document, res -> {

      if (res.succeeded()) {

        // ...

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example3(MongoClient mongoClient) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit");

    mongoClient.insert("books", document, res -> {

      if (res.succeeded()) {

        String id = res.result();
        System.out.println("Inserted book with id " + id);

      } else {
        res.cause().printStackTrace();
      }

    });

  }

  public void example4(MongoClient mongoClient) {

    // Document has an id already

    JsonObject document = new JsonObject().put("title", "The Hobbit").put("_id", "123244");

    mongoClient.insert("books", document, res -> {

      if (res.succeeded()) {

        //...

      } else {

        // Will fail if the book with that id already exists.
      }

    });

  }

  public void example5(MongoClient mongoClient) {

    // Match any documents with title=The Hobbit
    JsonObject query = new JsonObject().put("title", "The Hobbit");

    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("author", "J. R. R. Tolkien"));

    mongoClient.update("books", query, update, res -> {

      if (res.succeeded()) {

        System.out.println("Book updated !");

      } else {

        res.cause().printStackTrace();
      }

    });

  }

  public void example6(MongoClient mongoClient) {

    // Match any documents with title=The Hobbit
    JsonObject query = new JsonObject().put("title", "The Hobbit");

    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("author", "J. R. R. Tolkien"));

    UpdateOptions options = new UpdateOptions().setMulti(true);

    mongoClient.updateWithOptions("books", query, update, options, res -> {

      if (res.succeeded()) {

        System.out.println("Book updated !");

      } else {

        res.cause().printStackTrace();
      }

    });

  }

  public void example7(MongoClient mongoClient) {

    JsonObject query = new JsonObject().put("title", "The Hobbit");

    JsonObject replace = new JsonObject().put("title", "The Lord of the Rings").put("author", "J. R. R. Tolkien");

    mongoClient.replace("books", query, replace, res -> {

      if (res.succeeded()) {

        System.out.println("Book replaced !");

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example8(MongoClient mongoClient) {

    // empty query = match any
    JsonObject query = new JsonObject();

    mongoClient.find("books", query, res -> {

      if (res.succeeded()) {

        for (JsonObject json : res.result()) {

          System.out.println(json.encodePrettily());

        }

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example9(MongoClient mongoClient) {

    // will match all Tolkien books
    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoClient.find("books", query, res -> {

      if (res.succeeded()) {

        for (JsonObject json : res.result()) {

          System.out.println(json.encodePrettily());

        }

      } else {

        res.cause().printStackTrace();

      }

    });

  }

  public void example10(MongoClient mongoClient) {

    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoClient.remove("books", query, res -> {

      if (res.succeeded()) {

        System.out.println("Never much liked Tolkien stuff!");

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11(MongoClient mongoClient) {

    JsonObject query = new JsonObject().put("author", "J. R. R. Tolkien");

    mongoClient.count("books", query, res -> {

      if (res.succeeded()) {

        long num = res.result();

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_1(MongoClient mongoClient) {

    mongoClient.getCollections(res -> {

      if (res.succeeded()) {

        List<String> collections = res.result();

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_2(MongoClient mongoClient) {

    mongoClient.createCollection("mynewcollectionr", res -> {

      if (res.succeeded()) {

        // Created ok!

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example11_3(MongoClient mongoClient) {

    mongoClient.dropCollection("mynewcollectionr", res -> {

      if (res.succeeded()) {

        // Dropped ok!

      } else {

        res.cause().printStackTrace();

      }
    });

  }

  public void example12(MongoClient mongoClient) {

    mongoClient.runCommand(new JsonObject().put("ping", 1), res -> {

      if (res.succeeded()) {

        System.out.println("Result: " + res.result().encodePrettily());

      } else {

        res.cause().printStackTrace();

      }
    });

  }

}
