package io.vertx.ext.mongo;


import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * Wrapper for a buffer used by GridFs.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
@DataObject
public class GridFsBuffer {

  private Buffer buffer;

  /**
   * Default constructor
   */
  public GridFsBuffer() {
  }

  /**
   * Copy constructor
   *
   * @param options  the one to copy
   */
  public GridFsBuffer(GridFsBuffer copy) {
    this.buffer = copy.buffer;
  }

  /**
   * Constructor from JSON
   *
   * @param options  the JSON
   */
  public GridFsBuffer(JsonObject json) {
    buffer = Buffer.buffer(json.getBinary("buffer"));
  }

  /**
   * Convert to JSON
   *
   * @return  the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("buffer", buffer.getBytes());

    return json;
  }

  public Buffer getBuffer() {
    return buffer;
  }

  public GridFsBuffer setBuffer(Buffer buffer) {
    this.buffer = buffer;

    return this;
  }
}
