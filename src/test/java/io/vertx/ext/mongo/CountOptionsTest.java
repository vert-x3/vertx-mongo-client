package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
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
      a.setMaxTime(10, TimeUnit.SECONDS);
      a.setMaxTime(10, TimeUnit.MINUTES);
    });
    assertNotEqual((a, b) -> {
      a.setMaxTime(10, TimeUnit.SECONDS);
      a.setMaxTime(20, TimeUnit.SECONDS);
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
    assertNotEqual(hash, o -> o.setMaxTime(10, TimeUnit.SECONDS));
  }
}
