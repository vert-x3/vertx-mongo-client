package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.codec.VertxCodecRegistry;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoClientOptionsParser {

  private final MongoClientSettings settings;

  public MongoClientOptionsParser(JsonObject config) {
    Objects.requireNonNull(config);

    MongoClientSettings.Builder options = MongoClientSettings.builder();
    options.codecRegistry(new VertxCodecRegistry());

    // All parsers should support connection_string first
    String cs = config.getString("connection_string");
    ConnectionString connectionString = (cs == null) ? null : new ConnectionString(cs);

    // ClusterSettings
    ClusterSettings clusterSettings = new ClusterSettingsParser(connectionString, config).settings();
    options.clusterSettings(clusterSettings);

    // ConnectionPoolSettings
    ConnectionPoolSettings connectionPoolSettings = new ConnectionPoolSettingsParser(connectionString, config).settings();
    options.connectionPoolSettings(connectionPoolSettings);

    // Credentials
    List<MongoCredential> credentials = new CredentialListParser(connectionString, config).credentials();
    options.credentialList(credentials);

    // SocketSettings
    SocketSettings socketSettings = new SocketSettingsParser(connectionString, config).settings();
    options.socketSettings(socketSettings);

    // Heartbeat SocketSettings
    JsonObject hbConfig = config.getJsonObject("heartbeat.socket");
    if (hbConfig != null) {
      SocketSettings heartBetaSocketSettings = new SocketSettingsParser(null, hbConfig).settings();
      options.socketSettings(heartBetaSocketSettings);
    }

    // ServerSettings
    ServerSettings serverSettings = new ServerSettingsParser(config).settings();
    options.serverSettings(serverSettings);

    // SSLSettings
    SslSettings sslSettings = new SSLSettingsParser(connectionString, config).settings();
    options.sslSettings(sslSettings);

    // WriteConcern
    WriteConcern writeConcern = new WriteConcernParser(config).writeConcern();
    if (writeConcern != null) {
      options.writeConcern(writeConcern);
    }

    // ReadPreference
    ReadPreference readPreference = new ReadPreferenceParser(config).readPreference();
    if (readPreference != null) {
      options.readPreference(readPreference);
    }

    this.settings = options.build();
  }

  public MongoClientSettings settings() {
    return settings;
  }
}
