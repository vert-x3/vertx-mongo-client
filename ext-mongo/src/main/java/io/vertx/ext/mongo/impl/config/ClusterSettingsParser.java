package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ClusterType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class ClusterSettingsParser {

  private final ClusterSettings settings;

  public ClusterSettingsParser(ConnectionString connectionString, JsonObject config) {
    ClusterSettings.Builder settings = ClusterSettings.builder();
    // ConnectionString takes precedence
    if (connectionString != null) {
      settings.applyConnectionString(connectionString);
    } else {
      // hosts
      List<ServerAddress> hosts = parseHosts(config);
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

    this.settings = settings.build();
  }

  public ClusterSettings settings() {
    return settings;
  }

  private static List<ServerAddress> parseHosts(JsonObject config) {
    List<ServerAddress> hosts = new ArrayList<>();
    JsonArray jsonHosts = config.getJsonArray("hosts");
    if (jsonHosts != null) {
      jsonHosts.forEach(jsonHost -> {
        ServerAddress address = serverAddress((JsonObject) jsonHost);
        if (address != null) {
          hosts.add(address);
        }
      });
    } else {
      // Support host / port properties and if not present use default ServerAddress (127.0.0.1:27017)
      ServerAddress address = serverAddress(config);
      hosts.add(address == null ? new ServerAddress() : address);
    }

    return hosts;
  }

  private static ServerAddress serverAddress(JsonObject json) {
    if (json == null) return null;

    String host = json.getString("host");
    Integer port = json.getInteger("port");
    if (host == null && port == null) {
      return null;
    } else {
      return new ServerAddress(host, port);
    }
  }
}
