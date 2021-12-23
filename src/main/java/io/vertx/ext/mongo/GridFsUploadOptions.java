package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class GridFsUploadOptions {
  private static final Integer DEFAULT_CHUNK_SIZE_IN_BYTES = 255 * 1000;
  private JsonObject metadata;
  private Integer chunkSizeBytes;

  /**
   * Default constructor
   */
  public GridFsUploadOptions() {
    this.metadata = null;
    this.chunkSizeBytes = DEFAULT_CHUNK_SIZE_IN_BYTES;
  }

  /**
   * Copy constructor
   *
   * @param options the one to copy
   */
  public GridFsUploadOptions(GridFsUploadOptions options) {
    this.metadata = options.metadata;
    this.chunkSizeBytes = options.chunkSizeBytes;
  }

  /**
   * Constructor from JSON
   *
   * @param options the JSON
   */
  public GridFsUploadOptions(JsonObject options) {
    this.metadata = options.getJsonObject("metadata");
    this.chunkSizeBytes = options.getInteger("chunkSizeBytes");
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
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

  public GridFsUploadOptions setMetadata(JsonObject metadata) {
    this.metadata = metadata;
    return this;
  }

  public Integer getChunkSizeBytes() {
    return chunkSizeBytes;
  }

  public GridFsUploadOptions setChunkSizeBytes(Integer chunkSizeBytes) {
    this.chunkSizeBytes = chunkSizeBytes;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GridFsUploadOptions that = (GridFsUploadOptions) o;
    return Objects.equals(getMetadata(), that.getMetadata()) && Objects.equals(getChunkSizeBytes(), that.getChunkSizeBytes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMetadata(), getChunkSizeBytes());
  }

  @Override
  public String toString() {
    return "GridFsUploadOptions{" +
      "metadata=" + metadata +
      ", chunkSizeBytes=" + chunkSizeBytes +
      '}';
  }
}
