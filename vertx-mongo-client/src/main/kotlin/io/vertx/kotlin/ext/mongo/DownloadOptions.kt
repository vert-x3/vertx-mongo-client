package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.DownloadOptions

/**
 * A function providing a DSL for building [io.vertx.ext.mongo.DownloadOptions] objects.
 *
 * Options used to configure downloads from GridFS.
 *
 * @param revision 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.mongo.DownloadOptions original] using Vert.x codegen.
 */
fun DownloadOptions(
  revision: Int? = null): DownloadOptions = io.vertx.ext.mongo.DownloadOptions().apply {

  if (revision != null) {
    this.setRevision(revision)
  }
}

