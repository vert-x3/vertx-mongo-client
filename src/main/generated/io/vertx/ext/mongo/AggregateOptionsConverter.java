/*
 * Copyright (c) 2014 Red Hat, Inc. and others
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

package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;

/**
 * Converter for {@link io.vertx.ext.mongo.AggregateOptions}.
 * <p>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.AggregateOptions} original class using Vert.x codegen.
 */
public class AggregateOptionsConverter {

  public static void fromJson(JsonObject json, AggregateOptions obj) {
    if (json.getValue("allowDiskUse") instanceof Boolean) {
      obj.setAllowDiskUse((Boolean) json.getValue("allowDiskUse"));
    }
    if (json.getValue("batchSize") instanceof Number) {
      obj.setBatchSize(((Number) json.getValue("batchSize")).intValue());
    }
    if (json.getValue("maxAwaitTime") instanceof Number) {
      obj.setMaxAwaitTime(((Number) json.getValue("maxAwaitTime")).longValue());
    }
    if (json.getValue("maxTime") instanceof Number) {
      obj.setMaxTime(((Number) json.getValue("maxTime")).longValue());
    }
  }

  public static void toJson(AggregateOptions obj, JsonObject json) {
    if (obj.getAllowDiskUse() != null) {
      json.put("allowDiskUse", obj.getAllowDiskUse());
    }
    json.put("batchSize", obj.getBatchSize());
    json.put("maxAwaitTime", obj.getMaxAwaitTime());
    json.put("maxTime", obj.getMaxTime());
  }
}
