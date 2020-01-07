package io.vertx.ext.mongo.impl.config;

import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.SslSettings;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class ParsingSSLOptionsTest {
  private Vertx vertx;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @After
  public void tearDown() {
    vertx.close();
  }

  @Test
  public void ssl_should_be_disabled_by_default() {
    // given
    final JsonObject configWithoutSSLInfo = new JsonObject().put(
      "connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs"
    );

    // when
    final MongoClientSettings parsedSettings = new MongoClientOptionsParser(vertx, configWithoutSSLInfo).settings();

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
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLEnabled).settings().getSslSettings();

    // then
    assertTrue(sslSettings.isEnabled());
  }

  @Test
  public void one_should_be_able_to_enable_ssl_support_via_config_property() {
    // given
    final JsonObject withSSLEnabled = new JsonObject().put("ssl", true);

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLEnabled).settings().getSslSettings();

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
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndInvalidHostnameEnabled)
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
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndInvalidHostnameEnabled)
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
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndTrustAllEnabled)
      .settings()
      .getSslSettings();

    // then
    assertNotNull(sslSettings.getContext());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCaPathProperty() {
    // given
    final JsonObject withSSLAndCaPath = new JsonObject()
      .put("ssl", true)
      .put("caPath", "notExisting.pem");

    // then
    new MongoClientOptionsParser(vertx, withSSLAndCaPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyPemCertificate() throws IOException {
    // given
    final File tmpFile = tmpFolder.newFile("invalidCa.pem");
    final JsonObject withSSLAndCaPath = new JsonObject()
      .put("ssl", true)
      .put("caPath", tmpFile.getAbsolutePath());

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndCaPath)
      .settings()
      .getSslSettings();

    // then
    assertNull(sslSettings.getContext());
  }

  @Test
  public void testValidPemCertificate() throws IOException {
    // given
    final File tmpFile = tmpFolder.newFile("validCa.pem");
    try (final FileWriter tmpWriter = new FileWriter(tmpFile)) {
      tmpWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICljCCAfigAwIBAgIJAK0oe+f4DaojMAoGCCqGSM49BAMEMFkxCzAJBgNVBAYT\n" +
        "AkFUMQ8wDQYDVQQIDAZWaWVubmExDjAMBgNVBAoMBU5vRW52MSkwJwYDVQQLDCBO\n" +
        "b0VudiBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eTAeFw0xNjEwMjcxNTAwNTFa\n" +
        "Fw00NjEwMjAxNTAwNTFaMFkxCzAJBgNVBAYTAkFUMQ8wDQYDVQQIDAZWaWVubmEx\n" +
        "DjAMBgNVBAoMBU5vRW52MSkwJwYDVQQLDCBOb0VudiBSb290IENlcnRpZmljYXRl\n" +
        "IEF1dGhvcml0eTCBmzAQBgcqhkjOPQIBBgUrgQQAIwOBhgAEAHpsMQth12N0d+aE\n" +
        "FIFRd8in4MTYZNSQEyQ4fuPDNq0Zb+4TXpUmedLZQJKkAQxorak8ESC/tXuQJDUL\n" +
        "OoKa+R6NAT4EKR1aaVVd7clC9rfGqVwGYslppycy9zsN6O4XLUiripamQF78FzRF\n" +
        "8wRZvkwYhzud+jpV6shgEMw3zmcwDSYKo2YwZDAdBgNVHQ4EFgQUD96n//91CReu\n" +
        "Cz1K0qics6aNFV0wHwYDVR0jBBgwFoAUD96n//91CReuCz1K0qics6aNFV0wEgYD\n" +
        "VR0TAQH/BAgwBgEB/wIBATAOBgNVHQ8BAf8EBAMCAYYwCgYIKoZIzj0EAwQDgYsA\n" +
        "MIGHAkFOxsApSB7fn8ZnYG/EUscn/uAkjxHsvdEkPKCC+XYCKMssW4YP2kR6gZjo\n" +
        "J8vaOAJZwNevBe/R9J8zMvsAWRJmWgJCAKLedGLnBuJOK9jjnKBwbVm5OIQfApMA\n" +
        "I2mJVnNXvS12w4DTZlP0K1t63WxsykBBTOIVXnYdPkdZvvnoAIcfA7iM\n" +
        "-----END CERTIFICATE-----");
    }
    final JsonObject withSSLAndCaPath = new JsonObject()
      .put("ssl", true)
      .put("caPath", tmpFile.getAbsolutePath());

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndCaPath)
      .settings()
      .getSslSettings();

    // then
    assertNotNull(sslSettings.getContext());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPemCertificate() throws IOException {
    // given
    final File tmpFile = tmpFolder.newFile("validCa.pem");
    try (final FileWriter tmpWriter = new FileWriter(tmpFile)) {
      tmpWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICljCCAfigAwIBAgI...BROKEN...xsykBBTOIVXnYdPkdZvvnoAIcfA7iM\n" +
        "-----END CERTIFICATE-----");
    }
    final JsonObject withSSLAndCaPath = new JsonObject()
      .put("ssl", true)
      .put("caPath", tmpFile.getAbsolutePath());

    // then
    new MongoClientOptionsParser(vertx, withSSLAndCaPath);
  }
}
