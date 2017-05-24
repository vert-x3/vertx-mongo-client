package io.vertx.kotlin.ext.mongo.bulk

import io.vertx.ext.mongo.bulk.BulkOperationType

fun BulkOperationType(
  document: io.vertx.core.json.JsonObject? = null,
  filter: io.vertx.core.json.JsonObject? = null,
  multi: Boolean? = null,
  type: String? = null,
  upsert: Boolean? = null): BulkOperationType = io.vertx.ext.mongo.bulk.BulkOperationType(io.vertx.core.json.JsonObject()).apply {

  if (document != null) {
    this.setDocument(document)
  }
  if (filter != null) {
    this.setFilter(filter)
  }
  if (multi != null) {
    this.setMulti(multi)
  }
  if (type != null) {
    this.setType(type)
  }
  if (upsert != null) {
    this.setUpsert(upsert)
  }
}

