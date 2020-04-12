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
