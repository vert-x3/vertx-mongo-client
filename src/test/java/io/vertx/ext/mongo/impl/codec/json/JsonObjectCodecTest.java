package io.vertx.ext.mongo.impl.codec.json;

import io.vertx.core.json.JsonObject;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class JsonObjectCodecTest {

  private JsonObject options = new JsonObject();

  @Test
  public void getBsonType_returnsDateTimeType_WhenValueIsJsonObjectAndContainsDateField() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    JsonObject value = new JsonObject();
    value.put(JsonObjectCodec.DATE_FIELD, "2015-05-30T22:50:02+02:00");

    assertEquals(BsonType.DATE_TIME, codec.getBsonType(value));
  }

  @Test
  public void writeDocument_supportBsonDateTime() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

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
    JsonObjectCodec codec = new JsonObjectCodec(options);

    Instant now = Instant.now();
    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonDateTime(now.toEpochMilli()));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    JsonObject resultValue = result.getJsonObject("test");
    assertEquals(Instant.ofEpochMilli(now.toEpochMilli()), OffsetDateTime.parse(resultValue.getString(JsonObjectCodec.DATE_FIELD)).toInstant());
  }

  @Test
  public void writeDocument_supportBsonDateTimeWithMillis() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    JsonObject dateValue = new JsonObject();
    dateValue.put(JsonObjectCodec.DATE_FIELD, "2011-12-03T10:15:30.500+01:00");
    JsonObject value = new JsonObject();
    value.put("test", dateValue);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    long millis = 1322903730500L;

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.DATE_TIME, resultValue.getBsonType());
    assertEquals(millis, resultValue.asDateTime().getValue());

    String back =
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(1322903730500L), ZoneOffset.UTC).format(ISO_OFFSET_DATE_TIME);

    // we encode always in UTC
    assertEquals("2011-12-03T09:15:30.5Z", back);
  }

  @Test
  public void writeDocument_supportBsonBinary() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

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
  public void writeDocument_supportBsonBinaryUUID() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    UUID uuid = UUID.randomUUID();

    byte[] byteUuid = ByteBuffer.allocate(16)
      .putLong(uuid.getMostSignificantBits())
      .putLong(uuid.getLeastSignificantBits())
      .array();

    JsonObject value = new JsonObject();

    value.put("test", new JsonObject()
      .put(JsonObjectCodec.BINARY_FIELD, byteUuid)
      .put(JsonObjectCodec.TYPE_FIELD, BsonBinarySubType.UUID_STANDARD.getValue())
    );

    BsonDocument bsonDocument = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonDocument);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonDocument.get("test");
    assertEquals(BsonType.BINARY, resultValue.getBsonType());

    BsonBinary bsonBinary = resultValue.asBinary();
    ByteBuffer byteBuffer = ByteBuffer.wrap(bsonBinary.getData());

    assertEquals(BsonBinarySubType.UUID_STANDARD.getValue(), bsonBinary.getType());
    assertEquals(uuid, new UUID(byteBuffer.getLong(), byteBuffer.getLong()));
  }

  @Test
  public void readDocument_supportBsonBinaryUUID() {
    JsonObjectCodec codec = new JsonObjectCodec(options);
    BsonDocument bsonDocument = new BsonDocument();

    UUID uuid = UUID.randomUUID();

    byte[] byteUuid = ByteBuffer.allocate(16)
      .putLong(uuid.getMostSignificantBits())
      .putLong(uuid.getLeastSignificantBits())
      .array();

    bsonDocument.put("test", new BsonBinary(BsonBinarySubType.UUID_STANDARD, byteUuid));
    BsonDocumentReader reader = new BsonDocumentReader(bsonDocument);
    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());
    JsonObject resultValue = result.getJsonObject("test");

    assertTrue(resultValue.containsKey(JsonObjectCodec.BINARY_FIELD));
    assertTrue(resultValue.containsKey(JsonObjectCodec.TYPE_FIELD));

    ByteBuffer byteBuffer = ByteBuffer.wrap(resultValue.getBinary(JsonObjectCodec.BINARY_FIELD));

    assertEquals(Integer.valueOf(BsonBinarySubType.UUID_STANDARD.getValue()), resultValue.getInteger(JsonObjectCodec.TYPE_FIELD));
    assertEquals(uuid, new UUID(byteBuffer.getLong(), byteBuffer.getLong()));
  }

  @Test
  public void readDocument_supportBsonBinary() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

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
    JsonObjectCodec codec = new JsonObjectCodec(options);

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
    JsonObjectCodec codec = new JsonObjectCodec(options);

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

  @Test
  public void readDocument_supportBsonTimeStamp(){
    JsonObjectCodec codec = new JsonObjectCodec(options);

    int time = (int)(System.currentTimeMillis() / 1000L);
    int increment = 5;

    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonTimestamp(time, increment));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    JsonObject timeStampValue = result.getJsonObject("test").getJsonObject(JsonObjectCodec.TIMESTAMP_FIELD);

    assertEquals(time, timeStampValue.getInteger(JsonObjectCodec.TIMESTAMP_TIME_FIELD).intValue());
    assertEquals(increment, timeStampValue.getInteger(JsonObjectCodec.TIMESTAMP_INCREMENT_FIELD).intValue());
  }

  @Test
  public void writeDocument_supportBsonTimeStamp(){
    JsonObjectCodec codec = new JsonObjectCodec(options);

    int time = (int)(System.currentTimeMillis() / 1000L);
    int increment = 5;

    JsonObject timeStampComponent = new JsonObject();
    timeStampComponent.put(JsonObjectCodec.TIMESTAMP_TIME_FIELD, time);
    timeStampComponent.put(JsonObjectCodec.TIMESTAMP_INCREMENT_FIELD, increment);

    JsonObject timeStamp = new JsonObject();
    timeStamp.put(JsonObjectCodec.TIMESTAMP_FIELD, timeStampComponent);

    JsonObject value = new JsonObject();
    value.put("test", timeStamp);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");

    assertEquals(BsonType.TIMESTAMP, resultValue.getBsonType());
    assertEquals(time, resultValue.asTimestamp().getTime());
    assertEquals(increment, resultValue.asTimestamp().getInc());
  }



  @Test
  public void hexStringAsKeyDefault() {

    JsonObject document = new JsonObject();

    JsonObjectCodec codec = new JsonObjectCodec(options);
    document = codec.generateIdIfAbsentFromDocument(document);

    assertTrue(document.containsKey("_id"));
    assertTrue(document.getValue("_id") instanceof String);

  }

  @Test
  public void objectIdAsKeySpecified() {

    JsonObject customOptions = new JsonObject().put("useObjectId", false);
    JsonObject document = new JsonObject();

    JsonObjectCodec codec = new JsonObjectCodec(customOptions);
    document = codec.generateIdIfAbsentFromDocument(document);

    assertTrue(document.containsKey("_id"));
    assertTrue(document.getValue("_id") instanceof String);
  }

  @Test
  public void objectIdAsKey() {

    JsonObject customOptions = new JsonObject().put("useObjectId", true);
    JsonObject document = new JsonObject();

    JsonObjectCodec codec = new JsonObjectCodec(customOptions);
    document = codec.generateIdIfAbsentFromDocument(document);

    assertTrue(document.containsKey("_id"));
    assertTrue(document.getValue("_id") instanceof JsonObject);
    assertTrue(document.getJsonObject("_id").containsKey(JsonObjectCodec.OID_FIELD));
  }

}
