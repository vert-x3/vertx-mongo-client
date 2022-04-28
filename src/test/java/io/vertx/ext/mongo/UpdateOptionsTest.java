package io.vertx.ext.mongo;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static io.vertx.ext.mongo.WriteOption.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class UpdateOptionsTest {
  @Test
  public void testOptions() {
    UpdateOptions options = new UpdateOptions();

    WriteOption writeOption = ACKNOWLEDGED;
    assertEquals(options, options.setWriteOption(writeOption));
    assertEquals(writeOption, options.getWriteOption());

    boolean multi = TestUtils.randomBoolean();
    assertEquals(options, options.setMulti(multi));
    assertEquals(multi, options.isMulti());

    boolean upsert = TestUtils.randomBoolean();
    assertEquals(options, options.setUpsert(upsert));
    assertEquals(upsert, options.isUpsert());

    JsonArray arrayFilters = new JsonArray().add(new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomAlphaString(5)));
    assertEquals(options, options.setArrayFilters(arrayFilters));
    assertEquals(arrayFilters, options.getArrayFilters());

    JsonObject hint = new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomInt());
    assertEquals(options, options.setHint(hint));
    assertEquals(hint, options.getHint());

    String hintString = TestUtils.randomAlphaString(12);
    assertEquals(options, options.setHintString(hintString));
    assertEquals(hintString, options.getHintString());
  }

  @Test
  public void testDefaultOptions() {
    UpdateOptions options = new UpdateOptions();
    assertNull(options.getWriteOption());
    assertFalse(options.isMulti());
    assertFalse(options.isUpsert());
    assertNull(options.getArrayFilters());
    assertNull(options.getHint());
    assertNull(options.getHintString());
  }

  @Test
  public void testOptionsJson() {
    JsonObject json = new JsonObject();

    WriteOption writeOption = JOURNALED;
    json.put("writeOption", writeOption.name());

    boolean multi = TestUtils.randomBoolean();
    json.put("multi", multi);

    boolean upsert = TestUtils.randomBoolean();
    json.put("upsert", upsert);

    JsonArray arrayFilters = new JsonArray().add(new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomAlphaString(5)));
    json.put("arrayFilters", arrayFilters);

    JsonObject hint = new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomInt());
    json.put("hint", hint);

    String hintString = TestUtils.randomAlphaString(12);
    json.put("hintString", hintString);

    UpdateOptions options = new UpdateOptions(json);
    assertEquals(writeOption, options.getWriteOption());
    assertEquals(multi, options.isMulti());
    assertEquals(upsert, options.isUpsert());
    assertEquals(arrayFilters, options.getArrayFilters());
    assertEquals(hint, options.getHint());
    assertEquals(hintString, options.getHintString());
  }

  @Test
  public void testDefaultOptionsJson() {
    UpdateOptions options = new UpdateOptions(new JsonObject());
    UpdateOptions def = new UpdateOptions();
    assertEquals(def.getWriteOption(), options.getWriteOption());
    assertEquals(def.isMulti(), options.isMulti());
    assertEquals(def.isUpsert(), options.isUpsert());
    assertEquals(def.getArrayFilters(), options.getArrayFilters());
    assertEquals(def.getHint(), options.getHint());
    assertEquals(def.getHintString(), options.getHintString());
  }

  @Test
  public void testCopyOptions() {
    UpdateOptions options = new UpdateOptions();
    WriteOption writeOption = REPLICA_ACKNOWLEDGED;
    boolean multi = TestUtils.randomBoolean();
    boolean upsert = TestUtils.randomBoolean();
    JsonArray arrayFilters = new JsonArray().add(new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomAlphaString(5)));
    JsonObject hint = new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomInt());
    String hintString = TestUtils.randomAlphaString(12);

    options.setWriteOption(writeOption);
    options.setMulti(multi);
    options.setUpsert(upsert);
    options.setArrayFilters(arrayFilters);
    options.setHint(hint);
    options.setHintString(hintString);

    UpdateOptions copy = new UpdateOptions(options);
    assertEquals(options.getWriteOption(), copy.getWriteOption());
    assertEquals(options.isMulti(), copy.isMulti());
    assertEquals(options.isUpsert(), copy.isUpsert());
    assertEquals(options.getArrayFilters(), copy.getArrayFilters());
    assertEquals(options.getHint(), copy.getHint());
    assertEquals(options.getHintString(), copy.getHintString());
  }

  @Test
  public void testToJson() {
    UpdateOptions options = new UpdateOptions();
    WriteOption writeOption = MAJORITY;
    boolean multi = TestUtils.randomBoolean();
    boolean upsert = TestUtils.randomBoolean();
    JsonArray arrayFilters = new JsonArray().add(new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomAlphaString(5)));
    JsonObject hint = new JsonObject().put(TestUtils.randomAlphaString(5), TestUtils.randomInt());
    String hintString = TestUtils.randomAlphaString(12);

    options.setWriteOption(writeOption);
    options.setMulti(multi);
    options.setUpsert(upsert);
    options.setArrayFilters(arrayFilters);
    options.setHint(hint);
    options.setHintString(hintString);

    assertEquals(options, new UpdateOptions(options.toJson()));
  }
}
