package io.vertx.ext.mongo;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CreateCollectionOptionsTest {


  private static void assertNotEqual(BiConsumer<CreateCollectionOptions, CreateCollectionOptions> f) {
    CreateCollectionOptions a = new CreateCollectionOptions();
    CreateCollectionOptions b = new CreateCollectionOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<CreateCollectionOptions> f) {
    CreateCollectionOptions o = new CreateCollectionOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new CreateCollectionOptions(), new CreateCollectionOptions());

    assertNotEqual((a, b) -> {
      a.setCapped(true);
      b.setCapped(false);
    });
    assertNotEqual((a, b) -> {
      a.setCollation(new CollationOptions().setLocale("de_AT"));
      b.setCollation(new CollationOptions().setLocale("en_US"));
    });
    assertNotEqual((a, b) -> {
      a.setIndexOptionDefaults(new JsonObject().put("some", "option"));
      b.setIndexOptionDefaults(new JsonObject());
    });
    assertNotEqual((a, b) -> {
      a.setValidationOptions(new ValidationOptions().setValidationAction(ValidationAction.WARN));
      b.setValidationOptions(new ValidationOptions().setValidationAction(ValidationAction.ERROR));
    });
    assertNotEqual((a, b) -> {
      a.setMaxDocuments(12345L);
      b.setMaxDocuments(10L);
    });
    assertNotEqual((a, b) -> {
      a.setSizeInBytes(1024L);
      b.setSizeInBytes(2048L);
    });
    assertNotEqual((a, b) -> {
      a.setStorageEngineOptions(new JsonObject().put("some", "option"));
      b.setStorageEngineOptions(new JsonObject());
    });

    assertNotEquals(new CreateCollectionOptions(), null);
  }

  @Test
  public void testHashCode() {
    CreateCollectionOptions a = new CreateCollectionOptions();
    int hash = a.hashCode();

    assertEquals(hash, new CreateCollectionOptions().hashCode());

    assertNotEqual(hash, o -> o.setMaxDocuments(12345L));
    assertNotEqual(hash, o -> o.setSizeInBytes(4096L));
    assertNotEqual(hash, o -> o.setCapped(true));
    assertNotEqual(hash, o -> o.setValidationOptions(new ValidationOptions().setValidationLevel(ValidationLevel.MODERATE)));
    assertNotEqual(hash, o -> o.setIndexOptionDefaults(new JsonObject().put("some", "option")));
    assertNotEqual(hash, o -> o.setCollation(new CollationOptions().setLocale("de_AT").setStrength(5)));
    assertNotEqual(hash, o -> o.setStorageEngineOptions(new JsonObject().put("some", "option")));
  }
}
