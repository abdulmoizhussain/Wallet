package com.example.abdul.bank.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);
    private static final SimpleDateFormat DATE_MONTH_YEAR = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    private static final SimpleDateFormat timeStamp_12Hour = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US);

    /**
     * Returns time stamp against the provided date instance with format: yyyy-MM-dd-HH.mm.ss. Cannot put colon in the name, as windows file system does not support it.
     *
     * @param date java.util.Date instance
     * @return returns the formatted time in string.
     */
    public static String formatForFileNameTimeStamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }

    /**
     * Formats date from the calendar instance in the format: dd-MMM-yyyy
     *
     * @param calendar The calendar instance.
     * @return Returns the formatted date string.
     */
    public static String formatAsDateMonthYear(Calendar calendar) {
        return DATE_MONTH_YEAR.format(new Date(calendar.getTimeInMillis()));
    }

    public static String formatIn12HourFormat(Calendar calendar) {
        Date date = new Date(calendar.getTimeInMillis());
        return timeStamp_12Hour.format(date);
    }

    public static String formatIn12HourFormat(long milliseconds) {
        Date date = new Date(milliseconds);
        return timeStamp_12Hour.format(date);
    }
}
