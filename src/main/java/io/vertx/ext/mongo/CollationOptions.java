package io.vertx.ext.mongo;

import com.mongodb.client.model.*;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;

import java.util.Locale;
import java.util.Objects;

/**
 * Options used to configure collation options.
 *
 * @author <a href="mailto:christoph.spoerk@gmail.com">Christoph Spörk</a>
 */
@DataObject(generateConverter = true)
public class CollationOptions {

  private String locale;
  private Boolean caseLevel;
  private CaseFirst caseFirst;
  private Integer strength;
  private Boolean numericOrdering;
  private String alternate;
  private MaxVariable maxVariable;
  private Boolean backwards;
  private Boolean normalization;

  /**
   * Default constructor for setting
   */
  public CollationOptions() {
    locale = Locale.getDefault().toString();
    caseLevel = null;
    caseFirst = null;
    strength = null;
    numericOrdering = null;
    backwards = null;
    maxVariable = null;
    normalization = null;
    alternate = null;
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
    Collation.Builder collation = Collation.builder();
    if (alternate != null) {
      collation.collationAlternate(CollationAlternate.fromString(alternate));
    }
    if (backwards != null) {
      collation.backwards(backwards);
    }
    if (caseLevel != null) {
      collation.caseLevel(caseLevel);
    }
    if (caseFirst != null) {
      collation.collationCaseFirst(CollationCaseFirst.fromString(caseFirst.name()));
    }
    if (maxVariable != null) {
      collation.collationMaxVariable(CollationMaxVariable.fromString(maxVariable.name()));
    }
    if (strength != null) {
      collation.collationStrength(CollationStrength.fromInt(strength));
    }
    if (locale != null) {
      collation.locale(locale);
    }
    if (numericOrdering != null) {
      collation.numericOrdering(numericOrdering);
    }
    if (alternate != null) {
      collation.collationAlternate(CollationAlternate.fromString(alternate));
    }
    if (normalization != null) {
      collation.normalization(normalization);
    }
    return collation.build();
  }

  /**
   * Constructing from a JsonObject with provided attributes
   *
   * @param json containing collation options
   */
  public CollationOptions(JsonObject json) {
    CollationOptionsConverter.fromJson(json, this);
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
   * To specify simple binary comparison, specify locale value of "simple".
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
  public Boolean getCaseLevel() {
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
  public CaseFirst getCaseFirst() {
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
  public CollationOptions setCaseFirst(CaseFirst caseFirst) {
    this.caseFirst = caseFirst;
    return this;
  }

  /**
   * Get strength level
   *
   * @return strength level
   */
  public Integer getStrength() {
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
  public CollationOptions setStrength(Integer strength) {
    if (strength < 1 || strength > 5) {
      throw new IllegalArgumentException("Unsupported strength level: Must be 1-5");
    }
    this.strength = strength;
    return this;
  }

  /**
   * Get numeric ordering
   *
   * @return Numeric ordering
   */
  public Boolean getNumericOrdering() {
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
  public String getAlternate() {
    return alternate;
  }

  /**
   * Get alternate
   *
   * @return alternate
   */
  Alternate alternate() {
    return Alternate.fromString(alternate);
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
  public CollationOptions setAlternate(String alternate) {
    this.alternate = alternate;
    return this;
  }

  @GenIgnore
  CollationOptions alternate(Alternate alternate) {
    this.alternate = alternate.toString();
    return this;
  }

  /**
   * Get max variable
   *
   * @return max variable
   */
  public MaxVariable getMaxVariable() {
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
  public CollationOptions setMaxVariable(MaxVariable maxVariable) {
    this.maxVariable = maxVariable;
    return this;
  }

  /**
   * Get backwards
   *
   * @return backwards
   */
  public Boolean getBackwards() {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CollationOptions that = (CollationOptions) o;
    return Objects.equals(getLocale(), that.getLocale()) && Objects.equals(getCaseLevel(), that.getCaseLevel()) && getCaseFirst() == that.getCaseFirst() && Objects.equals(getStrength(), that.getStrength()) && Objects.equals(getNumericOrdering(), that.getNumericOrdering()) && Objects.equals(getAlternate(), that.getAlternate()) && getMaxVariable() == that.getMaxVariable() && Objects.equals(getBackwards(), that.getBackwards()) && Objects.equals(isNormalization(), that.isNormalization());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLocale(), getCaseLevel(), getCaseFirst(), getStrength(), getNumericOrdering(), getAlternate(), getMaxVariable(), getBackwards(), isNormalization());
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
}
