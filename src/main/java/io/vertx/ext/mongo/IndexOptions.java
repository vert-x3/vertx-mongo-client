package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

/**
 * Options used to configure index.
 *
 * @author <a href="mailto:ruslan.sennov@gmail.com">Ruslan Sennov</a>
 */
@DataObject
public class IndexOptions {

  public static final boolean DEFAULT_BACKGROUND = false;
  public static final boolean DEFAULT_UNIQUE = false;
  public static final boolean DEFAULT_SPARSE = false;

  private boolean background;
  private boolean unique;
  private String name;
  private boolean sparse;
  private Long expireAfterSeconds;
  private Integer version;
  private JsonObject weights;
  private String defaultLanguage;
  private String languageOverride;
  private Integer textVersion;
  private Integer sphereVersion;
  private Integer bits;
  private Double min;
  private Double max;
  private Double bucketSize;
  private JsonObject storageEngine;
  private JsonObject partialFilterExpression;
  private CollationOptions collation;

  /**
   * Default constructor
   */
  public IndexOptions() {
    background = DEFAULT_BACKGROUND;
    unique = DEFAULT_UNIQUE;
    sparse = DEFAULT_SPARSE;
  }

  /**
   * Copy constructor
   *
   * @param options the one to copy
   */
  public IndexOptions(IndexOptions options) {
    background = options.background;
    unique = options.unique;
    name = options.name;
    sparse = options.sparse;
    expireAfterSeconds = options.expireAfterSeconds;
    version = options.version;
    weights = options.weights;
    defaultLanguage = options.defaultLanguage;
    languageOverride = options.languageOverride;
    textVersion = options.textVersion;
    sphereVersion = options.sphereVersion;
    bits = options.bits;
    min = options.min;
    max = options.max;
    bucketSize = options.bucketSize;
    storageEngine = options.storageEngine;
    partialFilterExpression = options.partialFilterExpression;
    collation = options.collation;
  }

  /**
   * Constructor from JSON
   *
   * @param options the JSON
   */
  public IndexOptions(JsonObject options) {
    background = options.getBoolean("background", DEFAULT_BACKGROUND);
    unique = options.getBoolean("unique", DEFAULT_UNIQUE);
    name = options.getString("name");
    sparse = options.getBoolean("sparse", DEFAULT_SPARSE);
    expireAfterSeconds = options.getLong("expireAfterSeconds");
    version = options.getInteger("version");
    weights = options.getJsonObject("weights");
    defaultLanguage = options.getString("defaultLanguage");
    languageOverride = options.getString("languageOverride");
    textVersion = options.getInteger("textVersion");
    sphereVersion = options.getInteger("sphereVersion");
    bits = options.getInteger("bits");
    min = options.getDouble("min");
    max = options.getDouble("max");
    bucketSize = options.getDouble("bucketSize");
    storageEngine = options.getJsonObject("storageEngine");
    partialFilterExpression = options.getJsonObject("partialFilterExpression");
    collation = new CollationOptions(options.getJsonObject("collation"));
  }

  public CollationOptions getCollation() {
    return collation;
  }

  /**
   * Optional. Specifies the collation for the index.
   * <p>
   * Collation allows users to specify language-specific rules for string comparison, such as rules for lettercase and accent marks.
   * <p>
   * If you have specified a collation at the collection level, then:
   * If you do not specify a collation when creating the index, MongoDB creates the index with the collection's default collation.
   * If you do specify a collation when creating the index, MongoDB creates the index with the specified collation.
   * When specifying collation, the locale field is mandatory; all other collation fields are optional.
   * For descriptions of the fields, see <a href="https://docs.mongodb.com/manual/reference/collation/#std-label-collation-document-fields">Collation Document</a>.
   *
   * @param collation as CollationOption
   * @return IndexOptions
   * @see <a href="https://docs.mongodb.com/manual/reference/collation/#std-label-collation-document-fields">Collation Document</a>
   */
  public IndexOptions setCollation(CollationOptions collation) {
    this.collation = collation;
    return this;
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("background", background);
    json.put("unique", unique);
    json.put("sparse", sparse);
    if (name != null) {
      json.put("name", name);
    }
    if (expireAfterSeconds != null) {
      json.put("expireAfterSeconds", expireAfterSeconds);
    }
    if (version != null) {
      json.put("version", version);
    }
    if (weights != null) {
      json.put("weights", weights);
    }
    if (defaultLanguage != null) {
      json.put("defaultLanguage", defaultLanguage);
    }
    if (languageOverride != null) {
      json.put("languageOverride", languageOverride);
    }
    if (textVersion != null) {
      json.put("textVersion", textVersion);
    }
    if (sphereVersion != null) {
      json.put("sphereVersion", sphereVersion);
    }
    if (bits != null) {
      json.put("bits", bits);
    }
    if (min != null) {
      json.put("min", min);
    }
    if (max != null) {
      json.put("max", max);
    }
    if (bucketSize != null) {
      json.put("bucketSize", bucketSize);
    }
    if (storageEngine != null) {
      json.put("storageEngine", storageEngine);
    }
    if (partialFilterExpression != null) {
      json.put("partialFilterExpression", partialFilterExpression);
    }
    if (collation != null) {
      json.put("collation", collation.toJson());
    }
    return json;
  }

  /**
   * Create the index in the background
   *
   * @return true if should create the index in the background
   */
  public boolean isBackground() {
    return background;
  }

  /**
   * Should the index be created in the background
   *
   * @param background true if should create the index in the background
   * @return reference to this, for fluency
   */
  public IndexOptions background(boolean background) {
    this.background = background;
    return this;
  }

  /**
   * Gets if the index should be unique.
   *
   * @return true if the index should be unique
   */
  public boolean isUnique() {
    return unique;
  }

  /**
   * Should the index should be unique.
   *
   * @param unique if the index should be unique
   * @return reference to this, for fluency
   */
  public IndexOptions unique(boolean unique) {
    this.unique = unique;
    return this;
  }

  /**
   * Gets the name of the index.
   *
   * @return the name of the index
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the index.
   *
   * @param name of the index
   * @return reference to this, for fluency
   */
  public IndexOptions name(String name) {
    this.name = name;
    return this;
  }

  /**
   * If true, the index only references documents with the specified field
   *
   * @return if the index should only reference documents with the specified field
   */
  public boolean isSparse() {
    return sparse;
  }

  /**
   * Should the index only references documents with the specified field
   *
   * @param sparse if true, the index only references documents with the specified field
   * @return reference to this, for fluency
   */
  public IndexOptions sparse(boolean sparse) {
    this.sparse = sparse;
    return this;
  }

  /**
   * Gets the time to live for documents in the collection
   *
   * @param timeUnit the time unit
   * @return the time to live for documents in the collection
   */
  public Long getExpireAfter(TimeUnit timeUnit) {
    if (expireAfterSeconds == null) {
      return null;
    }
    return timeUnit.convert(expireAfterSeconds, TimeUnit.SECONDS);
  }

  /**
   * Sets the time to live for documents in the collection
   *
   * @param expireAfter the time to live for documents in the collection
   * @param timeUnit    the time unit for expireAfter
   * @return reference to this, for fluency
   */
  public IndexOptions expireAfter(Long expireAfter, TimeUnit timeUnit) {
    if (expireAfter == null) {
      this.expireAfterSeconds = null;
    } else {
      this.expireAfterSeconds = TimeUnit.SECONDS.convert(expireAfter, timeUnit);
    }
    return this;
  }

  /**
   * Gets the index version number.
   *
   * @return the index version number
   */
  public Integer getVersion() {
    return this.version;
  }

  /**
   * Sets the index version number.
   *
   * @param version the index version number
   * @return reference to this, for fluency
   */
  public IndexOptions version(Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Gets the weighting object for use with a text index
   *
   * <p>A document that represents field and weight pairs. The weight is an integer ranging from 1 to 99,999 and denotes the significance
   * of the field relative to the other indexed fields in terms of the score.</p>
   *
   * @return the weighting object
   */
  public JsonObject getWeights() {
    return weights;
  }

  /**
   * Sets the weighting object for use with a text index.
   *
   * <p>An document that represents field and weight pairs. The weight is an integer ranging from 1 to 99,999 and denotes the significance
   * of the field relative to the other indexed fields in terms of the score.</p>
   *
   * @param weights the weighting object
   * @return reference to this, for fluency
   */
  public IndexOptions weights(JsonObject weights) {
    this.weights = weights;
    return this;
  }

  /**
   * Gets the language for a text index.
   *
   * <p>The language that determines the list of stop words and the rules for the stemmer and tokenizer.</p>
   *
   * @return the language for a text index.
   */
  public String getDefaultLanguage() {
    return defaultLanguage;
  }

  /**
   * Sets the language for the text index.
   *
   * <p>The language that determines the list of stop words and the rules for the stemmer and tokenizer.</p>
   *
   * @param defaultLanguage the language for the text index.
   * @return reference to this, for fluency
   */
  public IndexOptions defaultLanguage(String defaultLanguage) {
    this.defaultLanguage = defaultLanguage;
    return this;
  }

  /**
   * Gets the name of the field that contains the language string.
   *
   * <p>For text indexes, the name of the field, in the collection's documents, that contains the override language for the document.</p>
   *
   * @return the name of the field that contains the language string.
   */
  public String getLanguageOverride() {
    return languageOverride;
  }

  /**
   * Sets the name of the field that contains the language string.
   *
   * <p>For text indexes, the name of the field, in the collection's documents, that contains the override language for the document.</p>
   *
   * @param languageOverride the name of the field that contains the language string.
   * @return reference to this, for fluency
   */
  public IndexOptions languageOverride(String languageOverride) {
    this.languageOverride = languageOverride;
    return this;
  }

  /**
   * The text index version number.
   *
   * @return the text index version number.
   */
  public Integer getTextVersion() {
    return textVersion;
  }

  /**
   * Set the text index version number.
   *
   * @param textVersion the text index version number.
   * @return reference to this, for fluency
   */
  public IndexOptions textVersion(Integer textVersion) {
    this.textVersion = textVersion;
    return this;
  }

  /**
   * Gets the 2dsphere index version number.
   *
   * @return the 2dsphere index version number
   */
  public Integer getSphereVersion() {
    return sphereVersion;
  }

  /**
   * Sets the 2dsphere index version number.
   *
   * @param sphereVersion the 2dsphere index version number.
   * @return reference to this, for fluency
   */
  public IndexOptions sphereVersion(Integer sphereVersion) {
    this.sphereVersion = sphereVersion;
    return this;
  }

  /**
   * Gets the number of precision of the stored geohash value of the location data in 2d indexes.
   *
   * @return the number of precision of the stored geohash value
   */
  public Integer getBits() {
    return bits;
  }

  /**
   * Sets the number of precision of the stored geohash value of the location data in 2d indexes.
   *
   * @param bits the number of precision of the stored geohash value
   * @return reference to this, for fluency
   */
  public IndexOptions bits(Integer bits) {
    this.bits = bits;
    return this;
  }

  /**
   * Gets the lower inclusive boundary for the longitude and latitude values for 2d indexes..
   *
   * @return the lower inclusive boundary for the longitude and latitude values.
   */
  public Double getMin() {
    return min;
  }

  /**
   * Sets the lower inclusive boundary for the longitude and latitude values for 2d indexes..
   *
   * @param min the lower inclusive boundary for the longitude and latitude values
   * @return reference to this, for fluency
   */
  public IndexOptions min(Double min) {
    this.min = min;
    return this;
  }

  /**
   * Gets the upper inclusive boundary for the longitude and latitude values for 2d indexes..
   *
   * @return the upper inclusive boundary for the longitude and latitude values.
   */
  public Double getMax() {
    return max;
  }

  /**
   * Sets the upper inclusive boundary for the longitude and latitude values for 2d indexes..
   *
   * @param max the upper inclusive boundary for the longitude and latitude values
   * @return reference to this, for fluency
   */
  public IndexOptions max(Double max) {
    this.max = max;
    return this;
  }

  /**
   * Gets the specified the number of units within which to group the location values for geoHaystack Indexes
   *
   * @return the specified the number of units within which to group the location values for geoHaystack Indexes
   */
  public Double getBucketSize() {
    return bucketSize;
  }

  /**
   * Sets the specified the number of units within which to group the location values for geoHaystack Indexes
   *
   * @param bucketSize the specified the number of units within which to group the location values for geoHaystack Indexes
   * @return reference to this, for fluency
   */
  public IndexOptions bucketSize(Double bucketSize) {
    this.bucketSize = bucketSize;
    return this;
  }

  /**
   * Gets the storage engine options document for this index.
   *
   * @return the storage engine options
   */
  public JsonObject getStorageEngine() {
    return storageEngine;
  }

  /**
   * Sets the storage engine options document for this index.
   *
   * @param storageEngine the storage engine options
   * @return reference to this, for fluency
   */
  public IndexOptions storageEngine(JsonObject storageEngine) {
    this.storageEngine = storageEngine;
    return this;
  }

  /**
   * Get the filter expression for the documents to be included in the index or null if not set
   *
   * @return the filter expression for the documents to be included in the index or null if not set
   */
  public JsonObject getPartialFilterExpression() {
    return partialFilterExpression;
  }

  /**
   * Sets the filter expression for the documents to be included in the index
   *
   * @param partialFilterExpression the filter expression for the documents to be included in the index
   * @return reference to this, for fluency
   */
  public IndexOptions partialFilterExpression(JsonObject partialFilterExpression) {
    this.partialFilterExpression = partialFilterExpression;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IndexOptions options = (IndexOptions) o;

    if (background != options.background) return false;
    if (unique != options.unique) return false;
    if (name != null ? !name.equals(options.name) : options.name != null) return false;
    if (sparse != options.sparse) return false;
    if (expireAfterSeconds != null ? !expireAfterSeconds.equals(options.expireAfterSeconds) : options.expireAfterSeconds != null)
      return false;
    if (version != null ? !version.equals(options.version) : options.version != null) return false;
    if (weights != null ? !weights.equals(options.weights) : options.weights != null) return false;
    if (defaultLanguage != null ? !defaultLanguage.equals(options.defaultLanguage) : options.defaultLanguage != null)
      return false;
    if (languageOverride != null ? !languageOverride.equals(options.languageOverride) : options.languageOverride != null)
      return false;
    if (textVersion != null ? !textVersion.equals(options.textVersion) : options.textVersion != null) return false;
    if (sphereVersion != null ? !sphereVersion.equals(options.sphereVersion) : options.sphereVersion != null)
      return false;
    if (bits != null ? !bits.equals(options.bits) : options.bits != null) return false;
    if (min != null ? !min.equals(options.min) : options.min != null) return false;
    if (max != null ? !max.equals(options.max) : options.max != null) return false;
    if (bucketSize != null ? !bucketSize.equals(options.bucketSize) : options.bucketSize != null) return false;
    if (storageEngine != null ? !storageEngine.equals(options.storageEngine) : options.storageEngine != null)
      return false;
    if (partialFilterExpression != null ? !partialFilterExpression.equals(options.partialFilterExpression) : options.partialFilterExpression != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = background ? 1 : 0;
    result = 31 * result + (unique ? 1 : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (sparse ? 1 : 0);
    result = 31 * result + (expireAfterSeconds != null ? expireAfterSeconds.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    result = 31 * result + (weights != null ? weights.hashCode() : 0);
    result = 31 * result + (defaultLanguage != null ? defaultLanguage.hashCode() : 0);
    result = 31 * result + (languageOverride != null ? languageOverride.hashCode() : 0);
    result = 31 * result + (textVersion != null ? textVersion.hashCode() : 0);
    result = 31 * result + (sphereVersion != null ? sphereVersion.hashCode() : 0);
    result = 31 * result + (bits != null ? bits.hashCode() : 0);
    result = 31 * result + (min != null ? min.hashCode() : 0);
    result = 31 * result + (max != null ? max.hashCode() : 0);
    result = 31 * result + (bucketSize != null ? bucketSize.hashCode() : 0);
    result = 31 * result + (storageEngine != null ? storageEngine.hashCode() : 0);
    result = 31 * result + (partialFilterExpression != null ? partialFilterExpression.hashCode() : 0);
    return result;
  }
}
