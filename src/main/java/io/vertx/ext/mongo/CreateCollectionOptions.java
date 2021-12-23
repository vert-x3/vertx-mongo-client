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
  private Long maxDocuments;
  private Boolean capped;
  private Long sizeInBytes;
  private JsonObject storageEngineOptions;
  private JsonObject indexOptionDefaults;
  private ValidationOptions validationOptions;
  private CollationOptions collation;

  public CreateCollectionOptions() {
    this.maxDocuments = null;
    this.capped = null;
    this.sizeInBytes = null;
    this.storageEngineOptions = null;
    this.indexOptionDefaults = null;
    this.validationOptions = null;
    this.collation = null;
  }

  public CreateCollectionOptions(CreateCollectionOptions createCollectionOptions) {
    this.maxDocuments = createCollectionOptions.getMaxDocuments();
    this.capped = createCollectionOptions.getCapped();
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

  public Long getMaxDocuments() {
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
  public CreateCollectionOptions setMaxDocuments(Long maxDocuments) {
    this.maxDocuments = maxDocuments;
    return this;
  }

  /**
   * Returns the mongo-java-driver specific object.
   * @return com.mongodb.client.model.CreateCollectionOptions
   */
  public com.mongodb.client.model.CreateCollectionOptions toMongoDriverObject() {
    com.mongodb.client.model.CreateCollectionOptions createCollectionOptions = new com.mongodb.client.model.CreateCollectionOptions();
    if (capped != null) {
      createCollectionOptions.capped(capped);
    }
    if (maxDocuments != null) {
      createCollectionOptions.maxDocuments(maxDocuments);
    }
    if (sizeInBytes != null) {
      createCollectionOptions.sizeInBytes(sizeInBytes);
    }
    if (collation != null) {
      createCollectionOptions.collation(collation.toMongoDriverObject());
    }
    if (indexOptionDefaults != null) {
      createCollectionOptions.indexOptionDefaults(new IndexOptionDefaults()
        .storageEngine(org.bson.BsonDocument.parse(indexOptionDefaults.encode()))
      );
    }
    if (validationOptions != null) {
      createCollectionOptions.validationOptions(validationOptions.toMongoDriverObject());
    }
    if (storageEngineOptions != null) {
      createCollectionOptions.storageEngineOptions(org.bson.BsonDocument.parse(storageEngineOptions.encode()));
    }
    return createCollectionOptions;
  }

  /**
   * Optional. To create a capped collection, specify true. If you specify true, you must also set a maximum size in the size field.
   * @param capped
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setCapped(Boolean capped) {
    this.capped = capped;
    return this;
  }

  public Boolean getCapped() {
    return capped;
  }

  public Long getSizeInBytes() {
    return this.sizeInBytes;
  }

  /**
   * Optional. Specify a maximum size in bytes for a capped collection. Once a capped collection reaches its maximum size, MongoDB removes the older documents to make space for the new documents.
   *           The size field is required for capped collections and ignored for other collections.
   * @param sizeInBytes
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setSizeInBytes(Long sizeInBytes) {
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

  @Override
  public String toString() {
    return "CreateCollectionOptions{" +
      "maxDocuments=" + maxDocuments +
      ", capped=" + capped +
      ", sizeInBytes=" + sizeInBytes +
      ", storageEngineOptions=" + storageEngineOptions +
      ", indexOptionDefaults=" + indexOptionDefaults +
      ", validationOptions=" + validationOptions +
      ", collation=" + collation +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollectionOptions that = (CreateCollectionOptions) o;
    return Objects.equals(getMaxDocuments(), that.getMaxDocuments()) && Objects.equals(getCapped(), that.getCapped()) && Objects.equals(getSizeInBytes(), that.getSizeInBytes()) && Objects.equals(getStorageEngineOptions(), that.getStorageEngineOptions()) && Objects.equals(getIndexOptionDefaults(), that.getIndexOptionDefaults()) && Objects.equals(getValidationOptions(), that.getValidationOptions()) && Objects.equals(getCollation(), that.getCollation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMaxDocuments(), getCapped(), getSizeInBytes(), getStorageEngineOptions(), getIndexOptionDefaults(), getValidationOptions(), getCollation());
  }
}
