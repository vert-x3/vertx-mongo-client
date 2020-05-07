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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;


public class GridFSReadStreamPublisher implements Publisher<ByteBuffer> {
  private final ReadStream<Buffer> stream;
  private final AtomicReference<Subscription> current;

  public GridFSReadStreamPublisher(ReadStream<Buffer> stream) {
    this.stream = stream;
    this.current = new AtomicReference<>();
  }

  private void release() {
    Subscription sub = current.get();
    if (sub != null) {
      if (current.compareAndSet(sub, null)) {
        try {
          stream.exceptionHandler(null);
          stream.endHandler(null);
          stream.handler(null);
        } catch (Exception ignore) {
        } finally {
          try {
            stream.resume();
          } catch (Exception ignore) {
          }
        }
      }
    }
  }

  @Override
  public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
    Subscription sub = new Subscription() {
      @Override
      public void request(long l) {
        if (current.get() == this) {
          stream.fetch(l);
        }
      }

      @Override
      public void cancel() {
        release();
      }
    };

    if (!current.compareAndSet(null, sub)) {
      subscriber.onError(new IllegalStateException("This processor allows only a single Subscriber"));
      return;
    }

    stream.pause();

    stream.endHandler(v -> {
      release();
      subscriber.onComplete();
    });
    stream.exceptionHandler(err -> {
      release();
      subscriber.onError(err);
    });
    stream.handler(buffer -> {
      final byte[] bytes = buffer.getBytes();
      final ByteBuffer wrapper = ByteBuffer.wrap(bytes);
      subscriber.onNext(wrapper);
    });

    subscriber.onSubscribe(sub);
  }
}
