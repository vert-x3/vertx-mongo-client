package io.vertx.ext.mongo.impl.config;

import com.mongodb.*;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoClientOptionsParser {

  private final static CodecRegistry commonCodecRegistry = CodecRegistries.fromCodecs(new StringCodec(), new IntegerCodec(),
    new BooleanCodec(), new DoubleCodec(), new LongCodec(), new BsonDocumentCodec());
  private final MongoClientSettings settings;
  private final String database;

  public MongoClientOptionsParser(final Vertx vertx, final JsonObject config) {
    Objects.requireNonNull(config);

    MongoClientSettings.Builder options = MongoClientSettings.builder();
    options.codecRegistry(CodecRegistries.fromRegistries(commonCodecRegistry, CodecRegistries.fromCodecs(new JsonObjectCodec(config))));

    // All parsers should support connection_string first
    String cs = config.getString("connection_string");
    ConnectionString connectionString = (cs == null) ? null : new ConnectionString(cs);
    String csDatabase = (connectionString != null) ? connectionString.getDatabase() : null;
    this.database = csDatabase != null ? csDatabase : config.getString("db_name", MongoClient.DEFAULT_DB_NAME);

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

    // Transport type
    new StreamTypeParser(connectionString, config).streamFactory().ifPresent(options::streamFactoryFactory);

    // SSLSettings
    SslSettings sslSettings = new SSLSettingsParser(connectionString, config).settings(vertx);
    options.sslSettings(sslSettings);

    // WriteConcern
    WriteConcern writeConcern = new WriteConcernParser(connectionString, config).writeConcern();
    if (writeConcern != null) {
      options.writeConcern(writeConcern);
    }

    // ReadConcern
    maybeReadConcern(connectionString, config).ifPresent(options::readConcern);

    // ReadPreference
    ReadPreference readPreference = new ReadPreferenceParser(connectionString, config).readPreference();
    if (readPreference != null) {
      options.readPreference(readPreference);
    }

    // Heartbeat SocketSettings
    JsonObject hbConfig = config.getJsonObject("heartbeat.socket");
    if (hbConfig != null) {
      SocketSettings heartBeatSocketSettings = new SocketSettingsParser(null, hbConfig).settings();
      options.heartbeatSocketSettings(heartBeatSocketSettings);
    }

    // ServerSettings
    ServerSettings serverSettings = new ServerSettingsParser(config).settings();
    options.serverSettings(serverSettings);

    this.settings = options.build();
  }

  public MongoClientSettings settings() {
    return settings;
  }

  public String database() {
    return database;
  }

  private Optional<ReadConcern> maybeReadConcern(ConnectionString connectionString, JsonObject config) {
    return new ReadConcernLevelParser(connectionString, config).readConcern();
  }
}
