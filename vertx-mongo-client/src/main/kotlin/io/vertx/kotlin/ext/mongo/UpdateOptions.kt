package io.vertx.kotlin.ext.mongo

import io.vertx.ext.mongo.UpdateOptions
import io.vertx.ext.mongo.WriteOption

fun UpdateOptions(
        multi: Boolean? = null,
    returningNewDocument: Boolean? = null,
    upsert: Boolean? = null,
    writeOption: WriteOption? = null): UpdateOptions = io.vertx.ext.mongo.UpdateOptions().apply {

    if (multi != null) {
        this.isMulti = multi
    }

    if (returningNewDocument != null) {
        this.isReturningNewDocument = returningNewDocument
    }

    if (upsert != null) {
        this.isUpsert = upsert
    }

    if (writeOption != null) {
        this.writeOption = writeOption
    }

}

