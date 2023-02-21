package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCompressor;
import com.mongodb.lang.Nullable;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CompressorListParser {

  private final List<String> compressorKeys;
  private final ConnectionString connectionString;
  private final JsonObject config;

  public CompressorListParser(ConnectionString connectionString, JsonObject config) {
    this.connectionString = connectionString;
    this.config = config;

    this.compressorKeys = new ArrayList<>();
    this.compressorKeys.add("compressors");
    this.compressorKeys.add("zlibcompressionlevel");
  }

  public List<MongoCompressor> compressorList() {
    String compressors = "";
    Integer zlibCompressionLevel = null;

    for (final String key : compressorKeys) {
      String value = config.getString(key);
      if (value == null) {
        continue;
      }

      if (key.equals("compressors")) {
        compressors = value;
      } else if (key.equals("zlibcompressionlevel")) {
        zlibCompressionLevel = Integer.parseInt(value);
      }
    }

    List<MongoCompressor> connStrCompressors = connectionString.getCompressorList();
    if (compressors.trim().length() == 0) {
      return connStrCompressors;
    } else {
      List<MongoCompressor> compressorList = buildCompressors(compressors, zlibCompressionLevel);
      if (connStrCompressors != null) {
        compressorList.addAll(connStrCompressors);
      }
      return compressorList;
    }
  }

  private List<MongoCompressor> buildCompressors(final String compressors, @Nullable final Integer zlibCompressionLevel) {
    List<MongoCompressor> compressorsList = new ArrayList<>();

    for (String cur : compressors.split(",")) {
      if (cur.equals("zlib")) {
        MongoCompressor zlibCompressor = MongoCompressor.createZlibCompressor();
        if (zlibCompressionLevel != null) {
          zlibCompressor = zlibCompressor.withProperty(MongoCompressor.LEVEL, zlibCompressionLevel);
        }
        compressorsList.add(zlibCompressor);
      } else if (cur.equals("snappy")) {
        compressorsList.add(MongoCompressor.createSnappyCompressor());
      } else if (cur.equals("zstd")) {
        compressorsList.add(MongoCompressor.createZstdCompressor());
      } else if (!cur.isEmpty()) {
        throw new IllegalArgumentException("Unsupported compressor '" + cur + "'");
      }
    }

    return compressorsList;
  }

}
