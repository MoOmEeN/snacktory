package de.jetwick.snacktory.utils;

import java.util.Date;

public class DateUtils {

  public static Date parseDate(String dateStr) {
    String[] parsePatterns = {
        "yyyy-MM-dd'T'HH:mm:ssz",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy/MM/dd HH:mm",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "MM/dd/yyyy HH:mm:ss",
        "MM-dd-yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "MM-dd-yyyy HH:mm",
        "MM/dd/yyyy",
        "MM-dd-yyyy",
        "EEE, MMM dd, yyyy",
        "MM/dd/yyyy hh:mm:ss a",
        "MM-dd-yyyy hh:mm:ss a",
        "MM/dd/yyyy hh:mm a",
        "MM-dd-yyyy hh:mm a",
        "yyyy-MM-dd hh:mm:ss a",
        "yyyy/MM/dd hh:mm:ss a ",
        "yyyy-MM-dd hh:mm a",
        "yyyy/MM/dd hh:mm ",
        "dd MMM yyyy",
        "dd MMMM yyyy",
        "yyyyMMddHHmm",
        "yyyyMMdd HHmm",
        "dd-MM-yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm:ss",
        "dd MMM yyyy HH:mm:ss",
        "dd MMMM yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd/MM/yyyy HH:mm",
        "dd MMM yyyy HH:mm",
        "dd MMMM yyyy HH:mm",
        "yyyyMMddHHmmss",
        "yyyyMMdd HHmmss",
        "yyyyMMdd"
    };

    try {
      return org.apache.commons.lang.time.DateUtils.parseDateStrictly(dateStr, parsePatterns);
    } catch (Exception ex) {
      return null;
    }
  }
}
