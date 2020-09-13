package com.example.abdul.bank;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    TextView textViewTotal;
    EditText editTextAmount;
    Cursor cursor;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateListViewFromDatabase();
    }

    public void addOrSubtract(View v) {
        startActivity(new Intent(this, AddOrSubtract.class));
    }

    public void delete(String id) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.onDelete(id);
        dbHelper.close();
    }

    private void populateListViewFromDatabase() {
        DBHelper dbHelper = new DBHelper(this);
        String[] fromFieldNames = new String[]{DBHelper.ColumnNames.Id, DBHelper.ColumnNames.Date, DBHelper.ColumnNames.Amount, DBHelper.ColumnNames.Details};
        int[] toViewIDs = new int[]{R.id.id_field, R.id.date_field, R.id.amount_field, R.id.details_field};

        cursor = dbHelper.onSelectAll();
        dbHelper.close();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_single_wallet_entry_layout,
                cursor,
                fromFieldNames,
                toViewIDs,
                0
        );

        ListView listView = (ListView) findViewById(R.id.listViewWalletEntries);
        listView.setAdapter(simpleCursorAdapter);

        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                int columnIndexOfAmountColumn = 2;
                if (columnIndex == columnIndexOfAmountColumn) {
                    int amount = cursor.getInt(columnIndexOfAmountColumn);
                    ((TextView) view).setText(decimalFormat.format(amount));
                    return true;
                }
                return false;
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String ID = ((TextView) arg1.findViewById(R.id.id_field)).getText().toString();
                String DATE = ((TextView) arg1.findViewById(R.id.date_field)).getText().toString();
                String AMOUNT = ((TextView) arg1.findViewById(R.id.amount_field)).getText().toString();
                String DETAILS = ((TextView) arg1.findViewById(R.id.details_field)).getText().toString();

                deleteEntry(ID, DATE, AMOUNT, DETAILS);
                return true;
            }
        });

        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        textViewTotal = (TextView) findViewById(R.id.textViewTotalAmount);
        String total = String.format("%s %s", getResources().getString(R.string.total), decimalFormat.format(dbHelper.onSelectTotal()));
        textViewTotal.setText(total);
    }

    private void deleteEntry(final String ID, String DATE, String AMOUNT, String DETAILS) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Do you want to remove this entry from Wallet?\n\n" +
                DATE + "\n" + AMOUNT + "\n" + DETAILS);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                delete(ID);
                populateListViewFromDatabase();
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}