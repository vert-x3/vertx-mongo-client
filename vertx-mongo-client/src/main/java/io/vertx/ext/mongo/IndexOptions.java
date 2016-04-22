package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.JsonObjectBsonAdapter;
import org.bson.conversions.Bson;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:sergey.kobec@gmail.com">Sergey Kobets</a>
 */
@DataObject
public class IndexOptions {

  private final com.mongodb.client.model.IndexOptions wrap;
  private JsonObject partialFilterExpression;
  private JsonObject storageEngine;
  private JsonObject weights;

  public IndexOptions() {
    wrap = new com.mongodb.client.model.IndexOptions();
  }

  public IndexOptions(IndexOptions other) {
    wrap = other.unwrap();
  }

  // TODO not implemented
  public IndexOptions(JsonObject json) {
    wrap = new com.mongodb.client.model.IndexOptions();
  }

  // TODO not implemented
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject = isBackground() ? jsonObject.put("background", true) : jsonObject;
    jsonObject = isSparse() ? jsonObject.put("sparse", true) : jsonObject;
    jsonObject = isUnique() ? jsonObject.put("unique", true) : jsonObject;
    jsonObject = getBits() != null ? jsonObject.put("bits", getBits()) : jsonObject;
    jsonObject = getBucketSize() != null ? jsonObject.put("bucketSize", getBucketSize()) : jsonObject;
    jsonObject = getExpireAfter(TimeUnit.SECONDS) != null ? jsonObject.put("expireAfterSeconds", getExpireAfter(TimeUnit.SECONDS)) : jsonObject;
    jsonObject = getDefaultLanguage() != null ? jsonObject.put("defaultLanguage", getDefaultLanguage()): jsonObject;
    return jsonObject;
  }

  public boolean isBackground() {
    return wrap.isBackground();
  }

  public IndexOptions background(boolean background) {
    wrap.background(background);
    return this;
  }

  public boolean isUnique() {
    return wrap.isUnique();
  }

  public IndexOptions unique(boolean unique) {
    wrap.unique(unique);
    return this;
  }

  public String getName() {
    return wrap.getName();
  }

  public IndexOptions name(String name) {
    wrap.name(name);
    return this;
  }

  public boolean isSparse() {
    return wrap.isSparse();
  }

  public IndexOptions sparse(boolean sparse) {
    wrap.sparse(sparse);
    return this;
  }

  public Long getExpireAfter(TimeUnit timeUnit) {
    return wrap.getExpireAfter(timeUnit);
  }

  public IndexOptions expireAfter(Long expireAfter, TimeUnit timeUnit) {
    wrap.expireAfter(expireAfter, timeUnit);
    return this;
  }

  public Integer getVersion() {
    return wrap.getVersion();
  }

  public IndexOptions version(Integer version) {
    wrap.version(version);
    return this;
  }

  public JsonObject getWeights() {
    return weights;
  }

  public IndexOptions weights(JsonObject weights) {
    this.weights = weights;
    wrap.weights(wrap(weights));
    return this;
  }

  public String getDefaultLanguage() {
    return wrap.getDefaultLanguage();
  }

  public IndexOptions defaultLanguage(String defaultLanguage) {
    wrap.defaultLanguage(defaultLanguage);
    return this;
  }

  public String getLanguageOverride() {
    return wrap.getLanguageOverride();
  }

  public IndexOptions languageOverride(String languageOverride) {
    wrap.languageOverride(languageOverride);
    return this;
  }

  public Integer getTextVersion() {
    return wrap.getTextVersion();
  }

  public IndexOptions textVersion(Integer textVersion) {
    wrap.textVersion(textVersion);
    return this;
  }

  public Integer getSphereVersion() {
    return wrap.getSphereVersion();
  }

  public IndexOptions sphereVersion(Integer sphereVersion) {
    wrap.sphereVersion(sphereVersion);
    return this;
  }

  public Integer getBits() {
    return wrap.getBits();
  }

  public IndexOptions bits(Integer bits) {
    wrap.bits(bits);
    return this;
  }

  public Double getMin() {
    return wrap.getMin();
  }

  public IndexOptions min(Double min) {
    wrap.min(min);
    return this;
  }

  public Double getMax() {
    return wrap.getMax();
  }

  public IndexOptions max(Double max) {
    wrap.max(max);
    return this;
  }

  public Double getBucketSize() {
    return wrap.getBucketSize();
  }

  public IndexOptions bucketSize(Double bucketSize) {
    wrap.bucketSize(bucketSize);
    return this;
  }

  public JsonObject getStorageEngine() {
    return storageEngine;
  }

  public IndexOptions storageEngine(JsonObject storageEngine) {
    this.storageEngine = storageEngine;
    wrap.storageEngine(wrap(storageEngine));
    return this;
  }

  public JsonObject getPartialFilterExpression() {
    return partialFilterExpression;
  }

  public IndexOptions partialFilterExpression(JsonObject partialFilterExpression) {
    this.partialFilterExpression = partialFilterExpression;
    wrap.partialFilterExpression(wrap(partialFilterExpression));
    return this;
  }

  public com.mongodb.client.model.IndexOptions unwrap() {
    return wrap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IndexOptions that = (IndexOptions) o;
    return Objects.equals(wrap, that.wrap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wrap);
  }

  private JsonObjectBsonAdapter wrap(JsonObject jsonObject) {
    return jsonObject == null ? null : new JsonObjectBsonAdapter(jsonObject);
  }
}
