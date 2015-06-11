package io.vertx.ext.mongo.impl.codec.json;

import java.time.DateTimeException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISODate {

  private static final Pattern isoDateRegex = Pattern.compile("(\\d{4})-?(\\d{2})-?(\\d{2})([T ](\\d{2})(:?(\\d{2})(:?(\\d{2}(\\.\\d+)?))?)?(Z|([+-])(\\d{2}):?(\\d{2})?)?)?");

  private static int parseInt(String integer, int defaultValue) {
    if (integer == null) {
      return defaultValue;
    }

    try {
      return Integer.parseInt(integer, 10);
    } catch (NumberFormatException e) {
      throw new DateTimeException("invalid date segment");
    }
  }

  private static float parseFloat(String real, float defaultValue) {
    if (real == null) {
      return defaultValue;
    }

    try {
      return Float.parseFloat(real);
    } catch (NumberFormatException e) {
      throw new DateTimeException("invalid date segment");
    }
  }

  public static long parse(String isoDate) {

    Matcher res = isoDateRegex.matcher(isoDate);

    if (!res.matches()) {
      throw new DateTimeException("invalid ISO date");
    }

    int year = parseInt(res.group(1), 1970); // this should always be present
    int month = parseInt(res.group(2), 1) - 1;
    int date = parseInt(res.group(3), 0);
    int hour = parseInt(res.group(5), 0);
    int min = parseInt(res.group(7), 0);

    String secSegment = res.group(9);

    if (secSegment != null) {
      secSegment = secSegment.substring(0, 2);
    }

    int sec = parseInt(secSegment, 0);
    int ms = Math.round(parseFloat(res.group(10), 0) * 1000);

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DATE, date);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, min);
    cal.set(Calendar.SECOND, sec);
    cal.set(Calendar.MILLISECOND, ms);

    String tzSegment = res.group(11);

    if (tzSegment != null && !"Z".equals(tzSegment)) {
      int ahead = "+".equals(res.group(12)) ? 1 : -1;

      cal.add(Calendar.HOUR_OF_DAY, ahead * parseInt(res.group(13), 0));
      cal.add(Calendar.MINUTE, ahead * parseInt(res.group(14), 0));
    }

    return cal.getTimeInMillis();
  }
}
