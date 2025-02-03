/*
 * Copyright 2019 The Vert.x Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.ext.mongo.impl;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.internal.ContextInternal;
import io.vertx.core.internal.EventExecutor;
import io.vertx.core.internal.concurrent.InboundMessageChannel;
import io.vertx.core.streams.ReadStream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PublisherAdapter<T> implements ReadStream<T> {

  private final ContextInternal context;
  private final Publisher<T> publisher;

  private final int batchSize;

  private Handler<T> handler;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;

  private Subscriber subscriber;
  private long demand;

  public PublisherAdapter(Context context, Publisher<T> publisher, int batchSize) {
    Objects.requireNonNull(context, "context is null");
    Objects.requireNonNull(publisher, "publisher is null");
    this.context = (ContextInternal) context;
    this.publisher = publisher;
    this.batchSize = batchSize;
    this.demand = Long.MAX_VALUE;
  }

  @Override
  public synchronized ReadStream<T> exceptionHandler(Handler<Throwable> handler) {
    exceptionHandler = handler;
    return this;
  }

  @Override
  public synchronized ReadStream<T> endHandler(Handler<Void> handler) {
    endHandler = handler;
    return this;
  }

  @Override
  public ReadStream<T> handler(Handler<T> h) {
    Subscriber s;
    if (h == null) {
      synchronized (this) {
        handler = h;
        s = subscriber;
        subscriber = null;
        demand = Long.MAX_VALUE;
      }
      if (s != null) {
        s.cancel();
      }
    } else {
      long d;
      synchronized (this) {
        handler = h;
        s = subscriber;
        if (s != null) {
          return this;
        }
        s = new Subscriber();
        subscriber = s;
        d = demand;
        if (d > 0L) {
          s.fetch(d);
        } else {
          s.pause();
        }
      }
      publisher.subscribe(s);
    }
    return this;
  }

  @Override
  public ReadStream<T> pause() {
    Subscriber s;
    synchronized (this) {
      demand = 0L;
      s = subscriber;
    }
    if (s != null) {
      s.pause();
    }
    return this;
  }

  @Override
  public ReadStream<T> resume() {
    return fetch(Long.MAX_VALUE);
  }

  @Override
  public synchronized ReadStream<T> fetch(long amount) {
    if (amount < 0L) {
      throw new IllegalArgumentException();
    }
    if (amount == 0L) {
      return this;
    }
    long d;
    Subscriber s;
    synchronized (this) {
      demand += amount;
      if (demand < 0L) {
        demand = Long.MAX_VALUE;
      }
      d = demand;
      s = subscriber;
    }
    if (s != null) {
      s.fetch(d);
    }
    return this;
  }

  private final Lock lock = new ReentrantLock();
  private final EventExecutor syncExec = new EventExecutor() {
    @Override
    public boolean inThread() {
      return true;
    }
    @Override
    public void execute(Runnable command) {
      lock.lock();
      try {
        command.run();
      } finally {
        lock.unlock();
      }
    }
  };

  private static final Object END = new Object();

  private class Subscriber extends InboundMessageChannel<Object> implements org.reactivestreams.Subscriber<T> {

    public Subscriber() {
      super(syncExec, context.executor());
    }

    private Subscription subscription;
    private boolean paused;
    private int inflight = batchSize;

    @Override
    protected void handleResume() {
      paused = false;
      if (inflight == 0) {
        inflight += batchSize;
        subscription.request(batchSize);
      }
    }

    @Override
    protected void handlePause() {
      paused = true;
    }

    @Override
    protected void handleMessage(Object msg) {
      Handler handler;
      synchronized (PublisherAdapter.this) {
        if (msg == END) {
          msg = null;
          handler = PublisherAdapter.this.endHandler;
        } else if (msg instanceof Throwable) {
          handler = PublisherAdapter.this.exceptionHandler;
        } else {
          handler = PublisherAdapter.this.handler;
        }
      }
      if (handler != null) {
        context.dispatch(msg, handler);
      }
    }

    @Override
    public void onSubscribe(Subscription subscription) {
      synchronized (PublisherAdapter.this) {
        this.subscription = subscription;
      }
      subscription.request(batchSize);
    }

    void cancel() {
      subscription.cancel();
    }

    @Override
    public void onNext(T t) {
      lock.lock();
      inflight--;
      try {
        write(t);
        if (inflight == 0 && !paused) {
          inflight += batchSize;
          subscription.request(batchSize);
        }
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void onError(Throwable t) {
      lock.lock();
      try {
        write(t);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void onComplete() {
      lock.lock();
      try {
        write(END);
      } finally {
        lock.unlock();
      }
    }
  }
}
