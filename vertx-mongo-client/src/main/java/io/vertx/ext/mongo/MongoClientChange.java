package io.vertx.ext.mongo;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.codecs.DecoderContext;

import java.util.ArrayList;
import java.util.List;

@DataObject
public class MongoClientChange {
  private static final JsonObjectCodec CODEC = new JsonObjectCodec(new JsonObject());
  private List<String> removedFields;
  private JsonObject updatedFields;
  private JsonObject fullDocument;
  private JsonObject resumeToken;
  private JsonObject namespace;
  private MongoClientChangeOperationType operationType;

  public MongoClientChange() {
    removedFields = new ArrayList<>();
    updatedFields = new JsonObject();
  }

  public MongoClientChange(MongoClientChange other) {
    this();
    removedFields.addAll(other.removedFields);
    updatedFields = other.updatedFields.copy();
    fullDocument = other.fullDocument.copy();
    resumeToken = other.resumeToken.copy();
    operationType = other.operationType;
    namespace = other.namespace.copy();
  }

  @SuppressWarnings("unchecked")
  public MongoClientChange(JsonObject json) {
    this();
    JsonArray removedFields = json.getJsonArray("removedFields");
    if (removedFields != null) {
      this.removedFields.addAll(removedFields.getList());
    }
    this.updatedFields = json.getJsonObject("updatedFields");
    this.fullDocument = json.getJsonObject("fullDocument");
    this.resumeToken = json.getJsonObject("resumeToken");
    this.operationType = MongoClientChangeOperationType.valueOf(json.getString("operationType"));
    this.namespace = json.getJsonObject("namespace");
  }

  private static JsonObjectCodec getCodec() {
    return CODEC;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("removedFields", new JsonArray(removedFields));
    if (updatedFields != null) {
      json.put("updatedFields", updatedFields);
    }
    if (fullDocument != null) {
      json.put("fullDocument", fullDocument);
    }
    if (resumeToken != null) {
      json.put("resumeToken", resumeToken);
    }
    if (operationType != null) {
      json.put("operationType", operationType);
    }
    if (namespace != null) {
      json.put("namespace", namespace);
    }
    return json;
  }

  public MongoClientChange(ChangeStreamDocument<JsonObject> obj) {
    this();
    if (obj.getUpdateDescription() != null) {
      removedFields = obj.getUpdateDescription().getRemovedFields();
      final BsonDocument updatedFields = obj.getUpdateDescription().getUpdatedFields();
      if (updatedFields != null) {
        this.updatedFields = getCodec().decode(new BsonDocumentReader(updatedFields), DecoderContext.builder().build());
      }
    }

    fullDocument = obj.getFullDocument();
    final BsonDocument resumeToken = obj.getResumeToken();
    if (resumeToken != null) {
      this.resumeToken = getCodec().decode(new BsonDocumentReader(resumeToken), DecoderContext.builder().build());
    }
    operationType = MongoClientChangeOperationType.valueOf(obj.getOperationType().name());
    namespace = new JsonObject()
      .put("db", obj.getNamespace().getDatabaseName())
      .put("coll", obj.getNamespace().getCollectionName());
  }

  public MongoClientChange removedFields(List<String> removedFields) {
    this.removedFields = removedFields;
    return this;
  }

  public MongoClientChange updatedFields(JsonObject updatedFields) {
    this.updatedFields = updatedFields;
    return this;
  }

  public MongoClientChange fullDocument(JsonObject fullDocument) {
    this.fullDocument = fullDocument;
    return this;
  }

  public MongoClientChange resumeToken(JsonObject resumeToken) {
    this.resumeToken = resumeToken;
    return this;
  }

  public MongoClientChange operationType(MongoClientChangeOperationType operationType) {
    this.operationType = operationType;
    return this;
  }

  public MongoClientChange namespace(JsonObject namespace) {
    this.namespace = namespace;
    return this;
  }

  public List<String> getRemovedFields() {
    return removedFields;
  }

  public JsonObject getUpdatedFields() {
    return updatedFields;
  }

  public JsonObject getFullDocument() {
    return fullDocument;
  }

  /**
   * A token that can be passed to {@link WatchOptions#resumeAfter(JsonObject)} to resume watching after a certain point
   * of time in a closed {@link MongoClientChangeStream}.
   * <p>
   * It is recommended to use {@link MongoClientChangeStream#lastResumeToken()} to retrieve the very last resume token
   * that a watch cursor observed.
   *
   * @return a token.
   */
  public JsonObject getResumeToken() {
    return resumeToken;
  }

  public MongoClientChangeOperationType getOperationType() {
    return operationType;
  }

  /**
   * The namespace is a JSON document of the form:
   * <pre>
   * {
   *    "db": The database against which the watch cursor was opened
   *    "coll": The collection the watch cursor is observing
   * }
   * </pre>
   *
   * @return A JSON document matching the MongoDB namespace field in a ChangeStreamDocument.
   */
  public JsonObject getNamespace() {
    return namespace;
  }
}
