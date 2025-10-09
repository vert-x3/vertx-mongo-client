package io.vertx.ext.mongo.impl;

import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.reactivestreams.client.gridfs.GridFSBucket;
import com.mongodb.reactivestreams.client.gridfs.GridFSDownloadPublisher;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.internal.ContextInternal;
import io.vertx.core.internal.PromiseInternal;
import io.vertx.core.internal.VertxInternal;
import io.vertx.core.internal.buffer.BufferInternal;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.GridFsDownloadOptions;
import io.vertx.ext.mongo.GridFsUploadOptions;
import io.vertx.ext.mongo.MongoGridFsClient;
import io.vertx.ext.mongo.impl.tracing.MongoTracer;
import io.vertx.ext.mongo.tracing.MongoTracerRequest;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

import static io.netty.buffer.Unpooled.copiedBuffer;
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
  private final CodecRegistry codecRegistry;

  public MongoGridFsClientImpl(VertxInternal vertx, MongoClientImpl mongoClient, GridFSBucket gridFSBucket, CodecRegistry codecRegistry) {
    this.vertx = vertx;
    this.clientImpl = mongoClient;
    this.bucket = gridFSBucket;
    this.codecRegistry = codecRegistry;
  }

  private MongoTracerRequest.Builder tracingRequest(String operation) {
    return MongoTracerRequest.create(clientImpl.databaseName(), bucket.getBucketName(), operation);
  }

  private <T> void subscribeWithTracing(Promise<?> promise, MongoTracerRequest.Builder builder, Publisher<T> publisher, Subscriber<T> subscriber) {
    MongoTracer.subscribe(contextFromPromise(promise), builder.build(), publisher, subscriber);
  }

  private ContextInternal contextFromPromise(Promise<?> promise) {
    if (promise instanceof PromiseInternal) {
      ContextInternal context = ((PromiseInternal<?>) promise).context();
      if (context != null) {
        return context;
      }
    }
    return vertx.getOrCreateContext();
  }

  @Override
  public Future<String> uploadByFileName(ReadStream<Buffer> stream, String fileName) {
    GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(stream);
    Promise<ObjectId> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("upload")
      .command("fileName", fileName);
    subscribeWithTracing(promise, trace, bucket.uploadFromPublisher(fileName, publisher), new SingleResultSubscriber<>(promise));
    return promise.future().map(ObjectId::toHexString);
  }

  @Override
  public Future<String> uploadByFileNameWithOptions(ReadStream<Buffer> stream, String fileName, GridFsUploadOptions options) {
    GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
    uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
    if (options.getMetadata() != null) {
      uploadOptions.metadata(wrap(options.getMetadata()));
    }

    GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(stream);
    Promise<ObjectId> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("upload")
      .command("fileName", fileName);
    if (options != null) {
      trace.options(options.toJson());
    }
    subscribeWithTracing(promise, trace, bucket.uploadFromPublisher(fileName, publisher, uploadOptions), new SingleResultSubscriber<>(promise));
    return promise.future().map(ObjectId::toHexString);
  }

  private Document wrap(JsonObject json) {
    Codec<Document> codec = codecRegistry.get(Document.class);
    BsonDocument bsonDocument = new JsonObjectBsonAdapter(json).toBsonDocument(BsonDocument.class, codecRegistry);
    return codec.decode(bsonDocument.asBsonReader(), DecoderContext.builder().build());
  }

  private <T> PublisherAdapter<T> tracedAdapter(Publisher<T> publisher, int batchSize, MongoTracerRequest request) {
    ContextInternal context = vertx.getOrCreateContext();
    return new PublisherAdapter<>(context, MongoTracer.publisher(context, request, publisher), batchSize);
  }

  @Override
  public Future<String> uploadFile(String fileName) {
    requireNonNull(fileName, "fileName cannot be null");
    return uploadFileWithOptions(fileName, null);
  }

  @Override
  public Future<String> uploadFileWithOptions(String fileName, GridFsUploadOptions options) {
    requireNonNull(fileName, "fileName cannot be null");

    OpenOptions openOptions = new OpenOptions().setRead(true);

    return vertx.fileSystem().open(fileName, openOptions)
      .flatMap(file -> {
        GridFSReadStreamPublisher publisher = new GridFSReadStreamPublisher(file);
        Promise<ObjectId> promise = vertx.promise();
        MongoTracerRequest.Builder trace = tracingRequest("upload")
          .command("fileName", fileName);
        if (options == null) {
          subscribeWithTracing(promise, trace, bucket.uploadFromPublisher(fileName, publisher), new SingleResultSubscriber<>(promise));
        } else {
          GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
          uploadOptions.chunkSizeBytes(options.getChunkSizeBytes());
          if (options.getMetadata() != null) {
            uploadOptions.metadata(wrap(options.getMetadata()));
          }
          trace.options(options.toJson());
          subscribeWithTracing(promise, trace, bucket.uploadFromPublisher(fileName, publisher, uploadOptions), new SingleResultSubscriber<>(promise));
        }
        return promise.future().map(ObjectId::toHexString);
      });
  }

  @Override
  public void close() {
  }

  @Override
  public Future<Void> delete(String id) {
    requireNonNull(id, "id cannot be null");

    ObjectId objectId = new ObjectId(id);
    Promise<Void> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("delete")
      .command("fileId", id);
    subscribeWithTracing(promise, trace, bucket.delete(objectId), new CompletionSubscriber<>(promise));
    return promise.future();
  }

  @Override
  public ReadStream<Buffer> readByFileName(String fileName) {
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
    MongoTracerRequest request = tracingRequest("read")
      .command("fileName", fileName)
      .build();
    return handleRead(publisher, request);
  }

  @Override
  public ReadStream<Buffer> readByFileNameWithOptions(String fileName, GridFsDownloadOptions options) {
    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName, downloadOptions);
    MongoTracerRequest.Builder trace = tracingRequest("read")
      .command("fileName", fileName);
    if (options != null) {
      trace.options(options.toJson());
    }
    MongoTracerRequest request = trace.build();
    return handleRead(publisher, request);
  }

  @Override
  public ReadStream<Buffer> readById(String id) {
    ObjectId objectId = new ObjectId(id);
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
    MongoTracerRequest request = tracingRequest("read")
      .command("fileId", id)
      .build();
    return handleRead(publisher, request);
  }

  @Override
  public Future<Long> downloadByFileName(WriteStream<Buffer> stream, String fileName) {
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
    MongoTracerRequest request = tracingRequest("download")
      .command("fileName", fileName)
      .build();
    return handleDownload(publisher, stream, request);
  }

  @Override
  public Future<Long> downloadByFileNameWithOptions(WriteStream<Buffer> stream, String fileName, GridFsDownloadOptions options) {
    GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions();
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName, downloadOptions);
    MongoTracerRequest.Builder trace = tracingRequest("download")
      .command("fileName", fileName);
    if (options != null) {
      trace.options(options.toJson());
    }
    MongoTracerRequest request = trace.build();
    return handleDownload(publisher, stream, request);
  }

  @Override
  public Future<Long> downloadById(WriteStream<Buffer> stream, String id) {
    ObjectId objectId = new ObjectId(id);
    GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
    MongoTracerRequest request = tracingRequest("download")
      .command("fileId", id)
      .build();
    return handleDownload(publisher, stream, request);
  }

  @Override
  public Future<Long> downloadFile(String fileName) {
    requireNonNull(fileName, "fileName cannot be null");

    return downloadFileAs(fileName, fileName);
  }

  @Override
  public Future<Long> downloadFileAs(String fileName, String newFileName) {
    requireNonNull(fileName, "fileName cannot be null");
    requireNonNull(newFileName, "newFileName cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    return vertx.fileSystem().open(newFileName, options)
      .flatMap(file -> {
        GridFSDownloadPublisher publisher = bucket.downloadToPublisher(fileName);
        MongoTracerRequest request = tracingRequest("download")
          .command("fileName", fileName)
          .command("target", newFileName)
          .build();
        return handleDownload(publisher, file, request);
      });
  }
  @Override
  public Future<Long> downloadFileByID(String id, String fileName) {
    requireNonNull(fileName, "fileName cannot be null");

    OpenOptions options = new OpenOptions().setWrite(true);

    return vertx.fileSystem().open(fileName, options)
      .flatMap(file -> {
        ObjectId objectId = new ObjectId(id);
        GridFSDownloadPublisher publisher = bucket.downloadToPublisher(objectId);
        MongoTracerRequest request = tracingRequest("download")
          .command("fileId", id)
          .command("target", fileName)
          .build();
        return handleDownload(publisher, file, request);
      });
  }

  @Override
  public Future<Void> drop() {
    Promise<Void> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("drop");
    subscribeWithTracing(promise, trace, bucket.drop(), new CompletionSubscriber<>(promise));
    return promise.future();
  }

  @Override
  public Future<List<String>> findAllIds() {
    Promise<List<String>> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("find")
      .command("operation", "allIds");
    subscribeWithTracing(promise, trace, bucket.find(), new MappingAndBufferingSubscriber<>(gridFSFile -> gridFSFile.getObjectId().toHexString(), promise));
    return promise.future();
  }

  @Override
  public Future<List<String>> findIds(JsonObject query) {
    requireNonNull(query, "query cannot be null");

    JsonObject encodedQuery = clientImpl.encodeKeyWhenUseObjectId(query);

    Bson bquery = clientImpl.wrap(encodedQuery);
    Promise<List<String>> promise = vertx.promise();
    MongoTracerRequest.Builder trace = tracingRequest("find")
      .command("query", query);
    subscribeWithTracing(promise, trace, bucket.find(bquery), new MappingAndBufferingSubscriber<>(gridFSFile -> gridFSFile.getObjectId().toHexString(), promise));
    return promise.future();
  }

  private Future<Long> handleDownload(GridFSDownloadPublisher publisher, WriteStream<Buffer> stream, MongoTracerRequest request) {
    ReadStream<ByteBuffer> adapter = tracedAdapter(publisher, 16, request);
    MapAndCountBuffer mapper = new MapAndCountBuffer();
    MappingStream<ByteBuffer, Buffer> rs = new MappingStream<>(adapter, mapper);
    return rs.pipeTo(stream).map(v -> mapper.count);
  }

  private ReadStream<Buffer> handleRead(GridFSDownloadPublisher publisher, MongoTracerRequest request) {
    ReadStream<ByteBuffer> adapter = tracedAdapter(publisher, 16, request);
    MapBuffer mapper = new MapBuffer();
    return new MappingStream<>(adapter, mapper);
  }

  private static class MapAndCountBuffer implements Function<ByteBuffer, Buffer> {
    private long count = 0;

    @Override
    public Buffer apply(ByteBuffer bb) {
      Buffer buffer = BufferInternal.buffer(copiedBuffer(bb));
      count += buffer.length();
      return buffer;
    }
  }

  private static class MapBuffer implements Function<ByteBuffer, Buffer> {
    @Override
    public Buffer apply(ByteBuffer bb) {
      return BufferInternal.buffer(copiedBuffer(bb));
    }
  }
}
