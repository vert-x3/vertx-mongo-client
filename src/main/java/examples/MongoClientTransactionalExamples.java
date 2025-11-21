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

import com.mongodb.ClientSessionOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.SessionOptions;
import io.vertx.ext.mongo.UpdateOptions;

public class MongoClientTransactionalExamples {

  public void inTransactionExample(MongoClient mongoClient) {
    mongoClient.inTransaction(tx -> {
        // Match any documents with title=The Hobbit
        JsonObject query = new JsonObject()
          .put("title", "The Hobbit");
        // Set the author field
        JsonObject update = new JsonObject().put("$set", new JsonObject()
          .put("author", "J. R. R. Tolkien"));
        UpdateOptions options = new UpdateOptions().setMulti(true);

        return tx.updateCollectionWithOptions("books", query, update, options)
          .compose(updateResult -> tx.insert("authors", update));
      })
      .onComplete(res -> {
        if (res.succeeded()) {
          System.out.println("Book and Author updated !");
        } else {
          res.cause().printStackTrace();
        }
      });
  }

  public void createSessionExample(MongoClient mongoClient) {
    mongoClient.createSession(new SessionOptions().setCloseSession(false)
        .setClientSessionOptions(ClientSessionOptions.builder().build()))
      .flatMap(tx -> {
        // Match any documents with title=The Hobbit
        JsonObject query = new JsonObject()
          .put("title", "The Hobbit");
        // Set the author field
        JsonObject update = new JsonObject().put("$set", new JsonObject()
          .put("author", "J. R. R. Tolkien"));
        UpdateOptions options = new UpdateOptions().setMulti(true);

        return tx.updateCollectionWithOptions("books", query, update, options)
          .compose(
            updateResult -> tx.insert("authors", update))
          .compose(insert -> tx.commit())
          .onFailure(throwable -> {
            System.err.println(throwable.getMessage());
            tx.abort();
          });
      }).onComplete(res -> {
        if (res.succeeded()) {
          System.out.println("Book and Author updated !");
        } else {
          res.cause().printStackTrace();
        }
      });
  }

}
