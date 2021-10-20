package io.vertx.ext.mongo;

import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CollationOptionsTest {

  private static final String DEFAULT_LOCALE = "en_US";

  private static void assertNotEqual(BiConsumer<CollationOptions, CollationOptions> f) {
    CollationOptions a = new CollationOptions(DEFAULT_LOCALE);
    CollationOptions b = new CollationOptions(DEFAULT_LOCALE);
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<CollationOptions> f) {
    CollationOptions o = new CollationOptions(DEFAULT_LOCALE);
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new CollationOptions(DEFAULT_LOCALE), new CollationOptions(DEFAULT_LOCALE));

    assertNotEqual((a, b) -> {
      a.setAlternate(CollationOptions.Alternate.NON_IGNORABLE);
      b.setAlternate(CollationOptions.Alternate.SHIFTED);
    });
    assertNotEqual((a, b) -> {
      a.setBackwards(true);
      b.setBackwards(false);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CollationOptions.CaseFirst.off);
      b.setCaseFirst(CollationOptions.CaseFirst.lower);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CollationOptions.CaseFirst.off);
      b.setCaseFirst(CollationOptions.CaseFirst.upper);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CollationOptions.CaseFirst.lower);
      b.setCaseFirst(CollationOptions.CaseFirst.upper);
    });
    assertNotEqual((a, b) -> {
      a.setLocale("en_US");
      b.setLocale("de_AT");
    });
    assertNotEqual((a, b) -> {
      a.setCaseLevel(true);
      b.setCaseLevel(false);
    });
    assertNotEqual((a, b) -> {
      a.setMaxVariable(CollationOptions.MaxVariable.punct);
      b.setMaxVariable(CollationOptions.MaxVariable.space);
    });
    assertNotEqual((a, b) -> {
      a.setNormalization(true);
      b.setNormalization(false);
    });
    assertNotEqual((a, b) -> {
      a.setNumericOrdering(true);
      b.setNumericOrdering(false);
    });
    assertNotEqual((a, b) -> {
      a.setStrength(1);
      b.setStrength(2);
    });

    assertNotEquals(new CollationOptions(DEFAULT_LOCALE), null);
  }

  @Test
  public void testHashCode() {
    CollationOptions a = new CollationOptions(DEFAULT_LOCALE);
    int hash = a.hashCode();

    assertEquals(hash, new CollationOptions(DEFAULT_LOCALE).hashCode());

    assertNotEqual(hash, o -> o.setStrength(1));
    assertNotEqual(hash, o -> o.setNormalization(true));
    assertNotEqual(hash, o -> o.setNumericOrdering(true));
    assertNotEqual(hash, o -> o.setMaxVariable(CollationOptions.MaxVariable.space));
    assertNotEqual(hash, o -> o.setLocale("de_AT"));
    assertNotEqual(hash, o -> o.setCaseLevel(true));
    assertNotEqual(hash, o -> o.setCaseFirst(CollationOptions.CaseFirst.upper));
    assertNotEqual(hash, o -> o.setCaseFirst(CollationOptions.CaseFirst.lower));
    assertNotEqual(hash, o -> o.setAlternate(CollationOptions.Alternate.SHIFTED));
    assertNotEqual(hash, o -> o.setBackwards(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStrengthLevelLessThan1() {
    CollationOptions collationOptions = new CollationOptions(DEFAULT_LOCALE);
    collationOptions.setStrength(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStrengthLevelGreaterThan5() {
    CollationOptions collationOptions = new CollationOptions(DEFAULT_LOCALE);
    collationOptions.setStrength(6);
  }
}
