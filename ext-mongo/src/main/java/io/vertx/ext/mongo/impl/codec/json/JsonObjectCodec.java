package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
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

  // Support for the use of storing ObjectId's in mongoDB. This is handy if there's an existing database with ObjectId's.
  private final boolean useObjectId;

  public JsonObjectCodec(boolean useObjectId) {
    this.useObjectId = useObjectId;
  }

  public boolean isSupportingObjectId() {
    return useObjectId;
  }

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

    String id = json.getString(ID_FIELD);
    if (useObjectId) {
      return new BsonObjectId(new ObjectId(id));
    } else {
      return new BsonString(id);
    }
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
  protected void writeString(BsonWriter writer, String name, Object value, EncoderContext ctx) {
    // If useObjectId is true then write an ObjectId if we're writing the _id field
    if (useObjectId && ID_FIELD.equals(name)) {
      writer.writeObjectId(new ObjectId((String) value));
    } else {
      super.writeString(writer, name, value, ctx);
    }
  }

  @Override
  protected Object readDateTime(BsonReader reader, DecoderContext ctx) {
    return reader.readDateTime();
  }
}
