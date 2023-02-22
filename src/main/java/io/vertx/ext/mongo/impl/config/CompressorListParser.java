package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCompressor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompressorListParser {

  private final List<MongoCompressor> compressors;

  public CompressorListParser(ConnectionString connectionString, JsonObject config) {
    /*
    Configuration takes precedence over the value in the connection string,
    and the value in the connection string is ignored if they both exist.
     */
    List<MongoCompressor> compressors = fromConfig(config);
    if (compressors == null && connectionString != null) {
      compressors = connectionString.getCompressorList();
    }
    this.compressors = compressors;
  }

  private List<MongoCompressor> fromConfig(JsonObject config) {
    JsonArray compressors = config.getJsonArray("compressors");
    if (compressors == null || compressors.isEmpty()) {
      return null;
    }
    List<MongoCompressor> compressorsList = new ArrayList<>(compressors.size());
    for (Object o : compressors) {
      String name = (String) o;
      if (name.equalsIgnoreCase("zlib")) {
        MongoCompressor zlibCompressor = MongoCompressor.createZlibCompressor();
        Integer zlibCompressionLevel = config.getInteger("zlibCompressionLevel");
        if (zlibCompressionLevel != null) {
          zlibCompressor = zlibCompressor.withProperty(MongoCompressor.LEVEL, zlibCompressionLevel);
        }
        compressorsList.add(zlibCompressor);
      } else if (name.equalsIgnoreCase("snappy")) {
        compressorsList.add(MongoCompressor.createSnappyCompressor());
      } else if (name.equalsIgnoreCase("zstd")) {
        compressorsList.add(MongoCompressor.createZstdCompressor());
      } else {
        throw new IllegalArgumentException("Unsupported compressor '" + name + "'");
      }
    }
    return Collections.unmodifiableList(compressorsList);
  }

  public List<MongoCompressor> compressorList() {
    return compressors;
  }

}
