package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options used to configure uploads to GridFS.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
@DataObject
public class UploadOptions {

  private JsonObject metadata;
  private Integer chunkSizeBytes;

  /**
   * Default constructor
   */
  public UploadOptions() {
  }

  /**
   * Copy constructor
   *
   * @param options  the one to copy
   */
  public UploadOptions(UploadOptions options) {
    this.metadata = options.metadata;
    this.chunkSizeBytes = options.chunkSizeBytes;
  }

  /**
   * Constructor from JSON
   *
   * @param options  the JSON
   */
  public UploadOptions(JsonObject options) {
    this.metadata = options.getJsonObject("metadata");
    this.chunkSizeBytes = options.getInteger("chunkSizeBytes");
  }

  /**
   * Convert to JSON
   *
   * @return  the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (metadata != null) {
      json.put("metadata", metadata);
    }
    if (chunkSizeBytes != null) {
      json.put("chunkSizeBytes", chunkSizeBytes);
    }

    return json;
  }

  public JsonObject getMetadata() {
    return metadata;
  }

  public void setMetadata(JsonObject metadata) {
    this.metadata = metadata;
  }

  public Integer getChunkSizeBytes() {
    return chunkSizeBytes;
  }

  public void setChunkSizeBytes(Integer chunkSizeBytes) {
    this.chunkSizeBytes = chunkSizeBytes;
  }

}
