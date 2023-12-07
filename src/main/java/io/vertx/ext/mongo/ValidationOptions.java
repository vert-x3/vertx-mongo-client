package io.vertx.ext.mongo;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.lang.Nullable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
@JsonGen(publicConverter = false)
public final class ValidationOptions {
  private JsonObject validator = new JsonObject();
  private ValidationLevel validationLevel;
  private ValidationAction validationAction;

  public ValidationOptions() {
    validator = new JsonObject();
    validationLevel = ValidationLevel.STRICT;
    validationAction = ValidationAction.ERROR;
  }

  public ValidationOptions(ValidationOptions validationOptions) {
    validator = validationOptions.getValidator();
    validationLevel = validationOptions.getValidationLevel();
    validationAction = validationOptions.getValidationAction();
  }

  public ValidationOptions(JsonObject json) {
    ValidationOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ValidationOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * Returns the mongo-java-driver specific object.
   * @return com.mongodb.client.model.ValidationOptions
   */
  public com.mongodb.client.model.ValidationOptions toMongoDriverObject() {
    com.mongodb.client.model.ValidationOptions options = new com.mongodb.client.model.ValidationOptions()
      .validationAction(validationAction)
      .validationLevel(validationLevel);
    if (validator != null && !validator.isEmpty()) {
      options.validator(org.bson.BsonDocument.parse(validator.encode()));
    }
    return options;
  }

  @Nullable
  public JsonObject getValidator() {
    return this.validator;
  }

  /**
   * Optional. Allows users to specify validation rules or expressions for the collection.
   * For more information, see <a href="https://docs.mongodb.com/v4.4/core/schema-validation/">Schema Validation</a>.
   * @param validator
   * @return ValidationOptions
   */
  public ValidationOptions setValidator(@Nullable JsonObject validator) {
    this.validator = validator;
    return this;
  }

  @Nullable
  public ValidationLevel getValidationLevel() {
    return this.validationLevel;
  }

  /**
   * Optional. Determines how strictly MongoDB applies the validation rules to existing documents during an update.
   * @param validationLevel
   * @return ValidationOptions
   */
  public ValidationOptions setValidationLevel(@Nullable ValidationLevel validationLevel) {
    this.validationLevel = validationLevel;
    return this;
  }

  @Nullable
  public ValidationAction getValidationAction() {
    return this.validationAction;
  }

  /**
   * Optional. Determines whether to error on invalid documents or
   *           just warn about the violations but allow invalid documents to be inserted.
   * @param validationAction
   * @return ValidationOptions
   */
  public ValidationOptions setValidationAction(@Nullable ValidationAction validationAction) {
    this.validationAction = validationAction;
    return this;
  }

  @Override
  public String toString() {
    return "ValidationOptions{" +
      "validator=" + validator +
      ", validationLevel=" + validationLevel +
      ", validationAction=" + validationAction +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ValidationOptions that = (ValidationOptions) o;
    return Objects.equals(validator, that.validator) && validationLevel == that.validationLevel && validationAction == that.validationAction;
  }

  @Override
  public int hashCode() {
    return Objects.hash(validator, validationLevel, validationAction);
  }
}
