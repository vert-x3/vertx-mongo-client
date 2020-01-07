package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.impl.KeyStoreHelper;
import io.vertx.core.net.impl.TrustAllTrustManager;

import javax.net.ssl.*;
import java.security.*;
import java.util.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class SSLSettingsParser {
  private static final Logger log = LoggerFactory.getLogger(SSLSettingsParser.class);
  private final ConnectionString connectionString;
  private final JsonObject config;

  SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;
  }

  public SslSettings settings(final Vertx vertx) {
    final SslSettings.Builder builder = fromConnectionString().orElseGet(this::fromConfiguration);
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
        final KeyStoreHelper pemTrustStore = KeyStoreHelper.create((VertxInternal) vertx, pemTrustOptions);
        tms = pemTrustStore.getTrustMgrs((VertxInternal) vertx);
      }
      final KeyStoreHelper pemKeyCertStore = KeyStoreHelper.create((VertxInternal) vertx, pemKeyCertOptions);
      final SSLContext context = SSLContext.getInstance("TLS");
      context.init(pemKeyCertStore.getKeyMgr(), tms, new SecureRandom());
      builder.context(context);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
    return builder.build();
  }

  private Optional<SslSettings.Builder> fromConnectionString() {
    return Optional.ofNullable(connectionString).map(cs ->
      SslSettings.builder()
        .applyConnectionString(cs)
    );
  }

  private SslSettings.Builder fromConfiguration() {
    return SslSettings.builder()
      .enabled(config.getBoolean("ssl", false))
      .invalidHostNameAllowed(config.getBoolean("sslInvalidHostNameAllowed", false));
  }
}
