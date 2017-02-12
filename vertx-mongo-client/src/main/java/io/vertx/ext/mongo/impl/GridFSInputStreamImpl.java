package io.vertx.ext.mongo.impl;
import com.mongodb.async.SingleResultCallback;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSInputStream;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 *  Implementation of {@link GridFSInputStream} which allows streaming with
 *  Vertx's  {@link io.vertx.core.streams.Pump}
 *
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSInputStreamImpl
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */
public class GridFSInputStreamImpl implements GridFSInputStream {

  private int writeQueueMaxSize = 8192;

  private final Queue<Byte> pending = new ArrayDeque<>();
  private Handler<Void> drainHandler;

  ByteBuffer outBuffer;
  SingleResultCallback<Integer> callback;

  boolean closed = false;

  public void read(ByteBuffer byteBuffer, SingleResultCallback<Integer> singleResultCallback) {
    this.writeBytes(byteBuffer, singleResultCallback);
  }

  public void close(SingleResultCallback<Void> singleResultCallback) {
    callback.onResult(null, null);
  }

  public WriteStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
    return this;
  }

  public WriteStream<Buffer> write(Buffer buffer) {
    for (byte b : buffer.getBytes()) {
      pending.add(b);
    }
    writeBytes(null, null);
    return this;
  }

  public void end() {
    this.closed = true;
    // if there is no more data to write, call the callback immediately
    if (this.pending.size() == 0 && this.callback != null) {
      this.callback.onResult(-1, null);
      this.callback = null;
      this.outBuffer = null;
    }
  }

  public GridFSInputStream setWriteQueueMaxSize(int i) {
    this.writeQueueMaxSize = i;
    return this;
  }

  public boolean writeQueueFull() {
    return pending.size() >= writeQueueMaxSize;
  }

  public WriteStream<Buffer> drainHandler(Handler<Void> handler) {
    this.drainHandler = handler;
    return this;
  }

  /**
   * Write bytes to the buffer provided by the mongo driver. Buffer and Callback provided are cached in case no data
   * is available to write and will be fulfilled by future invocations.
   * @param b optional buffer provided by the mongo driver
   * @param c optional callback provided by the mongo driver
   */
  private synchronized void writeBytes(ByteBuffer b, SingleResultCallback<Integer> c) {
    int bytesWritten = 0;

    if (b != null && c != null) {
      if (this.outBuffer != null || this.callback != null) {
        c.onResult(null, new RuntimeException("mongo provided a new buffer or callback before the previous " +
          "one has been fulfilled"));
      }
      this.outBuffer = b;
      this.callback = c;
    }

    // a callback and a buffer to write to is available
    if (this.outBuffer != null && this.callback != null) {

      // there is space in the out buffer available and we have some data to write left, so we write it to the buffer
      if (this.outBuffer.remaining() > 0 && this.pending.size() > 0) {

        int bytesToWrite = Math.min(this.outBuffer.remaining(), this.pending.size());
        bytesWritten = bytesToWrite;

        while (bytesToWrite > 0) {
          this.outBuffer.put(this.pending.poll());
          bytesToWrite--;
        }
      }

      if (bytesWritten > 0) {
        // if bytes were written call the callback

        SingleResultCallback<Integer> tempCallback = this.callback;
        this.outBuffer = null;
        this.callback = null;
        tempCallback.onResult(bytesWritten, null);

      } else if (closed && this.pending.size() == 0) {
        // if the stream has been closed and there is no more data to write available, send -1 to the callback

        SingleResultCallback<Integer> tempCallback = this.callback;
        this.outBuffer = null;
        this.callback = null;
        tempCallback.onResult(-1, null);
      }
    }

    // if there is a drain handler and the buffer is less than half full, call the drain handler
    if (drainHandler != null && pending.size() < writeQueueMaxSize / 2) {
      drainHandler.handle(null);
      drainHandler = null;
    }
  }
}
