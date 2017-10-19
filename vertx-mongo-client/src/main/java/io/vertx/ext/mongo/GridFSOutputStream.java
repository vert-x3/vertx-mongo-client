package io.vertx.ext.mongo;

import com.mongodb.async.client.gridfs.AsyncOutputStream;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.impl.GridFSOutputStreamImpl;

/**
 * {@link io.vertx.ext.mongo.GridFSOutputStream} extension of {@link AsyncOutputStream}
 * for use with vertx's {@link io.vertx.core.streams.Pump}
 *
 * Adapted from com.github.sth.vertx.mongo.streams.GridFSOutputStream
 *
 * @author <a href="https://github.com/st-h">Steve Hummingbird</a>
 */
public interface GridFSOutputStream extends AsyncOutputStream {

    /**
     * Create a {@link GridFSOutputStream}.
     *
     * @param writeStream the stream to write data retrieved from GridFS to
     * @return the stream
     */
    static GridFSOutputStream create(WriteStream<Buffer> writeStream) {
        return new GridFSOutputStreamImpl(writeStream);
    }
}
