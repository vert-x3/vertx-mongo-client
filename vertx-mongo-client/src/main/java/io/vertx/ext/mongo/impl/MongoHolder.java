package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;

class MongoHolder implements Shareable {
  private com.mongodb.async.client.MongoClient mongo;
  private MongoDatabase db;
  private final JsonObject config;
  private final Runnable closeRunner;
  private int refCount = 1;

  MongoHolder(JsonObject config, Runnable closeRunner) {
    this.config = config;
    this.closeRunner = closeRunner;
  }

  private synchronized void init() {
    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    mongo = MongoClients.create(parser.settings());
    db = mongo.getDatabase(parser.database());
  }

  synchronized void incRefCount() {
    refCount++;
  }

  synchronized void close() {
    if (--refCount == 0) {
      if (mongo != null) {
        mongo.close();
        mongo = null;
        db = null;
      }

      if (closeRunner != null) {
        closeRunner.run();
      }
    }
  }

  synchronized MongoDatabase getDb() {
    if (db == null) {
      init();
    }
    return db;
  }
}
