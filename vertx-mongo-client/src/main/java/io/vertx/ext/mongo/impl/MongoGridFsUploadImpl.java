package io.vertx.ext.mongo.impl;

import com.mongodb.async.client.gridfs.GridFSUploadStream;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.GridFsBuffer;
import io.vertx.ext.mongo.MongoGridFsUpload;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link MongoGridFsUpload}.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class MongoGridFsUploadImpl extends MongoBaseImpl implements MongoGridFsUpload {

  GridFSUploadStream stream;

  private MongoGridFsUploadImpl(Vertx vertx, JsonObject config) {
    super(vertx, config);
  }

  public MongoGridFsUploadImpl(GridFSUploadStream stream, Vertx vertx, JsonObject config) {
    this(vertx, config);
    this.stream = stream;
  }

  @Override
  public MongoGridFsUpload uploadBuffer(GridFsBuffer gridFsBuffer, Handler<AsyncResult<Integer>> resultHandler) {
    requireNonNull(gridFsBuffer, "gridFsBuffer cannot be null");
    requireNonNull(resultHandler, "resultHandler cannot be null");

    ByteBuffer buffer = gridFsBuffer.getBuffer().getByteBuf().nioBuffer();

    stream.write(buffer, wrapCallback(resultHandler));

    return this;
  }

  @Override
  public MongoGridFsUpload end(Handler<AsyncResult<String>> resultHandler) {
    requireNonNull(resultHandler, "resultHandler cannot be null");

    stream.close(convertCallback(resultHandler, nothing -> {
      return stream.getObjectId().toHexString();
    }));
    return this;
  }

  @Override
  public void close() {

  }
}
