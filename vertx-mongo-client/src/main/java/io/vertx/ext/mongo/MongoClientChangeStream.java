package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Closeable;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

public interface MongoClientChangeStream<T> extends ReadStream<T>, Closeable {
  /**
   * Closes the cursor. It will not be resumable nor will it receive more changes.
   *
   * @param completionHandler called when the cursor is closed.
   */
  @Override
  void close(Handler<AsyncResult<Void>> completionHandler);

  /**
   * Returns the last resume token that a change returned.
   * <p>
   * Use this with {@link WatchOptions#resumeAfter(JsonObject)} after {@link #close(Handler)} to replay all missed
   * changes since {@link #close(Handler)} was called. The replaying occurs on the database.
   *
   * @return a resume token object.
   */
  JsonObject lastResumeToken();

  /**
   * Pauses the stream of changes from the client's point of view. This instance will queue change notifications, and
   * all will be delivered when {@link #resume()} is called.
   * <p>
   * To stop receiving messages from the database, call {@link #close(Handler)}. Then, call
   * {@link MongoClient#watch(String, JsonArray, WatchOptions, Handler)} with {@link WatchOptions#resumeAfter(JsonObject)}
   * set to the {@link #lastResumeToken()} of the closed instance.
   */
  @Override
  @Fluent
  ReadStream<T> pause();

  /**
   * Resumes a paused change stream, playing back all the changes that were delivered to this instance.
   *
   * @see #pause() for more on different ways to pause and resume a change stream.
   */
  @Override
  @Fluent
  ReadStream<T> resume();
}
