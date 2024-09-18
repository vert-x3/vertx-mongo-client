package io.vertx.ext.mongo.tests;

import io.vertx.ext.mongo.BulkWriteOptions;
import io.vertx.ext.mongo.WriteOption;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BulkWriteOptionsTest {

  @Test
  public void testEquals() {
    BulkWriteOptions a = new BulkWriteOptions();
    BulkWriteOptions b = new BulkWriteOptions();
    assertEquals(a, b);

    a.setWriteOption(WriteOption.ACKNOWLEDGED);
    b.setWriteOption(WriteOption.JOURNALED);
    assertNotEquals(a, b);

    a.setWriteOption(WriteOption.MAJORITY);
    b.setWriteOption(WriteOption.MAJORITY);
    assertEquals(a, b);

    a.setOrdered(true);
    b.setOrdered(false);
    assertNotEquals(a, b);

    assertNotEquals(a, null);
  }

  @Test
  public void testHashCode() {
    BulkWriteOptions a = new BulkWriteOptions()
      .setWriteOption(WriteOption.JOURNALED)
      .setOrdered(false);
    int hash = a.hashCode();

    a.setWriteOption(WriteOption.ACKNOWLEDGED);
    assertNotEquals(hash, a.hashCode());

    a.setWriteOption(WriteOption.JOURNALED);
    a.setOrdered(true);
    assertNotEquals(hash, a.hashCode());

    a.setWriteOption(WriteOption.JOURNALED);
    a.setOrdered(false);
    assertEquals(hash, a.hashCode());
  }
}
