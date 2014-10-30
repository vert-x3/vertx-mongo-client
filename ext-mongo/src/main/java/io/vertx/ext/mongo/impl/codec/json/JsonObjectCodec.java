package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.types.ObjectId;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class JsonObjectCodec extends AbstractJsonCodec<JsonObject, JsonArray> implements CollectibleCodec<JsonObject> {
  public static final String ID_FIELD = "_id";

  @Override
  public void generateIdIfAbsentFromDocument(JsonObject json) {
    //TODO: Is this faster/better then Java UUID ?
    if (!documentHasId(json)) {
      ObjectId id = new ObjectId();
      json.put(ID_FIELD, id.toHexString());
    }
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

    return new BsonString(json.getString(ID_FIELD));
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

  //---------- Support additional mappings

  @Override
  protected Object readObjectId(BsonReader reader, DecoderContext ctx) {
    return reader.readObjectId().toHexString();
  }

  @Override
  protected Object readDateTime(BsonReader reader, DecoderContext ctx) {
    return reader.readDateTime();
  }
}
