package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonObject;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.io.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void writeDocument_supportBsonDateTimeWithMillis() {
    JsonObjectCodec codec = new JsonObjectCodec();

    JsonObject dateValue = new JsonObject();
    dateValue.put(JsonObjectCodec.DATE_FIELD, "2011-12-03T10:15:30.500+01:00");
    JsonObject value = new JsonObject();
    value.put("test", dateValue);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    long millis = 1322903730500l;

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.DATE_TIME, resultValue.getBsonType());
    assertEquals(millis, resultValue.asDateTime().getValue());

    String back =
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(1322903730500l), ZoneOffset.UTC).format(ISO_OFFSET_DATE_TIME);

    // we encode always in UTC
    assertEquals("2011-12-03T09:15:30.5Z", back);
  }

  @Test
  public void writeDocument_supportBsonBinary() {
    JsonObjectCodec codec = new JsonObjectCodec();

    OffsetDateTime now = OffsetDateTime.now();

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(now);
      oos.close();

      JsonObject binaryJson = new JsonObject();
      binaryJson.put(JsonObjectCodec.BINARY_FIELD, baos.toByteArray());
      JsonObject value = new JsonObject();
      value.put("test", binaryJson);

      BsonDocument bsonResult = new BsonDocument();
      BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

      codec.writeDocument(writer, "", value, EncoderContext.builder().build());

      BsonValue resultValue = bsonResult.get("test");
      assertEquals(BsonType.BINARY, resultValue.getBsonType());

      BsonBinary bsonBinary = resultValue.asBinary();

      ByteArrayInputStream bais = new ByteArrayInputStream(bsonBinary.getData());
      ObjectInputStream ois = new ObjectInputStream(bais);
      OffsetDateTime reconstitutedNow = (OffsetDateTime) ois.readObject();

      assertEquals(now, reconstitutedNow);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      assertTrue(false);
    }

  }

  @Test
  public void readDocument_supportBsonBinary() {
    JsonObjectCodec codec = new JsonObjectCodec();

    Instant now = Instant.now();
    BsonDocument bson = new BsonDocument();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(now);
      oos.close();

      bson.append("test", new BsonBinary(baos.toByteArray()));

      BsonDocumentReader reader = new BsonDocumentReader(bson);

      JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

      JsonObject resultValue = result.getJsonObject("test");
      byte[] bytes = resultValue.getBinary(JsonObjectCodec.BINARY_FIELD);

      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Instant reconstitutedNow = (Instant) ois.readObject();

      assertEquals(now, reconstitutedNow);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }
  @Test
  public void readDocument_supportObjectId() {
    JsonObjectCodec codec = new JsonObjectCodec();

    BsonDocument bson = new BsonDocument();

      ObjectId objectId = new ObjectId();
      bson.append("test", new BsonObjectId(objectId));

      BsonDocumentReader reader = new BsonDocumentReader(bson);

      JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

      String sObjectId = result.getJsonObject("test").getString("$oid");

      assertEquals(objectId.toHexString(), sObjectId);
  }

  @Test
  public void writeDocument_supportObjectId() {
    JsonObjectCodec codec = new JsonObjectCodec();

    ObjectId objectId = new ObjectId();
    JsonObject oidJson = new JsonObject();
    oidJson.put(JsonObjectCodec.OID_FIELD, objectId.toHexString());
    JsonObject value = new JsonObject();
    value.put("test", oidJson);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.OBJECT_ID, resultValue.getBsonType());

    BsonObjectId bsonObjectId = resultValue.asObjectId();

    assertEquals(objectId.toHexString(), bsonObjectId.getValue().toHexString());

  }

}
