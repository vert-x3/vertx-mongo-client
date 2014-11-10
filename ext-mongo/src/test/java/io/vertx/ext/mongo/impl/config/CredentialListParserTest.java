package io.vertx.ext.mongo.impl.config;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoCredential;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class CredentialListParserTest {
  @Test
  public void testSimpleAuth() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    config.put("username", username);
    config.put("password", password);


    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertArrayEquals(password.toCharArray(), credential.getPassword());
    // default source should be admin
    assertEquals("admin", credential.getSource());
  }

  @Test
  public void testSimpleAuthWithSource() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("password", password);
    config.put("authSource", authSource);

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertArrayEquals(password.toCharArray(), credential.getPassword());
    assertEquals(authSource, credential.getSource());
  }

  @Test
  public void testAuth_GSSAPI() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("authSource", authSource);
    config.put("authMechanism", "GSSAPI");

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertNotEquals(authSource, credential.getSource()); // It should ignore the source we pass in

    assertEquals(AuthenticationMechanism.GSSAPI, credential.getAuthenticationMechanism());
  }

  @Test
  public void testAuth_GSSAPI_WithServiceName() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String authSource = TestUtils.randomAlphaString(10);
    String serviceName = TestUtils.randomAlphaString(11);
    config.put("username", username);
    config.put("authSource", authSource);
    config.put("authMechanism", "GSSAPI");
    config.put("gssapiServiceName", serviceName);

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertNotEquals(authSource, credential.getSource()); // It should ignore the source we pass in

    assertEquals(AuthenticationMechanism.GSSAPI, credential.getAuthenticationMechanism());
    assertEquals(serviceName, credential.getMechanismProperty("SERVICE_NAME", null));
  }

  @Test
  public void testAuth_PLAIN() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("password", password);
    config.put("authSource", authSource);
    config.put("authMechanism", "PLAIN");

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertArrayEquals(password.toCharArray(), credential.getPassword());
    assertEquals(authSource, credential.getSource());

    assertEquals(AuthenticationMechanism.PLAIN, credential.getAuthenticationMechanism());
  }

  @Test
  public void testAuth_MONGODB_CR() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("password", password);
    config.put("authSource", authSource);
    config.put("authMechanism", "MONGODB-CR");

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertArrayEquals(password.toCharArray(), credential.getPassword());
    assertEquals(authSource, credential.getSource());

    assertEquals(AuthenticationMechanism.MONGODB_CR, credential.getAuthenticationMechanism());
  }

  @Test
  public void testAuth_MONGODB_X509() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("authSource", authSource);
    config.put("authMechanism", "MONGODB-X509");

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertNotEquals(authSource, credential.getSource()); // It should ignore the source we pass in

    assertEquals(AuthenticationMechanism.MONGODB_X509, credential.getAuthenticationMechanism());
  }

  @Test
  public void testAuth_SCRAM_SHA_1() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("password", password);
    config.put("authSource", authSource);
    config.put("authMechanism", "SCRAM-SHA-1");

    List<MongoCredential> credentials = new CredentialListParser(null, config).credentials();
    assertEquals(1, credentials.size());
    MongoCredential credential = credentials.get(0);
    assertEquals(username, credential.getUserName());
    assertArrayEquals(password.toCharArray(), credential.getPassword());
    assertEquals(authSource, credential.getSource());

    assertEquals(AuthenticationMechanism.SCRAM_SHA_1, credential.getAuthenticationMechanism());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAuth_Invalid() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    String password = TestUtils.randomAlphaString(20);
    String authSource = TestUtils.randomAlphaString(10);
    config.put("username", username);
    config.put("password", password);
    config.put("authSource", authSource);
    config.put("authMechanism", "FOO-BAR");

    new CredentialListParser(null, config).credentials();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAuth_NoPassword() {
    JsonObject config = new JsonObject();
    String username = TestUtils.randomAlphaString(8);
    config.put("username", username);

    new CredentialListParser(null, config).credentials();
  }
}
