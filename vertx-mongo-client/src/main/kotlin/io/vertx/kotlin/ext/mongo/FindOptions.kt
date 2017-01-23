package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.FindOptions

fun FindOptions(
    fields: io.vertx.core.json.JsonObject? = null,
  limit: Int? = null,
  skip: Int? = null,
  sort: io.vertx.core.json.JsonObject? = null): FindOptions = io.vertx.ext.mongo.FindOptions().apply {

  if (fields != null) {
    this.fields = fields
  }

  if (limit != null) {
    this.limit = limit
  }

  if (skip != null) {
    this.skip = skip
  }

  if (sort != null) {
    this.sort = sort
  }

}

