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

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class JsonObjectCodecTest {

  @Test
  public void getBsonType_returnsDateTimeType_WhenValueIsJsonObjectAndContainsDateField() {
    JsonObjectCodec codec = new JsonObjectCodec();

    JsonObject value = new JsonObject();
    value.put(JsonObjectCodec.DATE_FIELD, "2015-05-30T22:50:02+02:00");

    assertEquals(BsonType.DATE_TIME, codec.getBsonType(value));
  }

  @Test
  public void writeDocument_supportBsonDateTime() {
    JsonObjectCodec codec = new JsonObjectCodec();

    OffsetDateTime now = OffsetDateTime.now();
    JsonObject dateValue = new JsonObject();
    dateValue.put(JsonObjectCodec.DATE_FIELD, now.format(ISO_OFFSET_DATE_TIME));
    JsonObject value = new JsonObject();
    value.put("test", dateValue);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.DATE_TIME, resultValue.getBsonType());
    assertEquals(now.toInstant().toEpochMilli(), resultValue.asDateTime().getValue());
  }

  @Test
  public void readDocument_supportBsonDateTime() {
    JsonObjectCodec codec = new JsonObjectCodec();

    Instant now = Instant.now();
    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonDateTime(now.toEpochMilli()));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    JsonObject resultValue = result.getJsonObject("test");
    assertEquals(now, OffsetDateTime.parse(resultValue.getString(JsonObjectCodec.DATE_FIELD)).toInstant());
  }
}
