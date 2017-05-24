package io.vertx.ext.mongo.impl;

import com.mongodb.async.SingleResultCallback;
import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSOutputStream;

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

  WriteStream<Buffer> writeStream;

  public GridFSOutputStreamImpl(WriteStream<Buffer> writeStream) {

    this.writeStream = writeStream;
    this.writeStream.exceptionHandler(this::exceptionHandler);
  }

  private void exceptionHandler(Throwable throwable) {
  }

  @Override
  public void write(ByteBuffer byteBuffer, SingleResultCallback<Integer> singleResultCallback) {
    //  Buffer does not expose the internal ByteBuffer hence this is the only way to correctly set position and limit
    final ByteBuf byteBuf = copiedBuffer(byteBuffer);
    final Buffer buffer = Buffer.buffer(byteBuf);
    if (writeStream.writeQueueFull()) {
      writeStream.drainHandler(handler -> {
        writeStream.write(buffer);
        singleResultCallback.onResult(byteBuf.readableBytes(), null);
      });
    } else {
      writeStream.write(buffer);
      singleResultCallback.onResult(byteBuf.readableBytes(), null);
    }
  }

  @Override
  public void close(SingleResultCallback<Void> singleResultCallback) {

    singleResultCallback.onResult(null, null);
    writeStream.end();
  }
}
