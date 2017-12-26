package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

class ReadConcernLevelParser {

  private final ConnectionString connectionString;
  private final JsonObject config;

  ReadConcernLevelParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;
  }

  Optional<ReadConcern> readConcern() {
    return tryToParseFromConnectionString().map(this::lift).orElseGet(this::tryToParseFromConfig);
  }

  private Optional<ReadConcern> lift(ReadConcern readConcern) {
    return Optional.ofNullable(readConcern);
  }

  private Optional<ReadConcern> tryToParseFromConnectionString() {
    return Optional.ofNullable(connectionString)
      .flatMap(cs -> Optional.ofNullable(cs.getReadConcern()));
  }

  private Optional<ReadConcern> tryToParseFromConfig() {
    return Optional.ofNullable(config)
      .flatMap(cfg -> Optional.ofNullable(cfg.getString("readConcernLevel")))
      .map(ReadConcernLevel::fromString)
      .map(ReadConcern::new);
  }
}
