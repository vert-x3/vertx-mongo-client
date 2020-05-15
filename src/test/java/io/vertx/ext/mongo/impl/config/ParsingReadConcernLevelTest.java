package io.vertx.ext.mongo.impl.config;

import com.mongodb.ReadConcern;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class ParsingReadConcernLevelTest {
  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @After
  public void tearDown() {
    vertx.close();
  }

  @Parameters(method = "validReadConcernValues")
  @Test
  public void should_parse_read_concern_from_connection_string(String readConcernString, ReadConcern expectedReadConcern) throws Exception {
    // given
    final JsonObject configWithConnectionString = new JsonObject().put(
      "connection_string",
      String.format("mongodb://localhost:27017/mydb?replicaSet=myRs&readconcernlevel=%s", readConcernString)
    );

    // when
    final ReadConcern parsedReadConcern = new MongoClientOptionsParser(vertx, configWithConnectionString)
      .settings()
      .getReadConcern();

    // then
    assertEquals(expectedReadConcern, parsedReadConcern);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_throw_an_exception_in_case_of_unrecognized_read_concern_level_in_connection_string() throws Exception {
    // given
    final JsonObject configWithConnectionString = new JsonObject().put(
      "connection_string",
      "mongodb://localhost:27017/mydb?replicaSet=myRs&readconcernlevel=unrecognized"
    );

    // when
    new MongoClientOptionsParser(vertx, configWithConnectionString).settings().getReadConcern();
  }

  @Parameters(method = "validReadConcernValues")
  @Test
  public void should_fallback_to_config_property_if_read_concern_not_present_in_connection_string(String readConcernString, ReadConcern expectedReadConcern) throws Exception {
    // given
    final JsonObject configWithReadConcernAsSeparateProperty = new JsonObject()
      .put("connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs")
      .put("readConcernLevel", readConcernString);

    // when
    final ReadConcern parsedReadConcern = new MongoClientOptionsParser(vertx, configWithReadConcernAsSeparateProperty)
      .settings()
      .getReadConcern();

    // then
    assertEquals(expectedReadConcern, parsedReadConcern);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_throw_an_exception_in_case_of_unrecognized_read_concern_level_passed_as_config_property() throws Exception {
    // given
    final JsonObject configWithReadConcernAsSeparateProperty = new JsonObject()
      .put("connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs")
      .put("readConcernLevel", "unrecognized");

    // when
    new MongoClientOptionsParser(vertx, configWithReadConcernAsSeparateProperty).settings().getReadConcern();
  }

  @Test
  public void should_return_default_read_concern_in_case_of_missing_read_concern_in_connection_string_or_config_object() throws Exception {
    // given
    final JsonObject configWithConnectionString = new JsonObject().put(
      "connection_string",
      "mongodb://localhost:27017/mydb?replicaSet=myRs"
    );

    // when
    final ReadConcern parsedReadConcern = new MongoClientOptionsParser(vertx, configWithConnectionString)
      .settings()
      .getReadConcern();

    // then
    assertEquals(ReadConcern.DEFAULT, parsedReadConcern);
  }

  @Test
  public void should_prefer_read_concern_passed_via_connection_string_over_property_value() throws Exception {
    // given
    final JsonObject configWithReadConcernPassedTwice = new JsonObject()
      .put("connection_string", "mongodb://localhost:27017/mydb?replicaSet=myRs&readconcernlevel=majority")
      .put("readConcernLevel", "linearizable");

    // when
    final ReadConcern parsedReadConcern = new MongoClientOptionsParser(vertx, configWithReadConcernPassedTwice)
      .settings()
      .getReadConcern();

    // then
    assertEquals(ReadConcern.MAJORITY, parsedReadConcern);
  }

  private Object[] validReadConcernValues() {
    return new Object[]{
      new Object[]{"local", ReadConcern.LOCAL},
      new Object[]{"majority", ReadConcern.MAJORITY},
      new Object[]{"linearizable", ReadConcern.LINEARIZABLE}
    };
  }
}
