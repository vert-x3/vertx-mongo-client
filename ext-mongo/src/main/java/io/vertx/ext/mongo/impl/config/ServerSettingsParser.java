package io.vertx.ext.mongo.impl.config;

import com.mongodb.connection.ServerSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class ServerSettingsParser {
  private final ServerSettings settings;

  public ServerSettingsParser(JsonObject config) {
    ServerSettings.Builder settings = ServerSettings.builder();

    Long heartbeatFrequencyMS = config.getLong("heartbeatFrequencyMS");
    if (heartbeatFrequencyMS != null) {
      settings.heartbeatFrequency(heartbeatFrequencyMS, MILLISECONDS);
    }
    Long minHeartbeatFrequencyMS = config.getLong("minHeartbeatFrequencyMS");
    if (minHeartbeatFrequencyMS != null) {
      settings.minHeartbeatFrequency(minHeartbeatFrequencyMS, MILLISECONDS);
    }

    this.settings = settings.build();
  }

  public ServerSettings settings() {
    return settings;
  }
}
