package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ReadPreferenceParser {
  private final ReadPreference readPreference;

  public ReadPreferenceParser(ConnectionString connectionString, JsonObject config) {
    ReadPreference readPreference = fromConfig(config);
    if (readPreference == null && connectionString != null) {
      readPreference = connectionString.getReadPreference();
    }
    this.readPreference = readPreference;
  }

  private ReadPreference fromConfig(JsonObject config) {
    ReadPreference readPreference = null;
    String rps = config.getString("readPreference");
    if (rps != null) {
      JsonArray readPreferenceTags = config.getJsonArray("readPreferenceTags");
      if (readPreferenceTags == null) {
        readPreference = ReadPreference.valueOf(rps);
        if (readPreference == null) throw new IllegalArgumentException("Invalid ReadPreference " + rps);
      } else {
        // Support advanced ReadPreference Tags
        List<TagSet> tagSet = new ArrayList<>();
        readPreferenceTags.forEach(o -> {
          String tagString = (String) o;
          List<Tag> tags = Stream.of(tagString.trim().split(","))
            .map(s -> s.split(":"))
            .filter(array -> {
              if (array.length != 2) {
                throw new IllegalArgumentException("Invalid readPreferenceTags value '" + tagString + "'");
              }
              return true;
            }).map(array -> new Tag(array[0], array[1])).collect(Collectors.toList());

          tagSet.add(new TagSet(tags));
        });
        readPreference = ReadPreference.valueOf(rps, tagSet);
      }
    }
    return readPreference;
  }

  public ReadPreference readPreference() {
    return readPreference;
  }
}
