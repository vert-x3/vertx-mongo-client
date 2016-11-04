package io.vertx.groovy.ext.mongo;
public class GroovyStaticExtension {
  public static io.vertx.ext.mongo.MongoClient createNonShared(io.vertx.ext.mongo.MongoClient j_receiver, io.vertx.core.Vertx vertx, java.util.Map<String, Object> config) {
    return io.vertx.lang.groovy.ConversionHelper.wrap(io.vertx.ext.mongo.MongoClient.createNonShared(vertx,
      config != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(config) : null));
  }
  public static io.vertx.ext.mongo.MongoClient createShared(io.vertx.ext.mongo.MongoClient j_receiver, io.vertx.core.Vertx vertx, java.util.Map<String, Object> config, java.lang.String dataSourceName) {
    return io.vertx.lang.groovy.ConversionHelper.wrap(io.vertx.ext.mongo.MongoClient.createShared(vertx,
      config != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(config) : null,
      dataSourceName));
  }
  public static io.vertx.ext.mongo.MongoClient createShared(io.vertx.ext.mongo.MongoClient j_receiver, io.vertx.core.Vertx vertx, java.util.Map<String, Object> config) {
    return io.vertx.lang.groovy.ConversionHelper.wrap(io.vertx.ext.mongo.MongoClient.createShared(vertx,
      config != null ? io.vertx.lang.groovy.ConversionHelper.toJsonObject(config) : null));
  }
}
