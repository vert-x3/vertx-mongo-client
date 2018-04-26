package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Result propagated from mongodb driver delete result.
 *
 * @author <a href="mailto:maziz.esa@gmail.com">Maziz Esa</a>
 */
@DataObject
public class MongoClientDeleteResult {

  /**
   * The default value is 0, signifying no document is removed
   */
  public static final long DEFAULT_REMOVEDCOUNT = 0l;

  /**
   * Constant to be used when storing and retrieving Json for documents removed.
   */
  public static final String REMOVED_COUNT = "removed_count";

  private long docRemovedCount;

  /**
   * Default constructor
   */
  public MongoClientDeleteResult() {
    docRemovedCount = DEFAULT_REMOVEDCOUNT;
  }

  /**
   * Constructor that specify the number of documents removed
   * @param docRemovedCount
   */
  public MongoClientDeleteResult(long docRemovedCount) {
    this.docRemovedCount = docRemovedCount;
  }

  /**
   * Copy constructor
   *
   * @param otherMongoClientDeleteResultCopy the one to copy
   */
  public MongoClientDeleteResult(MongoClientDeleteResult otherMongoClientDeleteResultCopy) {
    docRemovedCount = otherMongoClientDeleteResultCopy.getRemovedCount();
  }

  /**
   * Constructor form JSON
   *
   * @param mongoClientDeleteResultJson the JSON
   */
  public MongoClientDeleteResult(JsonObject mongoClientDeleteResultJson) {
    docRemovedCount = mongoClientDeleteResultJson.getLong(REMOVED_COUNT, DEFAULT_REMOVEDCOUNT);
  }

  /**
   * Convert to JSON
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();

    if (docRemovedCount != DEFAULT_REMOVEDCOUNT) {
      jsonObject.put(REMOVED_COUNT, docRemovedCount);
    }

    return jsonObject;
  }

  /**
   * Get the number of removed documents
   * @return number of removed documents
   */
  public long getRemovedCount() {
    return docRemovedCount;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj){
      return true;
    }

    if (obj instanceof MongoClientDeleteResult) {
      MongoClientDeleteResult mongoClientDeleteResult = (MongoClientDeleteResult)obj;
      if(this.docRemovedCount == mongoClientDeleteResult.getRemovedCount()){
        return true;
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    int result = 19; //Arbitrary value to reduce possibility of collision if removed field is 0
    result = 31 * result + ((int) (docRemovedCount ^ (docRemovedCount >>> 32)));
    return result;
  }
}
