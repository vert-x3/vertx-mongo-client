package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonObject;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class JsonObjectCodecTest {

  @Test
  public void getBsonType_returnsDateTimeType_WhenValueIsJsonObjectAndContainsDateField() {
    JsonObjectCodec codec = new JsonObjectCodec();

    JsonObject value = new JsonObject();
    value.put(JsonObjectCodec.DATE_FIELD, System.currentTimeMillis());

    assertEquals(BsonType.DATE_TIME, codec.getBsonType(value));
  }

  @Test
  public void writeDocument_supportBsonDateTime() {
    JsonObjectCodec codec = new JsonObjectCodec();

    Date date = new Date();
    JsonObject dateValue = new JsonObject();
    dateValue.put(JsonObjectCodec.DATE_FIELD, date.getTime());
    JsonObject value = new JsonObject();
    value.put("test", dateValue);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.DATE_TIME, resultValue.getBsonType());
    assertEquals(date.getTime(), resultValue.asDateTime().getValue());
  }

  @Test
  public void readDocument_supportBsonDateTime() {
    JsonObjectCodec codec = new JsonObjectCodec();

    Date date = new Date();
    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonDateTime(date.getTime()));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    JsonObject resultValue = result.getJsonObject("test");
    assertEquals(date.getTime(), (long) resultValue.getLong(JsonObjectCodec.DATE_FIELD));
  }
}
