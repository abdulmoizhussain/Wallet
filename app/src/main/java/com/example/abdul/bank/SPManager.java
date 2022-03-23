package com.example.abdul.bank;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * android.content.SharedPreferences manager class.
 */
public class SPManager {
    private final SharedPreferences sharedPreferences;

    public SPManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setStartDate(long startDateLong) {
        sharedPreferences.edit().putLong("START_DATE", startDateLong).apply();
    }

    public long getStartDate() {
        return sharedPreferences.getLong("START_DATE", 0);
    }

    public void setEndDate(long startDateLong) {
        sharedPreferences.edit().putLong("END_DATE", startDateLong).apply();
    }

    public long getEndDate() {
        return sharedPreferences.getLong("END_DATE", 0);
    }
}
