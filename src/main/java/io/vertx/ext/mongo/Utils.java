package io.vertx.ext.mongo;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.BulkWriteUpsert;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonValue;
import org.bson.codecs.DecoderContext;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {
  public static final String ID_FIELD = "_id";

  public static <T, R> Handler<AsyncResult<T>> convertResult(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
    return result -> {
      if (result.succeeded()) {
        resultHandler.handle(Future.succeededFuture(converter.apply(result.result())));
      } else {
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    };
  }

  public static <T> Handler<AsyncResult<T>> toVoidResult(Handler<AsyncResult<Void>> resultHandler) {
    return convertResult(resultHandler, t -> null);
  }

  public static <T> Handler<AsyncResult<List<T>>> toJsonArrayResult(Handler<AsyncResult<JsonArray>> resultHandler) {
    return convertResult(resultHandler, JsonArray::new);
  }

  public static <T> Handler<AsyncResult<List<T>>> toSingleResult(Handler<AsyncResult<T>> resultHandler) {
    return convertResult(resultHandler, ts -> {
      if (ts.isEmpty()) {
        return null;
      } else {
        return ts.get(ts.size() - 1);
      }
    });
  }

  public static Handler<AsyncResult<DeleteResult>> toMongoClientDeleteResult(Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
    return convertResult(resultHandler, deleteResult -> {
      if (deleteResult.wasAcknowledged()) {
        return new MongoClientDeleteResult(deleteResult.getDeletedCount());
      } else {
        return null;
      }
    });
  }

  public static Handler<AsyncResult<UpdateResult>> toMongoClientUpdateResult(Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
    return convertResult(resultHandler, updateResult -> {
      if (updateResult.wasAcknowledged()) {
        return new MongoClientUpdateResult(updateResult.getMatchedCount(), convertUpsertId(updateResult.getUpsertedId()), updateResult.getModifiedCount());
      } else {
        return null;
      }
    });
  }

  public static Handler<AsyncResult<BulkWriteResult>> toMongoClientBulkWriteResult(Handler<AsyncResult<MongoClientBulkWriteResult>> resultHandler) {
    return convertResult(resultHandler, bulkWriteResult -> {
      if (bulkWriteResult.wasAcknowledged()) {
        return convertToMongoClientBulkWriteResult(bulkWriteResult.getInsertedCount(),
          bulkWriteResult.getMatchedCount(),
          bulkWriteResult.getDeletedCount(),
          bulkWriteResult.getModifiedCount(),
          bulkWriteResult.getUpserts());
      } else {
        return null;
      }
    });
  }

  @Nullable
  private static JsonObject convertUpsertId(@Nullable BsonValue upsertId) {
    JsonObject jsonUpsertId;
    if (upsertId != null) {
      JsonObjectCodec jsonObjectCodec = new JsonObjectCodec(new JsonObject());

      BsonDocument upsertIdDocument = new BsonDocument();
      upsertIdDocument.append(ID_FIELD, upsertId);

      BsonDocumentReader bsonDocumentReader = new BsonDocumentReader(upsertIdDocument);
      jsonUpsertId = jsonObjectCodec.decode(bsonDocumentReader, DecoderContext.builder().build());
    } else {
      jsonUpsertId = null;
    }
    return jsonUpsertId;
  }

  public static class MappingObservableSubscriber<T, R> implements Subscriber<T> {
    private final List<R> received;
    private final Context context;
    private final Handler<AsyncResult<List<R>>> resultHandler;
    private final Function<T, R> converter;

    MappingObservableSubscriber(Context context, Function<T, R> converter, Handler<AsyncResult<List<R>>> resultHandler) {
      this.received = new ArrayList<>();
      this.context = context;
      this.converter = converter;
      this.resultHandler = resultHandler;
    }

    public MappingObservableSubscriber(Vertx vertx, Function<T, R> converter, Handler<AsyncResult<List<R>>> resultHandler) {
      this(vertx.getOrCreateContext(), converter, resultHandler);
    }


    @Override
    public void onSubscribe(Subscription s) {
      s.request(Integer.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {
      received.add(converter.apply(t));
    }

    @Override
    public void onError(Throwable t) {
      if (context == Vertx.currentContext()) {
        resultHandler.handle(Future.failedFuture(t));
      } else {
        context.runOnContext(u -> resultHandler.handle(Future.failedFuture(t)));
      }
    }

    @Override
    public void onComplete() {
      if (context == Vertx.currentContext()) {
        complete();
      } else {
        context.runOnContext(u -> complete());
      }
    }

    private void complete() {
      try {
        resultHandler.handle(Future.succeededFuture(received));
      } catch (Exception unhandledEx) {
        resultHandler.handle(Future.failedFuture(unhandledEx));
      }
    }
  }

  public static class ObservableSubscriber<T> extends MappingObservableSubscriber<T, T> {

    ObservableSubscriber(Context context, Handler<AsyncResult<List<T>>> asyncResultHandler) {
      super(context, v -> v, asyncResultHandler);
    }

    public ObservableSubscriber(Vertx vertx, Handler<AsyncResult<List<T>>> asyncResultHandler) {
      this(vertx.getOrCreateContext(), asyncResultHandler);
    }
  }

  private static MongoClientBulkWriteResult convertToMongoClientBulkWriteResult(int insertedCount, int matchedCount,
                                                                                int deletedCount, int modifiedCount, List<BulkWriteUpsert> upserts) {
    List<JsonObject> upsertResult = upserts.stream().map(upsert -> {
      JsonObject upsertValue = convertUpsertId(upsert.getId());
      upsertValue.put(MongoClientBulkWriteResult.INDEX, upsert.getIndex());
      return upsertValue;
    }).collect(Collectors.toList());
    return new MongoClientBulkWriteResult(insertedCount, matchedCount, deletedCount, modifiedCount, upsertResult);
  }

}
