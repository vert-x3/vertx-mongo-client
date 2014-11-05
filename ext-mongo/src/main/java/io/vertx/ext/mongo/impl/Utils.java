package io.vertx.ext.mongo.impl;

import com.mongodb.ConnectionString;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoCollectionOptions;
import com.mongodb.async.client.MongoDatabaseOptions;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SSLSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.WriteOptions;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class Utils {

  public static String idAsString(BsonValue value) {
    if (value instanceof BsonString) {
      return ((BsonString) value).getValue();
    }

    throw new IllegalArgumentException("Unvalid bson type " + value.getBsonType() + " for ID field");
  }

  //FIXME: All the manual conversion from JsonObject <-> Document should be removed when https://jira.mongodb.org/browse/JAVA-1325 is finished.
  public static Document toDocument(JsonObject json) {
    return toDocument(json, false);
  }

  public static Document toDocument(JsonObject json, boolean createIfNull) {
    if (json == null && createIfNull) {
      return new Document();
    } else if (json != null) {
      Document doc = new Document();
      json.getMap().forEach((k, v) -> doc.put(k, getDocumentValue(v)));
      return doc;
    } else {
      return null;
    }
  }

  public static JsonObject toJson(Document document) {
    JsonObject json = new JsonObject();
    document.forEach((k, v) -> json.put(k, getJsonValue(v)));

    return json;
  }

  //TODO: It would be nice if we could pass in a Map to configure mongo. See https://jira.mongodb.org/browse/JAVA-1518
  public static MongoClientSettings clientSettings(JsonObject json) {
    MongoClientSettings.Builder settings = MongoClientSettings.builder();
    MongoClientConfig config = new MongoClientConfig(json);

    // Cluster
    ClusterSettings.Builder cluster = ClusterSettings.builder();
    List<ServerAddress> hosts = config.hosts();
    if (hosts.size() == 1 && config.replicaSet() == null) {
      cluster.mode(ClusterConnectionMode.SINGLE);
    } else {
      cluster.mode(ClusterConnectionMode.MULTIPLE);
      if (config.replicaSet() != null) {
        cluster.requiredReplicaSetName(config.replicaSet());
      }
    }
    cluster.hosts(hosts);
    settings.clusterSettings(cluster.build());

    // ConnectionPoolSettings
    ConnectionPoolSettings.Builder connection = ConnectionPoolSettings.builder().maxSize(100).maxWaitQueueSize(500); // default from java driver
    if (config.maxPoolSize() != null) {
      connection.maxSize(config.maxPoolSize());
    }
    if (config.minPoolSize() != null) {
      connection.minSize(config.minPoolSize());
    }
    if (config.maxIdleTimeMS() != null) {
      connection.maxConnectionIdleTime(config.maxIdleTimeMS(), TimeUnit.MILLISECONDS);
    }
    if (config.maxLifeTimeMS() != null) {
      connection.maxConnectionLifeTime(config.maxLifeTimeMS(), TimeUnit.MILLISECONDS);
    }
    if (config.waitQueueTimeoutMS() != null) {
      connection.maxWaitTime(config.waitQueueTimeoutMS(), TimeUnit.MILLISECONDS);
    }
    if (config.waitQueueMultiple() != null) {
      connection.maxWaitQueueSize(config.waitQueueMultiple());
    }
    if (config.maintenanceFrequencyMS() != null) {
      connection.maintenanceFrequency(config.maintenanceFrequencyMS(), TimeUnit.MILLISECONDS);
    }
    if (config.maintenanceInitialDelayMS() != null) {
      connection.maintenanceInitialDelay(config.maintenanceInitialDelayMS(), TimeUnit.MILLISECONDS);
    }
    settings.connectionPoolSettings(connection.build());

    // ServerSettings
    ServerSettings.Builder server = ServerSettings.builder();
    if (config.heartbeatFrequencyMS() != null) {
      server.heartbeatFrequency(config.heartbeatFrequencyMS(), TimeUnit.MILLISECONDS);
    }
    if (config.minHeartbeatFrequencyMS() != null) {
      server.minHeartbeatFrequency(config.minHeartbeatFrequencyMS(), TimeUnit.MILLISECONDS);
    }

    // Credentials
    settings.credentialList(config.credentials());

    // SSL
    if (config.ssl() != null) {
      settings.sslSettings(SSLSettings.builder().enabled(config.ssl()).build());
    }

    // Socket
    SocketSettings.Builder socket = SocketSettings.builder();
    if (config.connectTimeoutMS() != null) {
      socket.connectTimeout(config.connectTimeoutMS(), TimeUnit.MILLISECONDS);
    }
    if (config.socketTimeoutMS() != null) {
      socket.readTimeout(config.socketTimeoutMS(), TimeUnit.MILLISECONDS);
    }
    if (config.keepAlive() != null) {
      socket.keepAlive(config.keepAlive());
    }
    if (config.receiveBufferSize() != null) {
      socket.receiveBufferSize(config.receiveBufferSize());
    }
    if (config.sendBufferSize() != null) {
      socket.sendBufferSize(config.sendBufferSize());
    }

    WriteConcern wc = config.writeConcern();
    if (wc != null) {
      settings.writeConcern(wc);
    }
    ReadPreference rp = config.readPreference();
    if (rp != null) {
      settings.readPreference(rp);
    }

    return settings.build();
  }

  // Take a mongo URI connection string and apply it for the settings of the MongoClient (the api should handle this tbh)
  public static MongoClientSettings clientSettings(String uri) {
    MongoClientSettings.Builder settings = MongoClientSettings.builder();
    ConnectionString connectionString = new ConnectionString(uri);
    settings.clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
      .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
      .credentialList(connectionString.getCredentialList())
      .sslSettings(SSLSettings.builder().applyConnectionString(connectionString).build())
      .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build());

    return settings.build();
  }

  public static MongoCollectionOptions collectionOptions(WriteOptions options, MongoClientSettings settings) {
    //TODO: If https://jira.mongodb.org/browse/JAVA-1524 gets resolved we won't need MongoClientSettings
    MongoCollectionOptions.Builder builder = MongoCollectionOptions.builder();
    if (options.getWriteConcern() != null) {
      builder.writeConcern(WriteConcern.valueOf(options.getWriteConcern()));
    }

    MongoDatabaseOptions dbOptions = MongoDatabaseOptions.builder().build().withDefaults(settings);
    return builder.build().withDefaults(dbOptions);
  }

  @SuppressWarnings("unchecked")
  private static Object getDocumentValue(Object value) {
    if (value instanceof JsonObject) {
      Document doc = new Document();
      ((JsonObject) value).getMap().forEach((k, v) -> {
        doc.put(k, getDocumentValue(v));
      });
      return doc;
    } else if (value instanceof JsonArray) {
      List<Object> list = new ArrayList<>();
      for (Object o : (JsonArray) value) {
        list.add(getDocumentValue(o));
      }
      return list;
    } else {
      return value;
    }
  }

  private static Object getJsonValue(Object value) {
    if (value instanceof Document) {
      JsonObject json = new JsonObject();
      ((Document) value).forEach((k, v) -> {
        json.put(k, getJsonValue(v));
      });
      return json;
    } else if (value instanceof List) {
      JsonArray array = new JsonArray();
      for (Object o : (List) value) {
        array.add(getJsonValue(o));
      }
      return array;
    } else if (value instanceof Date) {
      return ((Date) value).getTime();
    } else {
      return value;
    }
  }
}
