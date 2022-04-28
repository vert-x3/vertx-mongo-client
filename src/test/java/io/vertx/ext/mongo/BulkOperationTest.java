package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BulkOperationTest {

  @Test
  public void testEquals() {
    BulkOperation a = BulkOperation.createUpdate(new JsonObject(), new JsonObject());
    BulkOperation b = BulkOperation.createUpdate(new JsonObject(), new JsonObject());

    a.setType(BulkOperationType.UPDATE);
    b.setType(BulkOperationType.INSERT);
    assertNotEquals(a, b);
    b.setType(BulkOperationType.UPDATE);
    assertEquals(a, b);

    a.setFilter(new JsonObject().put("foo", "bar"));
    b.setFilter(new JsonObject().put("foo", "eek"));
    assertNotEquals(a, b);
    b.setFilter(new JsonObject().put("foo", "bar"));
    assertEquals(a, b);

    a.setDocument(new JsonObject().put("bar", "foo"));
    b.setDocument(new JsonObject().put("bar", "eek"));
    assertNotEquals(a, b);
    b.setDocument(new JsonObject().put("bar", "foo"));
    assertEquals(a, b);

    a.setMulti(true);
    b.setMulti(false);
    assertNotEquals(a, b);
    b.setMulti(true);
    assertEquals(a, b);

    a.setUpsert(true);
    b.setUpsert(false);
    assertNotEquals(a, b);
    b.setUpsert(true);
    assertEquals(a, b);

    a.setCollation(new CollationOptions());
    b.setCollation(new CollationOptions().setLocale("de_AT"));
    assertNotEquals(a,b);
    b.setCollation(new CollationOptions());
    assertEquals(a,b);

    a.setHint(new JsonObject().put("foo", "bar"));
    b.setHint(new JsonObject().put("bar", "foo"));
    assertNotEquals(a, b);
    b.setHint(new JsonObject().put("foo", "bar"));
    assertEquals(a, b);

    a.setHintString("foo");
    b.setHintString("bar");
    assertNotEquals(a, b);
    b.setHintString("foo");
    assertEquals(a, b);

    assertNotEquals(a, null);
  }

  @Test
  public void testHashCode() {
    BulkOperation a = BulkOperation.createUpdate(new JsonObject().put("foo", "bar"), new JsonObject().put("bar", "foo"), true, true);
    int hash = a.hashCode();

    a.setType(BulkOperationType.INSERT);
    assertNotEquals(hash, a.hashCode());
    a.setType(BulkOperationType.UPDATE);
    assertEquals(hash, a.hashCode());

    a.setFilter(new JsonObject().put("foo", "eek"));
    assertNotEquals(hash, a.hashCode());
    a.setFilter(new JsonObject().put("foo", "bar"));
    assertEquals(hash, a.hashCode());

    a.setDocument(new JsonObject().put("bar", "eek"));
    assertNotEquals(hash, a.hashCode());
    a.setDocument(new JsonObject().put("bar", "foo"));
    assertEquals(hash, a.hashCode());

    a.setMulti(false);
    assertNotEquals(hash, a.hashCode());
    a.setMulti(true);
    assertEquals(hash, a.hashCode());

    a.setUpsert(false);
    assertNotEquals(hash, a.hashCode());
    a.setUpsert(true);
    assertEquals(hash, a.hashCode());

    a.setCollation(new CollationOptions());
    assertNotEquals(hash, a.hashCode());
    a.setCollation(null);
    assertEquals(hash, a.hashCode());

    a.setHint(new JsonObject().put("foo", "bar"));
    assertNotEquals(hash, a.hashCode());
    a.setHint(null);
    assertEquals(hash, a.hashCode());

    a.setHintString("foo");
    assertNotEquals(hash, a.hashCode());
    a.setHintString(null);
    assertEquals(hash, a.hashCode());

    assertNotEquals(a, null);
  }
}
