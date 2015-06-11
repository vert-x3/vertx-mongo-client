package io.vertx.ext.mongo.impl.codec.json;

import org.junit.Test;

import java.util.Date;

public class ISODateTest {

  @Test
  public void testCompleteDate() {
    // pattern: YYYY-MM-DD
    ISODate.parse("1997-07-16");
  }

  @Test
  public void testCompleteDatePlusHourAndMinute() {
    // pattern YYYY-MM-DDThh:mmTZD
    ISODate.parse("1997-07-16T19:20+01:00");
  }

  @Test
  public void testCompleteDatePlusHourAndMinuteAndSec() {
    // pattern YYYY-MM-DDThh:mm:ssTZD
    ISODate.parse("1997-07-16T19:20:30+01:00");
  }

  @Test
  public void testCompleteDatePlusHourAndMinuteAndSecPlusFracSec() {
    // pattern YYYY-MM-DDThh:mm:ss.sTZD
    ISODate.parse("1997-07-16T19:20:30.45+01:00");
  }

  @Test
  public void testCompleteDatePlusHourAndMinuteAndSecPlusFracSec2() {
    // pattern YYYY-MM-DDThh:mm:ss.sTZD
    ISODate.parse("1997-07-16T19:20:30.45Z");
  }

  @Test
  public void testCompleteDatePlusHourAndMinuteAndSecPlusFracSec3() {
    // pattern YYYY-MM-DDThh:mm:ss.sTZD
    ISODate.parse("1997-07-16T19:20:30.45+0100");
  }

  @Test
  public void testCompleteDatePlusHourAndMinuteAndSecPlusFracSec4() {
    // pattern YYYY-MM-DDThh:mm:ss.sTZD
    ISODate.parse("1997-07-16T19:20:30.45+03");
  }

  @Test
  public void testMessedUpCase() {
    // it should not crash increment the hour
    ISODate.parse("1997-07-16T19:20:60");
  }

  @Test
  public void test1() {
    System.out.println(new Date(ISODate.parse("2015-06-11T16:41:44.845+02:00")));
  }
}
