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

import io.vertx.core.Promise;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MappingAndBufferingSubscriber<T, R> implements Subscriber<T> {

  private final List<R> received = new ArrayList<>();
  private final Function<T, R> converter;
  private final Promise<List<R>> promise;

  public MappingAndBufferingSubscriber(Function<T, R> converter, Promise<List<R>> promise) {
    Objects.requireNonNull(converter, "converter is null");
    Objects.requireNonNull(promise, "promise is null");
    this.promise = promise;
    this.converter = converter;
  }

  @Override
  public void onSubscribe(Subscription s) {
    s.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(T t) {
    received.add(converter.apply(t));
  }

  @Override
  public void onError(Throwable t) {
    promise.fail(t);
  }

  @Override
  public void onComplete() {
    promise.complete(received);
  }
}
