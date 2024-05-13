package com.example.abdul.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;

import com.example.abdul.bank.modelscore.WalletCore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Abdul on 10/20/2017.
 * ...
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String[] SELECTED_COLUMNS = new String[]{"_id", "DateLong", "Amount", "Details"};

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

    boolean updateOne(String id, String date, long dateLong, String amount, String details) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Date", date);
        contentValues.put("DateLong", dateLong);
        contentValues.put("Amount", amount);
        contentValues.put("Details", details);

        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.update("Wallet", contentValues, "_id = ?", new String[]{id});
        return affectedRows != -1;
    }

    void insertMany(List<WalletCore> walletCoreList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            for (WalletCore walletCore : walletCoreList) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("Date", walletCore.dateString);
                contentValues.put("DateLong", walletCore.dateLong);
                contentValues.put("Amount", walletCore.amount);
                contentValues.put("Details", walletCore.details);

                db.insert("Wallet", null, contentValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public WalletCore getOneById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id,Date,DateLong,Amount,Details FROM Wallet where _id = " + id, null);

        WalletCore walletCore;
        if (cursor.moveToFirst()) {
            walletCore = new WalletCore();
            walletCore.id = cursor.getLong(0);
            walletCore.dateString = cursor.getString(1);
            walletCore.dateLong = cursor.getLong(2);
            walletCore.amount = cursor.getLong(3);
            walletCore.details = cursor.getString(4);
        } else {
            walletCore = null;
        }
        db.close();
        cursor.close();
        return walletCore;
    }

    public Cursor getAllInDescOrder(EditText editTextSearchTerm, Calendar
            startDateMillis, Calendar endDateMillis) {
        SQLiteDatabase db = this.getReadableDatabase();

        String search_term = "'%" + editTextSearchTerm.getText().toString().trim() + "%'";

        String raw_query = String.format(Locale.US,
                "SELECT _id,DateLong,Amount,Details FROM Wallet WHERE Details LIKE %s AND DateLong >= %d AND DateLong <= %d ORDER BY DateLong DESC, _id DESC",
                search_term,
                startDateMillis.getTimeInMillis(),
                endDateMillis.getTimeInMillis()
        );

        Cursor cursor = db.rawQuery(raw_query, null);
        cursor.moveToFirst();

        db.close();
        return cursor;
    }

    public long getTotalAmount(EditText editTextSearchTerm, Calendar startDateMillis, Calendar endDateMillis) {
        String search_term = "'%" + editTextSearchTerm.getText().toString().trim() + "%'";

        String raw_query = String.format(Locale.US,
                "SELECT SUM(Amount) AS Total FROM Wallet WHERE Details LIKE %s AND DateLong >= %d AND DateLong <= %d",
                search_term,
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

    public void deleteAllEntriesFromWallet() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM Wallet");

        db.close();
    }
}
