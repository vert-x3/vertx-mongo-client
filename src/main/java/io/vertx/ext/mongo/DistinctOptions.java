package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class DistinctOptions {
  CollationOptions collation;

  public DistinctOptions() {
  }

  public DistinctOptions(DistinctOptions distinctOptions) {
    collation = distinctOptions.getCollation();
  }

  public DistinctOptions(JsonObject json) {
    DistinctOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    DistinctOptionsConverter.toJson(this, json);
    return json;
  }

  /**
   * @return Configured collationOptions
   */
  public CollationOptions getCollation() {
    return collation;
  }

  /**
   * Optional.
   *
   * Specifies the collation to use for the operation.
   *
   * Collation allows users to specify language-specific rules for string comparison, such as rules for letter-case and accent marks.
   * @param collation
   * @return reference to this, for fluency
   */
  public DistinctOptions setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DistinctOptions that = (DistinctOptions) o;
    return Objects.equals(getCollation(), that.getCollation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCollation());
  }

  @Override
  public String toString() {
    return "DistinctOptions{" +
      "collation=" + collation +
      '}';
  }
}
