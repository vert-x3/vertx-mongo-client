package io.vertx.ext.mongo.impl;

import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.BufferedQueue;
import io.vertx.core.streams.ReadStream;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Thomas Segismont
 */
class MongoIterableStream implements ReadStream<JsonObject> {

  private final Context context;
  private final MongoIterable<JsonObject> mongoIterable;
  private final int batchSize;

  // All the following fields are guarded by this instance
  private AsyncBatchCursor<JsonObject> batchCursor;
  private BufferedQueue<JsonObject> queue;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;
  private boolean closed;

  MongoIterableStream(Context context, MongoIterable<JsonObject> mongoIterable, int batchSize) {
    this.context = context;
    this.mongoIterable = mongoIterable;
    this.batchSize = batchSize;
    this.queue = BufferedQueue.queue(context);
    queue.readHandler(v -> doRead());
    queue.resume();
  }

  @Override
  public synchronized MongoIterableStream exceptionHandler(Handler<Throwable> handler) {
    this.exceptionHandler = handler;
    return this;
  }

  // Always called from a synchronized method or block
  private synchronized void checkClosed() {
    if (closed) {
      throw new IllegalArgumentException("Stream is closed");
    }
  }

  @Override
  public synchronized MongoIterableStream handler(Handler<JsonObject> handler) {
    queue.handler(handler);
    if (handler == null) {
      close();
    } else {
      SingleResultCallback<AsyncBatchCursor<JsonObject>> callback = (result, t) -> {
        context.runOnContext(v -> {
          synchronized (this) {
            if (t != null) {
              close();
              handleException(t);
            } else {
              batchCursor = result;
              batchCursor.setBatchSize(batchSize);
              if (!closed) {
                doRead();
              }
            }
          }
        });
      };
      try {
        mongoIterable.batchCursor(callback);
      } catch (Exception e) {
        close();
        handleException(e);
      }
    }
    return this;
  }

  @Override
  public MongoIterableStream pause() {
    checkClosed();
    queue.pause();
    return this;
  }

  @Override
  public MongoIterableStream resume() {
    checkClosed();
    queue.resume();
    return this;
  }

  @Override
  public ReadStream<JsonObject> fetch(long amount) {
    checkClosed();
    queue.fetch(amount);
    return this;
  }

  // Always called from a synchronized method or block
  private synchronized void doRead() {
    context.<List<JsonObject>>executeBlocking(fut -> {
      batchCursor.next((result, t) -> {
        if (t != null) {
          fut.fail(t);
        } else {
          fut.complete(result);
        }
      });
    }, true, ar -> {
      synchronized (this) {
        if (ar.succeeded()) {
          List<JsonObject> list = ar.result();
          if (list != null) {
            if (queue.pushAll(list)) {
              doRead();
            }
          } else {
            close();
            if (endHandler != null) {
              endHandler.handle(null);
            }
          }
        } else {
          close();
          handleException(ar.cause());
        }
      }
    });
  }

  // Always called from a synchronized method or block
  private void handleException(Throwable cause) {
    if (exceptionHandler != null) {
      exceptionHandler.handle(cause);
    }
  }

  @Override
  public synchronized MongoIterableStream endHandler(Handler<Void> handler) {
    endHandler = handler;
    return this;
  }

  // Always called from a synchronized method or block
  private void close() {
    if (closed) {
      return;
    }
    closed = true;
    AtomicReference<AsyncBatchCursor> cursorRef = new AtomicReference<>();
    context.executeBlocking(fut -> {
      synchronized (this) {
        cursorRef.set(batchCursor);
      }
      AsyncBatchCursor cursor = cursorRef.get();
      if (cursor != null) {
        cursor.close();
      }
      fut.complete();
    }, false, null);
  }
}
