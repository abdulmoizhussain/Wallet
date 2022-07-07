package com.example.abdul.bank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);
    private static final SimpleDateFormat DATE_MONTH_YEAR = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    private static final SimpleDateFormat DATE_MONTH_YEAR_TIME = new SimpleDateFormat("dd-MMM-yyyy-hh:mm a", Locale.US);

    public static String getTimeStamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }

    public static Date parseFrom12HourDateTime(String dateString) throws ParseException {
        return DATE_MONTH_YEAR_TIME.parse(dateString);
    }

    public static String formatTo12HourDateTime(Date date) {
        return DATE_MONTH_YEAR_TIME.format(date);
    }

    public static String formatAsDateMonthYear(Calendar calendar) {
        return DATE_MONTH_YEAR.format(new Date(calendar.getTimeInMillis()));
    }
}
