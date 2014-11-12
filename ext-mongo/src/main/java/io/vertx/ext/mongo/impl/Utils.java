package io.vertx.ext.mongo.impl;

import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoCollectionOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

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
    } else if (value instanceof BsonObjectId) {
      return ((BsonObjectId) value).getValue().toHexString();
    }

    throw new IllegalArgumentException("Unvalid bson type " + value.getBsonType() + " for ID field");
  }

  //FIXME: All the manual conversion from JsonObject <-> Document should be removed when https://jira.mongodb.org/browse/JAVA-1325 is finished.
  public static Document toDocument(JsonObject json, JsonObjectCodec codec) {
    return toDocument(json, false, codec);
  }

  public static Document toDocument(JsonObject json, boolean createIfNull, JsonObjectCodec codec) {
    if (json == null && createIfNull) {
      return new Document();
    } else if (json != null) {
      Document doc = new Document();
      json.getMap().forEach((k, v) -> doc.put(k, getDocumentValue(k, v, codec)));
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

  public static MongoCollectionOptions collectionOptions(String writeConcern) {
    MongoCollectionOptions.Builder collectionOptions = MongoCollectionOptions.builder();
    if (writeConcern != null) {
      collectionOptions.writeConcern(WriteConcern.valueOf(writeConcern));
    }

    return collectionOptions.build();
  }

  @SuppressWarnings("unchecked")
  private static Object getDocumentValue(String key, Object value, JsonObjectCodec codec) {
    if (value instanceof JsonObject) {
      Document doc = new Document();
      ((JsonObject) value).getMap().forEach((k, v) -> {
        doc.put(k, getDocumentValue(k, v, codec));
      });
      return doc;
    } else if (value instanceof JsonArray) {
      List<Object> list = new ArrayList<>();
      for (Object o : (JsonArray) value) {
        list.add(getDocumentValue(null, o, codec));
      }
      return list;
    } else if (value instanceof String) {
      // While this should go away, we need to support querying ObjectId's
      if (JsonObjectCodec.ID_FIELD.equals(key) && codec.isSupportingObjectId()) {
        return new ObjectId((String) value);
      } else {
        return value;
      }
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
