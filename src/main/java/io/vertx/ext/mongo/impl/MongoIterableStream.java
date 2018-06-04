package io.vertx.ext.mongo.impl;

import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
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
  private Deque<JsonObject> queue;
  private Handler<JsonObject> dataHandler;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;
  private boolean paused;
  private boolean readInProgress;
  private boolean closed;

  MongoIterableStream(Context context, MongoIterable<JsonObject> mongoIterable, int batchSize) {
    this.context = context;
    this.mongoIterable = mongoIterable;
    this.batchSize = batchSize;
  }

  @Override
  public synchronized MongoIterableStream exceptionHandler(Handler<Throwable> handler) {
    this.exceptionHandler = handler;
    return this;
  }

  // Always called from a synchronized method or block
  private void checkClosed() {
    if (closed) {
      throw new IllegalArgumentException("Stream is closed");
    }
  }

  @Override
  public synchronized MongoIterableStream handler(Handler<JsonObject> handler) {
    if ((dataHandler = handler) == null) {
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
              if (canRead()) {
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

  // Always called from a synchronized method or block
  private boolean canRead() {
    return !paused && !closed;
  }

  @Override
  public synchronized MongoIterableStream pause() {
    checkClosed();
    paused = true;
    return this;
  }

  @Override
  public synchronized MongoIterableStream resume() {
    checkClosed();
    if (paused) {
      paused = false;
      if (dataHandler != null) {
        doRead();
      }
    }
    return this;
  }

  // Always called from a synchronized method or block
  private synchronized void doRead() {
    if (readInProgress) {
      return;
    }
    readInProgress = true;
    if (queue == null) {
      queue = new ArrayDeque<>(batchSize);
    }
    if (!queue.isEmpty()) {
      context.runOnContext(v -> emitQueued());
      return;
    }
    context.<List<JsonObject>>executeBlocking(fut -> {
      batchCursor.next((result, t) -> {
        if (t != null) {
          fut.fail(t);
        } else {
          fut.complete(result == null ? Collections.emptyList() : result);
        }
      });
    }, false, ar -> {
      synchronized (this) {
        if (ar.succeeded()) {
          queue.addAll(ar.result());
          if (queue.isEmpty()) {
            close();
            if (endHandler != null) {
              endHandler.handle(null);
            }
          } else {
            emitQueued();
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

  // Always called from a synchronized method or block
  private synchronized void emitQueued() {
    while (!queue.isEmpty() && canRead()) {
      dataHandler.handle(queue.remove());
    }
    readInProgress = false;
    if (canRead()) {
      doRead();
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
