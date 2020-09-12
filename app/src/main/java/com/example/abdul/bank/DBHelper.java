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
    public static final String TABLE_NAME = "table1";

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DETAILS = "details";

    public static final String KEY_TOTAL = "total";
    public static final String[] ALL_KEYS = new String[]
            {KEY_ID, KEY_DATE, KEY_AMOUNT, KEY_DETAILS};

    /*
        public static final int COL_ID = 0;
        public static final int COL_DATE = 1;
        public static final int COL_AMOUNT = 2;
    */
    private static final String DATABASE_CREATE_SQL = "create table "
            + TABLE_NAME
            + " (" + KEY_ID + " integer primary key autoincrement, "
            + KEY_DATE + " text, "
            + KEY_AMOUNT + " integer, "
            + KEY_DETAILS + " text"
            + ");";

    DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    boolean onInsert(String date, String amount, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues a = new ContentValues();
        a.put(KEY_DATE, date);
        a.put(KEY_AMOUNT, amount);
        a.put(KEY_DETAILS, details);

        long r = db.insert(TABLE_NAME, null, a);

        return r != -1;
    }

    Cursor onSelectAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                ALL_KEYS,
                null, null, null, null,
                KEY_ID + " DESC", null);
        //Cursor cursor = db.rawQuery("select "+KEY_DATE+","+KEY_AMOUNT+", sum("+KEY_AMOUNT+") as "+KEY_TOTAL+" from "+TABLE_NAME,null);
        if (cursor != null)
            cursor.moveToFirst();
        db.close();
        return cursor;
    }

    void onDelete(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{ID});
        //db.execSQL("delete from "+TABLE_NAME+" where "+KEY_ID+" ="+ID+";");
        db.close();
    }

    int onSelectTotal() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select sum(" + KEY_AMOUNT + ") as " + KEY_TOTAL + " from " + TABLE_NAME, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        db.close();
        cursor.close();
        return total;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SQL);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        //onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
