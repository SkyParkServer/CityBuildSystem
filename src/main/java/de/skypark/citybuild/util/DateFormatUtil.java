package de.skypark.citybuild.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Mirrors Skript 'now formatted as "dd.MM.yyyy HH:mm"'. Uses server default timezone. */
public class DateFormatUtil {
  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public static String now() {
    return ZonedDateTime.now(ZoneId.systemDefault()).format(FMT);
  }
}
