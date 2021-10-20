package io.vertx.ext.mongo;

import com.mongodb.assertions.Assertions;
import com.mongodb.client.model.IndexOptionDefaults;
import com.mongodb.lang.Nullable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options for creating a collection
 */
@DataObject(generateConverter = true)
public class CreateCollectionOptions {
  private long maxDocuments;
  private boolean capped;
  private long sizeInBytes;
  private JsonObject storageEngineOptions = new JsonObject();
  private JsonObject indexOptionDefaults = new JsonObject();
  private ValidationOptions validationOptions = new ValidationOptions();
  private CollationOptions collation;

  public CreateCollectionOptions() {
  }

  public CreateCollectionOptions(CreateCollectionOptions createCollectionOptions) {
    this.maxDocuments = createCollectionOptions.getMaxDocuments();
    this.capped = createCollectionOptions.isCapped();
    this.sizeInBytes = createCollectionOptions.getSizeInBytes();
    this.storageEngineOptions = createCollectionOptions.getStorageEngineOptions();
    this.indexOptionDefaults = createCollectionOptions.getIndexOptionDefaults();
    this.validationOptions = createCollectionOptions.getValidationOptions();
    this.collation = createCollectionOptions.getCollation();
  }

  public CreateCollectionOptions(JsonObject json) {
    CreateCollectionOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CreateCollectionOptionsConverter.toJson(this, json);
    return json;
  }

  public long getMaxDocuments() {
    return this.maxDocuments;
  }

  /**
   * Optional. The maximum number of documents allowed in the capped collection.
   *           The size limit takes precedence over this limit.
   *           If a capped collection reaches the size limit before it reaches the maximum number of documents,
   *           MongoDB removes old documents.
   *           If you prefer to use the max limit, ensure that the size limit,
   *           which is required for a capped collection, is sufficient to contain the maximum number of documents.
   * @param maxDocuments
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setMaxDocuments(long maxDocuments) {
    this.maxDocuments = maxDocuments;
    return this;
  }

  /**
   * Returns the mongo-java-driver specific object.
   * @return com.mongodb.client.model.CreateCollectionOptions
   */
  public com.mongodb.client.model.CreateCollectionOptions toMongoDriverObject() {
    return new com.mongodb.client.model.CreateCollectionOptions()
      .collation(collation.toMongoDriverObject())
      .capped(capped)
      .indexOptionDefaults(new IndexOptionDefaults()
        .storageEngine(org.bson.BsonDocument.parse(indexOptionDefaults.encode()))
      )
      .validationOptions(validationOptions.toMongoDriverObject())
      .storageEngineOptions(org.bson.BsonDocument.parse(storageEngineOptions.encode()))
      .maxDocuments(maxDocuments)
      .sizeInBytes(sizeInBytes);
  }

  public boolean isCapped() {
    return this.capped;
  }

  /**
   * Optional. To create a capped collection, specify true. If you specify true, you must also set a maximum size in the size field.
   * @param capped
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setCapped(boolean capped) {
    this.capped = capped;
    return this;
  }

  public long getSizeInBytes() {
    return this.sizeInBytes;
  }

  /**
   * Optional. Specify a maximum size in bytes for a capped collection. Once a capped collection reaches its maximum size, MongoDB removes the older documents to make space for the new documents.
   *           The size field is required for capped collections and ignored for other collections.
   * @param sizeInBytes
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setSizeInBytes(long sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
    return this;
  }

  @Nullable
  public JsonObject getStorageEngineOptions() {
    return this.storageEngineOptions;
  }

  /**
   * Optional. Available for the WiredTiger storage engine only.
   *
   * Allows users to specify configuration to the storage engine on a per-collection basis when creating a collection.
   * @param storageEngineOptions
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setStorageEngineOptions(@Nullable JsonObject storageEngineOptions) {
    this.storageEngineOptions = storageEngineOptions;
    return this;
  }

  public JsonObject getIndexOptionDefaults() {
    return this.indexOptionDefaults;
  }

  /**
   * Optional. Allows users to specify a default configuration for indexes when creating a collection.
   * @param indexOptionDefaults
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setIndexOptionDefaults(JsonObject indexOptionDefaults) {
    this.indexOptionDefaults = Assertions.notNull("indexOptionDefaults", indexOptionDefaults);
    return this;
  }

  public ValidationOptions getValidationOptions() {
    return this.validationOptions;
  }

  public CreateCollectionOptions setValidationOptions(ValidationOptions validationOptions) {
    this.validationOptions = Assertions.notNull("validationOptions", validationOptions);
    return this;
  }

  @Nullable
  public CollationOptions getCollation() {
    return this.collation;
  }

  /**
   * Specifies the default collation for the collection.
   *
   * Collation allows users to specify language-specific rules for string comparison,
   * such as rules for lettercase and accent marks.
   * @param collation
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setCollation(@Nullable CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  public String toString() {
    return "CreateCollectionOptions{, maxDocuments=" + this.maxDocuments + ", capped=" + this.capped + ", sizeInBytes=" + this.sizeInBytes + ", storageEngineOptions=" + this.storageEngineOptions + ", indexOptionDefaults=" + this.indexOptionDefaults + ", validationOptions=" + this.validationOptions + ", collation=" + this.collation + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollectionOptions that = (CreateCollectionOptions) o;
    return getMaxDocuments() == that.getMaxDocuments() && isCapped() == that.isCapped() && getSizeInBytes() == that.getSizeInBytes() && Objects.equals(getStorageEngineOptions(), that.getStorageEngineOptions()) && Objects.equals(getIndexOptionDefaults(), that.getIndexOptionDefaults()) && Objects.equals(getValidationOptions(), that.getValidationOptions()) && Objects.equals(getCollation(), that.getCollation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMaxDocuments(), isCapped(), getSizeInBytes(), getStorageEngineOptions(), getIndexOptionDefaults(), getValidationOptions(), getCollation());
  }
}
