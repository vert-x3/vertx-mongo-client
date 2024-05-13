package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options used to configure rename collection options.
 *
 * @author <a href="mailto:wangzengyi1935@163.com">Zengyi Wang</a>
 */
@DataObject
@JsonGen(publicConverter = false)
public class RenameCollectionOptions {
  private Boolean dropTarget;

  public RenameCollectionOptions() {
    this.dropTarget = null;
  }

  public RenameCollectionOptions(RenameCollectionOptions other) {
    this.dropTarget = other.getDropTarget();
  }

  public RenameCollectionOptions(JsonObject json) {
    RenameCollectionOptionsConverter.fromJson(json, this);
  }

  public Boolean getDropTarget() {
    return dropTarget;
  }

  /**
   *  Sets if it should drop the target of renameCollection if exists.
   *
   * @param dropTarget the flag indicating to drop the target or not.
   * @return RenameCollectionOptions
   */
  public RenameCollectionOptions setDropTarget(Boolean dropTarget) {
    this.dropTarget = dropTarget;
    return this;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    RenameCollectionOptionsConverter.toJson(this, json);
    return json;
  }

  public com.mongodb.client.model.RenameCollectionOptions toMongoDriverObject() {
    com.mongodb.client.model.RenameCollectionOptions options = new com.mongodb.client.model.RenameCollectionOptions();
    if (dropTarget != null) {
      options.dropTarget(dropTarget);
    }
    return options;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RenameCollectionOptions that = (RenameCollectionOptions) o;
    return Objects.equals(dropTarget, that.dropTarget);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dropTarget);
  }

  @Override
  public String toString() {
    return "RenameCollectionOptions{" +
      "dropTarget=" + dropTarget +
      '}';
  }
}
