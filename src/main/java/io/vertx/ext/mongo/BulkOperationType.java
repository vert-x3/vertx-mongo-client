package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;

/**
 * Enum for the different possible operations
 */
@VertxGen
public enum BulkOperationType {
  UPDATE,
  REPLACE,
  INSERT,
  DELETE;
}
