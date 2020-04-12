package io.vertx.ext.mongo.impl.config;

import com.mongodb.connection.AsynchronousSocketChannelStreamFactoryFactory;
import com.mongodb.connection.StreamFactoryFactory;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

class StreamTypeParser {
  private final JsonObject config;

  StreamTypeParser(JsonObject config) {
    this.config = config;
  }

  Optional<StreamFactoryFactory> streamFactory() {
    return Optional.ofNullable(config.getString("streamType"))
      .map(StreamType::parse)
      .map(StreamType::streamFactory);
  }

  private enum StreamType {
    NIO2 {
      @Override
      StreamFactoryFactory streamFactory() {
        return AsynchronousSocketChannelStreamFactoryFactory.builder().build();
      }
    },

    NETTY {
      @Override
      StreamFactoryFactory streamFactory() {
        return NettyStreamFactoryFactory.builder().build();
      }
    };

    abstract StreamFactoryFactory streamFactory();

    static StreamType parse(String streamType) {
      try {
        return valueOf(streamType.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Not supported StreamType. Supported values are [nio2|netty]");
      }
    }
  }
}
