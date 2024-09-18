package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ClusterSettingsParser {

  private final ClusterSettings settings;

  public ClusterSettingsParser(ConnectionString connectionString, JsonObject config) {
    ClusterSettings.Builder settings = ClusterSettings.builder();

    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    }

    // hosts
    List<ServerAddress> hosts = parseHosts(connectionString, config);
    if (!hosts.isEmpty()) {
      settings.hosts(hosts);

      // replica set / mode
      String replicaSet = config.getString("replicaSet");
      if (hosts.size() == 1 && replicaSet == null) {
        settings.mode(ClusterConnectionMode.SINGLE);
      } else {
        settings.mode(ClusterConnectionMode.MULTIPLE);
      }
      if (replicaSet != null) {
        settings.requiredReplicaSetName(replicaSet);
      }
    }

    // serverSelectionTimeoutMS
    Long serverSelectionTimeoutMS = config.getLong("serverSelectionTimeoutMS");
    if (serverSelectionTimeoutMS != null) {
      settings.serverSelectionTimeout(serverSelectionTimeoutMS, MILLISECONDS);
    }

    this.settings = settings.build();
  }

  public ClusterSettings settings() {
    return settings;
  }

  private static List<ServerAddress> parseHosts(ConnectionString connectionString, JsonObject config) {
    List<ServerAddress> hosts = new ArrayList<>();
    JsonArray jsonHosts = config.getJsonArray("hosts");
    if (jsonHosts != null) {
      for (Object jsonHost : jsonHosts) {
        ServerAddress address = serverAddress((JsonObject) jsonHost);
        if (address != null) {
          hosts.add(address);
        }
      }
    } else {
      // Support host / port properties
      ServerAddress address = serverAddress(config);
      if (address != null) {
        hosts.add(address);
      }
    }
    if (hosts.isEmpty() && connectionString == null) {
      hosts.add(new ServerAddress());
    }
    return hosts;
  }

  private static ServerAddress serverAddress(JsonObject json) {
    if (json == null) return null;

    String host = json.getString("host");
    Integer port = json.getInteger("port");
    if (host == null) {
      return null;
    } else {
      if (port == null) {
        return new ServerAddress(host);
      }
      return new ServerAddress(host, port);
    }
  }
}
