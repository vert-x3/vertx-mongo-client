package io.vertx.ext.mongo.impl;

import com.mongodb.async.SingleResultCallback;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFSOutputStream;

import java.nio.ByteBuffer;

/**
 *  Implementation of {@link GridFSOutputStream} which allows streaming with
 *  Vertx's  {@link io.vertx.core.streams.Pump}
 *
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSOutputStreamImpl
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */
public class GridFSOutputStreamImpl implements GridFSOutputStream {

    WriteStream<Buffer> writeStream;

    public GridFSOutputStreamImpl(WriteStream<Buffer> writeStream) {
        this.writeStream = writeStream;
    }

    @Override
    public void write(ByteBuffer byteBuffer, SingleResultCallback<Integer> singleResultCallback) {

        byte[] bytes = byteBuffer.array();
        Buffer buffer = Buffer.buffer(bytes);

        writeStream.write(buffer);

        singleResultCallback.onResult(bytes.length, null);
    }

    @Override
    public void close(SingleResultCallback<Void> singleResultCallback) {
        singleResultCallback.onResult(null, null);
    }
}
