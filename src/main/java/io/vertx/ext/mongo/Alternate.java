package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.VertxGen;

import java.util.Locale;

@VertxGen
public enum Alternate {
  NON_IGNORABLE("non-ignorable"), SHIFTED("shifted");
  private final String value;

  Alternate(String value) {
    this.value = value;
  }

  public static Alternate fromString(String alternateValue) {
    if (alternateValue != null) {
      if (NON_IGNORABLE.value.equals(alternateValue.toLowerCase(Locale.ROOT))) {
        return NON_IGNORABLE;
      } else if (SHIFTED.value.equals(alternateValue.toLowerCase(Locale.ROOT))) {
        return SHIFTED;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
