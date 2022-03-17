package com.example.abdul.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abdul on 10/20/2017.
 * ...
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "wallet.db";

    public static class TableNames {
        public static final String Wallet = "Wallet";
    }

    public static class ColumnNames {
        public static final String Id = "_id";
        public static final String Date = "Date";
        public static final String DateLong = "DateLong";
        public static final String Amount = "Amount";
        public static final String Details = "Details";
        public static final String Total = "Total";
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "
                + TableNames.Wallet
                + " ("
                + ColumnNames.Id + " integer primary key autoincrement, "
                + ColumnNames.Date + " text, "
                + ColumnNames.DateLong + " integer, "
                + ColumnNames.Amount + " integer, "
                + ColumnNames.Details + " text"
                + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + TableNames.Wallet);
        onCreate(sqLiteDatabase);
    }

    boolean insertOne(String date, long dateLong, String amount, String details) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ColumnNames.Date, date);
        contentValues.put(ColumnNames.DateLong, dateLong);
        contentValues.put(ColumnNames.Amount, amount);
        contentValues.put(ColumnNames.Details, details);

        long result = sqLiteDatabase.insert(TableNames.Wallet, null, contentValues);
        return result != -1;
    }

    public Cursor getAllInDescOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TableNames.Wallet,
                new String[]{ColumnNames.Id, ColumnNames.Date, ColumnNames.Amount, ColumnNames.Details},
                null, null, null, null, ColumnNames.Id + " DESC", null);
        // Cursor cursor = db.rawQuery("select "+KEY_DATE+","+KEY_AMOUNT+", sum("+KEY_AMOUNT+") as "+KEY_TOTAL+" from "+TABLE_NAME,null);
        cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public String getAllSerialized() throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(false, TableNames.Wallet,
                new String[]{ColumnNames.Id, ColumnNames.Date, ColumnNames.DateLong, ColumnNames.Amount, ColumnNames.Details},
                null, null, null, null, null, null);

        JSONArray jsonArray = new JSONArray();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ColumnNames.Id);
            int dateStringIndex = cursor.getColumnIndex(ColumnNames.Date);
            int dateLongIndex = cursor.getColumnIndex(ColumnNames.DateLong);
            int amountIndex = cursor.getColumnIndex(ColumnNames.Amount);
            int detailsIndex = cursor.getColumnIndex(ColumnNames.Details);

            do {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", cursor.getLong(idIndex));
                jsonObject.put("date_string", cursor.getString(dateStringIndex));
                jsonObject.put("date_long", cursor.getLong(dateLongIndex));
                jsonObject.put("amount", cursor.getLong(amountIndex));
                jsonObject.put("details", cursor.getString(detailsIndex));

                jsonArray.put(jsonObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        this.close();

        return jsonArray.toString();
    }

    public void deleteOneById(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableNames.Wallet, ColumnNames.Id + " = ?", new String[]{ID});
        //db.execSQL("delete from "+TABLE_NAME+" where "+KEY_ID+" ="+ID+";");
        db.close();
    }

    public long getTotalAmount(long startDateMillis, long endDateMillis) {
        String dateFilter = " WHERE DateLong >= " + startDateMillis + " AND DateLong <= " + endDateMillis;
        String rawQuery = "SELECT SUM(" + ColumnNames.Amount + ") AS " + ColumnNames.Total + " FROM " + TableNames.Wallet + dateFilter;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        db.close();
        cursor.close();
        return total;
    }
}
