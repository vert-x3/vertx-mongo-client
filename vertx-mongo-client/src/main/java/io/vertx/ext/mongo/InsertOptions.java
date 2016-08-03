package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options for configuring insertions.
 *
 * @author <a href="mailto:llfbandit@gmail.com">Rémy Noël</a>
 */
@DataObject
public class InsertOptions {
  /**
   * The default value of ordered = true
   */
  public static final boolean DEFAULT_ORDERED = true;

  /**
   * The default value of bypassDocumentValidation = false
   */
  public static final boolean DEFAULT_BYPASS_VALIDATION = false;

  /**
   * A boolean specifying whether the mongod instance should perform an ordered or unordered insert.
   * Only valid when inserting many documents at once.
   */
  private boolean ordered = true;
  private boolean bypassDocumentValidation;

  /**
   * Default constructor
   */
  public InsertOptions() {
  }

  /**
   * Constructor from JSON
   *
   * @param json  the json
   */
  public InsertOptions(JsonObject json) {
    ordered = json.getBoolean("ordered", DEFAULT_ORDERED);
    bypassDocumentValidation = json.getBoolean("validation_bypass", DEFAULT_BYPASS_VALIDATION);
  }

  public boolean isOrdered() {
    return this.ordered;
  }

  public InsertOptions ordered(boolean ordered) {
    this.ordered = ordered;
    return this;
  }

  public boolean getBypassDocumentValidation() {
    return this.bypassDocumentValidation;
  }

  public InsertOptions bypassDocumentValidation(boolean bypassDocumentValidation) {
    this.bypassDocumentValidation = bypassDocumentValidation;
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (ordered) {
      json.put("ordered", true);
    }
    if (bypassDocumentValidation) {
      json.put("validation_bypass", true);
    }

    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InsertOptions options = (InsertOptions) o;

    return ordered == options.ordered && bypassDocumentValidation == options.bypassDocumentValidation;
  }

  @Override
  public int hashCode() {
    int result = 12;
    result = 312 * result + (ordered ? 1 : 0);
    result = 312 * result + (bypassDocumentValidation ? 1 : 0);
    return result;
  }
}
