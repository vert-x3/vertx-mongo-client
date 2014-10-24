package io.vertx.ext.mongo.impl;

import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.MongoService;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
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
    mongo = MongoClients.create(new ConnectionString(connectionString));
    db = mongo.getDatabase(dbName);
  }

  @Override
  public void stop() {
    mongo.close();
  }

  private List<ServerAddress> convertServers(JsonArray servers) throws UnknownHostException {
    List<ServerAddress> seeds = new ArrayList<>();
    for (Object elem : servers) {
      JsonObject address = (JsonObject) elem;
      String host = address.getString("host");
      int port = address.getInteger("port");
      seeds.add(new ServerAddress(host, port));
    }
    return seeds;
  }

  @Override
  public void save(String collection, JsonObject document, String writeConcern, Handler<AsyncResult<String>> resultHandler) {
    String genID = generateID(document);
    MongoCollection<Document> coll = getCollection(collection, writeConcern);
    Document mDoc = jsonToDoc(document);
    MongoFuture<WriteConcernResult> future = coll.save(mDoc);
    adaptFuture(future, resultHandler, wr -> genID);
  }

  @Override
  public void insert(String collection, JsonObject document, String writeConcern, Handler<AsyncResult<String>> resultHandler) {
    String genID = generateID(document);
    MongoCollection<Document> coll = getCollection(collection, writeConcern);
    Document mDoc = jsonToDoc(document);
    MongoFuture<WriteConcernResult> future = coll.insert(mDoc);
    adaptFuture(future, resultHandler, wr -> genID);
  }

  @Override
  public void update(String collection, JsonObject query, JsonObject update, String writeConcern, boolean upsert, boolean multi, Handler<AsyncResult<Void>> resultHandler) {
    Document mUpdate = jsonToDoc(update);
    MongoView<Document> view = getView(collection, query, null, null, -1, -1);
    if (upsert) {
      view.upsert();
    }
    MongoFuture<WriteConcernResult> future;
    if (multi) {
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
    adaptFuture(future, resultHandler, doc -> {
      return docToJsonObject(doc);
    });
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
    if (document.getField("_id") == null) {
      genID = UUID.randomUUID().toString();
      document.putString("_id", genID);
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
//    MongoCollectionOptions.Builder builder = MongoCollectionOptions.builder();
//    builder.readPreference(ReadPreference.nearest()); // FIXME shouldn't this be defaulted automatically?
//    if (writeConcern != null) {
//      WriteConcern wc = WriteConcern.valueOf(writeConcern);
//      if (wc == null) {
//        throw new IllegalArgumentException("Invalid WriteConcern: " + writeConcern);
//      }
//      builder.writeConcern(wc);
//    } else {
//      builder.writeConcern(WriteConcern.SAFE); // FIXME - shouldn't this be defaulted automatically?
//    }
//    return db.getCollection(name, builder.build());
    return db.getCollection(name);
  }

  // TODO better conversion
  private JsonObject docToJsonObject(Document doc) {
    JsonObject json = new JsonObject();
    for (Map.Entry<String, Object> entry: doc.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Date) {
        // Convert to long
        json.putNumber(key, ((Date)value).getTime());
      } else if (value instanceof ObjectId) {
        // Convert to String
        json.getString(key, ((ObjectId)value).toHexString());
      } else if (value instanceof Document) {
        json.putValue(key, docToJsonObject((Document) value));
      } else if (value instanceof List) {
        json.putValue(key, new JsonArray((List)value));
      } else {
        json.putValue(key, value);
      }
    }
    return json;
  }

  private Document jsonToDoc(JsonObject jsonObject) {
    if (jsonObject == null) {
      return new Document();
    } else {
      return jsonToDoc(jsonObject.toMap());
    }
  }

  // FIXME - this is very slow - there should be a better way of converting Map to Document in Mongo API!
  private Document jsonToDoc(Map<String, Object> map) {
    Document doc = new Document();
    for (Map.Entry<String, Object> entry: map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        Map inner = (Map)entry.getValue();
        doc.put(entry.getKey(), jsonToDoc(inner));
      } else {
        doc.put(entry.getKey(), entry.getValue());
      }
    }
    return doc;
  }

}
