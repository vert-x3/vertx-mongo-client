package io.vertx.ext.mongo.impl.config;

import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class WriteConcernParser {
  private final WriteConcern writeConcern;

  public WriteConcernParser(JsonObject config) {
    // Allow convenient string value for writeConcern e.g. ACKNOWLEDGED, SAFE, MAJORITY, etc
    WriteConcern wc;
    String wcs = config.getString("writeConcern");
    if (wcs != null) {
      wc = WriteConcern.valueOf(wcs);
      if (wc == null) throw new IllegalArgumentException("Invalid WriteConcern " + wcs);
    } else {
      // Support advanced write concern options. There's some inconsistencies between driver options
      // and mongo docs [http://bit.ly/10SYO6x] but we'll be consistent with the driver for this.
      Boolean safe = config.getBoolean("safe");
      Object w = config.getValue("w");
      Integer wtimeout = config.getInteger("wtimeoutMS", null);
      Boolean fsync = config.getBoolean("fsync", null); // This doesn't exist in mongo docs, but you can specify it for driver...
      Boolean j = config.getBoolean("j", null);
      if (j == null) {
        j = config.getBoolean("journal", null); //TODO: Inconsistency with driver and mongo docs, support both ?
      }


      if (w != null || wtimeout != null || (fsync !=null  && fsync) || (j != null  && j)) {
        if (w == null) {
          wc = new WriteConcern(1);
        } else {
          if (w instanceof String) {
            wc = new WriteConcern((String) w);
          } else if (w instanceof Integer) {
            wc = new WriteConcern((int) w);
          } else {
            throw new IllegalArgumentException("Invalid type " + w.getClass() + " for w of WriteConcern");
          }
        }

        if (wtimeout != null) {
          wc = wc.withWTimeout(wtimeout, TimeUnit.MILLISECONDS);
        }
        if (j != null) {
          wc = wc.withJournal(j);
        }
        if (fsync != null) {
          wc = wc.withFsync(fsync);
        }

      } else if (safe != null) {
        wc = safe ? WriteConcern.ACKNOWLEDGED : WriteConcern.UNACKNOWLEDGED;
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
