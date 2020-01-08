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
  public void testEmptyCaPemCertificate() throws IOException {
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
  public void testValidCaPemCertificate() throws IOException {
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

  @Test
  public void testValidCaPemCertificateChain() throws IOException {
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
        "-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\n" +
        "MIIE0zCCA7ugAwIBAgIQGNrRniZ96LtKIVjNzGs7SjANBgkqhkiG9w0BAQUFADCB\n" +
        "yjELMAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQL\n" +
        "ExZWZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTowOAYDVQQLEzEoYykgMjAwNiBWZXJp\n" +
        "U2lnbiwgSW5jLiAtIEZvciBhdXRob3JpemVkIHVzZSBvbmx5MUUwQwYDVQQDEzxW\n" +
        "ZXJpU2lnbiBDbGFzcyAzIFB1YmxpYyBQcmltYXJ5IENlcnRpZmljYXRpb24gQXV0\n" +
        "aG9yaXR5IC0gRzUwHhcNMDYxMTA4MDAwMDAwWhcNMzYwNzE2MjM1OTU5WjCByjEL\n" +
        "MAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQLExZW\n" +
        "ZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTowOAYDVQQLEzEoYykgMjAwNiBWZXJpU2ln\n" +
        "biwgSW5jLiAtIEZvciBhdXRob3JpemVkIHVzZSBvbmx5MUUwQwYDVQQDEzxWZXJp\n" +
        "U2lnbiBDbGFzcyAzIFB1YmxpYyBQcmltYXJ5IENlcnRpZmljYXRpb24gQXV0aG9y\n" +
        "aXR5IC0gRzUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCvJAgIKXo1\n" +
        "nmAMqudLO07cfLw8RRy7K+D+KQL5VwijZIUVJ/XxrcgxiV0i6CqqpkKzj/i5Vbex\n" +
        "t0uz/o9+B1fs70PbZmIVYc9gDaTY3vjgw2IIPVQT60nKWVSFJuUrjxuf6/WhkcIz\n" +
        "SdhDY2pSS9KP6HBRTdGJaXvHcPaz3BJ023tdS1bTlr8Vd6Gw9KIl8q8ckmcY5fQG\n" +
        "BO+QueQA5N06tRn/Arr0PO7gi+s3i+z016zy9vA9r911kTMZHRxAy3QkGSGT2RT+\n" +
        "rCpSx4/VBEnkjWNHiDxpg8v+R70rfk/Fla4OndTRQ8Bnc+MUCH7lP59zuDMKz10/\n" +
        "NIeWiu5T6CUVAgMBAAGjgbIwga8wDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8E\n" +
        "BAMCAQYwbQYIKwYBBQUHAQwEYTBfoV2gWzBZMFcwVRYJaW1hZ2UvZ2lmMCEwHzAH\n" +
        "BgUrDgMCGgQUj+XTGoasjY5rw8+AatRIGCx7GS4wJRYjaHR0cDovL2xvZ28udmVy\n" +
        "aXNpZ24uY29tL3ZzbG9nby5naWYwHQYDVR0OBBYEFH/TZafC3ey78DAJ80M5+gKv\n" +
        "MzEzMA0GCSqGSIb3DQEBBQUAA4IBAQCTJEowX2LP2BqYLz3q3JktvXf2pXkiOOzE\n" +
        "p6B4Eq1iDkVwZMXnl2YtmAl+X6/WzChl8gGqCBpH3vn5fJJaCGkgDdk+bW48DW7Y\n" +
        "5gaRQBi5+MHt39tBquCWIMnNZBU4gcmU7qKEKQsTb47bDN0lAtukixlE0kF6BWlK\n" +
        "WE9gyn6CagsCqiUXObXbf+eEZSqVir2G3l6BFoMtEMze/aiCKm0oHw0LxOXnGiYZ\n" +
        "4fQRbxC1lfznQgUy286dUV4otp6F01vvpX1FQHKOtw5rDgb7MzVIcbidJ4vEZV8N\n" +
        "hnacRHr2lVz2XTIIM6RUthg/aFzyQkqFOFSDX9HoLPKsEdao7WNq\n" +
        "-----END CERTIFICATE-----\n");
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
    final File tmpFile = tmpFolder.newFile("brokenCa.pem");
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

  @Test
  public void testValidKeyAndCertificate() throws IOException {
    // given
    final File tmpKeyFile = tmpFolder.newFile("validKey.pem");
    try (final FileWriter tmpKeyWriter = new FileWriter(tmpKeyFile)) {
      tmpKeyWriter.write("-----BEGIN PRIVATE KEY-----\n" +
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDVmCecLdUZU917\n" +
        "hweVz4JqvZ9vZEi1rH+BG98HYfRR/h3QaobxPImZu3hzKHZ+MPbm94HunLPAVA9y\n" +
        "ZhvZMToNfOuD4TUPBPloBuNzwBfZk2O4CaXeG4ailVWUfm5t/l+RD/55zYKuhw1/\n" +
        "Vl9lcOryF2XAmPQ2F1gwEKK7wt1Ak8zw8/yeYgBv1/F+ibCMvR6FVj9ABBEfTM+o\n" +
        "Os4oy51otUv0h63GqYgXMJyLX7q+AGWdC3srwwLQROtkzi7y00g/YryXUoIqdXEI\n" +
        "7CrNL35rZXcZ5LfGRwFX9evX11PpT3OShYlsJBcFE9KMatRoIWd6xUKlxTk0yLjo\n" +
        "OUE2tsMJAgMBAAECggEAdewZAjqzidYpU0eLQoRcBj5GRaNiGRrxEgCnM1Y7IwFe\n" +
        "yG/nrEu11DASIdHXCXhS99Tx4SCWhLpkBM6m1VQ+LrAm/ppZRr+CSpJzBLaq9C5R\n" +
        "QYviDSu5Ow2jP+ZFZWiorlfcMLbrTRu2sfSnmkOrEpkkTh6jxTFCONcWYP8GU93D\n" +
        "YCA3hSH0li7CueS+GYJ1JB2Cd7buu+tOhl36AhBD96miExlgNn0YGpTJJ3I0Hb+O\n" +
        "lKIIQy+KK8f9TXrSeZC3OYlTtJaIr9ejspTXxIYN11EIit5MFEwnnkCglcsePjsx\n" +
        "qeOFRumJ5Nj5H8qyCNZ5MtzwbLkyktJzlumvnyr+AQKBgQDv/QfGKZJFeoCEWpoj\n" +
        "f+078JxSYyPVNXxbbr2NuN/V79hJBol87ukycz2CZkDCubIKfubc50eXDmhWCp4p\n" +
        "aJgl6BMhnovftYrIrGWJLwqXnwFwsKJSrJJqHlHDJDRGfUSQEWNclNeaB3Mr8W46\n" +
        "Zcaadeikstvka9xKA1LOCG3oIQKBgQDj2FFOxZK27KhY/9Oz1dUsPtAYYbLOor/P\n" +
        "Rbne3jICQStH3dnUEmWKIKrdYV1u2saw5djn3ujwB0xEXydRvRgiSF0qxYjbm9CG\n" +
        "TJaiHhTsQDjWkYMZaxk3gc7Yfh8DHF0wlvWpu1wMXNsCJ6jxqW2e+jSRioZICPK6\n" +
        "McWWmArd6QKBgDWjoHEyKXdOAhuTBJCarzOOe+IONpwY8EqfXc6nW6A9k2H/DAvY\n" +
        "elbEWyMiJ6deSeT+qCsHpoCkv707ck5fCmKulFgXT7wYn4Rqw+b9lKh+6Zt+X0mL\n" +
        "OM5vKGctWGHI7eIlgMfYnLfYom1X8QMsbE9puy3UrEFJulrwkzlpuOcBAoGAVRNV\n" +
        "sNsXIFSXu7uyueizU3UU0LXSRVQB2QxJDg3bkHnzBj+xcX15Cq2N/2G2uIjaPf1l\n" +
        "E5dpVQ70jGcXUG8SDuMEXs8pfg7dOvhoGpqu51RHpN7qm9ggr1g5+x6Ex+2UYmtL\n" +
        "yZfbFAasBE74x1ujQgRdEqct4sHsmFezVrro+9kCgYEAgl70mKk9yK/f7515OaO0\n" +
        "Y39tgVzpAG6RN1NKnY6NR5VNNemZx5jhKfk5byaYxX4XBjygD0sQ5KTpaZmoQIIX\n" +
        "FxuwhLRRMn6vtsEf1HexJAtRd82aL5wKS62l0AXG/CVLAygn4aSSqLrgTyFFVUR3\n" +
        "cASPpPIdZaKZG6q4Hmcpl58=\n" +
        "-----END PRIVATE KEY-----");
    }
    final File tmpCertFile = tmpFolder.newFile("validCert.pem");
    try (final FileWriter tmpCertWriter = new FileWriter(tmpCertFile)) {
      tmpCertWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICwTCCAamgAwIBAgIEBeVm4jANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDEwZj\n" +
        "bGllbnQwHhcNMTgwNTI2MTEzNjUxWhcNMjEwNTI1MTEzNjUxWjARMQ8wDQYDVQQD\n" +
        "EwZjbGllbnQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDVmCecLdUZ\n" +
        "U917hweVz4JqvZ9vZEi1rH+BG98HYfRR/h3QaobxPImZu3hzKHZ+MPbm94HunLPA\n" +
        "VA9yZhvZMToNfOuD4TUPBPloBuNzwBfZk2O4CaXeG4ailVWUfm5t/l+RD/55zYKu\n" +
        "hw1/Vl9lcOryF2XAmPQ2F1gwEKK7wt1Ak8zw8/yeYgBv1/F+ibCMvR6FVj9ABBEf\n" +
        "TM+oOs4oy51otUv0h63GqYgXMJyLX7q+AGWdC3srwwLQROtkzi7y00g/YryXUoIq\n" +
        "dXEI7CrNL35rZXcZ5LfGRwFX9evX11PpT3OShYlsJBcFE9KMatRoIWd6xUKlxTk0\n" +
        "yLjoOUE2tsMJAgMBAAGjITAfMB0GA1UdDgQWBBQ6xJBQsJCJdj/u0iTLYYD2qQsB\n" +
        "DDANBgkqhkiG9w0BAQsFAAOCAQEAfoquV375+eAGmfnlLxB30v9VhsFckrxFVpYs\n" +
        "XXC6h2G8MtXLpIEpgJo+4SZ4YjNwf/8m9J5j/duU8RukYanyzJdgkFFqKDBYCX7U\n" +
        "SD1nQP7729KnQgxtbR/+i3zkNgo7FATdkLq+HOxklNOEE24Ldenya39bsG779B9n\n" +
        "Sskcbq++7rMM+onDYBv6PbUKCm6nfqPspq809CLxSaUJg9+9ykut6hiyke/i7GEP\n" +
        "XIZHrM+mEvG00ES/zBIdV6TE0AIBP7q2MN7ylT509Ko9sUBMOZdEzikYp5GaRdiv\n" +
        "zG9q6rqK5COK614BwJFOD1DKV1BoDFsgugvfvm/mrc3QfIUPDA==\n" +
        "-----END CERTIFICATE-----");
    }
    final JsonObject withSSLAndCertKeyPath = new JsonObject()
      .put("ssl", true)
      .put("keyPath", tmpKeyFile.getAbsolutePath())
      .put("certPath", tmpCertFile.getAbsolutePath());

    // when
    final SslSettings sslSettings = new MongoClientOptionsParser(vertx, withSSLAndCertKeyPath)
      .settings()
      .getSslSettings();

    // then
    assertNotNull(sslSettings.getContext());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKey() throws IOException {
    // given
    final File tmpKeyFile = tmpFolder.newFile("brokenKey.pem");
    try (final FileWriter tmpKeyWriter = new FileWriter(tmpKeyFile)) {
      tmpKeyWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICljCCAfigAwIBAgI...BROKEN...xsykBBTOIVXnYdPkdZvvnoAIcfA7iM\n" +
        "-----END CERTIFICATE-----");
    }
    final File tmpCertFile = tmpFolder.newFile("validCert.pem");
    try (final FileWriter tmpCertWriter = new FileWriter(tmpCertFile)) {
      tmpCertWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICwTCCAamgAwIBAgIEBeVm4jANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDEwZj\n" +
        "bGllbnQwHhcNMTgwNTI2MTEzNjUxWhcNMjEwNTI1MTEzNjUxWjARMQ8wDQYDVQQD\n" +
        "EwZjbGllbnQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDVmCecLdUZ\n" +
        "U917hweVz4JqvZ9vZEi1rH+BG98HYfRR/h3QaobxPImZu3hzKHZ+MPbm94HunLPA\n" +
        "VA9yZhvZMToNfOuD4TUPBPloBuNzwBfZk2O4CaXeG4ailVWUfm5t/l+RD/55zYKu\n" +
        "hw1/Vl9lcOryF2XAmPQ2F1gwEKK7wt1Ak8zw8/yeYgBv1/F+ibCMvR6FVj9ABBEf\n" +
        "TM+oOs4oy51otUv0h63GqYgXMJyLX7q+AGWdC3srwwLQROtkzi7y00g/YryXUoIq\n" +
        "dXEI7CrNL35rZXcZ5LfGRwFX9evX11PpT3OShYlsJBcFE9KMatRoIWd6xUKlxTk0\n" +
        "yLjoOUE2tsMJAgMBAAGjITAfMB0GA1UdDgQWBBQ6xJBQsJCJdj/u0iTLYYD2qQsB\n" +
        "DDANBgkqhkiG9w0BAQsFAAOCAQEAfoquV375+eAGmfnlLxB30v9VhsFckrxFVpYs\n" +
        "XXC6h2G8MtXLpIEpgJo+4SZ4YjNwf/8m9J5j/duU8RukYanyzJdgkFFqKDBYCX7U\n" +
        "SD1nQP7729KnQgxtbR/+i3zkNgo7FATdkLq+HOxklNOEE24Ldenya39bsG779B9n\n" +
        "Sskcbq++7rMM+onDYBv6PbUKCm6nfqPspq809CLxSaUJg9+9ykut6hiyke/i7GEP\n" +
        "XIZHrM+mEvG00ES/zBIdV6TE0AIBP7q2MN7ylT509Ko9sUBMOZdEzikYp5GaRdiv\n" +
        "zG9q6rqK5COK614BwJFOD1DKV1BoDFsgugvfvm/mrc3QfIUPDA==\n" +
        "-----END CERTIFICATE-----");
    }
    final JsonObject withSSLAndCertKeyPath = new JsonObject()
      .put("ssl", true)
      .put("keyPath", tmpKeyFile.getAbsolutePath())
      .put("certPath", tmpCertFile.getAbsolutePath());

    // then
    new MongoClientOptionsParser(vertx, withSSLAndCertKeyPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCertificate() throws IOException {
    // given
    final File tmpKeyFile = tmpFolder.newFile("validKey.pem");
    try (final FileWriter tmpKeyWriter = new FileWriter(tmpKeyFile)) {
      tmpKeyWriter.write("-----BEGIN PRIVATE KEY-----\n" +
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDVmCecLdUZU917\n" +
        "hweVz4JqvZ9vZEi1rH+BG98HYfRR/h3QaobxPImZu3hzKHZ+MPbm94HunLPAVA9y\n" +
        "ZhvZMToNfOuD4TUPBPloBuNzwBfZk2O4CaXeG4ailVWUfm5t/l+RD/55zYKuhw1/\n" +
        "Vl9lcOryF2XAmPQ2F1gwEKK7wt1Ak8zw8/yeYgBv1/F+ibCMvR6FVj9ABBEfTM+o\n" +
        "Os4oy51otUv0h63GqYgXMJyLX7q+AGWdC3srwwLQROtkzi7y00g/YryXUoIqdXEI\n" +
        "7CrNL35rZXcZ5LfGRwFX9evX11PpT3OShYlsJBcFE9KMatRoIWd6xUKlxTk0yLjo\n" +
        "OUE2tsMJAgMBAAECggEAdewZAjqzidYpU0eLQoRcBj5GRaNiGRrxEgCnM1Y7IwFe\n" +
        "yG/nrEu11DASIdHXCXhS99Tx4SCWhLpkBM6m1VQ+LrAm/ppZRr+CSpJzBLaq9C5R\n" +
        "QYviDSu5Ow2jP+ZFZWiorlfcMLbrTRu2sfSnmkOrEpkkTh6jxTFCONcWYP8GU93D\n" +
        "YCA3hSH0li7CueS+GYJ1JB2Cd7buu+tOhl36AhBD96miExlgNn0YGpTJJ3I0Hb+O\n" +
        "lKIIQy+KK8f9TXrSeZC3OYlTtJaIr9ejspTXxIYN11EIit5MFEwnnkCglcsePjsx\n" +
        "qeOFRumJ5Nj5H8qyCNZ5MtzwbLkyktJzlumvnyr+AQKBgQDv/QfGKZJFeoCEWpoj\n" +
        "f+078JxSYyPVNXxbbr2NuN/V79hJBol87ukycz2CZkDCubIKfubc50eXDmhWCp4p\n" +
        "aJgl6BMhnovftYrIrGWJLwqXnwFwsKJSrJJqHlHDJDRGfUSQEWNclNeaB3Mr8W46\n" +
        "Zcaadeikstvka9xKA1LOCG3oIQKBgQDj2FFOxZK27KhY/9Oz1dUsPtAYYbLOor/P\n" +
        "Rbne3jICQStH3dnUEmWKIKrdYV1u2saw5djn3ujwB0xEXydRvRgiSF0qxYjbm9CG\n" +
        "TJaiHhTsQDjWkYMZaxk3gc7Yfh8DHF0wlvWpu1wMXNsCJ6jxqW2e+jSRioZICPK6\n" +
        "McWWmArd6QKBgDWjoHEyKXdOAhuTBJCarzOOe+IONpwY8EqfXc6nW6A9k2H/DAvY\n" +
        "elbEWyMiJ6deSeT+qCsHpoCkv707ck5fCmKulFgXT7wYn4Rqw+b9lKh+6Zt+X0mL\n" +
        "OM5vKGctWGHI7eIlgMfYnLfYom1X8QMsbE9puy3UrEFJulrwkzlpuOcBAoGAVRNV\n" +
        "sNsXIFSXu7uyueizU3UU0LXSRVQB2QxJDg3bkHnzBj+xcX15Cq2N/2G2uIjaPf1l\n" +
        "E5dpVQ70jGcXUG8SDuMEXs8pfg7dOvhoGpqu51RHpN7qm9ggr1g5+x6Ex+2UYmtL\n" +
        "yZfbFAasBE74x1ujQgRdEqct4sHsmFezVrro+9kCgYEAgl70mKk9yK/f7515OaO0\n" +
        "Y39tgVzpAG6RN1NKnY6NR5VNNemZx5jhKfk5byaYxX4XBjygD0sQ5KTpaZmoQIIX\n" +
        "FxuwhLRRMn6vtsEf1HexJAtRd82aL5wKS62l0AXG/CVLAygn4aSSqLrgTyFFVUR3\n" +
        "cASPpPIdZaKZG6q4Hmcpl58=\n" +
        "-----END PRIVATE KEY-----");
    }
    final File tmpCertFile = tmpFolder.newFile("brokenCert.pem");
    try (final FileWriter tmpCertWriter = new FileWriter(tmpCertFile)) {
      tmpCertWriter.write("-----BEGIN CERTIFICATE-----\n" +
        "MIICwTCCAamgAwIBA...BROKEN...FOD1DKV1BoDFsgugvfvm/mrc3QfIUPDA==\n" +
        "-----END CERTIFICATE-----");
    }
    final JsonObject withSSLAndCertKeyPath = new JsonObject()
      .put("ssl", true)
      .put("keyPath", tmpKeyFile.getAbsolutePath())
      .put("certPath", tmpCertFile.getAbsolutePath());

    // then
    new MongoClientOptionsParser(vertx, withSSLAndCertKeyPath);
  }
}
