package io.vertx.ext.mongo;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ValidationOptionsTest {


  private static void assertNotEqual(BiConsumer<ValidationOptions, ValidationOptions> f) {
    ValidationOptions a = new ValidationOptions();
    ValidationOptions b = new ValidationOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<ValidationOptions> f) {
    ValidationOptions o = new ValidationOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new ValidationOptions(), new ValidationOptions());

    assertNotEqual((a, b) -> {
      a.setValidator(new JsonObject());
      b.setValidator(new JsonObject().put("some", "validatorValue"));
    });
    assertNotEqual((a, b) -> {
      a.setValidationAction(ValidationAction.ERROR);
      b.setValidationAction(ValidationAction.WARN);
    });
    assertNotEqual((a, b) -> {
      a.setValidationLevel(ValidationLevel.STRICT);
      b.setValidationLevel(ValidationLevel.OFF);
    });
    assertNotEquals(new ValidationOptions(), null);
  }

  @Test
  public void testHashCode() {
    ValidationOptions a = new ValidationOptions();
    int hash = a.hashCode();

    assertEquals(hash, new ValidationOptions().hashCode());

    assertNotEqual(hash, o -> o.setValidationLevel(ValidationLevel.MODERATE));
    assertNotEqual(hash, o -> o.setValidationAction(ValidationAction.WARN));
    assertNotEqual(hash, o -> o.setValidator(new JsonObject().put("some", "validatorValue")));
  }

  @Test
  public void testValidationOptionsFromJson() {
    JsonObject json = new JsonObject()
      .put("validationLevel", "MODERATE")
      .put("validationAction", "WARN")
      .put("validator", new JsonObject().put("some", "validatorValue"));

    ValidationOptions options = new ValidationOptions(json);
    assertEquals(ValidationLevel.MODERATE, options.getValidationLevel());
    assertEquals(ValidationAction.WARN, options.getValidationAction());
    assertEquals(new JsonObject().put("some", "validatorValue"), options.getValidator());
  }

  @Test
  public void testValidationOptionsToJson() {
    JsonObject json = new JsonObject()
      .put("validationLevel", "MODERATE")
      .put("validationAction", "WARN")
      .put("validator", new JsonObject().put("some", "validatorValue"));


    ValidationOptions options = new ValidationOptions()
      .setValidationAction(ValidationAction.WARN)
      .setValidationLevel(ValidationLevel.MODERATE)
      .setValidator(new JsonObject().put("some", "validatorValue"));

    assertEquals(json, options.toJson());
  }
}
