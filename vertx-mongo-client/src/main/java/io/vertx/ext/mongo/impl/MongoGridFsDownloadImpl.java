package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSDownloadStream;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.GridFsBuffer;
import io.vertx.ext.mongo.MongoGridFsDownload;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link MongoGridFsDownload}.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class MongoGridFsDownloadImpl extends MongoBaseImpl implements MongoGridFsDownload {

  GridFSDownloadStream stream;

  private MongoGridFsDownloadImpl(Vertx vertx, JsonObject config) {
    super(vertx, config);
  }

  public MongoGridFsDownloadImpl(GridFSDownloadStream stream, Vertx vertx, JsonObject config) {
    this(vertx, config);
    this.stream = stream;
  }

  @Override
  public MongoGridFsDownload read(Integer bufferSize, Handler<AsyncResult<GridFsBuffer>> resultHandler) {
    requireNonNull(bufferSize, "bufferSize cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
    stream.read(buffer, convertCallback(resultHandler, length -> {
      if (length == -1) {
        stream.close((nothing, throwable) -> {});
        return null;
      }

      GridFsBuffer gridFsBuffer = new GridFsBuffer();
      gridFsBuffer.setBuffer(Buffer.buffer(buffer.array()));

      return gridFsBuffer;

    }));
    return this;
  }

  @Override
  public void close() {
  }
}
