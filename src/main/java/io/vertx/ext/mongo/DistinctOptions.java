package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

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

}
