package io.vertx.ext.mongo.tests.impl;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.impl.PublisherAdapter;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PublisherAdapterTest {

  private Vertx vertx;
  private Context context;

  @Before
  public void before() {
    vertx = Vertx.vertx();
    context = vertx.getOrCreateContext();
  }

  @Test
  public void testSubscribe() {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<Integer>(context, publisher, 5);
    adapter.handler(h -> {
      fail();
    });
    assertNotNull(publisher.subscriber);
    assertEquals(5, publisher.subscription.requested);
    assertEquals(1, publisher.subscription.requestedTimes);
  }

  @Test
  public void testPaused() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<Integer>(context, publisher, 5);
    adapter.pause();
    adapter.handler(h -> {
      fail();
    });
    publisher.subscriber.onNext(0);
    publisher.subscriber.onNext(1);
    publisher.subscriber.onNext(2);
    publisher.subscriber.onNext(3);
    assertEquals(5, publisher.subscription.requested);
    assertEquals(1, publisher.subscription.requestedTimes);
  }

  @Test
  public void testFill() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<Integer>(context, publisher, 5);
    adapter.pause();
    adapter.handler(h -> {
      fail();
    });
    int seq = 0;
    while (publisher.subscription.requested-- > 0) {
      publisher.subscriber.onNext(seq++);
    }
  }

  @Test
  public void testResume() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<Integer>(context, publisher, 5);
    adapter.pause();
    AtomicInteger count = new AtomicInteger();
    adapter.handler(h -> {
      count.incrementAndGet();
    });
    int seq = 0;
    while (publisher.subscription.requested-- > 0) {
      publisher.subscriber.onNext(seq++);
    }
    adapter.resume();
    while (count.get() < seq) {
      Thread.sleep(1);
    }
    assertTrue(publisher.subscription.requested > 0);
  }

  @Test
  public void testComplete() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<>(context, publisher, 5);
    adapter.resume();
    adapter.handler(h -> {
    });
    CountDownLatch latch = new CountDownLatch(1);
    adapter.endHandler(v -> {
      latch.countDown();
    });
    publisher.subscriber.onComplete();
    assertTrue(latch.await(20, TimeUnit.SECONDS));
  }

  @Test
  public void testError() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<>(context, publisher, 5);
    adapter.resume();
    adapter.handler(h -> {
    });
    Throwable cause = new Throwable();
    CountDownLatch latch = new CountDownLatch(1);
    adapter.exceptionHandler(err -> {
      latch.countDown();
    });
    publisher.subscriber.onError(cause);
    assertTrue(latch.await(20, TimeUnit.SECONDS));
  }

  @Test
  public void testUnsubscribe() throws Exception {
    MyPublisher<Integer> publisher = new MyPublisher<>();
    PublisherAdapter<Integer> adapter = new PublisherAdapter<Integer>(context, publisher, 5);
    adapter.handler(h -> {
    });
    assertNotNull(publisher.subscriber);
    adapter.handler(null);
    assertNull(publisher.subscriber);
    adapter.handler(h -> {
    });
    assertNotNull(publisher.subscriber);
  }

  class MySubscription implements Subscription {

    long requested;
    int requestedTimes;
    final MyPublisher publisher;

    MySubscription(MyPublisher publisher) {
      this.publisher = publisher;
    }

    @Override
    public void request(long n) {
      requestedTimes++;
      requested += n;
    }

    @Override
    public void cancel() {
      publisher.subscription = null;
      publisher.subscriber = null;
    }
  }


  class MyPublisher<T> implements Publisher<T> {

    MySubscription subscription;
    Subscriber<? super T> subscriber;

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
      MySubscription subscription = new MySubscription(this);
      this.subscriber = subscriber;
      this.subscription = subscription;
      subscriber.onSubscribe(subscription);
    }
  }
}
