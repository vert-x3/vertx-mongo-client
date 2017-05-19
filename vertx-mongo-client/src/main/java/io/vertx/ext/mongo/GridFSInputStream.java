package io.vertx.ext.mongo;

import com.mongodb.async.client.gridfs.AsyncInputStream;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.impl.GridFSInputStreamImpl;

/**
 * {@link GridFSInputStream} which bridges the gap between vertx's {@link WriteStream}
 * and mongodb's {@link AsyncInputStream}.
 *
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSInputStream
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */
public interface GridFSInputStream extends AsyncInputStream, WriteStream<Buffer> {

  /**
   * Signals that all data has been consumed. After this method is called and the internal buffer
   * has been written to the database, the driver will be signalled that all data has been processed.
   */
  @Override
  void end();

  /**
   * Sets the maximum internal buffer size.
   * @param size the size in bytes.
   * @return {@link GridFSInputStream}
   */
  @Override
  GridFSInputStream setWriteQueueMaxSize(int size);

  /**
   * Create a {@link io.vertx.ext.mongo.GridFSInputStream}.
   *
   * @return the stream
   */
  static GridFSInputStream create() {
    return new GridFSInputStreamImpl();
  }
}
