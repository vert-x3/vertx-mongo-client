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
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

public class FailedStream implements ReadStream<JsonObject> {
  private final ClassNotFoundException e;

  public FailedStream(ClassNotFoundException e) {
    this.e = e;
  }

  @Override
  public ReadStream<JsonObject> exceptionHandler(Handler<Throwable> handler) {
    handler.handle(e);
    return this;
  }

  @Override
  public ReadStream<JsonObject> handler(Handler<JsonObject> handler) {
    return this;
  }

  @Override
  public ReadStream<JsonObject> pause() {
    return this;
  }

  @Override
  public ReadStream<JsonObject> fetch(long l) {
    return this;
  }

  @Override
  public ReadStream<JsonObject> resume() {
    return this;
  }

  @Override
  public ReadStream<JsonObject> endHandler(Handler<Void> endHandler) {
    return this;
  }
}
