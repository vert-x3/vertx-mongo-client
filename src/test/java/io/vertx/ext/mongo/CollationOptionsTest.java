package io.vertx.ext.mongo;

import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CollationOptionsTest {

  private static void assertNotEqual(BiConsumer<CollationOptions, CollationOptions> f) {
    CollationOptions a = new CollationOptions();
    CollationOptions b = new CollationOptions();
    f.accept(a, b);
    assertNotEquals(a, b);
  }

  private static void assertNotEqual(int expected, Consumer<CollationOptions> f) {
    CollationOptions o = new CollationOptions();
    f.accept(o);
    assertNotEquals(expected, o.hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(new CollationOptions(), new CollationOptions());

    assertNotEqual((a, b) -> {
      a.alternate(Alternate.NON_IGNORABLE);
      b.alternate(Alternate.SHIFTED);
    });
    assertNotEqual((a, b) -> {
      a.setBackwards(true);
      b.setBackwards(false);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CaseFirst.off);
      b.setCaseFirst(CaseFirst.lower);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CaseFirst.off);
      b.setCaseFirst(CaseFirst.upper);
    });
    assertNotEqual((a, b) -> {
      a.setCaseFirst(CaseFirst.lower);
      b.setCaseFirst(CaseFirst.upper);
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
      a.setMaxVariable(MaxVariable.punct);
      b.setMaxVariable(MaxVariable.space);
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

    assertNotEquals(new CollationOptions(), null);
  }

  @Test
  public void testHashCode() {
    CollationOptions a = new CollationOptions();
    int hash = a.hashCode();

    assertEquals(hash, new CollationOptions().hashCode());

    assertNotEqual(hash, o -> o.setStrength(1));
    assertNotEqual(hash, o -> o.setNormalization(true));
    assertNotEqual(hash, o -> o.setNumericOrdering(true));
    assertNotEqual(hash, o -> o.setMaxVariable(MaxVariable.space));
    assertNotEqual(hash, o -> o.setLocale("de_AT"));
    assertNotEqual(hash, o -> o.setCaseLevel(true));
    assertNotEqual(hash, o -> o.setCaseFirst(CaseFirst.upper));
    assertNotEqual(hash, o -> o.setCaseFirst(CaseFirst.lower));
    assertNotEqual(hash, o -> o.alternate(Alternate.SHIFTED));
    assertNotEqual(hash, o -> o.setBackwards(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStrengthLevelLessThan1() {
    CollationOptions collationOptions = new CollationOptions();
    collationOptions.setStrength(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidStrengthLevelGreaterThan5() {
    CollationOptions collationOptions = new CollationOptions();
    collationOptions.setStrength(6);
  }
}
