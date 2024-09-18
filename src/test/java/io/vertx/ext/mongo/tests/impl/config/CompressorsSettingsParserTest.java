package io.vertx.ext.mongo.tests.impl.config;

import com.mongodb.MongoCompressor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompressorsSettingsParserTest {

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
  public void testCompressorsConnectionString() {
    String connectionString = "mongodb://localhost:27017/?compressors=zstd,zlib&zlibCompressionLevel=1";
    JsonObject config = new JsonObject()
      .put("connection_string", connectionString);

    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    List<MongoCompressor> compressorList = parser.settings().getCompressorList();
    assertEquals("zstd", compressorList.get(0).getName());
    assertEquals("zlib", compressorList.get(1).getName());
    assertEquals(1, (int) compressorList.get(1).getPropertyNonNull(MongoCompressor.LEVEL, 0));
    assertEquals(2, compressorList.size());
  }

  @Test
  public void testCompressorsConfig() {
    String connectionString = "mongodb://localhost:27017/?compressors=zstd,zlib&zlibCompressionLevel=1";
    JsonObject config = new JsonObject()
      .put("connection_string", connectionString)
      .put("compressors", new JsonArray().add("snappy").add("zstd").add("zlib"))
      .put("zlibCompressionLevel", 6);

    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    List<MongoCompressor> compressorList = parser.settings().getCompressorList();
    assertEquals("snappy", compressorList.get(0).getName());
    assertEquals("zstd", compressorList.get(1).getName());
    assertEquals("zlib", compressorList.get(2).getName());
    assertEquals(6, (int) compressorList.get(2).getPropertyNonNull(MongoCompressor.LEVEL, 0));
    assertEquals(3, compressorList.size());
  }
}
