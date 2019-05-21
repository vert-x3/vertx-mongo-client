package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.impl.TrustAllTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;

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

  public SslSettings settings() {
    final SslSettings.Builder builder = fromConnectionString().orElseGet(this::fromConfiguration);
    if (config.getBoolean("trustAll", false)) {
      log.warn("Mongo client has been set to trust ALL certificates, this can open you up to security issues. Make sure you know the risks.");
      try {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{TrustAllTrustManager.INSTANCE}, new SecureRandom());
        builder.context(context);
      } catch (final NoSuchAlgorithmException | KeyManagementException e) {
        //fail silently on error
      }
    }
    if (config.containsKey("caPath")) {
      final String caPath = config.getString("caPath");
      try {
        final TrustManagerFactory tmf = buildTrustManagerFactory(caPath);
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), new SecureRandom());
        builder.context(context);
      } catch (final FileNotFoundException e) {
        throw new IllegalArgumentException("Invalid caPath " + e.getMessage());
      } catch (final NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
        throw new IllegalArgumentException("Unable to load certificate from caPath '" + caPath + "' " + e.getMessage());
      }
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

  private static TrustManagerFactory buildTrustManagerFactory(final String caPath)
    throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
    final CertificateFactory fact = CertificateFactory.getInstance("X.509");
    final FileInputStream is = new FileInputStream(caPath);
    final X509Certificate cert = (X509Certificate) fact.generateCertificate(is);

    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(null, null);
    ks.setCertificateEntry("1", cert);

    final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(ks);

    return trustManagerFactory;
  }
}
