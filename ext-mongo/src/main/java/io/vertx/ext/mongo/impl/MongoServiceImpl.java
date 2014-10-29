package io.vertx.ext.mongo.impl;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteConcernResult;
import com.mongodb.async.MongoFuture;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoCollectionOptions;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoView;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.InsertOptions;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoServiceImpl implements MongoService {

  private static final Logger log = LoggerFactory.getLogger(MongoServiceImpl.class);

  private final Vertx vertx;
  private final JsonObject config;

  protected MongoClient mongo;
  protected MongoDatabase db;

  public MongoServiceImpl(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    String connectionString = config.getString("connection_string", "mongodb://localhost:27017");
    String dbName = config.getString("db_name", "default_db");

    MongoClientSettings.Builder settings = MongoClientSettings.builder();

    //TODO: If https://jira.mongodb.org/browse/JAVA-1518 gets done we can go from Map -> MongoClientSettings

    // Default write concern for client (this can be overridden for individual operations)
    WriteConcern wc = Utils.writeConcern(config);
    if (wc != null) {
      settings.writeConcern(wc);
    }
    // Default read preference for client (this can be overridden for individual operations)
    ReadPreference rp = Utils.readPreference(config);
    if (rp != null) {
      settings.readPreference(rp);
    }
    // Apply settings from connection string
    Utils.applyConnectionString(settings, connectionString);

    mongo = MongoClients.create(settings.build());
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
    String genID = generateID(document);
    MongoCollection<Document> coll = getCollection(collection, options.getWriteConcern());
    Document mDoc = jsonToDoc(document);
    MongoFuture<WriteConcernResult> future = coll.save(mDoc);
    adaptFuture(future, resultHandler, wr -> genID);
  }

  @Override
  public void insert(String collection, JsonObject document, InsertOptions options, Handler<AsyncResult<String>> resultHandler) {
    String genID = generateID(document);
    MongoCollection<Document> coll = getCollection(collection, options.getWriteConcern());
    Document mDoc = jsonToDoc(document);
    MongoFuture<WriteConcernResult> future = coll.insert(mDoc);
    adaptFuture(future, resultHandler, wr -> genID);
  }

  @Override
  public void update(String collection, JsonObject query, JsonObject update, UpdateOptions options, Handler<AsyncResult<Void>> resultHandler) {
    Document mUpdate = jsonToDoc(update);
    MongoView<Document> view = getView(collection, query, null, null, -1, -1);
    if (options.isUpsert()) {
      view.upsert();
    }
    MongoFuture<WriteConcernResult> future;
    if (options.isMulti()) {
      future = view.update(mUpdate);
    } else {
      future = view.updateOne(mUpdate);
    }
    adaptFuture(future, resultHandler);
  }

  @Override
  public void find(String collection, JsonObject query, JsonObject fields, JsonObject sort, int limit, int skip, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
    MongoView<Document> view = getView(collection, query, fields, sort, limit, skip);
    List<JsonObject> results = new ArrayList<>();
    MongoFuture<Void> future = view.forEach(res -> results.add(docToJsonObject(res)));
    adaptFuture(future, resultHandler, wr -> results);
  }

  @Override
  public void findOne(String collection, JsonObject query, JsonObject fields, Handler<AsyncResult<JsonObject>> resultHandler) {
    MongoView<Document> view = getView(collection, query, fields, null, -1, -1);
    MongoFuture<Document> future = view.one();
    adaptFuture(future, resultHandler, (Document doc) -> {
      return doc == null ? null : docToJsonObject(doc);
    });
  }

  @Override
  public void delete(String collection, JsonObject query, String writeConcern, Handler<AsyncResult<Void>> resultHandler) {
    MongoView<Document> view = getView(collection, query, null, null, -1, -1);
    MongoFuture<WriteConcernResult> future = view.remove();
    adaptFuture(future, resultHandler);
  }

  @Override
  public void createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
    MongoFuture<Void> future = db.tools().createCollection(collectionName);
    adaptFuture(future, resultHandler, wr -> null);
  }

  @Override
  public void getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
    MongoFuture<List<String>> future = db.tools().getCollectionNames();
    adaptFuture(future, resultHandler, res -> res);
  }

  @Override
  public void dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
    MongoCollection<Document> coll = getCollection(collection, null);
    MongoFuture<Void> future = coll.tools().drop();
    adaptFuture(future, resultHandler, v -> null);
  }

  @Override
  public void runCommand(String collection, JsonObject command, Handler<AsyncResult<JsonObject>> resultHandler) {
    Document mCommand = jsonToDoc(command);
    MongoFuture<Document> future = db.executeCommand(mCommand);
    adaptFuture(future, resultHandler, this::docToJsonObject);
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

  private String generateID(JsonObject document) {
    // TODO - is it right that we generate the id here?
    String genID;
    if (document.getValue("_id") == null) {
      genID = UUID.randomUUID().toString();
      document.put("_id", genID);
    } else {
      genID = null;
    }
    return genID;
  }

  private MongoView<Document> getView(String collection, JsonObject query, JsonObject fields, JsonObject sort,
                                      int limit, int skip) {
    MongoCollection<Document> coll = getCollection(collection, null);
    Document mQuery = jsonToDoc(query);
    Document mFields = jsonToDoc(fields);
    Document mSort = jsonToDoc(sort);
    MongoView<Document> view = coll.find(mQuery);
    if (limit != -1) {
      view.limit(limit);
    }
    if (skip != -1) {
      view.skip(skip);
    }
    if (mSort != null) {
      view.sort(mSort);
    }
    if (mFields != null) {
      view.fields(mFields);
    }
    return view;
  }

  private MongoCollection<Document> getCollection(String name, String writeConcern) {
    // Apparently MongoCollectionOptions doesn't default to w/e is used when we call w/out
    if (writeConcern == null) {
      return db.getCollection(name);
    }

    MongoCollectionOptions.Builder options = MongoCollectionOptions.builder();
    options.writeConcern(WriteConcern.valueOf(writeConcern));
    return db.getCollection(name, options.build());
  }

  // TODO better conversion
  private JsonObject docToJsonObject(Document doc) {
    JsonObject json = new JsonObject();
    for (Map.Entry<String, Object> entry: doc.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Date) {
        // Convert to long
        json.put(key, ((Date) value).getTime());
      } else if (value instanceof ObjectId) {
        // Convert to String
        json.getString(key, ((ObjectId)value).toHexString());
      } else if (value instanceof Document) {
        json.put(key, docToJsonObject((Document) value));
      } else if (value instanceof List) {
        json.put(key, new JsonArray((List) value));
      } else {
        json.put(key, value);
      }
    }
    return json;
  }

  private Document jsonToDoc(JsonObject jsonObject) {
    if (jsonObject == null) {
      return new Document();
    } else {
      // FIXME there should be some way of specifying a codec for the mongo client to use, that way
      // we wouldn't have to do this!!
      return jsonToDoc(jsonObject.getMap());
    }
  }


  // FIXME - this is very slow - there should be a better way of converting Map to Document in Mongo API!
  // FIXME - also it won't work with JsonArrays/Lists that contain JsonObjects/Maps
  private Document jsonToDoc(Map<String, Object> map) {
    Document doc = new Document();
    for (Map.Entry<String, Object> entry: map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        Map inner = (Map) entry.getValue();
        doc.put(entry.getKey(), jsonToDoc(inner));
      } else if (entry.getValue() instanceof JsonObject) {
        Map inner = ((JsonObject) entry.getValue()).getMap();
        doc.put(entry.getKey(), jsonToDoc(inner));
      } else if (entry.getValue() instanceof JsonArray) {
        List inner = ((JsonArray) entry.getValue()).getList();
        doc.put(entry.getKey(), inner);
      } else {
        doc.put(entry.getKey(), entry.getValue());
      }
    }
    return doc;
  }



}
