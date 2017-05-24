package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.BulkOperation.BulkOperationType

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.BulkOperation] objects.
 *
 * Contains all data needed for one operation of a bulk write operation.
 *
 * @param document 
 * @param filter 
 * @param multi 
 * @param type 
 * @param upsert 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.BulkOperation original] using Vert.x codegen.
 */
fun BulkOperation(
  document: io.vertx.core.json.JsonObject? = null,
  filter: io.vertx.core.json.JsonObject? = null,
  multi: Boolean? = null,
  type: BulkOperationType? = null,
  upsert: Boolean? = null): BulkOperation = io.vertx.ext.mongo.BulkOperation(io.vertx.core.json.JsonObject()).apply {

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

