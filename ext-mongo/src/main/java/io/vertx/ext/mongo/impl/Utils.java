package io.vertx.ext.mongo.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class Utils {

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
    } else if (value instanceof List) {
      List<Object> list = new ArrayList<>();
      for (Object o : (List) value) {
        list.add(getDocumentValue(o));
      }
      return list;
    } else if (value instanceof Map) {
      Document doc = new Document();
      ((Map<String, Object>) value).forEach((k, v) -> {
        doc.put(k, getDocumentValue(v));
      });
      return doc;
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
