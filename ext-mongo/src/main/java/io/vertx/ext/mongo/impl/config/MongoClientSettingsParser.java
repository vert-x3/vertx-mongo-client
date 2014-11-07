package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SSLSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoClientSettingsParser {

  private final MongoClientSettings settings;

  public MongoClientSettingsParser(JsonObject config) {
    Objects.requireNonNull(config);

    MongoClientSettings.Builder settings = MongoClientSettings.builder();

    // All parsers should support connection_string first
    String cs = config.getString("connection_string");
    ConnectionString connectionString = (cs == null) ? null : new ConnectionString(cs);

    // ClusterSettings
    ClusterSettings clusterSettings = new ClusterSettingsParser(connectionString, config).settings();
    settings.clusterSettings(clusterSettings);

    // ConnectionPoolSettings
    ConnectionPoolSettings connectionPoolSettings = new ConnectionPoolSettingsParser(connectionString, config).settings();
    settings.connectionPoolSettings(connectionPoolSettings);

    // Credentials
    List<MongoCredential> credentials = new CredentialListParser(connectionString, config).credentials();
    settings.credentialList(credentials);

    // SocketSettings
    SocketSettings socketSettings = new SocketSettingsParser(connectionString, config).settings();
    settings.socketSettings(socketSettings);

    // Heartbeat SocketSettings
    JsonObject hbConfig = config.getJsonObject("heartbeat.socket");
    if (hbConfig != null) {
      SocketSettings heartBetaSocketSettings = new SocketSettingsParser(null, hbConfig).settings();
      settings.socketSettings(heartBetaSocketSettings);
    }

    // ServerSettings
    ServerSettings serverSettings = new ServerSettingsParser(config).settings();
    settings.serverSettings(serverSettings);

    // SSLSettings
    SSLSettings sslSettings = new SSLSettingsParser(connectionString, config).settings();
    settings.sslSettings(sslSettings);

    // WriteConcern
    WriteConcern writeConcern = new WriteConcernParser(config).writeConcern();
    if (writeConcern != null) {
      settings.writeConcern(writeConcern);
    }

    // ReadPreference
    ReadPreference readPreference = new ReadPreferenceParser(config).readPreference();
    if (readPreference != null) {
      settings.readPreference(readPreference);
    }

    this.settings = settings.build();
  }

  public MongoClientSettings settings() {
    return settings;
  }
}
