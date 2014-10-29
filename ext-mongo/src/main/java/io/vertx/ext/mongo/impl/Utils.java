package io.vertx.ext.mongo.impl;

import com.mongodb.ConnectionString;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SSLSettings;
import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class Utils {

  public static WriteConcern writeConcern(JsonObject json) {
    String writeConcern = json.getString("writeConcern");

    return (writeConcern == null) ? null : WriteConcern.valueOf(writeConcern);
  }

  public static ReadPreference readPreference(JsonObject json) {
    String readPreference = json.getString("readPreference");

    return (readPreference == null) ? null : ReadPreference.valueOf(readPreference);
  }

  // Take a mongo URI connection string and apply it for the settings of the MongoClient (the api should handle this tbh)
  public static void applyConnectionString(MongoClientSettings.Builder settings, String uri) {
    ConnectionString connectionString = new ConnectionString(uri);
    settings.clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
      .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
      .credentialList(connectionString.getCredentialList())
      .sslSettings(SSLSettings.builder().applyConnectionString(connectionString).build())
      .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build());
  }
}
