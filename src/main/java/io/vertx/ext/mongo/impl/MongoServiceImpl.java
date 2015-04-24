package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import io.vertx.core.AsyncResult;
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
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

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
    mongo = MongoClients.create(parser.settings());

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
  public MongoService save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    saveWithOptions(collection, document, null, resultHandler);
    return this;
  }

  @Override
  public MongoService saveWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    String id = document.getString(ID_FIELD);
    if (id == null) {
      coll.insertOne(document, convertCallback(resultHandler, wr -> document.getString(ID_FIELD)));
    } else {
      coll.replaceOne(wrap(new JsonObject().put(ID_FIELD, document.getString(ID_FIELD))), document, convertCallback(resultHandler, result -> null));
    }
    return this;
  }

  @Override
  public MongoService insert(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
    insertWithOptions(collection, document, null, resultHandler);
    return this;
  }

  @Override
  public MongoService insertWithOptions(String collection, JsonObject document, WriteOption writeOption, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    boolean id = document.containsKey(ID_FIELD);

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    coll.insertOne(document, convertCallback(resultHandler, wr -> {
      if (id) {
        return null;
      } else {
        return document.getString(ID_FIELD);
      }
    }));
    return this;
  }

  @Override
  public MongoService update(String collection, JsonObject query, JsonObject update, Handler<AsyncResult<Void>> resultHandler) {
    updateWithOptions(collection, query, update, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoService updateWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(query);
    Bson bupdate = wrap(update);
    if (options.isMulti()) {
      coll.updateMany(bquery, bupdate, mongoUpdateOptions(options), convertCallback(resultHandler, result -> null));
    } else {
      coll.updateOne(bquery, bupdate, mongoUpdateOptions(options), convertCallback(resultHandler, result -> null));
    }
    return this;
  }

  @Override
  public MongoService replace(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<Void>> resultHandler) {
    replaceWithOptions(collection, query, replace, DEFAULT_UPDATE_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoService replaceWithOptions(String collection, JsonObject query, JsonObject replace, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(replace, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, options.getWriteOption());
    Bson bquery = wrap(query);
    coll.replaceOne(bquery, replace, mongoUpdateOptions(options), convertCallback(resultHandler, result -> null));
    return this;
  }

  @Override
  public MongoService find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    findWithOptions(collection, query, DEFAULT_FIND_OPTIONS, resultHandler);
    return this;
  }

  @Override
  public MongoService findWithOptions(String collection, JsonObject query, FindOptions options, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    FindIterable<JsonObject> view = doFind(collection, query, options);
    List<JsonObject> results = new ArrayList<>();
    view.into(results, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoService findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    Bson bquery = wrap(query);
    Bson bfields = wrap(fields);
    getCollection(collection).find(bquery).projection(bfields).first(wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoService count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    Bson bquery = wrap(query);
    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.count(bquery, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoService remove(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoService removeWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    coll.deleteMany(bquery, convertCallback(resultHandler, result -> null));
    return this;
  }

  @Override
  public MongoService removeOne(String collection, JsonObject query, Handler<AsyncResult<Void>> resultHandler) {
    removeOneWithOptions(collection, query, null, resultHandler);
    return this;
  }

  @Override
  public MongoService removeOneWithOptions(String collection, JsonObject query, WriteOption writeOption, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    coll.deleteOne(bquery, convertCallback(resultHandler, result -> null));
    return this;
  }

  @Override
  public MongoService createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    db.createCollection(collection, wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoService getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");
    List<String> names = new ArrayList<>();
    db.listCollectionNames().into(names, (res, error) -> {
      vertx.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(names));
        }
      });
    });
    return this;
  }

  @Override
  public MongoService dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection);
    coll.drop(wrapCallback(resultHandler));
    return this;
  }

  @Override
  public MongoService runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");
    db.runCommand(wrap(command), JsonObject.class, wrapCallback(resultHandler));
    return this;
  }

  private <T, R> SingleResultCallback<T> convertCallback(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
    return (result, error) -> {
      vertx.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(converter.apply(result)));
        }
      });
    };
  }

  private <T> SingleResultCallback<T> wrapCallback(Handler<AsyncResult<T>> resultHandler) {
    return (result, error) -> {
      vertx.runOnContext(v -> {
        if (error != null) {
          resultHandler.handle(Future.failedFuture(error));
        } else {
          resultHandler.handle(Future.succeededFuture(result));
        }
      });
    };
  }

  private FindIterable<JsonObject> doFind(String collection, JsonObject query, FindOptions options) {
    return doFind(collection, null, query, options);
  }

  private FindIterable<JsonObject> doFind(String collection, WriteOption writeOption, JsonObject query, FindOptions options) {
    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    Bson bquery = wrap(query);
    FindIterable<JsonObject> find = coll.find(bquery, JsonObject.class);
    if (options.getLimit() != -1) {
      find.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      find.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      find.sort(wrap(options.getSort()));
    }
    if (options.getFields() != null) {
      find.projection(wrap(options.getFields()));
    }
    return find;
  }

  private MongoCollection<JsonObject> getCollection(String name) {
    return getCollection(name, null);
  }

  private MongoCollection<JsonObject> getCollection(String name, WriteOption writeOption) {
    MongoCollection<JsonObject> coll = db.getCollection(name, JsonObject.class);
    if (coll != null && writeOption != null) {
      coll = coll.withWriteConcern(WriteConcern.valueOf(writeOption.name()));
    }
    return coll;
  }

  private static com.mongodb.client.model.UpdateOptions mongoUpdateOptions(UpdateOptions options) {
    return new com.mongodb.client.model.UpdateOptions().upsert(options.isUpsert());
  }

  private JsonObjectBsonAdapter wrap(JsonObject jsonObject) {
    return jsonObject == null ? null : new JsonObjectBsonAdapter(jsonObject);
  }

}
