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

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.ClientSessionOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.TransactionOptions;
import io.vertx.ext.mongo.UpdateOptions;

import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;

import java.util.concurrent.TimeUnit;

public class MongoClientTransactionalExamples {

  public void executeTransactionExample(MongoClient mongoClient) {
    JsonObject query = new JsonObject()
      .put("title", "The Hobbit");
    JsonObject update = new JsonObject().put("$set", new JsonObject()
      .put("author", "J. R. R. Tolkien"));
    UpdateOptions options = new UpdateOptions().setMulti(true);

    mongoClient.executeTransaction(client -> Future.join(
        client.updateCollectionWithOptions("books", query, update, options),
        client.insert("authors", update)
      ))
      .onFailure(throwable -> System.err.println(throwable.getMessage()))
      .onComplete(res -> {
        final Object updateResult = res.result().resultAt(0);
        final Object insertResult = res.result().resultAt(1);
        if (res.succeeded()) {
          System.out.println("Book and Author updated ! updated:" + updateResult + " inserted: " + insertResult);
        } else {
          res.cause().printStackTrace();
        }
      });
  }

  public void startSessionExample(MongoClient mongoClient) {
    mongoClient.startSession(new ClientSessionOptions()
        .setAutoStartTransaction(true)
        .setAutoClose(true)
      )
      .flatMap(session -> {
        // Match any documents with title=The Hobbit
        JsonObject query = new JsonObject()
          .put("title", "The Hobbit");
        // Set the author field
        JsonObject update = new JsonObject().put("$set", new JsonObject()
          .put("author", "J. R. R. Tolkien"));
        UpdateOptions options = new UpdateOptions().setMulti(true);

        return session.executeTransaction(client ->
          Future.join(
            client.updateCollectionWithOptions("books", query, update, options),
            client.insert("authors", update))
        );
      })
      .onFailure(throwable -> System.err.println(throwable.getMessage()))
      .onComplete(res -> {
        final Object updateResult = res.result().resultAt(0);
        final Object insertResult = res.result().resultAt(1);
        if (res.succeeded()) {
          System.out.println("Book and Author updated ! updated:" + updateResult + " inserted: " + insertResult);
        } else {
          res.cause().printStackTrace();
        }
      });
  }

  public void manualTransactionExample(MongoClient mongoClient) {
    mongoClient.startSession(new ClientSessionOptions()
        .setAutoStartTransaction(false)
        .setAutoClose(false)
      )
      .flatMap(session ->
        session.start()
          .flatMap(v -> {
            JsonObject doc = new JsonObject()
              .put("title", "The Hobbit")
              .put("author", "J. R. R. Tolkien");

            return session.executeTransaction(client ->
              client.insert("books", doc)
                .flatMap(id -> client.findOne("books", new JsonObject().put("_id", id), null))
            );
          })
          .compose(
            result -> session.commit().map(result),
            err -> session.abort().compose(v -> Future.failedFuture(err))
          )
          .eventually(() -> session.close())
      )
      .onSuccess(book -> System.out.println("Inserted and verified: " + book.getString("title")))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

  public void transactionWithOptionsExample(MongoClient mongoClient) {
    ClientSessionOptions sessionOptions = new ClientSessionOptions()
      .setDefaultTransactionOptions(new TransactionOptions()
        .setReadConcern(ReadConcern.MAJORITY)
        .setWriteConcern(WriteConcern.MAJORITY)
        .setMaxCommitTime(30, TimeUnit.SECONDS)
      );

    mongoClient.executeTransaction(client -> {
        JsonObject book = new JsonObject()
          .put("title", "The Silmarillion")
          .put("author", "J. R. R. Tolkien");
        JsonObject author = new JsonObject()
          .put("name", "J. R. R. Tolkien")
          .put("genre", "Fantasy");

        return Future.join(
          client.insert("books", book),
          client.insert("authors", author)
        );
      }, sessionOptions)
      .onSuccess(cf -> System.out.println("Both inserts committed with majority write concern"))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

}
