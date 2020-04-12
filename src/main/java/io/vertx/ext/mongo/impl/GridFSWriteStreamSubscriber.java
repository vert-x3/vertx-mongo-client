/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.ext.mongo.impl;

import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;

import static io.netty.buffer.Unpooled.copiedBuffer;


public class GridFSWriteStreamSubscriber implements Subscriber<ByteBuffer> {
  private static final int BATCH_SIZE = 16;
  private final WriteStream<Buffer> writeStream;
  private final Promise<Long> promise;

  private Subscription subscription;
  private int outstanding;
  private boolean done;
  private long written;

  public GridFSWriteStreamSubscriber(WriteStream<Buffer> writeStream, Promise<Long> promise) {
    this.writeStream = writeStream;
    this.promise = promise;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    if (!setSubscription(subscription)) {
      subscription.cancel();
      return;
    }

    writeStream.exceptionHandler(t -> {
      if (!setDone()) {
        return;
      }
      try {
        getSubscription().cancel();
      } catch (Throwable ignored) {
      }
      promise.tryFail(t);
    });
    writeStream.drainHandler(v -> requestMore());
    requestMore();
  }

  @Override
  public void onNext(ByteBuffer buffer) {
    if (isDone()) {
      return;
    }

    try {
      Buffer buf = Buffer.buffer(copiedBuffer(buffer));
      writeStream.write(buf);
      written += buf.length();
      synchronized (this) {
        outstanding--;
      }
    } catch (Throwable t) {
      try {
        getSubscription().cancel();
      } catch (Throwable ignored) {
      }
      onError(t);
      return;
    }

    if (!writeStream.writeQueueFull()) {
      requestMore();
    }
  }

  @Override
  public void onError(Throwable throwable) {
    if (!setDone()) {
      return;
    }
    promise.tryFail(throwable);
  }

  @Override
  public void onComplete() {
    if (!setDone()) {
      return;
    }
    writeStream.end().onComplete(event -> {
      if (event.failed())
        promise.tryFail(event.cause());
      else
        promise.tryComplete(written);
    });
  }

  private synchronized Subscription getSubscription() {
    return subscription;
  }

  private synchronized boolean setSubscription(Subscription subscription) {
    if (this.subscription == null) {
      this.subscription = subscription;
      return true;
    }
    return false;
  }

  private synchronized boolean setDone() {
    return done ? false : (done = true);
  }

  private synchronized boolean isDone() {
    return done;
  }

  private void requestMore() {
    Subscription s = getSubscription();
    if (s == null) {
      return;
    }
    synchronized (this) {
      if (done || outstanding > 0) {
        return;
      }
      outstanding = BATCH_SIZE;
    }
    s.request(BATCH_SIZE);
  }
}
