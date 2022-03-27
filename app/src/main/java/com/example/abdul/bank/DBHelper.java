package com.example.abdul.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Abdul on 10/20/2017.
 * ...
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String[] SELECTED_COLUMNS = new String[]{"_id", "Date", "Amount", "Details"};

    public DBHelper(Context context) {
        super(context, "wallet.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Wallet (_id integer primary key autoincrement, Date text, DateLong integer, Amount integer, Details text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists Wallet");
        onCreate(sqLiteDatabase);
    }

    boolean insertOne(String date, long dateLong, String amount, String details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("Date", date);
        contentValues.put("DateLong", dateLong);
        contentValues.put("Amount", amount);
        contentValues.put("Details", details);

        long result = db.insert("Wallet", null, contentValues);

        return result != -1;
    }

    public Cursor getAllInDescOrder(Calendar startDateMillis, Calendar endDateMillis) {
        SQLiteDatabase db = this.getReadableDatabase();

        String raw_query = String.format(Locale.US,
                "SELECT _id,Date,Amount,Details FROM Wallet WHERE DateLong >= %d AND DateLong <= %d ORDER BY _id DESC",
                startDateMillis.getTimeInMillis(),
                endDateMillis.getTimeInMillis()
        );

        Cursor cursor = db.rawQuery(raw_query, null);
        cursor.moveToFirst();

        db.close();
        return cursor;
    }

    public long getTotalAmount(Calendar startDateMillis, Calendar endDateMillis) {
        String sql_query_format = "SELECT SUM(Amount) AS Total FROM Wallet WHERE DateLong >= %d AND DateLong <= %d";
        String raw_query = String.format(Locale.US,
                sql_query_format,
                startDateMillis.getTimeInMillis(),
                endDateMillis.getTimeInMillis()
        );

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(raw_query, null);

        long totalAmount = 0;
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getLong(0);
        }

        db.close();
        cursor.close();
        return totalAmount;
    }

    public String getAllSerialized() throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id,Date,DateLong,Amount,Details FROM Wallet", null);

        JSONArray jsonArray = new JSONArray();

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("_id", cursor.getLong(0));
                jsonObject.put("date_string", cursor.getString(1));
                jsonObject.put("date_long", cursor.getLong(2));
                jsonObject.put("amount", cursor.getLong(3));
                jsonObject.put("details", cursor.getString(4));

                jsonArray.put(jsonObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        this.close();

        return jsonArray.toString();
    }

    public void deleteOneById(String auto_increment_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("Wallet", "_id = ?", new String[]{auto_increment_id});
        // equivalent raw query: DELETE FROM Wallet WHERE _id = auto_increment_id

        db.close();
    }
}
