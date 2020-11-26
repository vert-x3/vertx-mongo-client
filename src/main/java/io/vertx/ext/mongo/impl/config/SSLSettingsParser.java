package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.impl.TrustAllTrustManager;

import javax.net.ssl.*;
import java.security.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @author Lukas Prettenthaler
 */
class SSLSettingsParser {
  private static final Logger log = LoggerFactory.getLogger(SSLSettingsParser.class);
  private final ConnectionString connectionString;
  private final JsonObject config;

  SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;
  }

  public SslSettings settings(Vertx vertx) {
    final SslSettings.Builder builder = SslSettings.builder();
    fromConnectionString(builder);
    fromConfiguration(builder);

    final SslSettings settings = builder.build();
    if (!settings.isEnabled()) {
      return settings;
    }
    final PemKeyCertOptions pemKeyCertOptions = new PemKeyCertOptions();
    final PemTrustOptions pemTrustOptions = new PemTrustOptions();
    if (config.containsKey("caPath")) {
      pemTrustOptions.addCertPath(config.getString("caPath"));
    }
    if (config.containsKey("keyPath") && config.containsKey("certPath")) {
      pemKeyCertOptions.addKeyPath(config.getString("keyPath"));
      pemKeyCertOptions.addCertPath(config.getString("certPath"));
    }
    try {
      final TrustManager[] tms;
      if (config.getBoolean("trustAll", false)) {
        log.warn("Mongo client has been set to trust ALL certificates, this can open you up to security issues. Make sure you know the risks.");
        tms = new TrustManager[]{TrustAllTrustManager.INSTANCE};
      } else {
        tms = pemTrustOptions.getTrustManagerFactory((Vertx) vertx).getTrustManagers();
      }
      final SSLContext context = SSLContext.getInstance("TLS");
      KeyManager[] mgr = pemKeyCertOptions.getKeyManagerFactory(vertx).getKeyManagers();
      context.init(mgr, tms, new SecureRandom());
      builder.context(context);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
    return builder.build();
  }

  private void fromConnectionString(SslSettings.Builder builder) {
    if (connectionString != null) {
      builder.applyConnectionString(connectionString);
    }
  }

  private void fromConfiguration(SslSettings.Builder builder) {
    if (config.containsKey("ssl")) {
      builder.enabled(config.getBoolean("ssl", false));
    }
    if (config.containsKey("sslInvalidHostNameAllowed")) {
      builder.invalidHostNameAllowed(config.getBoolean("sslInvalidHostNameAllowed", false));
    }
  }
}
