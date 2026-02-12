package io.vertx.ext.mongo.tests.impl.tracing;

import io.vertx.core.internal.ContextInternal;
import io.vertx.core.spi.tracing.TagExtractor;
import io.vertx.core.spi.tracing.VertxTracer;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.mongo.impl.tracing.MongoTracer;
import io.vertx.ext.mongo.tracing.MongoTracerRequest;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class MongoTracerTest {

  @Test
  public void shouldNotWrapSubscriberWhenNoTracer() {
    ContextInternal context = context(null, TracingPolicy.PROPAGATE);
    TestPublisher<String> publisher = new TestPublisher<>("value", null);
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op").build();

    MongoTracer.subscribe(context, request, TracingPolicy.PROPAGATE, publisher, subscriber);

    assertSame(subscriber, publisher.subscriber);
    subscriber.request(1);
    assertEquals(Collections.singletonList("value"), subscriber.items);
    assertNull(subscriber.failure);
    assertTrue(subscriber.completed);
  }

  @Test
  public void shouldNotWrapSubscriberWhenTracingIgnored() {
    RecordingTracer recorder = new RecordingTracer(true);
    ContextInternal context = context(recorder.tracer, TracingPolicy.IGNORE);
    TestPublisher<String> publisher = new TestPublisher<>("value", null);
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "ignored").build();

    MongoTracer.subscribe(context, request, TracingPolicy.IGNORE, publisher, subscriber);

    assertSame(subscriber, publisher.subscriber);
    subscriber.request(1);
    assertTrue(recorder.requests.isEmpty());
  }

  @Test
  public void shouldInstrumentSuccessfulCompletion() {
    RecordingTracer recorder = new RecordingTracer(true);
    ContextInternal context = context(recorder.tracer, TracingPolicy.PROPAGATE);
    TestPublisher<String> publisher = new TestPublisher<>("value", null);
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op").build();

    MongoTracer.subscribe(context, request, TracingPolicy.PROPAGATE, publisher, subscriber);

    assertNotSame(subscriber, publisher.subscriber);
    subscriber.request(1);

    assertEquals(Collections.singletonList("value"), subscriber.items);
    assertTrue(subscriber.completed);
    assertNull(subscriber.failure);

    assertEquals(Collections.singletonList(request), recorder.requests);
    assertEquals(Collections.singletonList("op"), recorder.operations);
    assertEquals(Collections.singletonList(recorder.traceToken), recorder.traces);
    assertEquals(Collections.singletonList(null), recorder.failures);
    assertNotNull(recorder.headerInjectors.get(0));
    assertSame(MongoTracerRequest.TAG_EXTRACTOR, recorder.requestExtractors.get(0));
    assertSame(TagExtractor.empty(), recorder.responseExtractors.get(0));
  }

  @Test
  public void shouldInstrumentFailure() {
    RuntimeException boom = new RuntimeException("boom");
    RecordingTracer recorder = new RecordingTracer(true);
    ContextInternal context = context(recorder.tracer, TracingPolicy.PROPAGATE);
    TestPublisher<String> publisher = new TestPublisher<>(null, boom);
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op").build();

    MongoTracer.subscribe(context, request, TracingPolicy.PROPAGATE, publisher, subscriber);

    subscriber.request(1);

    assertTrue(subscriber.items.isEmpty());
    assertSame(boom, subscriber.failure);
    assertFalse(subscriber.completed);

    assertEquals(Collections.singletonList(request), recorder.requests);
    assertEquals(Collections.singletonList("op"), recorder.operations);
    assertEquals(Collections.singletonList(recorder.traceToken), recorder.traces);
    assertEquals(Collections.singletonList(boom), recorder.failures);
    assertNotNull(recorder.headerInjectors.get(0));
    assertSame(MongoTracerRequest.TAG_EXTRACTOR, recorder.requestExtractors.get(0));
    assertSame(TagExtractor.empty(), recorder.responseExtractors.get(0));
  }

  @Test
  public void shouldBypassWhenTracerReturnsNull() {
    RecordingTracer recorder = new RecordingTracer(false);
    ContextInternal context = context(recorder.tracer, TracingPolicy.PROPAGATE);
    TestPublisher<String> publisher = new TestPublisher<>("value", null);
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    MongoTracerRequest request = MongoTracerRequest.create("db", "collection", "op").build();

    MongoTracer.subscribe(context, request, TracingPolicy.PROPAGATE, publisher, subscriber);

    assertSame(subscriber, publisher.subscriber);
    subscriber.request(1);

    assertTrue(recorder.requests.contains(request));
    assertEquals(Collections.singletonList("op"), recorder.operations);
    assertNotNull(recorder.headerInjectors.get(0));
    assertSame(MongoTracerRequest.TAG_EXTRACTOR, recorder.requestExtractors.get(0));
    assertTrue(recorder.traces.isEmpty());
    assertTrue(recorder.responseExtractors.isEmpty());
    assertTrue(recorder.failures.isEmpty());
  }

  private static ContextInternal context(VertxTracer tracer, TracingPolicy policy) {
    return (ContextInternal) Proxy.newProxyInstance(
      ContextInternal.class.getClassLoader(),
      new Class[]{ContextInternal.class},
      (proxy, method, args) -> {
        String name = method.getName();
        if ("tracer".equals(name)) {
          return tracer;
        }
        if ("isEventLoopContext".equals(name) || "isWorkerContext".equals(name) || "isMultiThreadedWorkerContext".equals(name)) {
          return Boolean.FALSE;
        }
        if ("isMetricEnabled".equals(name)) {
          return Boolean.FALSE;
        }
        if ("<init>".equals(name)) {
          return null;
        }
        if ("getTracingPolicy".equals(name) || "tracingPolicy".equals(name) || "tracingpolicy".equals(name)) {
          return policy;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive()) {
          if (returnType == boolean.class) {
            return false;
          }
          if (returnType == int.class) {
            return 0;
          }
          if (returnType == long.class) {
            return 0L;
          }
          if (returnType == float.class) {
            return 0f;
          }
          if (returnType == double.class) {
            return 0d;
          }
          if (returnType == short.class) {
            return (short) 0;
          }
          if (returnType == byte.class) {
            return (byte) 0;
          }
          if (returnType == char.class) {
            return (char) 0;
          }
        }
        return null;
      }
    );
  }

  private static final class RecordingTracer {
    final Object traceToken = new Object();
    final List<MongoTracerRequest> requests = new ArrayList<>();
    final List<String> operations = new ArrayList<>();
    final List<TagExtractor<?>> requestExtractors = new ArrayList<>();
    final List<TagExtractor<?>> responseExtractors = new ArrayList<>();
    final List<BiConsumer<String, String>> headerInjectors = new ArrayList<>();
    final List<Object> traces = new ArrayList<>();
    final List<Throwable> failures = new ArrayList<>();
    final VertxTracer tracer;

    RecordingTracer(boolean returnTrace) {
      tracer = (VertxTracer) Proxy.newProxyInstance(
        VertxTracer.class.getClassLoader(),
        new Class[]{VertxTracer.class},
        (proxy, method, args) -> {
          String name = method.getName();
          if ("sendRequest".equals(name)) {
            requests.add((MongoTracerRequest) args[3]);
            operations.add((String) args[4]);
            //noinspection unchecked
            headerInjectors.add((BiConsumer<String, String>) args[5]);
            requestExtractors.add((TagExtractor<?>) args[6]);
            return returnTrace ? traceToken : null;
          }
          if ("receiveResponse".equals(name)) {
            traces.add(args[2]);
            failures.add((Throwable) args[3]);
            responseExtractors.add((TagExtractor<?>) args[4]);
            return null;
          }
          if (method.getReturnType().isPrimitive()) {
            if (method.getReturnType() == boolean.class) {
              return false;
            }
            if (method.getReturnType() == int.class) {
              return 0;
            }
            if (method.getReturnType() == long.class) {
              return 0L;
            }
            if (method.getReturnType() == float.class) {
              return 0f;
            }
            if (method.getReturnType() == double.class) {
              return 0d;
            }
            if (method.getReturnType() == short.class) {
              return (short) 0;
            }
            if (method.getReturnType() == byte.class) {
              return (byte) 0;
            }
            if (method.getReturnType() == char.class) {
              return (char) 0;
            }
          }
          return null;
        }
      );
    }
  }

  private static final class TestPublisher<T> implements Publisher<T> {
    private final T item;
    private final Throwable failure;
    private Subscriber<? super T> subscriber;

    private TestPublisher(T item, Throwable failure) {
      this.item = item;
      this.failure = failure;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
      if (subscriber != null) {
        s.onError(new IllegalStateException("Only a single subscriber is supported"));
        return;
      }
      subscriber = s;
      s.onSubscribe(new Subscription() {
        private boolean done;

        @Override
        public void request(long n) {
          if (done) {
            return;
          }
          done = true;
          if (failure != null) {
            s.onError(failure);
          } else {
            if (item != null) {
              s.onNext(item);
            }
            s.onComplete();
          }
        }

        @Override
        public void cancel() {
          done = true;
        }
      });
    }
  }

  private static final class TestSubscriber<T> implements Subscriber<T> {
    private final List<T> items = new ArrayList<>();
    private Throwable failure;
    private boolean completed;
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription s) {
      subscription = s;
    }

    @Override
    public void onNext(T t) {
      items.add(t);
    }

    @Override
    public void onError(Throwable t) {
      failure = t;
    }

    @Override
    public void onComplete() {
      completed = true;
    }

    void request(long n) {
      subscription.request(n);
    }
  }
}
