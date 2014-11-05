package io.vertx.ext.mongo.impl.config;

import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class WriteConcernParser extends AbstractParser {
  private final WriteConcern writeConcern;

  public WriteConcernParser(JsonObject config) {
    // Allow convenient string value for writeConcern e.g. ACKNOWLEDGED, SAFE, MAJORITY, etc
    WriteConcern wc;
    String wcs = get(config, "writeConcern", String.class);
    if (wcs != null) {
      wc = WriteConcern.valueOf(wcs);
      if (wc == null) throw new IllegalArgumentException("Invalid WriteConcern " + wcs);
    } else {
      // Support advanced write concern options. There's some inconsistencies between driver options
      // and mongo docs [http://bit.ly/10SYO6x] but we'll be consistent with the driver for this.
      Boolean safe = get(config, "safe", Boolean.class);
      Object w = config.getValue("w");
      int wtimeout = get(config, "wtimeoutMS", Integer.class, 0);
      boolean fsync = get(config, "fsync", Boolean.class, false); // This doesn't exist in mongo docs, but you can specify it for driver...
      boolean j = get(config, "j", Boolean.class, false);
      if (!j) {
        j = get(config, "journal", Boolean.class, false); //TODO: Inconsistency with driver and mongo docs, support both ?
      }

      if (w != null || wtimeout != 0 || fsync || j) {
        if (w == null) {
          wc = new WriteConcern(1, wtimeout, fsync, j);
        } else {
          if (w instanceof String) {
            wc = new WriteConcern((String) w, wtimeout, fsync, j);
          } else if (w instanceof Integer) {
            wc = new WriteConcern((int) w, wtimeout, fsync, j);
          } else {
            throw new IllegalArgumentException("Invalid type " + w.getClass() + " for w of WriteConcern");
          }
        }
      } else if (safe != null) {
        wc = (safe) ? WriteConcern.ACKNOWLEDGED : WriteConcern.UNACKNOWLEDGED;
      } else {
        wc = null; // no write concern
      }
    }

    writeConcern = wc;
  }

  public WriteConcern writeConcern() {
    return writeConcern;
  }
}
