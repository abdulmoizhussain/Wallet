package com.example.abdul.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abdul on 10/20/2017.
 * ...
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "Bank";

    public static class TableNames {
        public static final String Wallet = "Wallet";
    }

    public static class ColumnNames {
        public static final String Id = "_id";
        public static final String Date = "Date";
        public static final String Amount = "Amount";
        public static final String Details = "Details";
        public static final String Total = "Total";
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "
                + TableNames.Wallet
                + " ("
                + ColumnNames.Id + " integer primary key autoincrement, "
                + ColumnNames.Date + " text, "
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

    boolean onInsert(String date, String amount, String details) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ColumnNames.Date, date);
        contentValues.put(ColumnNames.Amount, amount);
        contentValues.put(ColumnNames.Details, details);

        long result = sqLiteDatabase.insert(TableNames.Wallet, null, contentValues);
        return result != -1;
    }

    public Cursor onSelectAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(true,
                TableNames.Wallet,
                new String[]{ColumnNames.Id, ColumnNames.Date, ColumnNames.Amount, ColumnNames.Details},
                null, null, null, null,
                ColumnNames.Id + " DESC", null);
        //Cursor cursor = sqLiteDatabase.rawQuery("select "+KEY_DATE+","+KEY_AMOUNT+", sum("+KEY_AMOUNT+") as "+KEY_TOTAL+" from "+TABLE_NAME,null);
        if (cursor != null)
            cursor.moveToFirst();
        sqLiteDatabase.close();
        return cursor;
    }

    public void onDelete(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TableNames.Wallet, ColumnNames.Id + " = ?", new String[]{ID});
        //sqLiteDatabase.execSQL("delete from "+TABLE_NAME+" where "+KEY_ID+" ="+ID+";");
        sqLiteDatabase.close();
    }

    int onSelectTotal() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select sum(" + ColumnNames.Amount + ") as " + ColumnNames.Total + " from " + TableNames.Wallet, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        sqLiteDatabase.close();
        cursor.close();
        return total;
    }
}
