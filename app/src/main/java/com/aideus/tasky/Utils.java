package com.aideus.tasky;

import java.text.SimpleDateFormat;

public class Utils {

    // Formatting dates in a locale-sensitive and user-friendly manner.
    // Formatting turns Date into a String.
    public static String getDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    // Formatting time in a locale-sensitive and user-friendly manner.
    // Formatting turns time into a String.
    public static String getTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }

    public static String getFullDate(long date) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return fullDateFormat.format(date);
    }

}
