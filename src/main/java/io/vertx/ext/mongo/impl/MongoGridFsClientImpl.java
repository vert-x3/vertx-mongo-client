package io.vertx.ext.mongo.impl;

import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.reactivestreams.client.gridfs.GridFSBucket;
import com.mongodb.reactivestreams.client.gridfs.GridFSDownloadPublisher;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.vertx.ext.mongo.impl.Utils.setHandler;
import static java.util.Objects.requireNonNull;

/**
 * The implementation of the {@link MongoGridFsClient}. This implementation is based on the async driver
 * provided by Mongo.
 *
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class MongoGridFsClientImpl implements MongoGridFsClient {

  private final GridFSBucket bucket;
  private final MongoClientImpl clientImpl;
  private final VertxInternal vertx;

  public MongoGridFsClientImpl(VertxInternal vertx, MongoClientImpl mongoClient, GridFSBucket gridFSBucket) {
    this.vertx = vertx;
    this.clientImpl = mongoClient;
    this.bucket = gridFSBucket;
  }

  @Override
  public MongoGridFsClient uploadByFileName(ReadStream<Buffer> stream, String fileName, Handler<AsyncResult<String>> resultHandler) {
    Future<String> future = uploadByFileName(stream, fileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<String> uploadByFileName(ReadStream<Buffer> stream, String fileName) {
    GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(stream);
    Promise<ObjectId> promise = vertx.promise();
    bucket.uploadFromPublisher(fileName, publisher).subscribe(new SingleResultSubscriber<>(promise));
    return promise.future().map(ObjectId::toHexString);
  }

  @Override
  public MongoGridFsClient uploadByFileNameWithOptions(ReadStream<Buffer> stream, String fileName, GridFsUploadOptions options, Handler<AsyncResult<String>> resultHandler) {
    Future<String> future = uploadByFileNameWithOptions(stream, fileName, options);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<String> uploadByFileNameWithOptions(ReadStream<Buffer> stream, String fileName, GridFsUploadOptions options) {
    GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
    uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
    if (options.getMetadata() != null) uploadOptions.metadata(new Document(options.getMetadata().getMap()));

    GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(stream);
    Promise<ObjectId> promise = vertx.promise();
    bucket.uploadFromPublisher(fileName, publisher, uploadOptions).subscribe(new SingleResultSubscriber<>(promise));
    return promise.future().map(ObjectId::toHexString);
  }

  @Override
  public MongoGridFsClient uploadFile(String fileName, Handler<AsyncResult<String>> resultHandler) {
    Future<String> future = uploadFile(fileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<String> uploadFile(String fileName) {
    requireNonNull(fileName, "fileName cannot be null");
    return uploadFileWithOptions(fileName, null);
  }

  @Override
  public MongoGridFsClient uploadFileWithOptions(String fileName, GridFsUploadOptions options, Handler<AsyncResult<String>> resultHandler) {
    Future<String> future = uploadFileWithOptions(fileName, options);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<String> uploadFileWithOptions(String fileName, GridFsUploadOptions options) {
    requireNonNull(fileName, "fileName cannot be null");

    OpenOptions openOptions = new OpenOptions().setRead(true);

    return vertx.fileSystem().open(fileName, openOptions)
      .flatMap(file -> {
        GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(file);
        Promise<ObjectId> promise = vertx.promise();
        if (options == null) {
          bucket.uploadFromPublisher(fileName, publisher).subscribe(new SingleResultSubscriber<>(promise));
        } else {
          GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
          uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
          if (options.getMetadata() != null) {
            uploadOptions.metadata(new Document(options.getMetadata().getMap()));
          }
          bucket.uploadFromPublisher(fileName, publisher, uploadOptions).subscribe(new SingleResultSubscriber<>(promise));
        }
        return promise.future().map(ObjectId::toHexString);
      });
  }

  @Override
  public void close() {
  }

  @Override
  public MongoGridFsClient delete(String id, Handler<AsyncResult<Void>> resultHandler) {
    Future<Void> future = delete(id);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Void> delete(String id) {
    requireNonNull(id, "id cannot be null");

    ObjectId objectId = new ObjectId(id);
    Promise<Void> promise = vertx.promise();
    bucket.delete(objectId).subscribe(new CompletionSubscriber<>(promise));
    return promise.future();
  }

  @Override
  public ReadStream<Buffer> readByFileName(String fileName) {
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
    return handleRead(publisher);
  }

  @Override
  public ReadStream<Buffer> readByFileNameWithOptions(String fileName, GridFsDownloadOptions options) {
    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName, downloadOptions);
    return handleRead(publisher);
  }

  @Override
  public ReadStream<Buffer> readById(String id) {
    ObjectId objectId = new ObjectId(id);
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
    return handleRead(publisher);
  }

  @Override
  public MongoGridFsClient downloadByFileName(WriteStream<Buffer> stream, String fileName, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadByFileName(stream, fileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadByFileName(WriteStream<Buffer> stream, String fileName) {
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
    return handleDownload(publisher, stream);
  }

  @Override
  public MongoGridFsClient downloadByFileNameWithOptions(WriteStream<Buffer> stream, String fileName, GridFsDownloadOptions options, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadByFileNameWithOptions(stream, fileName, options);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadByFileNameWithOptions(WriteStream<Buffer> stream, String fileName, GridFsDownloadOptions options) {
    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName, downloadOptions);
    return handleDownload(publisher, stream);
  }

  @Override
  public MongoGridFsClient downloadById(WriteStream<Buffer> stream, String id, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadById(stream, id);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadById(WriteStream<Buffer> stream, String id) {
    ObjectId objectId = new ObjectId(id);
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
    return handleDownload(publisher, stream);
  }

  @Override
  public MongoGridFsClient downloadFile(String fileName, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadFile(fileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadFile(String fileName) {
    requireNonNull(fileName, "fileName cannot be null");

    return downloadFileAs(fileName, fileName);
  }

  @Override
  public MongoGridFsClient downloadFileAs(String fileName, String newFileName, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadFileAs(fileName, newFileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadFileAs(String fileName, String newFileName) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(newFileName, "newFileName cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    return vertx.fileSystem().open(newFileName, options)
      .flatMap(file -> {
        GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
        return handleDownload(publisher, file);
      });
  }

  @Override
  public MongoGridFsClient downloadFileByID(String id, String fileName, Handler<AsyncResult<Long>> resultHandler) {
    Future<Long> future = downloadFileByID(id, fileName);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Long> downloadFileByID(String id, String fileName) {
    requireNonNull(fileName, "fileName cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    return vertx.fileSystem().open(fileName, options)
      .flatMap(file -> {
        ObjectId objectId = new ObjectId(id);
        GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
        return handleDownload(publisher, file);
      });
  }

  @Override
  public MongoGridFsClient drop(Handler<AsyncResult<Void>> resultHandler) {
    Future<Void> future = drop();
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<Void> drop() {
    Promise<Void> promise = vertx.promise();
    bucket.drop().subscribe(new CompletionSubscriber<>(promise));
    return promise.future();
  }

  @Override
  public MongoGridFsClient findAllIds(Handler<AsyncResult<List<String>>> resultHandler) {
    Future<List<String>> future = findAllIds();
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<List<String>> findAllIds() {
    Promise<List<String>> promise = vertx.promise();
    bucket.find().subscribe(new MappingAndBufferingSubscriber<>(gridFSFile -> gridFSFile.getObjectId().toHexString(), promise));
    return promise.future();
  }

  @Override
  public MongoGridFsClient findIds(JsonObject query, Handler<AsyncResult<List<String>>> resultHandler) {
    Future<List<String>> future = findIds(query);
    setHandler(future, resultHandler);
    return this;
  }

  @Override
  public Future<List<String>> findIds(JsonObject query) {
    requireNonNull(query, "query cannot be null");

    JsonObject encodedQuery = clientImpl.encodeKeyWhenUseObjectId(query);

    Bson bquery = clientImpl.wrap(encodedQuery);
    Promise<List<String>> promise = vertx.promise();
    bucket.find(bquery).subscribe(new MappingAndBufferingSubscriber<>(gridFSFile -> gridFSFile.getObjectId().toHexString(), promise));
    return promise.future();
  }

  private Future<Long> handleDownload(GridFSDownloadPublisher publisher, WriteStream<Buffer> stream) {
    ReadStream<ByteBuffer> adapter = new PublisherAdapter<>(vertx.getOrCreateContext(), publisher, 16);
    MapAndCountBuffer mapper = new MapAndCountBuffer();
    MappingStream<ByteBuffer, Buffer> rs = new MappingStream<>(adapter, mapper);
    return rs.pipeTo(stream).map(v -> mapper.count);
  }

  private ReadStream<Buffer> handleRead(GridFSDownloadPublisher publisher) {
    ReadStream<ByteBuffer> adapter = new PublisherAdapter<>(vertx.getOrCreateContext(), publisher, 16);
    MapBuffer mapper = new MapBuffer();
    return new MappingStream<>(adapter, mapper);
  }

  private static class MapAndCountBuffer implements Function<ByteBuffer, Buffer> {
    private long count = 0;

    @Override
    public Buffer apply(ByteBuffer bb) {
      Buffer buffer = Buffer.buffer(copiedBuffer(bb));
      count += buffer.length();
      return buffer;
    }
  }

  private static class MapBuffer implements Function<ByteBuffer, Buffer> {
    @Override
    public Buffer apply(ByteBuffer bb) {
      Buffer buffer = Buffer.buffer(copiedBuffer(bb));
      return buffer;
    }
  }
}
