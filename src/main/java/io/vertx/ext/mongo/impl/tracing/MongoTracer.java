package io.vertx.ext.mongo.impl.tracing;

import io.vertx.core.internal.ContextInternal;
import io.vertx.core.spi.tracing.SpanKind;
import io.vertx.core.spi.tracing.TagExtractor;
import io.vertx.core.spi.tracing.VertxTracer;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.mongo.tracing.MongoTracerRequest;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.BiConsumer;

/**
 * Helper class that delegates to the configured {@link VertxTracer} implementation when MongoDB commands are executed.
 */
public final class MongoTracer {

  private static final BiConsumer<String, String> NOOP_HEADERS = (k, v) -> {
  };

  private MongoTracer() {
  }

  public static <T> void subscribe(ContextInternal context, MongoTracerRequest request, TracingPolicy policy, Publisher<? extends T> publisher, Subscriber<? super T> subscriber) {
    VertxTracer tracer = context.tracer();
    TracingPolicy effectivePolicy = policy != null ? policy : TracingPolicy.PROPAGATE;
    if (tracer == null || effectivePolicy == TracingPolicy.IGNORE) {
      publisher.subscribe(subscriber);
      return;
    }
    Object trace = tracer.sendRequest(context, SpanKind.RPC, effectivePolicy, request, request.operationName(), NOOP_HEADERS, MongoTracerRequest.TAG_EXTRACTOR);
    if (trace == null) {
      publisher.subscribe(subscriber);
      return;
    }
    publisher.subscribe(new TracingSubscriber<>(context, tracer, trace, subscriber));
  }

  public static <T> Publisher<T> publisher(ContextInternal context, MongoTracerRequest request, TracingPolicy policy, Publisher<? extends T> publisher) {
    return subscriber -> subscribe(context, request, policy, publisher, subscriber);
  }

  private static final class TracingSubscriber<T> implements Subscriber<T> {

    private final ContextInternal context;
    private final VertxTracer tracer;
    private final Object trace;
    private final Subscriber<T> actual;
    private boolean ended;

    private TracingSubscriber(ContextInternal context, VertxTracer tracer, Object trace, Subscriber<T> actual) {
      this.context = context;
      this.tracer = tracer;
      this.trace = trace;
      this.actual = actual;
    }

    @Override
    public void onSubscribe(org.reactivestreams.Subscription s) {
      actual.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
      actual.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
      try {
        actual.onError(t);
      } finally {
        end(t);
      }
    }

    @Override
    public void onComplete() {
      try {
        actual.onComplete();
      } finally {
        end(null);
      }
    }

    private void end(Throwable failure) {
      if (!ended) {
        ended = true;
        tracer.receiveResponse(context, null, trace, failure, TagExtractor.empty());
      }
    }
  }
}
