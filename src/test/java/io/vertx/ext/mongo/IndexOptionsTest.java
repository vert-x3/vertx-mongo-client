package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IndexOptionsTest {

  @Test
  public void testEquals() {
    assertEquals(new IndexOptions(), new IndexOptions());

    assertNotEqual((a, b) -> { a.background(true); b.background(false); });
    assertNotEqual((a, b) -> { a.unique(true); b.unique(false); });
    assertNotEqual((a, b) -> { a.name("a"); b.name("b"); });
    assertNotEqual((a, b) -> { a.sparse(true); b.sparse(false); });
    assertNotEqual((a, b) -> { a.expireAfter(4L, TimeUnit.SECONDS); b.expireAfter(3L, TimeUnit.SECONDS); });
    assertNotEqual((a, b) -> { a.version(1); b.version(2); });
    assertNotEqual((a, b) -> { a.weights(new JsonObject("{ \"f\": 3 }")); b.weights(new JsonObject()); });
    assertNotEqual((a, b) -> { a.defaultLanguage("en"); b.defaultLanguage("de"); });
    assertNotEqual((a, b) -> { a.languageOverride("en"); b.languageOverride("de"); });
    assertNotEqual((a, b) -> { a.textVersion(2); b.textVersion(1); });
    assertNotEqual((a, b) -> { a.sphereVersion(1); b.sphereVersion(3); });
    assertNotEqual((a, b) -> { a.bits(5); b.bits(6); });
    assertNotEqual((a, b) -> { a.min(2.3); b.min(2.4); });
    assertNotEqual((a, b) -> { a.max(3.2); b.max(3.3); });
    assertNotEqual((a, b) -> { a.storageEngine(new JsonObject("{ \"f\": 3 }")); b.storageEngine(new JsonObject()); });
    assertNotEqual((a, b) -> { a.partialFilterExpression(new JsonObject("{ \"f\": 3 }")); b.partialFilterExpression(new JsonObject()); });

    assertNotEquals(new IndexOptions(), null);
  }

  @Test
  public void testHashCode() {
    IndexOptions a = new IndexOptions();
    int hash = a.hashCode();

    assertEquals(hash, new IndexOptions().hashCode());

    assertNotEqual(hash, o -> o.background(true));
    assertNotEqual(hash, o -> o.unique(true));
    assertNotEqual(hash, o -> o.name("foobar"));
    assertNotEqual(hash, o -> o.sparse(true));
    assertNotEqual(hash, o -> o.expireAfter(6L, TimeUnit.SECONDS));
    assertNotEqual(hash, o -> o.version(22));
    assertNotEqual(hash, o -> o.weights(new JsonObject("{ \"f\": 42 }")));
    assertNotEqual(hash, o -> o.defaultLanguage("pl"));
    assertNotEqual(hash, o -> o.languageOverride("ru"));
    assertNotEqual(hash, o -> o.textVersion(39));
    assertNotEqual(hash, o -> o.sphereVersion(31));
    assertNotEqual(hash, o -> o.bits(13));
    assertNotEqual(hash, o -> o.min(2.5));
    assertNotEqual(hash, o -> o.max(6.1));
    assertNotEqual(hash, o -> o.storageEngine(new JsonObject("{ \"f\": 12 }")));
    assertNotEqual(hash, o -> o.partialFilterExpression(new JsonObject("{ \"f\": 4 }")));
  }

  private static void assertNotEqual(BiConsumer<IndexOptions, IndexOptions> f) {
    IndexOptions a = new IndexOptions();
    IndexOptions b = new IndexOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<IndexOptions> f) {
    IndexOptions o = new IndexOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }
}
