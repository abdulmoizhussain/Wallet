package com.example.abdul.bank;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", Locale.US);

    public static String getTimeStamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }

    public static String stringJoin(String delimiter, String[] elements) {
        int length = elements.length;
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(elements[0]);

        if (length == 1) {
            return sb.toString();
        }

        for (int index = 1; index < length; index++) {
            sb.append(delimiter).append(elements[index]);
        }
        return sb.toString();
    }

    public static String stringJoin2(String delimiter, String[] elements) {
        StringBuilder sb = new StringBuilder();
        for (String element : elements) {
            sb.append(",").append(element);
        }
        return sb.toString().replaceFirst(",", "");
    }
}
