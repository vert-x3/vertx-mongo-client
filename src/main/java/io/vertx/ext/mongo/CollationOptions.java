package io.vertx.ext.mongo;

import com.mongodb.client.model.*;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Options used to configure collation options.
 *
 * @author <a href="mailto:christoph.spoerk@gmail.com">Christoph Spörk</a>
 */
@DataObject(generateConverter = true)
public class CollationOptions {
  /**
   * Default locale : {@code simple}
   */
  public static final String DEFAULT_LOCALE = "simple";

  private String locale;
  private Boolean caseLevel;
  private CollationCaseFirst caseFirst;
  private CollationStrength strength;
  private Boolean numericOrdering;
  private CollationAlternate alternate;
  private CollationMaxVariable maxVariable;
  private Boolean backwards;
  private Boolean normalization;

  /**
   * Default constructor for setting
   */
  public CollationOptions() {
    locale = DEFAULT_LOCALE;
  }

  /**
   * Copy constructor
   *
   * @param options
   */
  public CollationOptions(CollationOptions options) {
    locale = options.locale;
    caseLevel = options.caseLevel;
    caseFirst = options.caseFirst;
    strength = options.strength;
    numericOrdering = options.numericOrdering;
    alternate = options.alternate;
    maxVariable = options.maxVariable;
    backwards = options.backwards;
    normalization = options.normalization;
  }

  public Collation toMongoDriverObject() {
    if (locale == null || "simple".equals(locale)) {
      return Collation.builder().locale("simple").build();
    } else {
      return Collation.builder()
        .backwards(isBackwards())
        .caseLevel(isCaseLevel())
        .collationCaseFirst(getCaseFirst())
        .collationMaxVariable(getMaxVariable())
        .collationStrength(getStrength())
        .locale(getLocale())
        .numericOrdering(isNumericOrdering())
        .collationAlternate(getAlternate())
        .normalization(isNormalization())
        .build();
    }
  }

  /**
   * Constructing from a JsonObject with provided attributes
   *
   * @param json containing collation options
   */
  public CollationOptions(JsonObject json) {
    this();
    CollationOptionsConverter.fromJson(json, this);
    if (json.getValue("alternate") instanceof String) {
      alternate = CollationAlternate.fromString(json.getString("alternate"));
    }
    if (json.getValue("caseFirst") instanceof String) {
      caseFirst = CollationCaseFirst.fromString(json.getString("caseFirst"));
    }
    if (json.getValue("maxVariable") instanceof String) {
      maxVariable = CollationMaxVariable.fromString(json.getString("maxVariable"));
    }
    if(json.getValue("strength") instanceof Integer) {
      strength = CollationStrength.fromInt(json.getInteger("strength"));
    }
  }

  public Boolean isNormalization() {
    return normalization;
  }

  /**
   * Optional. Flag that determines whether to check if text require normalization and to perform normalization. Generally, majority of text does not require this normalization processing.
   * <p>
   * If true, check if fully normalized and perform normalization to compare text.
   * If false, does not check.
   * <p>
   * The default value is false.
   *
   * @param normalization
   * @return CollationOption
   * @see <a href="http://userguide.icu-project.org/collation/concepts#TOC-Normalization">TOC Normalization</a> for details.
   */
  public CollationOptions setNormalization(Boolean normalization) {
    this.normalization = normalization;
    return this;
  }

  /**
   * Convert to JSON
   *
   * @return CollationOption as JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CollationOptionsConverter.toJson(this, json);
    if (alternate != null) {
      json.put("alternate", alternate.getValue());
    }
    if (caseFirst != null) {
      json.put("caseFirst", caseFirst.getValue());
    }
    if (maxVariable != null) {
      json.put("maxVariable", maxVariable.getValue());
    }
    if (strength != null) {
      json.put("strength", strength.getIntRepresentation());
    }
    return json;
  }

  /**
   * Get the locale
   *
   * @return locale string
   */
  public String getLocale() {
    return locale;
  }

  /**
   * The ICU locale. See <a href="https://docs.mongodb.com/manual/reference/collation-locales-defaults/#std-label-collation-languages-locales">Supported Languages and Locales</a>
   * for a list of supported locales.
   * <p>
   * The default value is {@code simple} which specifies simple binary comparison.
   *
   * @param locale string
   * @return collationOption
   * @see <a href="https://docs.mongodb.com/manual/reference/collation-locales-defaults/#std-label-collation-languages-locales">Supported Languages and Locales</a>
   */
  public CollationOptions setLocale(String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Get case level
   *
   * @return caseLevel
   */
  public Boolean isCaseLevel() {
    return caseLevel;
  }

  /**
   * Optional. Flag that determines whether to include case comparison at strength level 1 or 2.
   * <p>
   * If true, include case comparison; i.e.
   * <p>
   * When used with strength:1, collation compares base characters and case.
   * When used with strength:2, collation compares base characters, diacritics (and possible other secondary differences) and case.
   * If false, do not include case comparison at level 1 or 2. The default is false.
   *
   * @param caseLevel
   * @return CollationOption
   * @see <a href="http://userguide.icu-project.org/collation/concepts#TOC-CaseLevel">ICU Collation: Case Level</a>
   */
  public CollationOptions setCaseLevel(Boolean caseLevel) {
    this.caseLevel = caseLevel;
    return this;
  }

  /**
   * Get case first
   *
   * @return case first
   */
  @GenIgnore
  public CollationCaseFirst getCaseFirst() {
    return caseFirst;
  }

  /**
   * Optional. A field that determines sort order of case differences during tertiary level comparisons.
   * <p>
   * Possible values are:
   * "upper" Uppercase sorts before lowercase.
   * "lower" Lowercase sorts before uppercase.
   * "off"   Default value. Similar to "lower" with slight differences.
   * See <a href="http://userguide.icu-project.org/collation/customization">Collation customization</a> for details of differences.
   *
   * @param caseFirst one of UPPER, LOWER, OFF
   * @return CollationOption
   * @see <a href="http://userguide.icu-project.org/collation/customization">Collation customization</a>
   */
  @GenIgnore
  public CollationOptions setCaseFirst(CollationCaseFirst caseFirst) {
    this.caseFirst = caseFirst;
    return this;
  }

  /**
   * Get strength level
   *
   * @return strength level
   */
  public CollationStrength getStrength() {
    return strength;
  }

  /**
   * Optional. The level of comparison to perform. Corresponds to ICU Comparison Levels. Possible values are:
   * <p>
   * Value
   * Description
   * 1 Primary level of comparison. Collation performs comparisons of the base characters only, ignoring other differences such as diacritics and case.
   * 2 Secondary level of comparison. Collation performs comparisons up to secondary differences, such as diacritics. That is, collation performs comparisons of base characters (primary differences) and diacritics (secondary differences). Differences between base characters takes precedence over secondary differences.
   * 3 Tertiary level of comparison. Collation performs comparisons up to tertiary differences, such as case and letter variants. That is, collation performs comparisons of base characters (primary differences), diacritics (secondary differences), and case and variants (tertiary differences). Differences between base characters takes precedence over secondary differences, which takes precedence over tertiary differences.
   * This is the default level.
   * <p>
   * 4 Quaternary Level. Limited for specific use case to consider punctuation when levels 1-3 ignore punctuation or for processing Japanese text.
   * 5 Identical Level. Limited for specific use case of tie breaker.
   *
   * @param strength level
   * @return CollationOption
   * @see <a href="http://userguide.icu-project.org/collation/concepts#TOC-Comparison-Levels">ICU Collation: Comparison Levels</a>
   */
  public CollationOptions setStrength(CollationStrength strength) {
    this.strength = strength;
    return this;
  }

  /**
   * Get numeric ordering
   *
   * @return Numeric ordering
   */
  public Boolean isNumericOrdering() {
    return numericOrdering;
  }

  /**
   * Optional. Flag that determines whether to compare numeric strings as numbers or as strings.
   * <p>
   * If true, compare as numbers; i.e. "10" is greater than "2".
   * If false, compare as strings; i.e. "10" is less than "2".
   * <p>
   * Default is false.
   *
   * @param numericOrdering value
   * @return CollationOption
   */
  public CollationOptions setNumericOrdering(Boolean numericOrdering) {
    this.numericOrdering = numericOrdering;
    return this;
  }

  /**
   * Get alternate
   *
   * @return alternate
   */
  @GenIgnore
  public CollationAlternate getAlternate() {
    return alternate;
  }

  /**
   * Optional. Field that determines whether collation should consider whitespace and punctuation as base characters for purposes of comparison.
   * <p>
   * Possible values are:
   * "non-ignorable" Whitespace and punctuation are considered base characters.
   * "shifted" Whitespace and punctuation are not considered base characters and are only distinguished at strength levels greater than 3.
   * See <a href="http://userguide.icu-project.org/collation/concepts#TOC-Comparison-Levels">ICU Collation: Comparison Levels</a> for more information.
   * <p>
   * Default is "non-ignorable".
   *
   * @param alternate either of NON_IGNORABLE, SHIFTED
   * @return CollationOption
   * @see <a href="http://userguide.icu-project.org/collation/concepts#TOC-Comparison-Levels">ICU Collation: Comparison Levels</a>
   */
  @GenIgnore
  public CollationOptions setAlternate(CollationAlternate alternate) {
    this.alternate = alternate;
    return this;
  }

  /**
   * Get max variable
   *
   * @return max variable
   */
  @GenIgnore
  public CollationMaxVariable getMaxVariable() {
    return maxVariable;
  }

  /**
   * Optional. Field that determines up to which characters are considered ignorable when alternate: "shifted". Has no effect if alternate: "non-ignorable"
   * <p>
   * Possible values are:
   * "punct" Both whitespace and punctuation are "ignorable", i.e. not considered base characters.
   * "space" Whitespace are "ignorable", i.e. not considered base characters.
   *
   * @param maxVariable either of PUNCT, SPACE
   * @return CollationOption
   */
  @GenIgnore
  public CollationOptions setMaxVariable(CollationMaxVariable maxVariable) {
    this.maxVariable = maxVariable;
    return this;
  }

  /**
   * Get backwards
   *
   * @return backwards
   */
  public Boolean isBackwards() {
    return backwards;
  }

  /**
   * Optional. Flag that determines whether strings with diacritics sort from back of the string, such as with some French dictionary ordering.
   * <p>
   * If true, compare from back to front.
   * If false, compare from front to back.
   * <p>
   * The default value is false.
   *
   * @param backwards
   * @return CollationOption
   */
  public CollationOptions setBackwards(Boolean backwards) {
    this.backwards = backwards;
    return this;
  }

  @Override
  public String toString() {
    return "CollationOptions{" +
      "locale='" + locale + '\'' +
      ", caseLevel=" + caseLevel +
      ", caseFirst=" + caseFirst +
      ", strength=" + strength +
      ", numericOrdering=" + numericOrdering +
      ", alternate='" + alternate + '\'' +
      ", maxVariable=" + maxVariable +
      ", backwards=" + backwards +
      ", normalization=" + normalization +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CollationOptions that = (CollationOptions) o;
    return caseLevel == that.caseLevel && strength == that.strength && numericOrdering == that.numericOrdering && backwards == that.backwards && normalization == that.normalization && Objects.equals(locale, that.locale) && caseFirst == that.caseFirst && Objects.equals(alternate, that.alternate) && maxVariable == that.maxVariable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(locale, caseLevel, caseFirst, strength, numericOrdering, alternate, maxVariable, backwards, normalization);
  }
}
