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

import java.util.concurrent.TimeUnit;

public class MongoClientTransactionalExamples {

  public void withTransactionExample(MongoClient mongoClient) {
    JsonObject query = JsonObject.of("title", "The Hobbit");
    JsonObject update = JsonObject.of("$set", JsonObject.of("author", "J. R. R. Tolkien"));
    UpdateOptions options = new UpdateOptions().setMulti(true);
    JsonObject author = JsonObject.of("name", "J. R. R. Tolkien");

    mongoClient.withTransaction(client ->
        client.updateCollectionWithOptions("books", query, update, options)
          .compose(updateResult -> client.insert("authors", author))
      )
      .onSuccess(insertedId -> System.out.println("Book updated and author inserted: " + insertedId))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

  public void startSessionExample(MongoClient mongoClient) {
    JsonObject query = JsonObject.of("title", "The Hobbit");
    JsonObject update = JsonObject.of("$set", JsonObject.of("author", "J. R. R. Tolkien"));
    UpdateOptions options = new UpdateOptions().setMulti(true);
    JsonObject author = JsonObject.of("name", "J. R. R. Tolkien");

    mongoClient.startSession()
      .flatMap(session ->
        session.withTransaction(client ->
            client.updateCollectionWithOptions("books", query, update, options)
              .compose(updateResult -> client.insert("authors", author))
          )
          .eventually(session::close)
      )
      .onSuccess(insertedId -> System.out.println("Book updated and author inserted: " + insertedId))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

  public void sessionReuseExample(MongoClient mongoClient) {
    JsonObject firstBook = JsonObject.of("title", "The Fellowship of the Ring");
    JsonObject secondBook = JsonObject.of("title", "The Two Towers");

    mongoClient.startSession()
      .flatMap(session ->
        session.withTransaction(client -> client.insert("books", firstBook))
          .compose(id -> session.withTransaction(client -> client.insert("books", secondBook)))
          .eventually(session::close)
      )
      .onSuccess(id -> System.out.println("Both transactions committed"))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

  public void manualTransactionExample(MongoClient mongoClient) {
    JsonObject doc = JsonObject.of("title", "The Hobbit", "author", "J. R. R. Tolkien");

    mongoClient.startSession()
      .flatMap(session -> {
        MongoClient client = session.client();
        return session.startTransaction()
          .flatMap(v -> client.insert("books", doc))
          .flatMap(id -> client.findOne("books", JsonObject.of("_id", id), null))
          .compose(
            book -> session.commit().map(book),
            err -> session.abort().transform(v -> Future.failedFuture(err))
          )
          .eventually(session::close);
      })
      .onSuccess(book -> System.out.println("Inserted and verified: " + book.getString("title")))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

  public void transactionWithOptionsExample(MongoClient mongoClient) {
    ClientSessionOptions sessionOptions = new ClientSessionOptions()
      .setDefaultTransactionOptions(new TransactionOptions()
        .setReadConcernLevel("majority")
        .setWriteConcern("majority")
        .setMaxCommitTime(30, TimeUnit.SECONDS)
      );

    JsonObject book = JsonObject.of("title", "The Silmarillion", "author", "J. R. R. Tolkien");
    JsonObject author = JsonObject.of("name", "J. R. R. Tolkien", "genre", "Fantasy");

    mongoClient.withTransaction(client ->
        client.insert("books", book)
          .compose(bookId -> client.insert("authors", author))
        , sessionOptions)
      .onSuccess(id -> System.out.println("Both inserts committed with majority write concern"))
      .onFailure(err -> System.err.println("Transaction failed: " + err.getMessage()));
  }

}
