package io.vertx.ext.mongo;

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
  }

  @Test
  public void testDefaultOptions() {
    UpdateOptions options = new UpdateOptions();
    assertNull(options.getWriteOption());
    assertFalse(options.isMulti());
    assertFalse(options.isUpsert());
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

    UpdateOptions options = new UpdateOptions(json);
    assertEquals(writeOption, options.getWriteOption());
    assertEquals(multi, options.isMulti());
    assertEquals(upsert, options.isUpsert());
  }

  @Test
  public void testDefaultOptionsJson() {
    UpdateOptions options = new UpdateOptions(new JsonObject());
    UpdateOptions def = new UpdateOptions();
    assertEquals(def.getWriteOption(), options.getWriteOption());
    assertEquals(def.isMulti(), options.isMulti());
    assertEquals(def.isUpsert(), options.isUpsert());
  }

  @Test
  public void testCopyOptions() {
    UpdateOptions options = new UpdateOptions();
    WriteOption writeOption = REPLICA_ACKNOWLEDGED;
    boolean multi = TestUtils.randomBoolean();
    boolean upsert = TestUtils.randomBoolean();

    options.setWriteOption(writeOption);
    options.setMulti(multi);
    options.setUpsert(upsert);

    UpdateOptions copy = new UpdateOptions(options);
    assertEquals(options.getWriteOption(), copy.getWriteOption());
    assertEquals(options.isMulti(), copy.isMulti());
    assertEquals(options.isUpsert(), copy.isUpsert());
  }

  @Test
  public void testToJson() {
    UpdateOptions options = new UpdateOptions();
    WriteOption writeOption = MAJORITY;
    boolean multi = TestUtils.randomBoolean();
    boolean upsert = TestUtils.randomBoolean();

    options.setWriteOption(writeOption);
    options.setMulti(multi);
    options.setUpsert(upsert);

    assertEquals(options, new UpdateOptions(options.toJson()));
  }
}
