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
  public static final Boolean DEFAULT_CASE_LEVEL = false;
  public static final CaseFirst DEFAULT_CASE_FIRST = CaseFirst.off;
  public static final Alternate DEFAULT_ALTERNATE = Alternate.NON_IGNORABLE;
  public static final Integer DEFAULT_STRENGTH = 3;
  public static final Boolean DEFAULT_NUMERIC_ORDERING = false;
  public static final Boolean DEFAULT_BACKWARDS = false;
  public static final Boolean DEFAULT_NORMALIZATION = false;
  public static final MaxVariable DEFAULT_MAX_VARIABLE = MaxVariable.punct;
  private static final String DEFAULT_LOCALE = Locale.getDefault().toString();

  private String locale;
  private boolean caseLevel;
  private CaseFirst caseFirst;
  private int strength;
  private boolean numericOrdering;
  private String alternate;
  private MaxVariable maxVariable;
  private boolean backwards;
  private boolean normalization;

  /**
   * Default constructor for setting
   */
  public CollationOptions() {
    this.locale = DEFAULT_LOCALE;
    caseLevel = DEFAULT_CASE_LEVEL;
    caseFirst = DEFAULT_CASE_FIRST;
    strength = DEFAULT_STRENGTH;
    numericOrdering = DEFAULT_NUMERIC_ORDERING;
    backwards = DEFAULT_BACKWARDS;
    maxVariable = DEFAULT_MAX_VARIABLE;
    normalization = DEFAULT_NORMALIZATION;
    alternate = DEFAULT_ALTERNATE.toString();
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
    return Collation.builder()
      .collationAlternate(CollationAlternate.fromString(getAlternate()))
      .backwards(isBackwards())
      .caseLevel(isCaseLevel())
      .collationCaseFirst(CollationCaseFirst.fromString(getCaseFirst().name()))
      .collationMaxVariable(CollationMaxVariable.fromString(getMaxVariable().name()))
      .collationStrength(CollationStrength.fromInt(getStrength()))
      .locale(getLocale())
      .numericOrdering(isNumericOrdering())
      .collationAlternate(CollationAlternate.fromString(getAlternate()))
      .normalization(isNormalization())
      .build();
  }

  /**
   * Constructing from a JsonObject with provided attributes
   *
   * @param json containing collation options
   */
  public CollationOptions(JsonObject json) {
    CollationOptionsConverter.fromJson(json, this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CollationOptions that = (CollationOptions) o;
    return isCaseLevel() == that.isCaseLevel() && getStrength() == that.getStrength() && isNumericOrdering() == that.isNumericOrdering() && isBackwards() == that.isBackwards() && isNormalization() == that.isNormalization() && Objects.equals(getLocale(), that.getLocale()) && getCaseFirst() == that.getCaseFirst() && Objects.equals(getAlternate(), that.getAlternate()) && getMaxVariable() == that.getMaxVariable();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLocale(), isCaseLevel(), getCaseFirst(), getStrength(), isNumericOrdering(), getAlternate(), getMaxVariable(), isBackwards(), isNormalization());
  }

  public boolean isNormalization() {
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
  public CollationOptions setNormalization(boolean normalization) {
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
  public boolean isCaseLevel() {
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
  public CollationOptions setCaseLevel(boolean caseLevel) {
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
  public int getStrength() {
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
  public CollationOptions setStrength(int strength) {
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
  public boolean isNumericOrdering() {
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
  public CollationOptions setNumericOrdering(boolean numericOrdering) {
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
  public boolean isBackwards() {
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
  public CollationOptions setBackwards(boolean backwards) {
    this.backwards = backwards;
    return this;
  }

}
