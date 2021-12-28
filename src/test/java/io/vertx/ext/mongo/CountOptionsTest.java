package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CountOptionsTest {

  private static void assertNotEqual(BiConsumer<CountOptions, CountOptions> f) {
    CountOptions a = new CountOptions();
    CountOptions b = new CountOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<CountOptions> f) {
    CountOptions o = new CountOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new CountOptions(), new CountOptions());

    assertNotEqual((a, b) -> {
      a.setCollation(new CollationOptions().setLocale("de_AT"));
      b.setCollation(new CollationOptions().setLocale("de_DE"));
    });
    assertNotEqual((a, b) -> {
      a.setHint(new JsonObject("{ \"$natural\" : 1 }"));
      b.setHint(new JsonObject("{ \"$natural\" : -1 }"));
    });
    assertNotEqual((a, b) -> {
      a.setHintString("x");
      b.setHintString("y");
    });
    assertNotEqual((a, b) -> {
      a.setLimit(10);
      b.setLimit(20);
    });
    assertNotEqual((a, b) -> {
      a.setSkip(1);
      b.setSkip(2);
    });
    assertNotEqual((a, b) -> {
      a.setMaxTime(100L);
      a.setMaxTime(200L);
    });

    assertNotEquals(new CountOptions(), null);
  }

  @Test
  public void testHashCode() {
    CountOptions a = new CountOptions();
    int hash = a.hashCode();

    assertEquals(hash, new CountOptions().hashCode());

    assertNotEqual(hash, o -> o.setCollation(new CollationOptions()));
    assertNotEqual(hash, o -> o.setHint(new JsonObject("{ \"$natural\" : 1 }")));
    assertNotEqual(hash, o -> o.setHintString("x"));
    assertNotEqual(hash, o -> o.setLimit(10));
    assertNotEqual(hash, o -> o.setSkip(2));
    assertNotEqual(hash, o -> o.setMaxTime(10L));
  }

  @Test
  public void testCountOptionsFromJson() {
    JsonObject json = new JsonObject()
      .put("hint", new JsonObject().put("some", "hint"))
      .put("hintString", "someHintString")
      .put("limit", 10)
      .put("skip", 20)
      .put("maxTime", 30L)
      .put("collation", new JsonObject());

    CountOptions options = new CountOptions(json);
    assertEquals(new JsonObject().put("some", "hint"), options.getHint());
    assertEquals("someHintString", options.getHintString());
    assertEquals((Integer) 10, options.getLimit());
    assertEquals((Integer) 20, options.getSkip());
    assertEquals((Long) 30L, options.getMaxTime());
    assertEquals(new CollationOptions(), options.getCollation());
  }

  @Test
  public void testCountOptionsToJson() {
    JsonObject json = new JsonObject()
      .put("hint", new JsonObject().put("some", "hint"))
      .put("hintString", "someHintString")
      .put("limit", 10)
      .put("skip", 20)
      .put("maxTime", 30L)
      .put("collation", new JsonObject());

    CountOptions options = new CountOptions()
      .setCollation(new CollationOptions())
      .setHint(new JsonObject().put("some", "hint"))
      .setHintString("someHintString")
      .setLimit(10)
      .setSkip(20)
      .setMaxTime(30L);

    assertEquals(json, options.toJson());
  }
}
