package io.vertx.ext.mongo.impl;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteConcernResult;
import com.mongodb.async.MongoFuture;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoView;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.InsertOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOptions;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.vertx.ext.mongo.impl.Utils.*;
import static java.util.Objects.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceImpl implements MongoService {

  private static final Logger log = LoggerFactory.getLogger(MongoServiceImpl.class);

  private final Vertx vertx;
  private final JsonObject config;

  protected MongoClient mongo;
  protected MongoDatabase db;
  private MongoClientSettings mongoClientSettings;
  private JsonObjectCodec codec;

  public MongoServiceImpl(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    String connectionString = config.getString("connection_string", "mongodb://localhost:27017");
    String dbName = config.getString("db_name", "default_db");

    codec = new JsonObjectCodec();

    MongoClientSettings.Builder mcs = MongoClientSettings.builder();

    //TODO: If https://jira.mongodb.org/browse/JAVA-1518 gets done we can go from Map -> MongoClientSettings

    // Default write concern for client (this can be overridden for individual operations)
    WriteConcern wc = writeConcern(config);
    if (wc != null) {
      mcs.writeConcern(wc);
    }
    // Default read preference for client (this can be overridden for individual operations)
    ReadPreference rp = readPreference(config);
    if (rp != null) {
      mcs.readPreference(rp);
    }
    // Apply settings from connection string
    applyConnectionString(mcs, connectionString);
    mongoClientSettings = mcs.build();

    mongo = MongoClients.create(mongoClientSettings);
    db = mongo.getDatabase(dbName);

    log.debug("mongoDB service started");
  }

  @Override
  public void stop() {
    mongo.close();
    log.debug("mongoDB service stopped");
  }

  @Override
  public void save(String collection, JsonObject document, WriteOptions options, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    //FIXME: Use MongoCollection<JsonObject> when https://jira.mongodb.org/browse/JAVA-1325 is complete and no need for this genId malarkey
    boolean insert = !codec.documentHasId(document);

    codec.generateIdIfAbsentFromDocument(document);
    MongoCollection<Document> coll = db.getCollection(collection, collectionOptions(options, mongoClientSettings));

    //TODO: Consider returning WriteConcernResult as a JsonObject, instead of just the id mayhaps ?
    MongoFuture<WriteConcernResult> future = coll.save(toDocument(document));
    adaptFuture(future, resultHandler, wr -> {
      if (insert) {
        return idAsString(codec.getDocumentId(document));
      } else {
        return null;
      }
    });
  }

  @Override
  public void insert(String collection, JsonObject document, InsertOptions options, Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(document, "document cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    boolean insert = !codec.documentHasId(document);

    MongoCollection<JsonObject> coll = getCollection(collection, options);
    MongoFuture<WriteConcernResult> future = coll.insert(document);
    adaptFuture(future, resultHandler, wr -> {
      if (insert) {
        return idAsString(codec.getDocumentId(document));
      } else {
        return null;
      }
    });
  }

  @Override
  public void update(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(update, "update cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query, null, null, -1, -1);
    if (isTrue(options.isUpsert())) {
      view.upsert();
    }
    MongoFuture<WriteConcernResult> future;
    if (isTrue(options.isMulti())) {
      future = view.update(toDocument(update));
    } else {
      future = view.updateOne(toDocument(update));
    }
    adaptFuture(future, resultHandler);
  }

  @Override
  public void find(String collection, JsonObject query, JsonObject fields, JsonObject sort, int limit, int skip, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query, fields, sort, limit, skip);
    List<JsonObject> results = new ArrayList<>();
    MongoFuture<List<JsonObject>> future = view.into(results);
    handleFuture(future, resultHandler);
  }

  @Override
  public void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query, fields, null, -1, -1);
    MongoFuture<JsonObject> future = view.one();
    handleFuture(future, resultHandler);
  }

  @Override
  public void delete(String collection, JsonObject query, WriteOptions options, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(options, "options cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query, null, null, -1, -1);
    MongoFuture<WriteConcernResult> future = view.remove();
    adaptFuture(future, resultHandler);
  }

  @Override
  public void createCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoFuture<Void> future = db.tools().createCollection(collection);
    adaptFuture(future, resultHandler, wr -> null);
  }

  @Override
  public void getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    MongoFuture<List<String>> future = db.tools().getCollectionNames();
    requireNonNull(resultHandler, "resultHandler cannot be null");

    adaptFuture(future, resultHandler, res -> res);
  }

  @Override
  public void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoCollection<JsonObject> coll = getCollection(collection);
    MongoFuture<Void> future = coll.tools().drop();
    handleFuture(future, resultHandler);
  }

  @Override
  public void runCommand(String collection, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoFuture<Document> future = db.executeCommand(toDocument(command));
    adaptFuture(future, resultHandler, Utils::toJson);
  }

  private void adaptFuture(MongoFuture<WriteConcernResult> future, Handler<AsyncResult<Void>> resultHandler) {
    adaptFuture(future, resultHandler, wr -> null);
  }

  private <T, U> void adaptFuture(MongoFuture<T> future, Handler<AsyncResult<U>> resultHandler, Function<T, U> converter) {
    Context context = vertx.context();
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
    Context context = vertx.context();
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

  private MongoView<JsonObject> getView(String collection, JsonObject query, JsonObject fields, JsonObject sort, int limit, int skip) {
    MongoCollection<JsonObject> coll = getCollection(collection);
    MongoView<JsonObject> view = coll.find(toDocument(query));
    if (limit != -1) {
      view.limit(limit);
    }
    if (skip != -1) {
      view.skip(skip);
    }
    if (sort != null) {
      view.sort(toDocument(sort));
    }
    if (fields != null) {
      view.fields(toDocument(fields));
    }
    return view;
  }

  private MongoCollection<JsonObject> getCollection(String name) {
    return getCollection(name, new WriteOptions());
  }

  private MongoCollection<JsonObject> getCollection(String name, WriteOptions options) {
    return db.getCollection(name, codec, collectionOptions(options, mongoClientSettings));
  }

  private static boolean isTrue(Boolean bool) {
    return bool != null && bool;
  }
}
