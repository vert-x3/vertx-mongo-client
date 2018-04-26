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
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.ext.mongo.FindOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.mongo.FindOptions} original class using Vert.x codegen.
 */
public class FindOptionsConverter {

  public static void fromJson(JsonObject json, FindOptions obj) {
    if (json.getValue("batchSize") instanceof Number) {
      obj.setBatchSize(((Number)json.getValue("batchSize")).intValue());
    }
    if (json.getValue("fields") instanceof JsonObject) {
      obj.setFields(((JsonObject)json.getValue("fields")).copy());
    }
    if (json.getValue("limit") instanceof Number) {
      obj.setLimit(((Number)json.getValue("limit")).intValue());
    }
    if (json.getValue("skip") instanceof Number) {
      obj.setSkip(((Number)json.getValue("skip")).intValue());
    }
    if (json.getValue("sort") instanceof JsonObject) {
      obj.setSort(((JsonObject)json.getValue("sort")).copy());
    }
  }

  public static void toJson(FindOptions obj, JsonObject json) {
    json.put("batchSize", obj.getBatchSize());
    if (obj.getFields() != null) {
      json.put("fields", obj.getFields());
    }
    json.put("limit", obj.getLimit());
    json.put("skip", obj.getSkip());
    if (obj.getSort() != null) {
      json.put("sort", obj.getSort());
    }
  }
}