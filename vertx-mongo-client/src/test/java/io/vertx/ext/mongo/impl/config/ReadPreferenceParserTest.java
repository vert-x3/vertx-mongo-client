package io.vertx.ext.mongo.impl.config;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.TaggableReadPreference;
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
    ReadPreference rp = new ReadPreferenceParser(new JsonObject()).readPreference();
    assertNull(rp);
  }

  @Test
  public void testReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "primary");

    ReadPreference rp = new ReadPreferenceParser(config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test
  public void testReadPreferenceCaseInsenitive() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "PRIMARY");

    ReadPreference rp = new ReadPreferenceParser(config).readPreference();
    assertNotNull(rp);
    assertEquals(ReadPreference.primary(), rp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "foo");

    new ReadPreferenceParser(config).readPreference();
  }

  @Test(expected = ClassCastException.class)
  public void testInvalidTypeReadPreference() {
    JsonObject config = new JsonObject();
    config.put("readPreference", 123);

    new ReadPreferenceParser(config).readPreference();
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

    ReadPreference rp = new ReadPreferenceParser(config).readPreference();
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
    new ReadPreferenceParser(config).readPreference();
  }

  @Test(expected = ClassCastException.class)
  public void testInvalidReadPreferenceTagType() {
    JsonObject config = new JsonObject();
    config.put("readPreference", "nearest");

    JsonArray array = new JsonArray();
    array.add(1);
    config.put("readPreferenceTags", array);
    new ReadPreferenceParser(config).readPreference();
  }
}
