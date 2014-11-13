package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class FindOptionsTest {
  @Test
  public void testOptions() {
    FindOptions options = new FindOptions();

    JsonObject fields = randomJsonObject();
    assertEquals(options, options.setFields(fields));
    assertEquals(fields, options.getFields());

    JsonObject sort = randomJsonObject();
    assertEquals(options, options.setSort(sort));
    assertEquals(sort, options.getSort());

    int limit = TestUtils.randomInt();
    assertEquals(options, options.setLimit(limit));
    assertEquals(limit, options.getLimit());

    int skip = TestUtils.randomInt();
    assertEquals(options, options.setSkip(skip));
    assertEquals(skip, options.getSkip());
  }

  @Test
  public void testDefaultOptions() {
    FindOptions options = new FindOptions();
    assertNull(options.getFields());
    assertNull(options.getSort());
    assertEquals(-1, options.getLimit());
    assertEquals(0, options.getSkip());
  }

  @Test
  public void testOptionsJson() {
    JsonObject json = new JsonObject();

    JsonObject fields = randomJsonObject();
    json.put("fields", fields);

    JsonObject sort = randomJsonObject();
    json.put("sort", sort);

    int limit = TestUtils.randomInt();
    json.put("limit", limit);

    int skip = TestUtils.randomInt();
    json.put("skip", skip);

    FindOptions options = new FindOptions(json);
    assertEquals(fields, options.getFields());
    assertEquals(sort, options.getSort());
    assertEquals(limit, options.getLimit());
    assertEquals(skip, options.getSkip());
  }

  @Test
  public void testDefaultOptionsJson() {
    FindOptions options = new FindOptions(new JsonObject());
    FindOptions def = new FindOptions();
    assertEquals(def.getFields(), options.getFields());
    assertEquals(def.getSort(), options.getSort());
    assertEquals(def.getLimit(), options.getLimit());
    assertEquals(def.getSkip(), options.getSkip());
  }

  @Test
  public void testCopyOptions() {
    FindOptions options = new FindOptions();
    JsonObject fields = randomJsonObject();
    JsonObject sort = randomJsonObject();
    int limit = TestUtils.randomInt();
    int skip = TestUtils.randomInt();
    options.setFields(fields);
    options.setSort(sort);
    options.setLimit(limit);
    options.setSkip(skip);

    FindOptions copy = new FindOptions(options);
    assertEquals(options.getFields(), copy.getFields());
    assertEquals(options.getSort(), copy.getSort());
    assertEquals(options.getLimit(), copy.getLimit());
    assertEquals(options.getSkip(), copy.getSkip());
  }

  private static JsonObject randomJsonObject() {
    JsonObject json = new JsonObject();
    json.put("string", TestUtils.randomAlphaString(10));
    json.put("int", TestUtils.randomInt());
    json.put("boolean", TestUtils.randomBoolean());

    return json;
  }

  @Test
  public void testToJson() {
    FindOptions options = new FindOptions();
    JsonObject fields = randomJsonObject();
    JsonObject sort = randomJsonObject();
    int limit = TestUtils.randomPositiveInt();
    int skip = TestUtils.randomPositiveInt();
    options.setFields(fields);
    options.setSort(sort);
    options.setLimit(limit);
    options.setSkip(skip);

    assertEquals(options, new FindOptions(options.toJson()));
  }
}
