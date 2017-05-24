package io.vertx.kotlin.ext.mongo.bulk

import io.vertx.ext.mongo.bulk.BulkInsert

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.bulk.BulkInsert] objects.
 *
 * Insert operation for bulk operations. Inserts one document.
 *
 * @param document  Set the document that should be inserted
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.bulk.BulkInsert original] using Vert.x codegen.
 */
fun BulkInsert(
  document: io.vertx.core.json.JsonObject? = null): BulkInsert = io.vertx.ext.mongo.bulk.BulkInsert(io.vertx.core.json.JsonObject()).apply {

  if (document != null) {
    this.setDocument(document)
  }
}

