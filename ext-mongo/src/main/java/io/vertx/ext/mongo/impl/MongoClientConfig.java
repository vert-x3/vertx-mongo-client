package io.vertx.ext.mongo.impl;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.AuthenticationMechanism.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoClientConfig {
  private static final Logger log = LoggerFactory.getLogger(MongoClientConfig.class);

  private final List<ServerAddress> hosts = new ArrayList<>();
  private final String replicaSet;
  private final Boolean ssl;
  private final Integer connectTimeoutMS;
  private final Integer socketTimeoutMS;
  private final Boolean keepAlive;
  private final Integer receiveBufferSize;
  private final Integer sendBufferSize;
  private final Integer maxPoolSize;
  private final Integer minPoolSize;
  private final Long maxIdleTimeMS;
  private final Long maxLifeTimeMS;
  private final Integer waitQueueMultiple;
  private final Long waitQueueTimeoutMS;
  private final Long maintenanceInitialDelayMS;
  private final Long maintenanceFrequencyMS;
  private final Long heartbeatFrequencyMS;
  private final Long minHeartbeatFrequencyMS;
  private final List<MongoCredential> credentials;
  private final WriteConcern writeConcern;
  private final ReadPreference readPreference;

  public MongoClientConfig(JsonObject config) {
    // Cluster / Host(s)
    JsonArray jsonHosts = get(config, "hosts", JsonArray.class);
    if (jsonHosts != null) {
      forEach(jsonHosts, "hosts", JsonObject.class, jsonHost -> {
        ServerAddress address = serverAddress(jsonHost);
        if (address != null) {
          hosts.add(address);
        }
      });
    } else {
      // Support host / port properties and if not present use default ServerAddress (127.0.0.1:27017)
      ServerAddress address = serverAddress(config);
      hosts.add(address == null ? new ServerAddress() : address);
    }
    replicaSet = get(config, "replicaSet", String.class);

    // SSL
    ssl = get(config, "ssl", Boolean.class);

    // Socket
    connectTimeoutMS = get(config, "connectTimeoutMS", Integer.class);
    socketTimeoutMS = get(config, "socketTimeoutMS", Integer.class);
    keepAlive = get(config, "keepAlive", Boolean.class);
    receiveBufferSize = get(config, "receiveBufferSize", Integer.class);
    sendBufferSize = get(config, "sendBufferSize", Integer.class);

    // Connection Pool
    maxPoolSize = get(config, "maxPoolSize", Integer.class);
    minPoolSize = get(config, "minPoolSize", Integer.class);
    maxIdleTimeMS = get(config, "maxIdleTimeMS", Long.class);
    maxLifeTimeMS = get(config, "maxLifeTimeMS", Long.class);
    waitQueueMultiple = get(config, "waitQueueMultiple", Integer.class);
    waitQueueTimeoutMS = get(config, "waitQueueTimeoutMS", Long.class);
    maintenanceInitialDelayMS = get(config, "maintenanceInitialDelayMS", Long.class);
    maintenanceFrequencyMS = get(config, "maintenanceFrequencyMS", Long.class);

    // Server
    heartbeatFrequencyMS = get(config, "heartbeatFrequencyMS", Long.class);
    minHeartbeatFrequencyMS = get(config, "minHeartbeatFrequencyMS", Long.class);

    // Credentials
    credentials = createCredentials(config);

    // Write Concern
    writeConcern = writeConcern(config);
    // Read preference
    readPreference = readPreference(config);
  }

  public List<ServerAddress> hosts() {
    return hosts;
  }

  public String replicaSet() {
    return replicaSet;
  }

  public Boolean ssl() {
    return ssl;
  }

  public Integer connectTimeoutMS() {
    return connectTimeoutMS;
  }

  public Integer socketTimeoutMS() {
    return socketTimeoutMS;
  }

  public Boolean keepAlive() {
    return keepAlive;
  }

  public Integer receiveBufferSize() {
    return receiveBufferSize;
  }

  public Integer sendBufferSize() {
    return sendBufferSize;
  }

  public Integer maxPoolSize() {
    return maxPoolSize;
  }

  public Integer minPoolSize() {
    return minPoolSize;
  }

  public Long maxIdleTimeMS() {
    return maxIdleTimeMS;
  }

  public Long maxLifeTimeMS() {
    return maxLifeTimeMS;
  }

  // ConnectionPoolSettings#maxWaitQueueSize
  public Integer waitQueueMultiple() {
    return waitQueueMultiple;
  }

  // ConnectionPoolSettings#maxWaitTime
  public Long waitQueueTimeoutMS() {
    return waitQueueTimeoutMS;
  }

  public Long maintenanceInitialDelayMS() {
    return maintenanceInitialDelayMS;
  }

  public Long maintenanceFrequencyMS() {
    return maintenanceFrequencyMS;
  }

  public Long heartbeatFrequencyMS() {
    return heartbeatFrequencyMS;
  }

  public Long minHeartbeatFrequencyMS() {
    return minHeartbeatFrequencyMS;
  }

  public List<MongoCredential> credentials() {
    return credentials;
  }

  public WriteConcern writeConcern() {
    return writeConcern;
  }

  public ReadPreference readPreference() {
    return readPreference;
  }

  private static WriteConcern writeConcern(JsonObject json) {
    // Allow convenient string value for writeConcern e.g. ACKNOWLEDGED, SAFE, MAJORITY, etc
    String wcs = get(json, "writeConcern", String.class);
    if (wcs != null) {
      try {
        // ReadPreference throws an exception while this returns null. Try to handle both in case they change their minds...
        WriteConcern wc = WriteConcern.valueOf(wcs);
        if (wc == null) throw new IllegalArgumentException("Invalid WriteConcern " + wcs);

        return wc;
      } catch (IllegalArgumentException e) {
        log.warn("Invalid value '" + wcs + "' for writeConcern. Ignoring this option...");
        return null; // Default to null for invalid write concern
      }
    }

    // Support advanced write concern options. There's some inconsistencies between driver options
    // and mongo docs [http://bit.ly/10SYO6x] but we'll be consistent with the driver for this.
    Boolean safe = get(json, "safe", Boolean.class);
    Object w = json.getValue("w");
    int wtimeout = get(json, "wtimeoutMS", Integer.class, 0);
    boolean fsync = get(json, "fsync", Boolean.class, false); // This doesn't exist in mongo docs, but you can specify it for driver...
    boolean j = get(json, "j", Boolean.class, false);
    if (!j) {
      j = get(json, "journal", Boolean.class, false); //TODO: Inconsistency with driver and mongo docs, support both ?
    }

    if (w != null || wtimeout != 0 || fsync || j) {
      if (w == null) {
        return new WriteConcern(1, wtimeout, fsync, j);
      } else {
        if (w instanceof String) {
          return new WriteConcern((String) w, wtimeout, fsync, j);
        } else if (w instanceof Integer) {
          return new WriteConcern((int) w, wtimeout, fsync, j);
        } else {
          WriteConcern wc = new WriteConcern(1, wtimeout, fsync, j);
          log.warn("Invalid type " + w.getClass() + " for w of a WriteConcern, defaulting to " + wc);
          return wc;
        }
      }
    } else if (safe != null) {
      return (safe) ? WriteConcern.ACKNOWLEDGED : WriteConcern.UNACKNOWLEDGED;
    }

    return null;
  }

  private static ReadPreference readPreference(JsonObject json) {
    String rps = get(json, "readPreference", String.class);
    if (rps != null) {
      JsonArray readPreferenceTags = get(json, "readPreferenceTags", JsonArray.class);
      if (readPreferenceTags == null) {
        try {
          ReadPreference rp = ReadPreference.valueOf(rps);
          // WriteConcern will return null for an invalid value, this throws an exception. Try to handle both in case they change their minds...
          if (rp == null) throw new IllegalArgumentException("Invalid ReadPreference " + rps);

          return rp;
        } catch (IllegalArgumentException ia) {
          log.warn("Invalid value '" + rps + "' for readPreference. Ignoring this option...");
          return null; // Default to null for invalid read preference
        }
      } else {
        // Support advanced ReadPreference's
        List<TagSet> tagSet = new ArrayList<>();
        forEach(readPreferenceTags, "readPreferenceTags", String.class, tagString -> {
          List<Tag> tags = Stream.of(tagString.trim().split(","))
            .map(s -> s.split(":"))
            .filter(array -> {
              if (array.length != 2) {
                throw new IllegalArgumentException("Invalid readPreferenceTags value '" + tagString + "'");
              }
              return true;
            }).map(array -> new Tag(array[0], array[1])).collect(Collectors.toList());

          tagSet.add(new TagSet(tags));
        });
        return ReadPreference.valueOf(rps, tagSet);
      }
    }

    return null;
  }

  private static List<MongoCredential> createCredentials(JsonObject json) {
    String username = json.getString("username");
    if (username == null) {
      return Collections.emptyList();
    }
    String passwd = json.getString("password");
    char[] password = (passwd == null) ? null : passwd.toCharArray();

    String authSource = json.getString("authSource", "admin");

    AuthenticationMechanism mechanism = null;
    String authMechanism = json.getString("authMechanism");
    if (authMechanism != null) {
      try {
        mechanism = AuthenticationMechanism.fromMechanismName(authMechanism);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid authMechanism '" + authMechanism + "'");
      }
    }
    String gssapiServiceName = json.getString("gssapiServiceName");
    MongoCredential credential;
    if (mechanism == GSSAPI) {
      credential = MongoCredential.createGSSAPICredential(username);
      if (gssapiServiceName != null) {
        credential = credential.withMechanismProperty("SERVICE_NAME", gssapiServiceName);
      }
    } else if (mechanism == PLAIN) {
      credential = MongoCredential.createPlainCredential(username, authSource, password);
    } else if (mechanism == MONGODB_CR) {
      credential = MongoCredential.createMongoCRCredential(username, authSource, password);
    } else if (mechanism == MONGODB_X509) {
      credential = MongoCredential.createMongoX509Credential(username);
    } else if (mechanism == SCRAM_SHA_1) {
      credential = MongoCredential.createScramSha1Credential(username, authSource, password);
    } else if (mechanism == null) {
      credential = MongoCredential.createCredential(username, authSource, password);
    } else {
      throw new UnsupportedOperationException("Unsupported authentication mechanism " + mechanism);
    }

    return Arrays.asList(credential);
  }

  private static ServerAddress serverAddress(JsonObject json) {
    String host = json.getString("host");
    Integer port = json.getInteger("port");
    if (host == null && port == null) {
      return null;
    } else {
      return new ServerAddress(host, port);
    }
  }

  private static <T> T get(JsonObject json, String key, Class<T> type) {
    return get(json, key, type, null);
  }

  private static <T> T get(JsonObject json, String key, Class<T> type, T def) {
    Object value = json.getValue(key);
    try {
      T val = type.cast(value);
      return (val == null) ? def : val;
    } catch (ClassCastException cce) {
      throw new IllegalArgumentException("Invalid type " + value.getClass().getName() + " for '" + key + "'. Was expecting type " + type.getName());
    }
  }

  private static <T> void forEach(JsonArray array, String key, Class<T> type, Consumer<T> consumer) {
    for (Object o : array) {
      try {
        consumer.accept(type.cast(o));
      } catch (ClassCastException cce) {
        throw new IllegalArgumentException("Invalid type " + o.getClass().getName() + " for array '" + key + "'. Was expecting type " + type.getName());
      }
    }
  }
}
