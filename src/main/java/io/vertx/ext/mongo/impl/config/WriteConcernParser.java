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
    WriteConcern writeConcern = fromConfig(config);
    if (writeConcern == null && connectionString != null) {
      writeConcern = connectionString.getWriteConcern();
    }
    this.writeConcern = writeConcern;
  }

  private WriteConcern fromConfig(JsonObject config) {
    WriteConcern writeConcern = null;
    // Allow convenient string value for writeConcern e.g. ACKNOWLEDGED, SAFE, MAJORITY, etc
    String wcs = config.getString("writeConcern");
    if (wcs != null) {
      writeConcern = WriteConcern.valueOf(wcs);
      if (writeConcern == null) throw new IllegalArgumentException("Invalid WriteConcern " + wcs);
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
          writeConcern = new WriteConcern(1);
        } else {
          writeConcern = getWriteConcern(w);
        }

        if (wtimeout != null) {
          writeConcern = writeConcern.withWTimeout(wtimeout, TimeUnit.MILLISECONDS);
        }
        if (j != null) {
          writeConcern = writeConcern.withJournal(j);
        }
      } else if (safe != null) {
        writeConcern = safe ? WriteConcern.ACKNOWLEDGED : WriteConcern.UNACKNOWLEDGED;
      }
    }
    return writeConcern;
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
