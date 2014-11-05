package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoCollectionOptions;
import com.mongodb.async.client.MongoDatabaseOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.WriteOptions;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class Utils {

  public static String idAsString(BsonValue value) {
    if (value instanceof BsonString) {
      return ((BsonString) value).getValue();
    }

    throw new IllegalArgumentException("Unvalid bson type " + value.getBsonType() + " for ID field");
  }

  //FIXME: All the manual conversion from JsonObject <-> Document should be removed when https://jira.mongodb.org/browse/JAVA-1325 is finished.
  public static Document toDocument(JsonObject json) {
    return toDocument(json, false);
  }

  public static Document toDocument(JsonObject json, boolean createIfNull) {
    if (json == null && createIfNull) {
      return new Document();
    } else if (json != null) {
      Document doc = new Document();
      json.getMap().forEach((k, v) -> doc.put(k, getDocumentValue(v)));
      return doc;
    } else {
      return null;
    }
  }

  public static JsonObject toJson(Document document) {
    JsonObject json = new JsonObject();
    document.forEach((k, v) -> json.put(k, getJsonValue(v)));

    return json;
  }

  public static MongoCollectionOptions collectionOptions(WriteOptions options, MongoClientSettings settings) {
    //TODO: If https://jira.mongodb.org/browse/JAVA-1524 gets resolved we won't need MongoClientSettings
    MongoCollectionOptions.Builder builder = MongoCollectionOptions.builder();
    if (options.getWriteConcern() != null) {
      builder.writeConcern(WriteConcern.valueOf(options.getWriteConcern()));
    }

    MongoDatabaseOptions dbOptions = MongoDatabaseOptions.builder().build().withDefaults(settings);
    return builder.build().withDefaults(dbOptions);
  }

  @SuppressWarnings("unchecked")
  private static Object getDocumentValue(Object value) {
    if (value instanceof JsonObject) {
      Document doc = new Document();
      ((JsonObject) value).getMap().forEach((k, v) -> {
        doc.put(k, getDocumentValue(v));
      });
      return doc;
    } else if (value instanceof JsonArray) {
      List<Object> list = new ArrayList<>();
      for (Object o : (JsonArray) value) {
        list.add(getDocumentValue(o));
      }
      return list;
    } else {
      return value;
    }
  }

  private static Object getJsonValue(Object value) {
    if (value instanceof Document) {
      JsonObject json = new JsonObject();
      ((Document) value).forEach((k, v) -> {
        json.put(k, getJsonValue(v));
      });
      return json;
    } else if (value instanceof List) {
      JsonArray array = new JsonArray();
      for (Object o : (List) value) {
        array.add(getJsonValue(o));
      }
      return array;
    } else if (value instanceof Date) {
      return ((Date) value).getTime();
    } else {
      return value;
    }
  }
}
