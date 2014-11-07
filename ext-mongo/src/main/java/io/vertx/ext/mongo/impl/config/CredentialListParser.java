package io.vertx.ext.mongo.impl.config;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.AuthenticationMechanism.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class CredentialListParser {

  private final List<MongoCredential> credentials;

  public CredentialListParser(ConnectionString connectionString, JsonObject config) {
    if (connectionString != null) {
      credentials = connectionString.getCredentialList();
    } else {
      String username = config.getString("username");
      if (username == null) {
        credentials = Collections.emptyList();
      } else {
        credentials = new ArrayList<>();
        String passwd = config.getString("password");
        char[] password = (passwd == null) ? null : passwd.toCharArray();
        String authSource = config.getString("authSource", "admin");

        // AuthMechanism
        AuthenticationMechanism mechanism = null;
        String authMechanism = config.getString("authMechanism");
        if (authMechanism != null) {
          try {
            mechanism = AuthenticationMechanism.fromMechanismName(authMechanism);
          } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid authMechanism '" + authMechanism + "'");
          }
        }

        // MongoCredential
        String gssapiServiceName = config.getString("gssapiServiceName");
        MongoCredential credential;
        if (mechanism == GSSAPI) {
          credential = MongoCredential.createGSSAPICredential(username);
          if (gssapiServiceName != null) {
            credential = credential.withMechanismProperty("SERVICE_NAME", gssapiServiceName);
          }
        } else if (mechanism == PLAIN) {
          credential = MongoCredential.createPlainCredential(username, authSource, password);
        } else if (mechanism == MONGODB_CR) {
          credential = MongoCredential.createMongoCRCredential(username, authSource, password);
        } else if (mechanism == MONGODB_X509) {
          credential = MongoCredential.createMongoX509Credential(username);
        } else if (mechanism == SCRAM_SHA_1) {
          credential = MongoCredential.createScramSha1Credential(username, authSource, password);
        } else if (mechanism == null) {
          credential = MongoCredential.createCredential(username, authSource, password);
        } else {
          throw new IllegalArgumentException("Unsupported authentication mechanism " + mechanism);
        }

        credentials.add(credential);
      }
    }
  }

  public List<MongoCredential> credentials() {
    return credentials;
  }
}
