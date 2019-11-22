package io.vertx.ext.mongo.impl;

import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.internal.SingleResultObservableToPublisher;
import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSOutputStream;
import org.reactivestreams.Publisher;

import java.nio.ByteBuffer;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * Implementation of {@link GridFSOutputStream} which allows streaming with
 * Vertx's  {@link io.vertx.core.streams.Pump}
 * <p>
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSOutputStreamImpl
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */
public class GridFSOutputStreamImpl implements GridFSOutputStream {

  private ByteBuffer pendingByteBuffer = null;
  private WriteStream<Buffer> writeStream;
  private Throwable throwable = null;

  public GridFSOutputStreamImpl(WriteStream<Buffer> writeStream) {

    this.writeStream = writeStream;
    this.writeStream.exceptionHandler(this::exceptionHandler);
  }

  private void exceptionHandler(Throwable throwable) {
    this.throwable = throwable;
  }

  @Override
  public Publisher<Integer> write(ByteBuffer byteBuffer) {
    return new SingleResultObservableToPublisher<>(singleResultCallback -> {
      if (throwable != null) {
        singleResultCallback.onResult(null, throwable);
        return;
      }
      if (writeStream.writeQueueFull()) {
        pendingByteBuffer = byteBuffer;
        writeStream.drainHandler(GridFSOutputStreamImpl.this::drainHandler);
      } else {
        //  Buffer does not expose the internal ByteBuffer hence this is the only way to correctly set position and limit
        final ByteBuf byteBuf = copiedBuffer(byteBuffer);
        final Buffer buffer = Buffer.buffer(byteBuf);
        writeStream.write(buffer);
        singleResultCallback.onResult(byteBuf.readableBytes(), null);
      }
    });
  }

  private void drainHandler(Void aVoid) {
    //  Buffer does not expose the internal ByteBuffer hence this is the only way to correctly set position and limit
    final ByteBuf byteBuf = copiedBuffer(pendingByteBuffer);
    final Buffer buffer = Buffer.buffer(byteBuf);
    writeStream.write(buffer);
    pendingByteBuffer = null;
  }

  @Override
  public Publisher<Success> close() {
    return new SingleResultObservableToPublisher<>(
      callback -> {
        if (throwable == null) {
          writeStream.end(result -> {
            if (result.failed()) {
              callback.onResult(null, result.cause());
            } else {
              callback.onResult(Success.SUCCESS, null);
            }
          });
        } else {
          writeStream.end();
          callback.onResult(null, throwable);
        }
      });
  }
}
