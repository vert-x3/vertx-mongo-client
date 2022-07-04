package io.vertx.ext.mongo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Result propagated from mongodb driver bulk write result.
 *
 * @author sschmitt
 */
@DataObject
public class MongoClientBulkWriteResult {

  /**
   * Constant to be used when storing and retrieving Json for documents inserted.
   */
  public static final String INSERTED_COUNT = "insertedCount";

  /**
   * Constant to be used when storing and retrieving Json for documents matched.
   */
  public static final String MATCHED_COUNT = "matchedCount";

  /**
   * Constant to be used when storing and retrieving Json for documents modified.
   */
  public static final String MODIFIED_COUNT = "modifiedCount";

  /**
   * Constant to be used when storing and retrieving Json for documents deleted.
   */
  public static final String DELETED_COUNT = "deletedCount";

  /**
   * Constant to be used when storing and retrieving Json for upsert information.
   */
  public static final String UPSERTS = "upserts";

  /**
   * Constant to be used when storing and retrieving Json for insert information.
   */
  public static final String INSERTS = "inserts";

  /**
   * Constant to be used when storing and retrieving Json for ID of upsert information.
   */
  public static final String ID = "_id";

  /**
   * Constant to be used when storing and retrieving Json for index of upsert information.
   */
  public static final String INDEX = "index";

  /**
   * The default value is 0, signifying no document was inserted
   */
  public static final long DEFAULT_INSERTED_COUNT = 0l;

  /**
   * The default value is 0, signifying no document was matched
   */
  public static final long DEFAULT_MATCHED_COUNT = 0l;

  /**
   * The default value is 0, signifying no document was deleted
   */
  public static final long DEFAULT_DELETED_COUNT = 0l;

  /**
   * The default value is 0, signifying no document was modified
   */
  public static final long DEFAULT_MODIFIED_COUNT = 0l;

  private long insertedCount;
  private long matchedCount;
  private long deletedCount;
  private long modifiedCount;
  private List<JsonObject> upserts;
  private List<JsonObject> inserts;

  /**
   * Default constructor
   */
  public MongoClientBulkWriteResult() {
    insertedCount = DEFAULT_INSERTED_COUNT;
    matchedCount = DEFAULT_MATCHED_COUNT;
    deletedCount = DEFAULT_DELETED_COUNT;
    modifiedCount = DEFAULT_MODIFIED_COUNT;
  }

  /**
   * Constructor to specify the result of the bulk write operation.
   *
   * @param insertedCount
   *          the number of inserted documents
   * @param matchedCount
   *          the number of documents matched by update or replacements
   * @param deletedCount
   *          the number of deleted documents
   * @param modifiedCount
   *          the number of modified documents
   * @param upserts
   *          the list of upserted items
   * @param inserts
   *          the list of inserted items
   */
  public MongoClientBulkWriteResult(long insertedCount, long matchedCount, long deletedCount, long modifiedCount,
      List<JsonObject> upserts, List<JsonObject> inserts) {
    this.insertedCount = insertedCount;
    this.matchedCount = matchedCount;
    this.deletedCount = deletedCount;
    this.modifiedCount = modifiedCount;
    this.upserts = upserts;
    this.inserts = inserts;
  }

  /**
   * Copy constructor
   *
   * @param other
   */
  public MongoClientBulkWriteResult(MongoClientBulkWriteResult other) {
    insertedCount = other.insertedCount;
    matchedCount = other.matchedCount;
    deletedCount = other.deletedCount;
    modifiedCount = other.modifiedCount;
    if (other.upserts != null) {
      this.upserts = other.upserts.stream().map(JsonObject::copy).collect(Collectors.toList());
    } else
      this.upserts = null;
    if (other.inserts != null) {
      this.inserts = other.inserts.stream().map(JsonObject::copy).collect(Collectors.toList());
    } else
      this.inserts = null;
  }

  /**
   * Constructor from JSON
   *
   * @param mongoClientBulkWriteResultJson
   */
  public MongoClientBulkWriteResult(JsonObject mongoClientBulkWriteResultJson) {
    insertedCount = mongoClientBulkWriteResultJson.getLong(INSERTED_COUNT, DEFAULT_INSERTED_COUNT);
    matchedCount = mongoClientBulkWriteResultJson.getLong(MATCHED_COUNT, DEFAULT_MATCHED_COUNT);
    deletedCount = mongoClientBulkWriteResultJson.getLong(DELETED_COUNT, DEFAULT_DELETED_COUNT);
    modifiedCount = mongoClientBulkWriteResultJson.getLong(MODIFIED_COUNT, DEFAULT_MODIFIED_COUNT);
    JsonArray upsertArray = mongoClientBulkWriteResultJson.getJsonArray(UPSERTS);
    if (upsertArray != null)
      this.upserts = upsertArray.stream().filter(object -> object instanceof JsonObject)
          .map(object -> (JsonObject) object).collect(Collectors.toList());
    JsonArray insertArray = mongoClientBulkWriteResultJson.getJsonArray(INSERTS);
    if (insertArray != null)
      this.inserts = insertArray.stream().filter(object -> object instanceof JsonObject)
        .map(object -> (JsonObject) object).collect(Collectors.toList());
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject mongoClientBulkWriteResultJson = new JsonObject();

    if (insertedCount != DEFAULT_INSERTED_COUNT) {
      mongoClientBulkWriteResultJson.put(INSERTED_COUNT, insertedCount);
    }
    if (matchedCount != DEFAULT_MATCHED_COUNT) {
      mongoClientBulkWriteResultJson.put(MATCHED_COUNT, matchedCount);
    }
    if (deletedCount != DEFAULT_DELETED_COUNT) {
      mongoClientBulkWriteResultJson.put(DELETED_COUNT, deletedCount);
    }
    if (modifiedCount != DEFAULT_MODIFIED_COUNT) {
      mongoClientBulkWriteResultJson.put(MODIFIED_COUNT, modifiedCount);
    }
    if (upserts != null) {
      mongoClientBulkWriteResultJson.put(UPSERTS, new JsonArray(upserts));
    }
    if (inserts != null) {
      mongoClientBulkWriteResultJson.put(INSERTS, new JsonArray(inserts));
    }
    return mongoClientBulkWriteResultJson;
  }

  /**
   * Returns the number of inserted documents
   *
   * @return the inserted documents
   */
  public long getInsertedCount() {
    return insertedCount;
  }

  /**
   * Returns the number of matched documents
   *
   * @return the matched documents
   */
  public long getMatchedCount() {
    return matchedCount;
  }

  /**
   * Returns the number of deleted documents
   *
   * @return the deleted documents
   */
  public long getDeletedCount() {
    return deletedCount;
  }

  /**
   * Returns the number of modified documents
   *
   * @return the modified documents
   */
  public long getModifiedCount() {
    return modifiedCount;
  }

  /**
   * An unmodifiable list of upsert data. Each entry has the index of the request that lead to the upsert, and the
   * generated ID of the upsert.
   *
   * @return an unmodifiable list of upsert info
   */
  public List<JsonObject> getUpserts() {
    if (upserts != null)
      return Collections.unmodifiableList(upserts);
    else
      return null;
  }

  /**
   * An unmodifiable list of inserts data. Each entry has the index of the request that lead to the insert, and the
   * generated ID of the insert.
   *
   * @return an unmodifiable list of insert info
   */
  public List<JsonObject> getInserts() {
    if (inserts != null)
      return Collections.unmodifiableList(inserts);
    else
      return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (deletedCount ^ (deletedCount >>> 32));
    result = prime * result + (int) (insertedCount ^ (insertedCount >>> 32));
    result = prime * result + (int) (matchedCount ^ (matchedCount >>> 32));
    result = prime * result + (int) (modifiedCount ^ (modifiedCount >>> 32));
    result = prime * result + ((upserts == null) ? 0 : upserts.hashCode());
    result = prime * result + ((inserts == null) ? 0 : inserts.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MongoClientBulkWriteResult other = (MongoClientBulkWriteResult) obj;
    if (deletedCount != other.deletedCount)
      return false;
    if (insertedCount != other.insertedCount)
      return false;
    if (matchedCount != other.matchedCount)
      return false;
    if (modifiedCount != other.modifiedCount)
      return false;
    if (upserts == null) {
      if (other.upserts != null)
        return false;
    } else if (!upserts.equals(other.upserts))
      return false;
    if (inserts == null) {
      if (other.inserts != null)
        return false;
    } else if (!inserts.equals(other.inserts))
      return false;
    return true;
  }

}
