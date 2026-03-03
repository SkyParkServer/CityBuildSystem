package de.skypark.citybuild.util;

import java.time.Duration;

/** Format durations similar to Skript's default duration formatting for simple cases. */
public class DurationFormatUtil {

  public static String format(Duration d) {
    if (d == null || d.isNegative() || d.isZero()) return "0 seconds";
    long seconds = (long) Math.ceil(d.toMillis() / 1000.0);

    long minutes = seconds / 60;
    long remSec = seconds % 60;

    if (minutes <= 0) {
      return seconds + (seconds == 1 ? " second" : " seconds");
    }

    if (remSec == 0) {
      return minutes + (minutes == 1 ? " minute" : " minutes");
    }

    return minutes
        + (minutes == 1 ? " minute " : " minutes ")
        + remSec
        + (remSec == 1 ? " second" : " seconds");
  }
}
