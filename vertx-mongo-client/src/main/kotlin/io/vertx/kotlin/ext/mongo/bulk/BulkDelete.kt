package io.vertx.kotlin.ext.mongo.bulk

import io.vertx.ext.mongo.bulk.BulkDelete

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.bulk.BulkDelete] objects.
 *
 * Delete operation for bulk write operations. Deletes one or more documents that match the filter, depending on the
 * "multi" configuration.
 *
 * @param filter  Set the filter for this delete operation.
 * @param multi  Set if the operation should delete multiple document, or only the first matching document
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.bulk.BulkDelete original] using Vert.x codegen.
 */
fun BulkDelete(
  filter: io.vertx.core.json.JsonObject? = null,
  multi: Boolean? = null): BulkDelete = io.vertx.ext.mongo.bulk.BulkDelete(io.vertx.core.json.JsonObject()).apply {

  if (filter != null) {
    this.setFilter(filter)
  }
  if (multi != null) {
    this.setMulti(multi)
  }
}

