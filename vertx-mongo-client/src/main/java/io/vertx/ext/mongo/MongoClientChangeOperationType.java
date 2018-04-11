package io.vertx.ext.mongo;

public enum MongoClientChangeOperationType {
  INSERT,
  UPDATE,
  REPLACE,
  DELETE,
  INVALIDATE;

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
