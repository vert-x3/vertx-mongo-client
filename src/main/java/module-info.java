module io.vertx.mongo.client {

  requires static io.vertx.docgen;
  requires static io.vertx.codegen.api;
  requires static io.vertx.codegen.json;

  requires io.vertx.core;
  requires io.vertx.core.logging;
  requires org.mongodb.bson;
  requires org.mongodb.driver.core;
  requires org.mongodb.driver.reactivestreams;
  requires org.reactivestreams;
  requires io.netty.buffer;
  requires io.netty.transport;
  requires io.netty.common;

  exports io.vertx.ext.mongo;

}
