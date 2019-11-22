package io.vertx.ext.mongo.impl.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.AsynchronousSocketChannelStreamFactoryFactory;
import com.mongodb.connection.StreamFactoryFactory;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import io.vertx.core.json.JsonObject;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class ParsingStreamTypeTest {

  @Test
  public void should_not_include_any_stream_type_by_default_for_backwards_compatibility() {
    // given
    final JsonObject noStreamTypeProvided = new JsonObject().put(
      "connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs"
    );

    // when
    final MongoClientSettings parsedSettings = new MongoClientOptionsParser(noStreamTypeProvided).settings();

    // then
    assertNull(parsedSettings.getStreamFactoryFactory());
  }

  @Parameters(method = "validSteamTypes")
  @Test
  public void should_parse_stream_type_from_connection_string(String streamTypeString, Class<StreamFactoryFactory> streamType) {
    // given
    final JsonObject cfgWithStreamTypeProvided = new JsonObject().put(
      "connection_string",
      format("mongodb://localhost:27017/mydb?replicaSet=myRs&streamType=%s", streamTypeString)
    );

    // when
    final MongoClientSettings parsedSettings = new MongoClientOptionsParser(cfgWithStreamTypeProvided).settings();

    // then
    assertThat(parsedSettings.getStreamFactoryFactory(), instanceOf(streamType));
  }

  @Test(expected = IllegalArgumentException.class)
  public void only_valid_stream_type_values_allowed_in_connection_string() {
    // given
    final JsonObject withInvalidStreamType = new JsonObject().put(
      "connection_string",
      "mongodb://localhost:27017/mydb?replicaSet=myRs&streamType=unrecognized"
    );

    // expect thrown
    new MongoClientOptionsParser(withInvalidStreamType).settings();
  }

  @Parameters(method = "validSteamTypes")
  @Test
  public void should_parse_stream_type_from_config_property(String streamTypeString, Class<StreamFactoryFactory> streamType) {
    // given
    final JsonObject cfgWithStreamTypeProvided = new JsonObject().put("streamType", streamTypeString);

    // when
    final MongoClientSettings parsedSettings = new MongoClientOptionsParser(cfgWithStreamTypeProvided).settings();

    // then
    assertThat(parsedSettings.getStreamFactoryFactory(), instanceOf(streamType));
  }

  @Test(expected = IllegalArgumentException.class)
  public void only_valid_stream_type_values_allowed_as_config_property() {
    // given
    final JsonObject withInvalidStreamType = new JsonObject().put("streamType", "unrecognized");

    // expect thrown
    new MongoClientOptionsParser(withInvalidStreamType).settings();
  }

  private Object[] validSteamTypes() {
    return new Object[]{
      new Object[]{"nio2", AsynchronousSocketChannelStreamFactoryFactory.class},
      new Object[]{"netty", NettyStreamFactoryFactory.class},
    };
  }
}
