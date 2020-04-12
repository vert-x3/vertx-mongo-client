package io.vertx.ext.mongo.impl.config;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoCredential;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoClientImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.AuthenticationMechanism.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class CredentialListParser {

  private final List<MongoCredential> credentials;

  public CredentialListParser(JsonObject config) {
    String username = config.getString("username");
    if (username == null) {
      credentials = Collections.emptyList();
    } else {
      credentials = new ArrayList<>();
      String passwd = config.getString("password");
      char[] password = (passwd == null) ? null : passwd.toCharArray();
      // See https://github.com/vert-x3/vertx-mongo-client/issues/46 - 'admin' as default is a security
      // concern, use  the 'db_name' if none is set.
      String authSource = config.getString("authSource",
        config.getString("db_name", MongoClientImpl.DEFAULT_DB_NAME));

      // AuthMechanism
      AuthenticationMechanism mechanism = null;
      String authMechanism = config.getString("authMechanism");
      if (authMechanism != null) {
        mechanism = getAuthenticationMechanism(authMechanism);
      }

      // MongoCredential
      String gssapiServiceName = config.getString("gssapiServiceName");
      MongoCredential credential;
      if (mechanism == GSSAPI) {
        credential = MongoCredential.createGSSAPICredential(username);
        credential = getMongoCredential(gssapiServiceName, credential);
      } else if (mechanism == PLAIN) {
        credential = MongoCredential.createPlainCredential(username, authSource, password);
      } else if (mechanism == MONGODB_X509) {
        credential = MongoCredential.createMongoX509Credential(username);
      } else if (mechanism == SCRAM_SHA_1) {
        credential = MongoCredential.createScramSha1Credential(username, authSource, password);
      } else if (mechanism == SCRAM_SHA_256) {
        credential = MongoCredential.createScramSha256Credential(username, authSource, password);
      } else if (mechanism == null) {
        credential = MongoCredential.createCredential(username, authSource, password);
      } else {
        throw new IllegalArgumentException("Unsupported authentication mechanism " + mechanism);
      }

      credentials.add(credential);
    }
  }

  private MongoCredential getMongoCredential(String gssapiServiceName, MongoCredential credential) {
    if (gssapiServiceName != null) {
      credential = credential.withMechanismProperty("SERVICE_NAME", gssapiServiceName);
    }
    return credential;
  }

  private AuthenticationMechanism getAuthenticationMechanism(String authMechanism) {
    AuthenticationMechanism mechanism;
    try {
      mechanism = AuthenticationMechanism.fromMechanismName(authMechanism);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid authMechanism '" + authMechanism + "'");
    }
    return mechanism;
  }

  public List<MongoCredential> credentials() {
    return credentials;
  }
}
