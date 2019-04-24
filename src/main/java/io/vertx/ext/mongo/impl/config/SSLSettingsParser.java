package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.connection.SslSettings;
import io.vertx.core.json.JsonObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class SSLSettingsParser {
  private final ConnectionString connectionString;
  private final JsonObject config;

  SSLSettingsParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;
  }

  public SslSettings settings() {
    final SslSettings.Builder builder = fromConnectionString().orElseGet(this::fromConfiguration);
    if (config.getBoolean("trustAll", false)) {
      try {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
          }

          @Override
          public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }}, new SecureRandom());
        builder.context(context);
      } catch (final NoSuchAlgorithmException | KeyManagementException e) {
        //fail silently on error
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
}
