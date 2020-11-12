package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IndexModelTest {

  private IndexModel a, b, c, d;

  @Before
  public void setup() {
    a = new IndexModel(new JsonObject().put("foo", "bar"));
    b = new IndexModel(new JsonObject().put("foo", "bar"));
    c = new IndexModel(new JsonObject().put("bar", "eek"), new IndexOptions().name("bar"));
    d = new IndexModel(new JsonObject().put("bar", "eek"), new IndexOptions().name("foo"));
  }

  @Test
  public void testEquals() {
    assertNotEquals(a, null);
    assertEquals(a, b);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
    assertNotEquals(b, null);
    assertNotEquals(b, c);
    assertNotEquals(b, d);
    assertNotEquals(c, d);
  }

  @Test
  public void testHashCode() {
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), c.hashCode());
    assertNotEquals(a.hashCode(), d.hashCode());
    assertNotEquals(b.hashCode(), c.hashCode());
    assertNotEquals(b.hashCode(), d.hashCode());
    assertNotEquals(c.hashCode(), d.hashCode());
  }
}
