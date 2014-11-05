package io.vertx.ext.mongo.impl.config;

import com.mongodb.connection.ServerSettings;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class ServerSettingsParser extends AbstractParser {
  private final ServerSettings settings;

  public ServerSettingsParser(JsonObject config) {
    ServerSettings.Builder settings = ServerSettings.builder();

    Long heartbeatFrequencyMS = get(config, "heartbeatFrequencyMS", Long.class);
    if (heartbeatFrequencyMS != null) {
      settings.heartbeatFrequency(heartbeatFrequencyMS, MILLISECONDS);
    }
    Long minHeartbeatFrequencyMS = get(config, "minHeartbeatFrequencyMS", Long.class);
    if (minHeartbeatFrequencyMS != null) {
      settings.minHeartbeatFrequency(minHeartbeatFrequencyMS, MILLISECONDS);
    }

    this.settings = settings.build();
  }

  public ServerSettings settings() {
    return settings;
  }
}
