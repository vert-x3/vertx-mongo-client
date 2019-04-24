package io.vertx.ext.mongo.impl.config;

import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.SslSettings;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParsingSSLOptionsTest {

  @Test
  public void ssl_should_be_disabled_by_default() {
    // given
    final JsonObject configWithoutSSLInfo = new JsonObject().put(
      "connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs"
    );

    // when
    final MongoClientSettings parsedSettings = new MongoClientOptionsParser(configWithoutSSLInfo).settings();

    // then
    assertFalse(parsedSettings.getSslSettings().isEnabled());
    assertFalse(parsedSettings.getSslSettings().isInvalidHostNameAllowed());
  }

  @Test
  public void one_should_be_able_to_enable_ssl_support_via_connection_string() {
    // given
    final JsonObject withSSLEnabled = new JsonObject().put(
      "connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs&ssl=true"
    );

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(withSSLEnabled).settings().getSslSettings();

    // then
    assertTrue(sslSettings.isEnabled());
  }

  @Test
  public void one_should_be_able_to_enable_ssl_support_via_config_property() {
    // given
    final JsonObject withSSLEnabled = new JsonObject().put("ssl", true);

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(withSSLEnabled).settings().getSslSettings();

    // then
    assertTrue(sslSettings.isEnabled());
  }

  @Test
  public void one_should_be_able_to_allow_invalid_host_names_via_connection_string() {
    // given
    final JsonObject withSSLAndInvalidHostnameEnabled = new JsonObject().put(
      "connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs&ssl=true&sslInvalidHostNameAllowed=true"
    );

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(withSSLAndInvalidHostnameEnabled)
      .settings()
      .getSslSettings();

    // then
    assertTrue(sslSettings.isInvalidHostNameAllowed());
  }

  @Test
  public void one_should_be_able_to_allow_invalid_host_names_via_config_property() {
    // given
    final JsonObject withSSLAndInvalidHostnameEnabled = new JsonObject()
      .put("ssl", true)
      .put("sslInvalidHostNameAllowed", true);

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(withSSLAndInvalidHostnameEnabled)
      .settings()
      .getSslSettings();

    // then
    assertTrue(sslSettings.isInvalidHostNameAllowed());
  }

  @Test
  public void testTrustAllProperty() {
    // given
    final JsonObject withSSLAndTrustAllEnabled = new JsonObject()
      .put("ssl", true)
      .put("trustAll", true);

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(withSSLAndTrustAllEnabled)
      .settings()
      .getSslSettings();

    // then
    assertNotNull(sslSettings.getContext());
  }
}
