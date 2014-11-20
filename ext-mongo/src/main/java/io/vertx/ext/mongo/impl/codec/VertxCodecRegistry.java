/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.mongo.impl.codec;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class VertxCodecRegistry implements CodecRegistry {
  private Codec<JsonObject> jsonObjectCodec = new JsonObjectCodec();

  @Override
  @SuppressWarnings("unchecked")
  public <T> Codec<T> get(Class<T> clazz) {
    if (clazz == JsonObject.class) {
      return (Codec<T>) jsonObjectCodec;
    } else {
      throw new IllegalArgumentException("No codec support for type " + clazz);
    }
  }
}
