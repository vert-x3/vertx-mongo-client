package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.options.OperationOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.WriteConcern;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;

import java.util.List;

import static java.util.Objects.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceImpl extends AbstractMongo implements MongoService {

  private static final Logger log = LoggerFactory.getLogger(MongoServiceImpl.class);

  private final Vertx vertx;
  private final JsonObject config;

  protected MongoClient mongo;
  protected MongoDatabase db;

  public MongoServiceImpl(Vertx vertx, JsonObject config) {
    super(vertx);
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    mongo = MongoClients.create(parser.options());

    String dbName = config.getString("db_name", "default_db");
    db = mongo.getDatabase(dbName);

    log.debug("mongoDB service started");
  }

  @Override
  public void stop() {
    if (mongo != null) {
      mongo.close();
    }
    log.debug("mongoDB service stopped");
  }

  @Override
  public void getCollection(String name, Handler<AsyncResult<io.vertx.ext.mongo.MongoCollection>> resultHandler) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollectionImpl collection = new MongoCollectionImpl(vertx, mongoCollection(name));
    resultHandler.handle(Future.succeededFuture(collection));
  }

  @Override
  public void getCollectionWithWriteConcern(String name, WriteConcern wc, Handler<AsyncResult<io.vertx.ext.mongo.MongoCollection>> resultHandler) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(wc, "wc cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollectionImpl collection = new MongoCollectionImpl(vertx, mongoCollection(name, wc));
    resultHandler.handle(Future.succeededFuture(collection));
  }

  @Override
  public void createCollection(String name, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    db.createCollection(name, wrapCallback(resultHandler));
  }

  @Override
  public void getCollectionNames(Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");

    db.getCollectionNames(wrapCallback(resultHandler));
  }

  @Override
  public void dropCollection(String name, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = mongoCollection(name);
    coll.dropCollection(wrapCallback(resultHandler));
  }

  @Override
  public void runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    db.executeCommand(command, db.getOptions().getReadPreference(), JsonObject.class, wrapCallback(resultHandler));
  }

  private MongoCollection<JsonObject> mongoCollection(String name) {
    return db.getCollection(name, JsonObject.class);
  }

  private MongoCollection<JsonObject> mongoCollection(String name, WriteConcern writeConcern) {
    // Get the options that were configured for the db, and overwrite the write concern
    OperationOptions dbOptions = db.getOptions();
    OperationOptions.Builder options = OperationOptions.builder();
    options.readPreference(dbOptions.getReadPreference());
    if (writeConcern != null) {
      options.writeConcern(com.mongodb.WriteConcern.valueOf(writeConcern.name()));
    } else {
      options.writeConcern(dbOptions.getWriteConcern());
    }
    return db.getCollection(name, JsonObject.class, options.build());
  }
}
