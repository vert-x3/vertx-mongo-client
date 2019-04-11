package io.vertx.ext.mongo.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.impl.InboundBuffer;

/**
 * @author Thomas Segismont
 */
class MongoIterableStream implements ReadStream<JsonObject> {

  private final Context context;
  private final MongoIterable<JsonObject> mongoIterable;
  private final int batchSize;

  // All the following fields are guarded by this instance
  private AsyncBatchCursor<JsonObject> batchCursor;
  private InboundBuffer<JsonObject> queue;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;
  private boolean closed;

  private boolean closeShouldBeCalled = false;

  MongoIterableStream(Context context, MongoIterable<JsonObject> mongoIterable, int batchSize) {
    this.context = context;
    this.mongoIterable = mongoIterable;
    this.batchSize = batchSize;
    this.queue = new InboundBuffer<>(context);
    queue.drainHandler(v -> doRead());
  }

  @Override
  public synchronized MongoIterableStream exceptionHandler(Handler<Throwable> handler) {
    this.exceptionHandler = handler;
    return this;
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
    synchronized (this) {
      if (closed) {
        return this;
      }
    }
    queue.pause();
    return this;
  }

  @Override
  public MongoIterableStream resume() {
    synchronized (this) {
      if (closed) {
        return this;
      }
    }

    queue.resume();
    if (queue.isEmpty() && closeShouldBeCalled) {
      tryClose();
    }
    return this;
  }

  @Override
  public ReadStream<JsonObject> fetch(long amount) {
    synchronized (this) {
      if (closed) {
        return this;
      }
    }
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
            if (queue.write(list)) {
              doRead();
            }
          } else {
            // try to close stream
            tryClose();
            queue.emptyHandler(h -> tryClose());
          }
        } else {
          close();
          handleException(ar.cause());
        }
      }
    });
  }

  private synchronized void tryClose() {
    if (queue.isPaused()) {
      closeShouldBeCalled = true;
      return;
    }

    if (queue.isEmpty()) {
      close();
      if (endHandler != null) {
        endHandler.handle(null);
      }
    }
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
