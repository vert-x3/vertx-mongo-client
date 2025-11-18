package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * A Vert.x service used to interact with MongoDB server instances.
 * <p>
 * Some of the operations might change <i>_id</i> field of passed {@link JsonObject} document.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface MongoTransactionalClient {

  Future<MongoClient> getClient();
  Future<Void> commit();
  Future<Void> abort();

}
