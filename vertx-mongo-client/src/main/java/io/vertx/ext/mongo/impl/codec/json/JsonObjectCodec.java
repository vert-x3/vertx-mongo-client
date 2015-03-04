package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class JsonObjectCodec extends AbstractJsonCodec<JsonObject, JsonArray> implements CollectibleCodec<JsonObject> {
  public static final String ID_FIELD = "_id";
  public static final String DATE_FIELD = "$date";

  @Override
  public JsonObject generateIdIfAbsentFromDocument(JsonObject json) {
    //TODO: Is this faster/better then Java UUID ?
    if (!documentHasId(json)) {
      ObjectId id = new ObjectId();
      json.put(ID_FIELD, id.toHexString());
    }
    return json;
  }

  @Override
  public boolean documentHasId(JsonObject json) {
    return json.containsKey(ID_FIELD);
  }

  @Override
  public BsonValue getDocumentId(JsonObject json) {
    if (!documentHasId(json)) {
      throw new IllegalStateException("The document does not contain an _id");
    }

    String id = json.getString(ID_FIELD);
    return new BsonString(id);
  }

  @Override
  public Class<JsonObject> getEncoderClass() {
    return JsonObject.class;
  }

  @Override
  protected void beforeFields(JsonObject object, BiConsumer<String, Object> objectConsumer) {
    if (object.containsKey(ID_FIELD)) {
      objectConsumer.accept(ID_FIELD, object.getString(ID_FIELD));
    }
  }

  @Override
  protected JsonObject newObject() {
    return new JsonObject();
  }

  @Override
  protected void add(JsonObject object, String name, Object value) {
    object.put(name, value);
  }

  @Override
  protected boolean isObjectInstance(Object instance) {
    return instance instanceof JsonObject;
  }

  @Override
  protected void forEach(JsonObject object, BiConsumer<String, Object> objectConsumer) {
    object.forEach(entry -> {
      objectConsumer.accept(entry.getKey(), entry.getValue());
    });
  }

  @Override
  protected JsonArray newArray() {
    return new JsonArray();
  }

  @Override
  protected void add(JsonArray array, Object value) {
    array.add(value);
  }

  @Override
  protected boolean isArrayInstance(Object instance) {
    return instance instanceof JsonArray;
  }

  @Override
  protected void forEach(JsonArray array, Consumer<Object> arrayConsumer) {
    array.forEach(arrayConsumer);
  }

  @Override
  protected BsonType getBsonType(Object value) {
    BsonType type = super.getBsonType(value);
    if (type == BsonType.DOCUMENT) {
      JsonObject obj = (JsonObject) value;
      if (obj.containsKey(DATE_FIELD)) {
        return BsonType.DATE_TIME;
      }
      //not supported yet
      /*else if (obj.containsKey("$binary")) {
        return BsonType.BINARY;
      } else if (obj.containsKey("$maxKey")) {
        return BsonType.MAX_KEY;
      } else if (obj.containsKey("$minKey")) {
        return BsonType.MIN_KEY;
      } else if (obj.containsKey("$oid")) {
        return BsonType.OBJECT_ID;
      } else if (obj.containsKey("$regex")) {
        return BsonType.REGULAR_EXPRESSION;
      } else if (obj.containsKey("$symbol")) {
        return BsonType.SYMBOL;
      } else if (obj.containsKey("$timestamp")) {
        return BsonType.TIMESTAMP;
      } else if (obj.containsKey("$undefined")) {
        return BsonType.UNDEFINED;
      } else if (obj.containsKey("$numberLong")) {
        return BsonType.INT64;
      } else if (obj.containsKey("$code")) {
        return JAVASCRIPT or JAVASCRIPT_WITH_SCOPE;
      } */
    }
    return type;
  }

  //---------- Support additional mappings

  @Override
  protected Object readObjectId(BsonReader reader, DecoderContext ctx) {
    return reader.readObjectId().toHexString();
  }

  @Override
  protected Object readDateTime(BsonReader reader, DecoderContext ctx) {
    final JsonObject result = new JsonObject();
    result.put(DATE_FIELD, reader.readDateTime());
    return result;
  }

  @Override
  protected void writeDateTime(BsonWriter writer, String name, Object value, EncoderContext ctx) {
    writer.writeDateTime(((JsonObject) value).getLong(DATE_FIELD));
  }
}
