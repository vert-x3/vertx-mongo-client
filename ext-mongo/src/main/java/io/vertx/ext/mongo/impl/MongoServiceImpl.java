package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcernResult;
import com.mongodb.async.MongoFuture;
import com.mongodb.async.client.MongoClient;
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
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
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
  private static final UpdateOptions DEFAULT_UPDATE_OPTIONS = new UpdateOptions();
  private static final FindOptions DEFAULT_FIND_OPTIONS = new FindOptions();

  private final Vertx vertx;
  private final JsonObject config;

  protected MongoClient mongo;
  protected MongoDatabase db;
  private JsonObjectCodec codec;

  public MongoServiceImpl(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    mongo = MongoClients.create(parser.options());

    String dbName = config.getString("db_name", "default_db");
    db = mongo.getDatabase(dbName);

    // TODO: Revisit this because update & replace generate an ObjectId on upsert because codecs are a lil broken
    codec = new JsonObjectCodec(config.getBoolean("useObjectId", false));

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

    //FIXME: Use MongoCollection<JsonObject> when https://jira.mongodb.org/browse/JAVA-1325 is complete and no need for this genId malarkey
    boolean insert = !codec.documentHasId(document);

    codec.generateIdIfAbsentFromDocument(document);
    MongoCollection<Document> coll = db.getCollection(collection, collectionOptions(writeOption));

    //TODO: Consider returning WriteConcernResult as a JsonObject, instead of just the id mayhaps ?
    MongoFuture<WriteConcernResult> future = coll.save(toDocument(document, codec));
    adaptFuture(future, resultHandler, wr -> {
      if (insert) {
        return idAsString(codec.getDocumentId(document));
      } else {
        return null;
      }
    });
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

    boolean insert = !codec.documentHasId(document);

    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
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

    MongoView<JsonObject> view = getView(collection, query);
    if (options.isUpsert()) {
      view.upsert();
    }
    MongoFuture<WriteConcernResult> future;
    if (options.isMulti()) {
      future = view.update(toDocument(update, codec));
    } else {
      future = view.updateOne(toDocument(update, codec));
    }
    adaptFuture(future, resultHandler);
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

    //FIXME: Use typed API when mongo driver is updated
    MongoView view = getView(collection, query);
    if (options.isUpsert()) {
      view.upsert();
    }
    @SuppressWarnings("unchecked")
    MongoFuture<WriteConcernResult> future = view.replace(toDocument(replace, codec));
    adaptFuture(future, resultHandler);
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

    MongoView<JsonObject> view = getView(collection, query, options);
    List<JsonObject> results = new ArrayList<>();
    MongoFuture<List<JsonObject>> future = view.into(results);
    handleFuture(future, resultHandler);
  }

  @Override
  public void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query, new FindOptions().setFields(fields));
    MongoFuture<JsonObject> future = view.one();
    handleFuture(future, resultHandler);
  }

  @Override
  public void count(String collection, JsonObject query, Handler<AsyncResult<Long>> resultHandler) {
    requireNonNull(collection, "collection cannot be null");
    requireNonNull(query, "query cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoView<JsonObject> view = getView(collection, query);
    MongoFuture<Long> future = view.count();
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

    MongoView<JsonObject> view = getView(collection, writeOption, query);
    MongoFuture<WriteConcernResult> future = view.remove();
    adaptFuture(future, resultHandler);
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

    MongoView<JsonObject> view = getView(collection, writeOption, query);
    MongoFuture<WriteConcernResult> future = view.removeOne();
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
  public void runCommand(JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    requireNonNull(command, "command cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    MongoFuture<Document> future = db.executeCommand(toDocument(command, codec));
    adaptFuture(future, resultHandler, Utils::toJson);
  }

  private void adaptFuture(MongoFuture<WriteConcernResult> future, Handler<AsyncResult<Void>> resultHandler) {
    adaptFuture(future, resultHandler, wr -> null);
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

  private MongoView<JsonObject> getView(String collection, JsonObject query) {
    return getView(collection, null, query);
  }

  private MongoView<JsonObject> getView(String collection, WriteOption writeOption, JsonObject query) {
    return getView(collection, writeOption, query, DEFAULT_FIND_OPTIONS);
  }

  private MongoView<JsonObject> getView(String collection, JsonObject query, FindOptions options) {
    return getView(collection, null, query, options);
  }

  private MongoView<JsonObject> getView(String collection, WriteOption writeOption, JsonObject query, FindOptions options) {
    MongoCollection<JsonObject> coll = getCollection(collection, writeOption);
    MongoView<JsonObject> view = coll.find(toDocument(query, codec));
    if (options.getLimit() != -1) {
      view.limit(options.getLimit());
    }
    if (options.getSkip() > 0) {
      view.skip(options.getSkip());
    }
    if (options.getSort() != null) {
      view.sort(toDocument(options.getSort(), codec));
    }
    if (options.getFields() != null) {
      view.fields(toDocument(options.getFields(), codec));
    }
    return view;
  }

  private MongoCollection<JsonObject> getCollection(String name) {
    return getCollection(name, null);
  }

  private MongoCollection<JsonObject> getCollection(String name, WriteOption writeOption) {
    return db.getCollection(name, codec, collectionOptions(writeOption));
  }
}
