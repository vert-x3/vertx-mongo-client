package io.vertx.ext.mongo.impl.config;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class MongoClientOptionsParserTest {
  @Test
  public void testConnStringDbName() {
    String connectionString = "mongodb://localhost:27017/mydb";
    JsonObject config = new JsonObject().put("connection_string", connectionString).put("db_name", "unused_db");

    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    assertEquals("mydb", parser.database());
  }

  @Test
  public void testDbName() {
    String connectionString = "mongodb://localhost:27017/";
    JsonObject config = new JsonObject().put("connection_string", connectionString).put("db_name", "my_db");

    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    assertEquals("my_db", parser.database());
  }
}
