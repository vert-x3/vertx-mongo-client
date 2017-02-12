package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.UploadOptions

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.UploadOptions] objects.
 *
 * Options used to configure file uploads to gridfs.
 *
 * @param chunkSizeBytes 
 * @param metadata 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.UploadOptions original] using Vert.x codegen.
 */
fun UploadOptions(
  chunkSizeBytes: Int? = null,
  metadata: io.vertx.core.json.JsonObject? = null): UploadOptions = io.vertx.ext.mongo.UploadOptions().apply {

  if (chunkSizeBytes != null) {
    this.setChunkSizeBytes(chunkSizeBytes)
  }
  if (metadata != null) {
    this.setMetadata(metadata)
  }
}

