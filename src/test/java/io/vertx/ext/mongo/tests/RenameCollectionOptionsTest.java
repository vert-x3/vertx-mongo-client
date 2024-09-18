package io.vertx.ext.mongo.tests;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.RenameCollectionOptions;
import org.junit.Test;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author <a href="mailto:wangzengyi1935@163.com">Zengyi Wang</a>
 */
public class RenameCollectionOptionsTest {

  private static void assertNotEqual(BiConsumer<RenameCollectionOptions, RenameCollectionOptions> f) {
    RenameCollectionOptions a = new RenameCollectionOptions();
    RenameCollectionOptions b = new RenameCollectionOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  @Test
  public void testEquals() {
    assertNotEqual((a, b) -> {a.setDropTarget(true); b.setDropTarget(false);});
  }

  @Test
  public void testHashCode() {
    RenameCollectionOptions options = new RenameCollectionOptions();
    int hash = options.hashCode();
    assertEquals(hash, new RenameCollectionOptions().hashCode());
    assertNotEquals(hash, new RenameCollectionOptions().setDropTarget(true).hashCode());
  }

  @Test
  public void testFromJson() {
    JsonObject jsonObject = JsonObject.of("dropTarget", true);
    assertEquals(new RenameCollectionOptions().setDropTarget(true), new RenameCollectionOptions(jsonObject));
  }

  @Test
  public void testToJson() {
    JsonObject jsonObject = JsonObject.of("dropTarget", true);
    assertEquals(new RenameCollectionOptions().setDropTarget(true).toJson(), jsonObject);
  }
}
