package io.vertx.ext.mongo.impl.config;

import com.mongodb.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ReadPreferenceParserTest {

  @Test
  public void testNoReadPreference() {
    ReadPreference rp = new ReadPreferenceParser(null, new JsonObject()).readPreference();
    assertNull(rp);
  }

  @Test
  public void testReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "primary");

    ReadPreference rp = new ReadPreferenceParser(null, config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test
  public void testReadPreferenceCaseInsenitive() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "PRIMARY");

    ReadPreference rp = new ReadPreferenceParser(null, config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "foo");

    new ReadPreferenceParser(null, config).readPreference();
  }

  @Test(expected = ClassCastException.class)
  public void testInvalidTypeReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", 123);

    new ReadPreferenceParser(null, config).readPreference();
  }

  @Test
  public void testReadPreferenceTags() {
    List<TagSet> tagSets = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    tags.add(new Tag("dc1", "ny"));
    tags.add(new Tag("dc2", "tx"));
    tags.add(new Tag("dc3", "ca"));
    tagSets.add(new TagSet(tags));

    tags = new ArrayList<>();
    tags.add(new Tag("ac1", "ny"));
    tags.add(new Tag("ac2", "tx"));
    tags.add(new Tag("ac3", "ca"));
    tagSets.add(new TagSet(tags));

    ReadPreference expected = ReadPreference.valueOf("nearest", tagSets);

    JsonObject config = new JsonObject();
    config.put("readPreference", "nearest");

    JsonArray array = new JsonArray();
    array.add("dc1:ny,dc2:tx,dc3:ca");
    array.add("ac1:ny,ac2:tx,ac3:ca");
    config.put("readPreferenceTags", array);

    ReadPreference rp = new ReadPreferenceParser(null, config).readPreference();
    assertNotNull(rp);
    assertEquals(expected, rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadPreferenceTag() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "nearest");

    JsonArray array = new JsonArray();
    array.add("dc1:ny,foo,bar");
    config.put("readPreferenceTags", array);
    new ReadPreferenceParser(null, config).readPreference();
  }

  @Test(expected = ClassCastException.class)
  public void testInvalidReadPreferenceTagType() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "nearest");

    JsonArray array = new JsonArray();
    array.add(1);
    config.put("readPreferenceTags", array);
    new ReadPreferenceParser(null, config).readPreference();
  }

  @Test
  public void testConnStringNoReadPreference() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp");
    ReadPreference rp = new ReadPreferenceParser(connString, new JsonObject()).readPreference();
    assertNull(rp);
  }

  @Test
  public void testConnStringReadPreference() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp&readPreference=primaryPreferred");
    ReadPreference rp = new ReadPreferenceParser(connString, new JsonObject()).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primaryPreferred(), rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnStringInvalidReadPreference() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp&readPreference=foobar");

    new ReadPreferenceParser(connString, new JsonObject()).readPreference();
  }

  @Test
  public void testConnStringReadPreferenceTags() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp" +
        "&readPreference=nearest" +
        "&readPreferenceTags=dc:ny,rack:1" +
        "&readPreferenceTags=dc:ny" +
        "&readPreferenceTags=");

    List<TagSet> tagSets = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    tags.add(new Tag("dc", "ny"));
    tags.add(new Tag("rack", "1"));
    tagSets.add(new TagSet(tags));
    tags = new ArrayList<>();
    tags.add(new Tag("dc", "ny"));
    tagSets.add(new TagSet(tags));
    tagSets.add(new TagSet());

    ReadPreference expected = ReadPreference.valueOf("nearest", tagSets);

    ReadPreference rp = new ReadPreferenceParser(connString, new JsonObject()).readPreference();
    assertNotNull(rp);
    assertEquals(expected, rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConnStringInvalidReadPreferenceTag() {
    final ConnectionString connString = new ConnectionString("mongodb://localhost:27017/mydb?replicaSet=myapp" +
        "&readPreference=nearest&readPreferenceTags=dc:ny,foo,bar");
    new ReadPreferenceParser(connString, new JsonObject()).readPreference();
  }
}
