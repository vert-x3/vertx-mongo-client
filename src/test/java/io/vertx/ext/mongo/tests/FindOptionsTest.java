package io.vertx.ext.mongo.tests;

import com.mongodb.client.model.CollationStrength;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.CollationOptions;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class FindOptionsTest {
  private static JsonObject randomJsonObject() {
    JsonObject json = new JsonObject();
    json.put("string", TestUtils.randomAlphaString(10));
    json.put("int", TestUtils.randomInt());
    json.put("boolean", TestUtils.randomBoolean());

    return json;
  }

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

    CollationOptions collationOptions = new CollationOptions();
    assertEquals(options, options.setCollation(collationOptions));
    assertEquals(collationOptions, options.getCollation());
  }

  @Test
  public void testDefaultOptions() {
    FindOptions options = new FindOptions();
    assertNotNull(options.getFields());
    assertTrue(options.getFields().isEmpty());
    assertNotNull(options.getSort());
    assertTrue(options.getSort().isEmpty());
    assertEquals(FindOptions.DEFAULT_LIMIT, options.getLimit());
    assertEquals(FindOptions.DEFAULT_SKIP, options.getSkip());
    assertNull(options.getCollation());
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

    JsonObject collationOptions = new JsonObject()
      .put("locale", Locale.getDefault().toString())
      .put("caseLevel", true)
      .put("caseFirst", "lower")
      .put("alternate", "non-ignorable") // this
      .put("strength", 1)
      .put("numericOrdering", true)
      .put("maxVariable", "punct")
      .put("backwards", false)
      .put("normalization", true);
    json.put("collation", collationOptions);

    FindOptions options = new FindOptions(json);
    assertEquals(fields, options.getFields());
    assertEquals(sort, options.getSort());
    assertEquals(limit, options.getLimit());
    assertEquals(skip, options.getSkip());
    assertEquals(collationOptions, options.getCollation().toJson());
  }

  @Test
  public void testDefaultOptionsJson() {
    FindOptions options = new FindOptions(new JsonObject());
    FindOptions def = new FindOptions();
    assertEquals(def.getFields(), options.getFields());
    assertEquals(def.getSort(), options.getSort());
    assertEquals(def.getLimit(), options.getLimit());
    assertEquals(def.getSkip(), options.getSkip());
    assertEquals(def.getCollation(), options.getCollation());
  }

  @Test
  public void testCopyOptions() {
    FindOptions options = new FindOptions();
    JsonObject fields = randomJsonObject();
    JsonObject sort = randomJsonObject();
    int limit = TestUtils.randomInt();
    int skip = TestUtils.randomInt();
    CollationOptions collationOptions = new CollationOptions().setStrength(CollationStrength.PRIMARY);
    options.setFields(fields);
    options.setSort(sort);
    options.setLimit(limit);
    options.setSkip(skip);
    options.setCollation(collationOptions);

    FindOptions copy = new FindOptions(options);
    assertEquals(options.getFields(), copy.getFields());
    assertEquals(options.getSort(), copy.getSort());
    assertEquals(options.getLimit(), copy.getLimit());
    assertEquals(options.getSkip(), copy.getSkip());
    assertEquals(options.getCollation(), copy.getCollation());
  }

  @Test
  public void testToJson() {
    FindOptions options = new FindOptions();
    JsonObject fields = randomJsonObject();
    JsonObject sort = randomJsonObject();
    int limit = TestUtils.randomPositiveInt();
    int skip = TestUtils.randomPositiveInt();
    CollationOptions collationOptions = new CollationOptions().setStrength(CollationStrength.PRIMARY);
    options.setFields(fields);
    options.setSort(sort);
    options.setLimit(limit);
    options.setSkip(skip);
    options.setCollation(collationOptions);

    assertEquals(options, new FindOptions(options.toJson()));
  }
}
