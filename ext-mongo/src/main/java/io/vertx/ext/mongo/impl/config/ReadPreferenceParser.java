package io.vertx.ext.mongo.impl.config;

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
class ReadPreferenceParser {
  private final ReadPreference readPreference;

  public ReadPreferenceParser(JsonObject config) {
    ReadPreference rp;
    String rps = config.getString("readPreference");
    if (rps != null) {
      JsonArray readPreferenceTags = config.getJsonArray("readPreferenceTags");
      if (readPreferenceTags == null) {
        rp = ReadPreference.valueOf(rps);
        if (rp == null) throw new IllegalArgumentException("Invalid ReadPreference " + rps);
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
        rp = ReadPreference.valueOf(rps, tagSet);
      }
    } else {
      rp = null;
    }

    this.readPreference = rp;
  }

  public ReadPreference readPreference() {
    return readPreference;
  }
}
