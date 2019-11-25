package io.vertx.ext.mongo.impl;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.internal.SingleResultObservableToPublisher;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSInputStream;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.reactivestreams.Publisher;

import java.nio.ByteBuffer;

/**
 * Implementation of {@link GridFSInputStream} which allows streaming with
 * Vertx's  {@link io.vertx.core.streams.Pump}
 * <p>
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSInputStreamImpl
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */

// Even though SingleResultCallback is deprecated SingleResultObservableToPublisher which internally uses
// SingleResultCallback is not. Also SingleResultObservableToPublisher is heavily used inside mongo-reactive-streams library
@SuppressWarnings("deprecation")
public class GridFSInputStreamImpl implements GridFSInputStream {

  private static int DEFAULT_BUFFER_SIZE = 8192;
  private int writeQueueMaxSize;
  private final CircularFifoQueue<Byte> buffer;
  private Handler<Void> drainHandler;
  private volatile boolean closed = false;
  private SingleResultCallback<Integer> pendingCallback;
  private ByteBuffer outputBuffer;

  public GridFSInputStreamImpl() {
    buffer = new CircularFifoQueue<>(DEFAULT_BUFFER_SIZE);
    writeQueueMaxSize = buffer.maxSize();
  }

  public GridFSInputStreamImpl(final int queueSize) {
    buffer = new CircularFifoQueue<>(DEFAULT_BUFFER_SIZE);
    writeQueueMaxSize = queueSize;
  }

  @Override
  public Publisher<Integer> read(ByteBuffer buffer) {
    return new SingleResultObservableToPublisher<>(
      callback -> {
        synchronized (this.buffer) {
          //If nothing pending and the stream is still open, store the callback for future processing
          if (this.buffer.isEmpty() && !closed) {
            storeCallback(buffer, callback);
          } else {
            doCallback(buffer, callback);
          }
        }
      });
  }

  private void storeCallback(final ByteBuffer buffer, final SingleResultCallback<Integer> resultCallback) {
    if (pendingCallback != null && pendingCallback != resultCallback) {
      resultCallback.onResult(null, new RuntimeException("mongo provided a new buffer or callback before the previous " +
        "one has been fulfilled"));
    }
    this.outputBuffer = buffer;
    this.pendingCallback = resultCallback;
  }

  private void doCallback(final ByteBuffer buffer, final SingleResultCallback<Integer> resultCallback) {
    pendingCallback = null;
    outputBuffer = null;
    int bytesWritten = 0;
    while (!this.buffer.isEmpty()) {
      buffer.put(this.buffer.remove());
      bytesWritten++;
    }
    resultCallback.onResult(bytesWritten, null);
    // if there is a drain handler and the buffer is less than half full, call the drain handler
    if (drainHandler != null && this.buffer.size() < writeQueueMaxSize / 2) {
      drainHandler.handle(null);
      drainHandler = null;
    }
  }

  public Future<Void> write(Buffer inputBuffer) {
    if (closed) throw new IllegalStateException("Stream is closed");
    final byte[] bytes = inputBuffer.getBytes();
    final ByteBuffer wrapper = ByteBuffer.wrap(bytes);
    synchronized (buffer) {
      if (pendingCallback != null) {
        int bytesWritten = writeOutput(wrapper);
        if (bytesWritten > 0) doCallback(bytesWritten);
      }
      // Drain content left in the input buffer
      while (wrapper.hasRemaining()) {
        buffer.offer(wrapper.get());
      }
    }
    return Future.succeededFuture();
  }

  @Override
  public void write(Buffer data, Handler<AsyncResult<Void>> handler) {
    handler.handle(write(data));
  }

  @Override
  public Publisher<Long> skip(long bytesToSkip) {
    throw new IllegalStateException("Not implemented");
  }

  private int writeOutput(final ByteBuffer wrapper) {
    // First we drain the pending buffer
    int bytesWritten = 0;
    while (!this.buffer.isEmpty()) {
      outputBuffer.put(this.buffer.remove());
      bytesWritten++;
    }
    final int remaining = outputBuffer.remaining();
    if (remaining > 0) {
      // If more space left in the output buffer we directly drain the input buffer
      final int newBytesWritten = Math.min(remaining, wrapper.capacity());
      // Store current limit to restore it in case we don't drain then whole buffer
      final int limit = wrapper.limit();
      wrapper.limit(newBytesWritten);
      outputBuffer.put(wrapper);
      wrapper.limit(limit);
      bytesWritten += newBytesWritten;
    }
    return bytesWritten;
  }

  private void doCallback(final int bytesWritten) {
    SingleResultCallback<Integer> c = pendingCallback;
    outputBuffer = null;
    pendingCallback = null;
    c.onResult(bytesWritten, null);
  }

  @Override
  public Publisher<Success> close() {
    closed = true;
    return new SingleResultObservableToPublisher<>(
      callback -> callback.onResult(Success.SUCCESS, null));
  }

  public void end(Handler<AsyncResult<Void>> resultHandler) {
    synchronized (buffer) {
      if (pendingCallback != null) {
        int bytesWritten = 0;
        while (!this.buffer.isEmpty()) {
          outputBuffer.put(this.buffer.remove());
          bytesWritten++;
        }
        doCallback(bytesWritten);
      }
      this.closed = true;
    }
  }

  public WriteStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
    return this;
  }

  public GridFSInputStream setWriteQueueMaxSize(int size) {
    writeQueueMaxSize = size;
    return this;
  }

  public boolean writeQueueFull() {
    return buffer.size() >= writeQueueMaxSize;
  }

  public WriteStream<Buffer> drainHandler(Handler<Void> handler) {
    this.drainHandler = handler;
    return this;
  }
}
