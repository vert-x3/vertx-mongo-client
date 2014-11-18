package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcern;
import com.mongodb.WriteConcernResult;
import com.mongodb.async.MongoFuture;
import com.mongodb.async.client.FindFluent;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.options.OperationOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceImpl implements MongoService {

  private static final Logger log = LoggerFactory.getLogger(MongoServiceImpl.class);
  private static final UpdateOptions DEFAULT_UPDATE_OPTIONS = new UpdateOptions();
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();
  private static final String ID_FIELD = "_id";

  private final Vertx vertx;
  private final JsonObject config;

  protected MongoClient mongo;
  protected MongoDatabase db;

  public MongoServiceImpl(Vertx vertx, JsonObject config) {
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
  public void save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    saveWithOptions(collection, document, null, resultHandler);
  }

  @Override
  public void saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    //TODO: Check to see if save could added back to API
    String id = document.getString(ID_FIELD);
    if (id == null) {
      MongoFuture<WriteConcernResult> future = coll.insertOne(document);
      adaptFuture(future, resultHandler, wr -> document.getString(ID_FIELD));
    } else {
      MongoFuture<UpdateResult> future = coll.replaceOne(new JsonObject().put(ID_FIELD, document.getString(ID_FIELD)), document);
      adaptFuture(future, resultHandler, result -> null);
    }
  }

  @Override
  public void insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    insertWithOptions(collection, document, null, resultHandler);
  }

  @Override
  public void insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    MongoFuture<WriteConcernResult> future = coll.insertOne(document);
    adaptFuture(future, resultHandler, wr -> document.getString(ID_FIELD));
  }

  @Override
  public void update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) {
    updateWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
  }

  @Override
  public void updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    MongoFuture<UpdateResult> future;
    if (options.isMulti()) {
      future = coll.updateMany(query, update, mongoUpdateOptions(options));
    } else {
      future = coll.updateOne(query, update, mongoUpdateOptions(options));
    }
    adaptFuture(future, resultHandler, result -> null);
  }

  @Override
  public void replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) {
    replaceWithOptions(collection, query, replace, DEFAULT_UPDATE_OPTIONS, resultHandler);
  }

  @Override
  public void replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(replace, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    MongoFuture<UpdateResult> future = coll.replaceOne(query, replace, mongoUpdateOptions(options));
    adaptFuture(future, resultHandler, result -> null);
  }

  @Override
  public void find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
  }

  @Override
  public void findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindFluent<JsonObject> view = doFind(collection, query, options);
    List<JsonObject> results = new ArrayList<>();
    MongoFuture<List<JsonObject>> future = view.into(results);
    handleFuture(future, resultHandler);
  }

  @Override
  public void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindFluent<JsonObject> find = doFind(collection, query, new FindOptions().setFields(fields));
    MongoFuture<JsonObject> future = find.first();
    handleFuture(future, resultHandler);
  }

  @Override
  public void count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection);
    MongoFuture<Long> future = coll.count();
    handleFuture(future, resultHandler);
  }

  @Override
  public void remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeWithOptions(collection, query, null, resultHandler);
  }

  @Override
  public void removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    MongoFuture<DeleteResult> future = coll.deleteMany(query);
    adaptFuture(future, resultHandler, result -> null);
  }

  @Override
  public void removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeOneWithOptions(collection, query, null, resultHandler);
  }

  @Override
  public void removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    MongoFuture<DeleteResult> future = coll.deleteOne(query);
    adaptFuture(future, resultHandler, result -> null);
  }

  @Override
  public void createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoFuture<Void> future = db.createCollection(collection);
    adaptFuture(future, resultHandler, wr -> null);
  }

  @Override
  public void getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    MongoFuture<List<String>> future = db.getCollectionNames();
    requireNonNull(resultHandler, "resultHandler cannot be null");

    adaptFuture(future, resultHandler, res -> res);
  }

  @Override
  public void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection);
    MongoFuture<Void> future = coll.dropCollection();
    handleFuture(future, resultHandler);
  }

  @Override
  public void runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    //TODO: Looks like executeCommand cannot accept a codec atm.
    MongoFuture<Document> future = db.executeCommand(Utils.toDocument(command));
    adaptFuture(future, resultHandler, Utils::toJson);
  }

  private <T, U> void adaptFuture(MongoFuture<T> future, Handler<AsyncResult<U>> resultHandler, Function<T, U> converter) {
    Context context = vertx.getOrCreateContext();
    future.register((wr, e) -> {
      context.runOnContext(v -> {
        if (e != null) {
          resultHandler.handle(Future.completedFuture(e));
        } else {
          resultHandler.handle(Future.completedFuture(converter.apply(wr)));
        }
      });
    });
  }

  private <T> void handleFuture(MongoFuture<T> future, Handler<AsyncResult<T>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    future.register((result, e) -> {
      context.runOnContext(v -> {
        if (e != null) {
          resultHandler.handle(Future.completedFuture(e));
        } else {
          resultHandler.handle(Future.completedFuture(result));
        }
      });
    });
  }

  private FindFluent<JsonObject> doFind(String collection, JsonObject query, FindOptions options) {
    return doFind(collection, null, query, options);
  }

  private FindFluent<JsonObject> doFind(String collection, WriteOption writeOption, JsonObject query, FindOptions options) {
    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    FindFluent<JsonObject> find = coll.find(query, JsonObject.class);
    if (options.getLimit() != -1) {
      find.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      find.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      find.sort(options.getSort());
    }
    if (options.getFields() != null) {
      find.projection(options.getFields());
    }
    return find;
  }

  private MongoCollection<JsonObject> getCollection(String name) {
    return getCollection(name, null);
  }

  private MongoCollection<JsonObject> getCollection(String name, WriteOption writeOption) {
    return db.getCollection(name, JsonObject.class, operationOptions(writeOption));
  }

  private static OperationOptions operationOptions(WriteOption writeOption) {
    OperationOptions.Builder options = OperationOptions.builder();
    if (writeOption != null) {
      options.writeConcern(WriteConcern.valueOf(writeOption.name()));
    }

    return options.build();
  }

  private static com.mongodb.client.model.UpdateOptions mongoUpdateOptions(UpdateOptions options) {
    return new com.mongodb.client.model.UpdateOptions().upsert(options.isUpsert());
  }
}
