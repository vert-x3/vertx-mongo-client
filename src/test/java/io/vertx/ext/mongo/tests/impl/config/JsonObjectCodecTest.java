package io.vertx.ext.mongo.tests.impl.config;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
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

  @Test
  public void writeDocument_supportNumberInt() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    int i = 24;

    JsonObject value = new JsonObject();

    value.put("test", i);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer,"", value, EncoderContext.builder().build());

    BsonValue resutlValue = bsonResult.get("test");
    assertEquals(BsonType.INT32, resutlValue.getBsonType());

    BsonInt32 bsonInt32 = resutlValue.asInt32();
    assertEquals(i, bsonInt32.getValue());
  }

  @Test
  public void readDocument_supportNumberInt() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    int i = 24;

    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonInt32(i));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    int numberIntValue = result.getInteger("test");

    assertEquals(i, numberIntValue);
  }

  @Test
  public void writeDocument_supportNumberLong() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    long l = Long.MAX_VALUE;

    JsonObject value = new JsonObject();

    value.put("test", new JsonObject().put(
      JsonObjectCodec.LONG_FIELD, l
    ));

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer,"", value, EncoderContext.builder().build());

    BsonValue resutlValue = bsonResult.get("test");
    assertEquals(BsonType.INT64, resutlValue.getBsonType());

    BsonInt64 bsonInt64 = resutlValue.asInt64();
    assertEquals(l, bsonInt64.getValue());
  }

  @Test
  public void readDocument_supportNumberLong() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    long l = Long.MAX_VALUE;

    BsonDocument bson = new BsonDocument();
    BsonDocument numberLong = new BsonDocument();
    numberLong.append(JsonObjectCodec.LONG_FIELD, new BsonInt64(l));
    bson.append("test", numberLong);

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    long numberLongValue = result.getJsonObject("test").getLong(JsonObjectCodec.LONG_FIELD);

    assertEquals(l, numberLongValue);
  }

  @Test
  public void writeDocument_supportNumberDouble() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    double d = Double.MAX_VALUE;

    JsonObject value = new JsonObject();

    value.put("test", d);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer,"", value, EncoderContext.builder().build());

    BsonValue resutlValue = bsonResult.get("test");
    assertEquals(BsonType.DOUBLE, resutlValue.getBsonType());

    BsonDouble bsonDouble = resutlValue.asDouble();
    assertEquals(d, bsonDouble.getValue(), 0.0d);
  }

  @Test
  public void readDocument_supportNumberDouble() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    double d = Double.MAX_VALUE;

    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonDouble(d));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    double numberDoubleValue = result.getDouble("test");

    assertEquals(d, numberDoubleValue, 0.0d);
  }

  @Test
  public void writeDocument_supportNumberFloat() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    float f = 1.123f;

    JsonObject value = new JsonObject();

    value.put("test", f);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer,"", value, EncoderContext.builder().build());

    BsonValue resutlValue = bsonResult.get("test");
    assertEquals(BsonType.DOUBLE, resutlValue.getBsonType());

    BsonDouble bsonDouble = resutlValue.asDouble();
    assertEquals(f, bsonDouble.getValue(), 0.0d);
  }

  @Test
  public void readDocument_supportNumberFloat() {

    JsonObjectCodec codec = new JsonObjectCodec(options);

    float f = 1.123f;

    BsonDocument bson = new BsonDocument();
    bson.append("test", new BsonDouble(f));

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    float numberFloatValue = result.getFloat("test");

    assertEquals(f, numberFloatValue, 0.0d);
  }

  @Test
  public void writeDocument_supportNumberDecimal() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    String l = "2222.454543";

    JsonObject value = new JsonObject();

    value.put("test", new JsonObject().put(
      JsonObjectCodec.DECIMAL_FIELD, l
    ));

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer,"", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.DECIMAL128, resultValue.getBsonType());

    BsonDecimal128 decimal128 = resultValue.asDecimal128();
    assertEquals(l, decimal128.getValue().toString());
  }

  @Test
  public void readDocument_supportNumberDecimal() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    String v = "24.56";

    BsonDocument bson = new BsonDocument();
    BsonDocument numberDecimal = new BsonDocument();
    numberDecimal.append(JsonObjectCodec.DECIMAL_FIELD, new BsonDecimal128(new Decimal128(new BigDecimal(v))));
    bson.append("test", numberDecimal);

    BsonDocumentReader reader = new BsonDocumentReader(bson);

    JsonObject result = codec.readDocument(reader, DecoderContext.builder().build());

    String decimalValue = result.getJsonObject("test").getString(JsonObjectCodec.DECIMAL_FIELD);

    assertEquals(v, decimalValue);
  }

  @Test
  public void writeDocument_supportBsonStringFromInstant() {
    JsonObjectCodec codec = new JsonObjectCodec(options);

    Instant now = Instant.now();
    JsonObject value = new JsonObject();
    value.put("test", now);

    BsonDocument bsonResult = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(bsonResult);

    codec.writeDocument(writer, "", value, EncoderContext.builder().build());

    BsonValue resultValue = bsonResult.get("test");
    assertEquals(BsonType.STRING, resultValue.getBsonType());
    assertEquals(now.toString(), resultValue.asString().getValue());
  }
}
