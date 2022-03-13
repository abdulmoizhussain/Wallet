package com.example.abdul.bank;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);

    public static String getTimeStamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }
}
