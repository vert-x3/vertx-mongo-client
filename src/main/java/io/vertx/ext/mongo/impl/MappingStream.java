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

import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.Objects;
import java.util.function.Function;

public class MappingStream<I, O> implements ReadStream<O> {

  private final ReadStream<I> input;
  private final Function<I, O> mapper;

  public MappingStream(ReadStream<I> input, Function<I, O> mapper) {
    Objects.requireNonNull(input, "input is null");
    Objects.requireNonNull(mapper, "mapper is null");
    this.input = input;
    this.mapper = mapper;
  }

  @Override
  public ReadStream<O> exceptionHandler(Handler<Throwable> handler) {
    input.exceptionHandler(handler);
    return this;
  }

  @Override
  public ReadStream<O> handler(Handler<O> handler) {
    if (handler != null) {
      input.handler(event -> handler.handle(mapper.apply(event)));
    } else {
      input.handler(null);
    }
    return this;
  }

  @Override
  public ReadStream<O> pause() {
    input.pause();
    return this;
  }

  @Override
  public ReadStream<O> resume() {
    input.resume();
    return this;
  }

  @Override
  public ReadStream<O> fetch(long amount) {
    input.fetch(amount);
    return this;
  }

  @Override
  public ReadStream<O> endHandler(Handler<Void> endHandler) {
    input.endHandler(endHandler);
    return this;
  }
}
