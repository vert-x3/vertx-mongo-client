package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Options used to configure downloads from GridFS.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
@DataObject
public class GridFsDownloadOptions {

  public static final Integer DEFAULT_REVISION = 0;

  private Integer revision = DEFAULT_REVISION;

  /**
   * Default constructor
   */
  public GridFsDownloadOptions() {
  }

  /**
   * Copy constructor
   *
   * @param options the one to copy
   */
  public GridFsDownloadOptions(GridFsDownloadOptions options) {
    this.revision = options.revision;
  }

  /**
   * Constructor from JSON
   *
   * @param options the JSON
   */
  public GridFsDownloadOptions(JsonObject options) {
    this.revision = options.getInteger("revision");
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (revision != null) {
      json.put("revision", revision);
    }

    return json;
  }

  public Integer getRevision() {
    return revision;
  }

  public GridFsDownloadOptions setRevision(Integer revision) {
    this.revision = revision;
    return this;
  }

}
