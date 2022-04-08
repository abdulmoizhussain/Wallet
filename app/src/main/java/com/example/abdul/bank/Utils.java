package com.example.abdul.bank;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);
    private static final SimpleDateFormat DATE_MONTH_YEAR = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

    public static String getTimeStamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }

    public static String formatAsDateMonthYear(Calendar calendar) {
        return DATE_MONTH_YEAR.format(new Date(calendar.getTimeInMillis()));
    }
}
