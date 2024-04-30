package com.example.abdul.bank.common.utils;

import java.util.Calendar;

public class CalendarUtil {
    public static Calendar trimSeconds(Calendar calendar) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
