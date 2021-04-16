package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import io.vertx.core.json.JsonObject;

class ReadConcernLevelParser {

  private final ReadConcern readConcern;

  ReadConcernLevelParser(ConnectionString connectionString, JsonObject config) {
    ReadConcern readConcern = fromConfig(config);
    if (readConcern == null && connectionString != null) {
      readConcern = connectionString.getReadConcern();
    }
    this.readConcern = readConcern;
  }

  private ReadConcern fromConfig(JsonObject config) {
    String readConcernLevel = config.getString("readConcernLevel");
    return readConcernLevel != null ? new ReadConcern(ReadConcernLevel.fromString(readConcernLevel)) : null;
  }

  ReadConcern readConcern() {
    return readConcern;
  }
}
