package io.vertx.ext.mongo.impl.config;

import com.mongodb.ConnectionString;
import com.mongodb.WriteConcern;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
class WriteConcernParser {
  private final WriteConcern writeConcern;

  WriteConcernParser(ConnectionString connectionString, JsonObject config) {
    WriteConcern connStringWriteConcern = null;
    if (connectionString != null) {
      // WRITE_CONCERN_KEYS("safe", "w", "wtimeoutms", "fsync", "journal");
      connStringWriteConcern = connectionString.getWriteConcern();
    }
    if (connStringWriteConcern != null) {
      // Prefer connection string's write concern
      writeConcern = connStringWriteConcern;
    } else {
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
        Boolean j = config.getBoolean("j", null);
        if (j == null) {
          j = config.getBoolean("journal", null); //TODO: Inconsistency with driver and mongo docs, support both ?
        }

        if (w != null || wtimeout != null || (j != null && j)) {
          if (w == null) {
            wc = new WriteConcern(1);
          } else {
            wc = getWriteConcern(w);
          }

          if (wtimeout != null) {
            wc = wc.withWTimeout(wtimeout, TimeUnit.MILLISECONDS);
          }
          if (j != null) {
            wc = wc.withJournal(j);
          }
        } else if (safe != null) {
          wc = safe ? WriteConcern.ACKNOWLEDGED : WriteConcern.UNACKNOWLEDGED;
        } else {
          wc = null; // no write concern
        }
      }

      writeConcern = wc;
    }
  }

  private WriteConcern getWriteConcern(Object w) {
    WriteConcern wc;
    if (w instanceof String) {
      wc = new WriteConcern((String) w);
    } else if (w instanceof Integer) {
      wc = new WriteConcern((int) w);
    } else {
      throw new IllegalArgumentException("Invalid type " + w.getClass() + " for w of WriteConcern");
    }
    return wc;
  }

  WriteConcern writeConcern() {
    return writeConcern;
  }
}
