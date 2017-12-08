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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.docgen.Source;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.bson.types.ObjectId;

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
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit");
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
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit")
      .put("_id", "123244");
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
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit");
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
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit")
      .put("_id", "123244");
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
    JsonObject query = new JsonObject()
      .put("title", "The Hobbit");
    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject()
      .put("author", "J. R. R. Tolkien"));
    mongoClient.updateCollection("books", query, update, res -> {
      if (res.succeeded()) {
        System.out.println("Book updated !");
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example6(MongoClient mongoClient) {
    // Match any documents with title=The Hobbit
    JsonObject query = new JsonObject()
      .put("title", "The Hobbit");
    // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject()
      .put("author", "J. R. R. Tolkien"));
    UpdateOptions options = new UpdateOptions().setMulti(true);
    mongoClient.updateCollectionWithOptions("books", query, update, options, res -> {
      if (res.succeeded()) {
        System.out.println("Book updated !");
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example7(MongoClient mongoClient) {
    JsonObject query = new JsonObject()
      .put("title", "The Hobbit");
    JsonObject replace = new JsonObject()
      .put("title", "The Lord of the Rings")
      .put("author", "J. R. R. Tolkien");
    mongoClient.replaceDocuments("books", query, replace, res -> {
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
    JsonObject query = new JsonObject()
      .put("author", "J. R. R. Tolkien");
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

  public void findBatch(MongoClient mongoClient) {
    // will match all Tolkien books
    JsonObject query = new JsonObject()
      .put("author", "J. R. R. Tolkien");
    mongoClient.findBatch("book", query)
      .exceptionHandler(throwable -> throwable.printStackTrace())
      .endHandler(v -> System.out.println("End of research"))
      .handler(doc -> System.out.println("Found doc: " + doc.encodePrettily()));
  }

  public void example10(MongoClient mongoClient) {
    JsonObject query = new JsonObject()
      .put("author", "J. R. R. Tolkien");
    mongoClient.removeDocuments("books", query, res -> {
      if (res.succeeded()) {
        System.out.println("Never much liked Tolkien stuff!");
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example11(MongoClient mongoClient) {
    JsonObject query = new JsonObject()
      .put("author", "J. R. R. Tolkien");
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
    JsonObject command = new JsonObject()
      .put("aggregate", "collection_name")
      .put("pipeline", new JsonArray());
    mongoClient.runCommand("aggregate", command, res -> {
      if (res.succeeded()) {
        JsonArray resArr = res.result().getJsonArray("result");
        // etc
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example13_0(MongoClient mongoService) {
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit")
      //ISO-8601 date
      .put("publicationDate", new JsonObject().put("$date", "1937-09-21T00:00:00+00:00"));
    mongoService.save("publishedBooks", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        mongoService.findOne("publishedBooks", new JsonObject().put("_id", id), null, res2 -> {
          if (res2.succeeded()) {
            System.out.println("To retrieve ISO-8601 date : "
              + res2.result().getJsonObject("publicationDate").getString("$date"));
          } else {
            res2.cause().printStackTrace();
          }
        });
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  @Source(translate = false)
  public void example14_01_dl(MongoClient mongoService) {
    //This could be a serialized object or the contents of a pdf file, etc,  in real life
    byte[] binaryObject = new byte[40];
    JsonObject document = new JsonObject()
      .put("name", "Alan Turing")
      .put("binaryStuff", new JsonObject().put("$binary", binaryObject));
    mongoService.save("smartPeople", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        mongoService.findOne("smartPeople", new JsonObject().put("_id", id), null, res2 -> {
          if (res2.succeeded()) {
            byte[] reconstitutedBinaryObject = res2.result().getJsonObject("binaryStuff").getBinary("$binary");
            //This could now be de-serialized into an object in real life
          } else {
            res2.cause().printStackTrace();
          }
        });
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example14_02_dl(MongoClient mongoService) {
    //This could be a the byte contents of a pdf file, etc converted to base 64
    String base64EncodedString = "a2FpbHVhIGlzIHRoZSAjMSBiZWFjaCBpbiB0aGUgd29ybGQ=";
    JsonObject document = new JsonObject()
      .put("name", "Alan Turing")
      .put("binaryStuff", new JsonObject().put("$binary", base64EncodedString));
    mongoService.save("smartPeople", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        mongoService.findOne("smartPeople", new JsonObject().put("_id", id), null, res2 -> {
          if (res2.succeeded()) {
            String reconstitutedBase64EncodedString = res2.result().getJsonObject("binaryStuff").getString("$binary");
            //This could now converted back to bytes from the base 64 string
          } else {
            res2.cause().printStackTrace();
          }
        });
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example15_dl(MongoClient mongoService) {
    String individualId = new ObjectId().toHexString();
    JsonObject document = new JsonObject()
      .put("name", "Stephen Hawking")
      .put("individualId", new JsonObject().put("$oid", individualId));
    mongoService.save("smartPeople", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        JsonObject query = new JsonObject().put("_id", id);
        mongoService.findOne("smartPeople", query, null, res2 -> {
          if (res2.succeeded()) {
            String reconstitutedIndividualId = res2.result()
              .getJsonObject("individualId").getString("$oid");
          } else {
            res2.cause().printStackTrace();
          }
        });
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example16(MongoClient mongoClient) {
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit");
    mongoClient.save("books", document, res -> {
      if (res.succeeded()) {
        mongoClient.distinct("books", "title", String.class.getName(), res2 -> {
          System.out.println("Title is : " + res2.result().getJsonArray(0));
        });
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example16_d1(MongoClient mongoClient) {
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit");
    mongoClient.save("books", document, res -> {
      if (res.succeeded()) {
        mongoClient.distinctBatch("books", "title", String.class.getName())
          .handler(book -> System.out.println("Title is : " + book.getString("title")));
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void example17(MongoClient mongoClient) {
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit")
      .put("publicationDate", new JsonObject().put("$date", "1937-09-21T00:00:00+00:00"));
    JsonObject query = new JsonObject()
      .put("publicationDate",
        new JsonObject().put("$gte", new JsonObject().put("$date", "1937-09-21T00:00:00+00:00")));
    mongoClient.save("books", document, res -> {
      if (res.succeeded()) {
        mongoClient.distinctWithQuery("books", "title", String.class.getName(), query, res2 -> {
          System.out.println("Title is : " + res2.result().getJsonArray(0));
        });
      }
    });
  }

  public void example17_d1(MongoClient mongoClient) {
    JsonObject document = new JsonObject()
      .put("title", "The Hobbit")
      .put("publicationDate", new JsonObject().put("$date", "1937-09-21T00:00:00+00:00"));
    JsonObject query = new JsonObject()
      .put("publicationDate", new JsonObject()
        .put("$gte", new JsonObject().put("$date", "1937-09-21T00:00:00+00:00")));
    mongoClient.save("books", document, res -> {
      if (res.succeeded()) {
        mongoClient.distinctBatchWithQuery("books", "title", String.class.getName(), query)
          .handler(book -> System.out.println("Title is : " + book.getString("title")));
      }
    });
  }
}
