package com.example.avjindersinghsekhon.minimaltodo.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
  public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";

  public static final String DATE_FORMAT = "d MMM, yyyy";


  public static final String TIME_FORMAT_12_HOUR = "h:mm a";
  public static final String TIME_FORMAT_24_HOUR = "k:mm";
  public static final String AM_PM_FORMAT = "a";

  public static String formatDateTime(Context context, Date dateTimeToFormat) {
    String format = DateFormat.is24HourFormat(context) ? DATE_TIME_FORMAT_24_HOUR : DATE_TIME_FORMAT_12_HOUR;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    return simpleDateFormat.format(dateTimeToFormat);
  }

  public static String formatDate(Context context, Date dateToFormat) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    return simpleDateFormat.format(dateToFormat);
  }

  public static String formatTime(Context context, Date timeToFormat) {
    String format = DateFormat.is24HourFormat(context) ? TIME_FORMAT_24_HOUR : TIME_FORMAT_12_HOUR;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    return simpleDateFormat.format(timeToFormat);
  }

  public static String formatAmPm(Context context, Date timeToFormat) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AM_PM_FORMAT);
    return simpleDateFormat.format(timeToFormat);
  }
}
