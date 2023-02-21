package io.vertx.ext.mongo.impl.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoClientOptionsParserTest {
  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @After
  public void tearDown() {
    vertx.close();
  }

  @Test
  public void testConnStringDbName() {
    String connectionString = "mongodb://localhost:27017/unused_db";
    JsonObject config = new JsonObject().put("connection_string", connectionString).put("db_name", "mydb");

    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    assertEquals("mydb", parser.database());
  }

  @Test
  public void testDbName() {
    String connectionString = "mongodb://localhost:27017/";
    JsonObject config = new JsonObject().put("connection_string", connectionString).put("db_name", "my_db");

    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    assertEquals("my_db", parser.database());
  }

  @Test
  public void testCompressorsSettings() {
    String connectionString = "mongodb://localhost:27017/";
    JsonObject config = new JsonObject()
      .put("connection_string", connectionString)
      .put("compressors", "snappy,zstd");

    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    assertEquals(2, parser.settings().getCompressorList().size());
  }
}
