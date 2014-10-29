package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class InsertOptions extends WriteOptions {
  private Boolean ordered;

  public InsertOptions() {
  }

  public InsertOptions(JsonObject json) {
    super(json);
    this.ordered = json.getBoolean("ordered");
  }

  public InsertOptions(InsertOptions other) {
    super(other);
    this.ordered = other.ordered;
  }

  public boolean isOrdered() {
    return ordered;
  }

  public InsertOptions setOrdered(boolean ordered) {
    this.ordered = ordered;
    return this;
  }

  @Override
  public InsertOptions setWriteConcern(String writeConcern) {
    super.setWriteConcern(writeConcern);
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = super.toJson();
    if (ordered != null) {
      json.put("ordered", ordered);
    }

    return json;
  }
}
