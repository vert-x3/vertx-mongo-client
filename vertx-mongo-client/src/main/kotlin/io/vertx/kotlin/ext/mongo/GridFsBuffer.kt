package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.GridFsBuffer

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.GridFsBuffer] objects.
 *
 * Wrapper for a buffer used by GridFs.
 *
 * @param buffer 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.GridFsBuffer original] using Vert.x codegen.
 */
fun GridFsBuffer(
  buffer: io.vertx.core.buffer.Buffer? = null): GridFsBuffer = io.vertx.ext.mongo.GridFsBuffer().apply {

  if (buffer != null) {
    this.setBuffer(buffer)
  }
}

