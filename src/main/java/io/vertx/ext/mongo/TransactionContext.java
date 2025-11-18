package io.vertx.ext.mongo;

import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import io.vertx.core.Future;

import java.util.function.BiFunction;

public interface TransactionContext {
  TransactionContext withOperation(BiFunction<MongoClient, ClientSession, Future<?>> operation);
  Future<Void> commit();
  Future<Void> abort();
}
