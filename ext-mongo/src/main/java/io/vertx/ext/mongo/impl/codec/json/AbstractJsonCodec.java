package io.vertx.ext.mongo.impl.codec.json;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@SuppressWarnings("unused")
public abstract class AbstractJsonCodec<O, A> implements Codec<O> {

  @Override
  public O decode(BsonReader reader, DecoderContext decoderContext) {
    return readDocument(reader, decoderContext);
  }

  @Override
  public void encode(BsonWriter writer, O value, EncoderContext encoderContext) {
    writeDocument(writer, value, encoderContext);
  }

  protected Object readValue(BsonReader reader, DecoderContext ctx) {
    BsonType type = reader.getCurrentBsonType();
    switch (type) {
      case NULL:
        return readNull(reader, ctx);
      case ARRAY:
        return readArray(reader, ctx);
      case BINARY:
        return readBinary(reader, ctx);
      case BOOLEAN:
        return readBoolean(reader, ctx);
      case DATE_TIME:
        return readDateTime(reader, ctx);
      case DB_POINTER:
        return readDbPointer(reader, ctx);
      case DOCUMENT:
        return readDocument(reader, ctx);
      case DOUBLE:
        return readDouble(reader, ctx);
      case INT32:
        return readInt32(reader, ctx);
      case INT64:
        return readInt64(reader, ctx);
      case MAX_KEY:
        return readMaxKey(reader, ctx);
      case MIN_KEY:
        return readMinKey(reader, ctx);
      case JAVASCRIPT:
        return readJavaScript(reader, ctx);
      case JAVASCRIPT_WITH_SCOPE:
        return readJavaScriptWithScope(reader, ctx);
      case OBJECT_ID:
        return readObjectId(reader, ctx);
      case REGULAR_EXPRESSION:
        return readRegularExpression(reader, ctx);
      case STRING:
        return readString(reader, ctx);
      case SYMBOL:
        return readSymbol(reader, ctx);
      case TIMESTAMP:
        return readTimeStamp(reader, ctx);
      case UNDEFINED:
        return readUndefined(reader, ctx);
      default:
        throw new IllegalStateException("Unknown bson type " + type);
    }
  }

  @SuppressWarnings("unchecked")
  protected void writeValue(BsonWriter writer, Object value, EncoderContext ctx) {
    BsonType type = getBsonType(value);
    switch (type) {
      case NULL:
        writeNull(writer, value, ctx);
        break;
      case ARRAY:
        writeArray(writer, value, ctx);
        break;
      case BINARY:
        writeBinary(writer, value, ctx);
        break;
      case BOOLEAN:
        writeBoolean(writer, value, ctx);
        break;
      case DATE_TIME:
        writeDateTime(writer, value, ctx);
        break;
      case DB_POINTER:
        writeDbPointer(writer, value, ctx);
        break;
      case DOCUMENT:
        writeDocument(writer, value, ctx);
        break;
      case DOUBLE:
        writeDouble(writer, value, ctx);
        break;
      case INT32:
        writeInt32(writer, value, ctx);
        break;
      case INT64:
        writeInt64(writer, value, ctx);
        break;
      case MAX_KEY:
        writeMaxKey(writer, value, ctx);
        break;
      case MIN_KEY:
        writeMinKey(writer, value, ctx);
        break;
      case JAVASCRIPT:
        writeJavaScript(writer, value, ctx);
        break;
      case JAVASCRIPT_WITH_SCOPE:
        writeJavaScriptWithScope(writer, value, ctx);
        break;
      case OBJECT_ID:
        writeObjectId(writer, value, ctx);
        break;
      case REGULAR_EXPRESSION:
        writeRegularExpression(writer, value, ctx);
        break;
      case STRING:
        writeString(writer, value, ctx);
        break;
      case SYMBOL:
        writeSymbol(writer, value, ctx);
        break;
      case TIMESTAMP:
        writeTimeStamp(writer, value, ctx);
        break;
      case UNDEFINED:
        writeUndefined(writer, value, ctx);
        break;
      default:
        throw new IllegalStateException("Unknown bson type " + type);
    }
  }

  protected BsonType getBsonType(Object value) {
    if (value instanceof Boolean) {
      return BsonType.BOOLEAN;
    } else if (value instanceof Double) {
      return BsonType.DOUBLE;
    } else if (value instanceof Integer) {
      return BsonType.INT32;
    } else if (value instanceof Long) {
      return BsonType.INT64;
    } else if (value instanceof String) {
      return BsonType.STRING;
    } else if (isObjectInstance(value)) {
      return BsonType.DOCUMENT;
    } else if (isArrayInstance(value)) {
      return BsonType.ARRAY;
    } else {
      return null;
    }
  }

  //------------------- Basic types

  protected Object readNull(BsonReader reader, DecoderContext ctx) {
    reader.readNull();
    return null;
  }

  protected void writeNull(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeNull();
  }

  protected Object readBoolean(BsonReader reader, DecoderContext ctx) {
    return reader.readBoolean();
  }

  protected void writeBoolean(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeBoolean((Boolean) value);
  }

  protected Object readDouble(BsonReader reader, DecoderContext ctx) {
    return reader.readDouble();
  }

  protected void writeDouble(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeDouble((double) value);
  }

  protected Object readInt32(BsonReader reader, DecoderContext ctx) {
    return reader.readInt32();
  }

  protected void writeInt32(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeInt32((int) value);
  }

  protected Object readInt64(BsonReader reader, DecoderContext ctx) {
    return reader.readInt64();
  }

  protected void writeInt64(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeInt64((long) value);
  }

  protected Object readString(BsonReader reader, DecoderContext ctx) {
    return reader.readString();
  }

  protected void writeString(BsonWriter writer, Object value, EncoderContext ctx) {
    writer.writeString((String) value);
  }

  //-------------- JSON Object

  protected O readDocument(BsonReader reader, DecoderContext ctx) {
    O object = newObject();

    reader.readStartDocument();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String name = reader.readName();
      add(object, name, readValue(reader, ctx));
    }
    reader.readEndDocument();

    return object;
  }

  protected void writeDocument(BsonWriter writer, Object value, EncoderContext ctx) {
    @SuppressWarnings("unchecked")
    O object = (O) value;

    writer.writeStartDocument();

    Set<String> skip = new HashSet<>();
    if (ctx.isEncodingCollectibleDocument()) {
      beforeFields(object, (k, v) -> {
        skip.add(k);
        writer.writeName(k);
        writeValue(writer, v, ctx);
      });
    }

    forEach(object, (k, v) -> {
      if (!skip.contains(k)) {
        writer.writeName(k);
        writeValue(writer, v, ctx);
      }
    });

    writer.writeEndDocument();
  }

  protected abstract O newObject();

  protected abstract void add(O object, String name, Object value);

  protected abstract boolean isObjectInstance(Object instance);

  protected void beforeFields(O object, BiConsumer<String, Object> objectConsumer) {
  }

  protected abstract void forEach(O object, BiConsumer<String, Object> objectConsumer);

  //-------------- JSON Array

  protected A readArray(BsonReader reader, DecoderContext ctx) {
    A array = newArray();

    reader.readStartArray();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      add(array, readValue(reader, ctx));
    }
    reader.readEndArray();

    return array;
  }

  protected void writeArray(BsonWriter writer, Object value, EncoderContext ctx) {
    @SuppressWarnings("unchecked")
    A array = (A) value;

    writer.writeStartArray();
    forEach(array, o -> writeValue(writer, o, ctx));
    writer.writeEndArray();
  }

  protected abstract A newArray();

  protected abstract void add(A array, Object value);

  protected abstract boolean isArrayInstance(Object instance);

  protected abstract void forEach(A array, Consumer<Object> arrayConsumer);

  //-------------- Extended Mongo JSON types

  protected Object readBinary(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeBinary(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readDateTime(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeDateTime(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readDbPointer(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeDbPointer(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readMaxKey(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeMaxKey(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readMinKey(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeMinKey(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readJavaScript(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeJavaScript(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readJavaScriptWithScope(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeJavaScriptWithScope(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readObjectId(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeObjectId(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readRegularExpression(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeRegularExpression(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readSymbol(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeSymbol(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readTimeStamp(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeTimeStamp(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected Object readUndefined(BsonReader reader, DecoderContext ctx) {
    throw new UnsupportedOperationException();
  }

  protected void writeUndefined(BsonWriter writer, Object value, EncoderContext ctx) {
    throw new UnsupportedOperationException();
  }
}
