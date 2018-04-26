package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Result propagated from mongodb driver update result.
 *
 * @author <a href="mailto:maziz.esa@gmail.com">Maziz Esa</a>
 */
@DataObject
public class MongoClientUpdateResult {

  /**
   * The default value is 0, signifying no match
   */
  public static final long DEFAULT_DOCMATCHED = 0l;

  /**
   * The default value is 0, signifying no document is modified
   */
  public static final long DEFAULT_DOCMODIFIED = 0l;

  /**
   * Constant to be used when storing and retrieving Json for documents matched.
   */
  public static final String DOC_MATCHED = "doc_matched";

  /**
   * Constant to be used when storing and retrieving Json for documents upserted id.
   */
  public static final String UPSERTED_ID = "upserted_id";

  /**
   * Constant to be used when storing and retrieving Json for documents modified.
   */
  public static final String DOC_MODIFIED = "doc_modified";

  /**
   * Constant to be used when storing and retrieving the _id within upserted_id
   */
  public static final String ID_FIELD = "_id";

  private long docMatched;
  private JsonObject docUpsertedId;
  private long docModified;

  /**
   * Default constructor
   */
  public MongoClientUpdateResult() {
    docMatched = DEFAULT_DOCMATCHED;
    docModified = DEFAULT_DOCMATCHED;
  }

  /**
   * Constructor to specify the status of the operation. Number of matched, upserted id JsonObject and number of doc modified.
   * @param docMatched
   * @param docUpsertedId
   * @param docModified
   */
  public MongoClientUpdateResult(long docMatched, JsonObject docUpsertedId, long docModified) {
    this.docMatched = docMatched;
    this.docUpsertedId = docUpsertedId;
    this.docModified = docModified;
  }

  /**
   * Copy constructor
   * @param mongoClientUpdateResultCopy
   */
  public MongoClientUpdateResult(MongoClientUpdateResult mongoClientUpdateResultCopy) {
    docMatched = mongoClientUpdateResultCopy.getDocMatched();
    docUpsertedId = mongoClientUpdateResultCopy.getDocUpsertedId();
    docModified = mongoClientUpdateResultCopy.getDocModified();
  }

  /**
   * Constructor from JSON
   * @param mongoClientUpdateResultJson
   */
  public MongoClientUpdateResult(JsonObject mongoClientUpdateResultJson) {
    docMatched = mongoClientUpdateResultJson.getLong(DOC_MATCHED, DEFAULT_DOCMATCHED);
    docUpsertedId = mongoClientUpdateResultJson.getJsonObject(UPSERTED_ID, null);
    docModified = mongoClientUpdateResultJson.getLong(DOC_MODIFIED, DEFAULT_DOCMODIFIED);
  }

  /**
   * Convert to JSON
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject mongoClientUpdateResultJson = new JsonObject();

    if(docMatched != DEFAULT_DOCMATCHED) {
      mongoClientUpdateResultJson.put(DOC_MATCHED, docMatched);
    }
    if(docUpsertedId != null) {
      mongoClientUpdateResultJson.put(UPSERTED_ID, docUpsertedId);
    }
    if(docModified != DEFAULT_DOCMODIFIED) {
      mongoClientUpdateResultJson.put(DOC_MODIFIED, docModified);
    }

    return mongoClientUpdateResultJson;
  }

  /**
   * Get the number of documents that're matched
   * @return number of documents that're matched
   */
  public long getDocMatched() {
    return docMatched;
  }

  /**
   * Get the document id that's upserted
   * @return document id that's upserted
   */
  public JsonObject getDocUpsertedId() {
    return docUpsertedId;
  }

  /**
   * Get the number of documents that're modified
   * @return number of documents that're modified
   */
  public long getDocModified() {
    return docModified;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj){
      return true;
    }

    if(obj instanceof  MongoClientUpdateResult){
      MongoClientUpdateResult mongoClientUpdateResult = (MongoClientUpdateResult)obj;

      if(docMatched == mongoClientUpdateResult.getDocMatched() && docUpsertedId.equals(mongoClientUpdateResult.getDocUpsertedId()) &&
        docModified == mongoClientUpdateResult.docModified){
        return true;
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    int result = 23; // //Arbitrary value to reduce possibility of collision if removed field is 0

    result = 31 * result + ((int) (docMatched ^ (docMatched >>> 32)));
    result = 31 * result + (docUpsertedId != null ? docUpsertedId.hashCode() : 0);
    result = 31 * result + ((int)(docModified ^ (docModified >>> 32)));

    return result;
  }
}
