package io.vertx.ext.mongo.impl;

import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

class MongoIterableStream<T> implements ReadStream<T> {
  protected final Context context;
  protected final MongoIterable<T> mongoIterable;
  protected final int batchSize;
  // All the following fields are guarded by this instance
  private AtomicBoolean readInProgress = new AtomicBoolean(false);
  private AtomicBoolean closed = new AtomicBoolean(false);

  private AsyncBatchCursor<T> batchCursor;
  private Deque<T> queue;
  private Handler<T> dataHandler;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;
  private boolean paused;

  public MongoIterableStream(MongoIterable<T> mongoIterable, Context context, int batchSize) {
    this.mongoIterable = mongoIterable;
    this.context = context;
    this.batchSize = batchSize;
  }

  @Override
  public synchronized MongoIterableStream<T> exceptionHandler(Handler<Throwable> handler) {
    checkClosed();
    this.exceptionHandler = handler;
    return this;
  }

  // Always called from a synchronized method or block
  private synchronized void checkClosed() {
    if (closed.get()) {
      throw new IllegalArgumentException("Stream is closed");
    }
  }

  @Override
  public synchronized MongoIterableStream<T> handler(Handler<T> handler) {
    checkClosed();
    if (handler == null) {
      close();
    } else {
      dataHandler = handler;
      SingleResultCallback<AsyncBatchCursor<T>> callback = (result, t) -> {
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
  private synchronized boolean canRead() {
    return !paused && !closed.get();
  }

  @Override
  public synchronized MongoIterableStream<T> pause() {
    checkClosed();
    paused = true;
    return this;
  }

  @Override
  public synchronized MongoIterableStream<T> resume() {
    checkClosed();
    if (paused) {
      paused = false;
      if (dataHandler != null) {
        doRead();
      }
    }
    return this;
  }

  private void doRead() {
    // This is essentially a semaphore
    if (!readInProgress.compareAndSet(false, true)) {
      return;
    }
    if (queue == null) {
      queue = new ConcurrentLinkedDeque<>();
    }
    if (!queue.isEmpty()) {
      context.runOnContext(v -> emitQueued());
      return;
    }

    batchCursor.next((result, t) -> {
      final Future<List<T>> ar;
      if (t != null) {
        ar = Future.failedFuture(t);
      } else {
        ar = Future.succeededFuture(result == null ? Collections.emptyList() : result);
      }

      context.runOnContext(v -> {
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
      });
    });
  }

  private synchronized void handleException(Throwable cause) {
    if (exceptionHandler != null) {
      exceptionHandler.handle(cause);
    }
  }

  private void emitQueued() {
    while (!queue.isEmpty() && canRead()) {
      dataHandler.handle(queue.remove());
    }
    readInProgress.set(false);
    if (canRead()) {
      doRead();
    }
  }

  @Override
  public synchronized MongoIterableStream<T> endHandler(Handler<Void> handler) {
    endHandler = handler;
    return this;
  }

  // Always called from a synchronized method or block
  void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }

    close(Future.future());
  }

  public void close(Handler<AsyncResult<Void>> handler) {
    if (!closed.compareAndSet(false, true)) {
      return;
    }

    context.executeBlocking(fut -> {
      batchCursor.close();
      fut.complete();
    }, handler);
  }
}
