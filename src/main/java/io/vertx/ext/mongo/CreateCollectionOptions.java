package io.vertx.ext.mongo;

import com.mongodb.assertions.Assertions;
import com.mongodb.client.model.IndexOptionDefaults;
import com.mongodb.lang.Nullable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Options for creating a collection
 */
@DataObject
@JsonGen(publicConverter = false)
public class CreateCollectionOptions {
  private Long maxDocuments;
  private Boolean capped;
  private TimeSeriesOptions timeSeriesOptions;
  private Long sizeInBytes;
  private JsonObject storageEngineOptions;
  private JsonObject indexOptionDefaults;
  private ValidationOptions validationOptions;
  private CollationOptions collation;
  private Long expireAfterSeconds;

  public CreateCollectionOptions() {
    this.maxDocuments = null;
    this.capped = null;
    this.timeSeriesOptions = null;
    this.sizeInBytes = null;
    this.storageEngineOptions = null;
    this.indexOptionDefaults = null;
    this.validationOptions = null;
    this.collation = null;
    this.expireAfterSeconds = null;
  }

  public CreateCollectionOptions(CreateCollectionOptions other) {
    this.maxDocuments = other.getMaxDocuments();
    this.capped = other.getCapped();
    this.timeSeriesOptions = other.getTimeSeriesOptions();
    this.sizeInBytes = other.getSizeInBytes();
    this.storageEngineOptions = other.getStorageEngineOptions();
    this.indexOptionDefaults = other.getIndexOptionDefaults();
    this.validationOptions = other.getValidationOptions();
    this.collation = other.getCollation();
    this.expireAfterSeconds = other.getExpireAfterSeconds();
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
    if (timeSeriesOptions != null) {
      createCollectionOptions.timeSeriesOptions(timeSeriesOptions.toMongoDriverObject());
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
    if (expireAfterSeconds != null) {
      createCollectionOptions.expireAfter(expireAfterSeconds, TimeUnit.SECONDS);
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

  public TimeSeriesOptions getTimeSeriesOptions() {
    return timeSeriesOptions;
  }

  public CreateCollectionOptions setTimeSeriesOptions(TimeSeriesOptions timeSeriesOptions) {
    this.timeSeriesOptions = Assertions.notNull("timeseries", timeSeriesOptions);
    return this;
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

  public Long getExpireAfterSeconds() {
    return expireAfterSeconds;
  }

  /**
   * Optional.
   * A duration indicating after how long old time-series data should be deleted.
   * Currently, applies only to time-series collections, so if this value is set then so must the time-series options.
   *
   * @param expireAfterSeconds duration, in seconds, indicating after how long old time-series data should be deleted
   * @return CreateCollectionOptions
   */
  public CreateCollectionOptions setExpireAfterSeconds(Long expireAfterSeconds) {
    this.expireAfterSeconds = expireAfterSeconds;
    return this;
  }

  @Override
  public String toString() {
    return "CreateCollectionOptions{" +
      "maxDocuments=" + maxDocuments +
      ", capped=" + capped +
      ", timeSeriesOptions=" + timeSeriesOptions +
      ", sizeInBytes=" + sizeInBytes +
      ", storageEngineOptions=" + storageEngineOptions +
      ", indexOptionDefaults=" + indexOptionDefaults +
      ", validationOptions=" + validationOptions +
      ", collation=" + collation +
      ", expireAfter=" + expireAfterSeconds +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollectionOptions that = (CreateCollectionOptions) o;
    return Objects.equals(maxDocuments, that.maxDocuments) && Objects.equals(capped, that.capped) && Objects.equals(timeSeriesOptions, that.timeSeriesOptions) && Objects.equals(sizeInBytes, that.sizeInBytes) && Objects.equals(storageEngineOptions, that.storageEngineOptions) && Objects.equals(indexOptionDefaults, that.indexOptionDefaults) && Objects.equals(validationOptions, that.validationOptions) && Objects.equals(collation, that.collation) && Objects.equals(expireAfterSeconds, that.expireAfterSeconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxDocuments, capped, timeSeriesOptions, sizeInBytes, storageEngineOptions, indexOptionDefaults, validationOptions, collation, expireAfterSeconds);
  }
}
