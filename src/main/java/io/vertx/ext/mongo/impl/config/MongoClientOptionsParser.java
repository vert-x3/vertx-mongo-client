package io.vertx.ext.mongo.impl.config;

import static io.vertx.core.transport.Transport.EPOLL;
import static io.vertx.core.transport.Transport.IO_URING;
import static io.vertx.core.transport.Transport.KQUEUE;
import static io.vertx.core.transport.Transport.NIO;

import com.mongodb.*;
import com.mongodb.connection.*;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.internal.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.transport.Transport;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.impl.codec.json.JsonObjectCodec;
import java.util.Locale;
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

  private static final String TRANSPORT_NAME_AUTO = "auto";
  private static final String TRANSPORT_NAME_NIO = "nio";
  private static final String TRANSPORT_NAME_EPOLL = "epoll";
  private static final String TRANSPORT_NAME_IO_URING = "io_uring";
  private static final String TRANSPORT_NAME_KQUEUE = "kqueue";
  private final static CodecRegistry commonCodecRegistry = CodecRegistries.fromCodecs(new StringCodec(), new IntegerCodec(),
    new BooleanCodec(), new DoubleCodec(), new LongCodec(), new BsonDocumentCodec(), new DocumentCodec());
  private final MongoClientSettings settings;
  private final String database;

  public MongoClientOptionsParser(Vertx vertx, JsonObject config) {
    Objects.requireNonNull(config);

    MongoClientSettings.Builder options = MongoClientSettings.builder();
    options.codecRegistry(CodecRegistries.fromRegistries(commonCodecRegistry, CodecRegistries.fromCodecs(new JsonObjectCodec(config))));

    // All parsers should support connection_string first
    String cs = config.getString("connection_string");
    ConnectionString connectionString = (cs == null) ? null : new ConnectionString(cs);
    String csDatabase = (connectionString != null) ? connectionString.getDatabase() : null;
    this.database = config.getString("db_name", csDatabase != null ? csDatabase : MongoClient.DEFAULT_DB_NAME);

    // ClusterSettings
    ClusterSettings clusterSettings = new ClusterSettingsParser(connectionString, config).settings();
    options.applyToClusterSettings(builder -> builder.applySettings(clusterSettings));

    // ConnectionPoolSettings
    ConnectionPoolSettings connectionPoolSettings = new ConnectionPoolSettingsParser(connectionString, config).settings();
    options.applyToConnectionPoolSettings(builder -> builder.applySettings(connectionPoolSettings));

    // Credentials
    // The previous mongo client supported credentials list but their new implementation supports only
    // one credentials. The deprecated code path resorts to using the last credentials if a list is passed
    // we are doing the same here.
    List<MongoCredential> credentials = new CredentialListParser(connectionString, config).credentials();
    if (!credentials.isEmpty())
      options.credential(credentials.get(credentials.size() - 1));

    // SocketSettings
    SocketSettings socketSettings = new SocketSettingsParser(connectionString, config).settings();
    options.applyToSocketSettings(builder -> builder.applySettings(socketSettings));

    // SSLSettings
    SslSettings sslSettings = new SSLSettingsParser(connectionString, config).settings(vertx);
    options.applyToSslSettings(builder -> builder.applySettings(sslSettings));

    // WriteConcern
    WriteConcern writeConcern = new WriteConcernParser(connectionString, config).writeConcern();
    if (writeConcern != null) {
      options.writeConcern(writeConcern);
    }

    // ReadConcern
    ReadConcern readConcern = new ReadConcernLevelParser(connectionString, config).readConcern();
    if (readConcern != null) {
      options.readConcern(readConcern);
    }

    // ReadPreference
    ReadPreference readPreference = new ReadPreferenceParser(connectionString, config).readPreference();
    if (readPreference != null) {
      options.readPreference(readPreference);
    }

    // ServerSettings
    ServerSettings serverSettings = new ServerSettingsParser(config).settings();
    options.applyToServerSettings(builder -> builder.applySettings(serverSettings));

    // CompressorsSettings
    List<MongoCompressor> compressorList = new CompressorListParser(connectionString, config).compressorList();
    if (compressorList != null) {
      options.compressorList(compressorList);
    }

    //retryable settings
    applyRetryableSetting(options, connectionString, config);

    applyTransportSettings(vertx, options, config);

    this.settings = options.build();
  }

  public void applyRetryableSetting(MongoClientSettings.Builder options, ConnectionString connectionString, JsonObject config) {
    Boolean retryWrites = Optional.ofNullable(connectionString)
      .flatMap(cs -> Optional.ofNullable(cs.getRetryWritesValue()))
      .orElse(config.getBoolean("retryWrites"));
    if (retryWrites != null) {
      options.retryWrites(retryWrites);
    }
    Boolean retryReads = Optional.ofNullable(connectionString)
      .flatMap(cs -> Optional.ofNullable(cs.getRetryReads()))
      .orElse(config.getBoolean("retryReads"));
    if (retryReads != null) {
      options.retryReads(retryReads);
    }
  }

  public MongoClientSettings settings() {
    return settings;
  }

  public String database() {
    return database;
  }

  @SuppressWarnings("unchecked") // for Class.forName()
  private void applyTransportSettings(Vertx vertx, MongoClientSettings.Builder options, JsonObject config) {
    NettyTransportSettings.Builder nettyBuilder = TransportSettings.nettyBuilder();
    String vertxTransport = transportNameOfTransportClass(((VertxInternal) vertx).transport().getClass());
    String mongoTransport = config.getString("transport", TRANSPORT_NAME_AUTO).strip().toLowerCase(Locale.ROOT);
    if (mongoTransport.equals(TRANSPORT_NAME_AUTO)) {
      mongoTransport = (vertxTransport != null) ? vertxTransport : TRANSPORT_NAME_NIO;
    }
    String channelClassName = channelClassNameForTransport(mongoTransport);
    try {
      nettyBuilder.socketChannelClass((Class<? extends SocketChannel>) Class.forName(channelClassName));
    } catch (ClassNotFoundException | NoClassDefFoundError | ClassCastException e) {
      // fallback to vertx transport
      mongoTransport = vertxTransport;
      channelClassName = channelClassNameForTransport(vertxTransport);
      try {
        nettyBuilder.socketChannelClass((Class<? extends SocketChannel>) Class.forName(channelClassName));
      } catch (ClassNotFoundException ex) {
        // this is impossible to happen - if vertx already using this transport, the class must be present
        throw new IllegalStateException("Can't load netty channel class used by Vertx", ex);
      }
    }
    if (mongoTransport.equals(vertxTransport)) {
      // reuse vertx event loop group
      nettyBuilder.eventLoopGroup(((VertxInternal) vertx).nettyEventLoopGroup());
    } else {
      // build separate EL for mongo
      nettyBuilder.eventLoopGroup(buildEventLoopGroup(mongoTransport, vertx));
    }
    options.transportSettings(nettyBuilder.build());
  }

  private static String channelClassNameForTransport(String transport) {
    switch (transport) {
      case TRANSPORT_NAME_NIO:
        return "io.netty.channel.socket.nio.NioSocketChannel";
      case TRANSPORT_NAME_EPOLL:
        return "io.netty.channel.epoll.EpollSocketChannel";
      case TRANSPORT_NAME_IO_URING:
        return "io.netty.channel.uring.IoUringSocketChannel";
      case TRANSPORT_NAME_KQUEUE:
        return "io.netty.channel.kqueue.KQueueSocketChannel";
      default:
        throw new IllegalArgumentException("Invalid MongoClient transport: " + transport);
    }
  }

  private static String transportNameOfTransportClass(Class<? extends Transport> vertxTransportClass) {
    String vertxTransport = null;
    // NOTE: EPOLL, IO_URING, KQUEUE can be null if corresponding jars are not present on the classpath
    if (vertxTransportClass.equals(NIO.implementation().getClass())) { // NIO is never null
      vertxTransport = TRANSPORT_NAME_NIO;
    } else if (EPOLL != null && vertxTransportClass.equals(EPOLL.implementation().getClass())) {
      vertxTransport = TRANSPORT_NAME_EPOLL;
    } else if (IO_URING != null && vertxTransportClass.equals(IO_URING.implementation().getClass())) {
      vertxTransport = TRANSPORT_NAME_IO_URING;
    } else if (KQUEUE != null && vertxTransportClass.equals(KQUEUE.implementation().getClass())) {
      vertxTransport = TRANSPORT_NAME_KQUEUE;
    }
    return vertxTransport;
  }

  private static EventLoopGroup buildEventLoopGroup(String transportName, Vertx vertx) {
    // follow the way how Vertx builds its event loop group (using Transport#ioHandlerFactory()), with the
    // same number of threads as in Vertx instance
    io.vertx.core.transport.Transport transport;
    switch (transportName) {
      case TRANSPORT_NAME_NIO:
        transport = NIO;
        break;
      case TRANSPORT_NAME_EPOLL:
        transport = EPOLL;
        break;
      case TRANSPORT_NAME_KQUEUE:
        transport = KQUEUE;
        break;
      case TRANSPORT_NAME_IO_URING:
        transport = IO_URING;
        break;
      default:
        throw new IllegalArgumentException("Unknown MongoClient transport: " + transportName);
    }
    // there's no simple method "size", need to iterate and count
    int numThreadsInVertxEl = 0;
    for (EventExecutor el : ((VertxInternal) vertx).nettyEventLoopGroup()) {
      numThreadsInVertxEl++;
    }
    return new MultiThreadIoEventLoopGroup(numThreadsInVertxEl, transport.implementation().ioHandlerFactory());
  }
}
