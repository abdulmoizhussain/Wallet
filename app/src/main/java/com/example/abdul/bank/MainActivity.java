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

public class MainActivity extends AppCompatActivity {
    TextView textViewTotal;
    EditText editTextAmount;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateListViewFromDB();
    }

    public void addOrSubtract(View v) {
        startActivity(new Intent(this, AddOrSubtract.class));
    }


    public void delete(String ID) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.onDelete(ID);
        dbHelper.close();
    }

    private void populateListViewFromDB() {
        DBHelper mDBHelper = new DBHelper(this);
        String[] fromFieldNames = new String[]{DBHelper.ColumnNames.Id, DBHelper.ColumnNames.Date, DBHelper.ColumnNames.Amount, DBHelper.ColumnNames.Details};
        int[] toViewIDs = new int[]{R.id.id_field, R.id.date_field, R.id.amount_field, R.id.details_field};

        cursor = mDBHelper.onSelectAll();
        mDBHelper.close();
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,
                        R.layout.list_view_layout, // Row layout template
                        cursor,                     // cursor (set of DB records to map)
                        fromFieldNames,             // DB Column names
                        toViewIDs,                  // View IDs to put information in
                        0
                );

        // Set the adapter for the list view
        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter(myCursorAdapter);

        myList.setLongClickable(true);
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                //to get the textView strings from listView layout.
                String ID = ((TextView) arg1.findViewById(R.id.id_field)).getText().toString();
                String DATE = ((TextView) arg1.findViewById(R.id.date_field)).getText().toString();
                String AMOUNT = ((TextView) arg1.findViewById(R.id.amount_field)).getText().toString();
                String DETAILS = ((TextView) arg1.findViewById(R.id.details_field)).getText().toString();

                deleteEntry(ID, DATE, AMOUNT, DETAILS);
                return true;
            }
        });

        textViewTotal = (TextView) findViewById(R.id.textView16);
        textViewTotal.setText(getResources().getString(R.string.total));
        textViewTotal.append(" " + mDBHelper.onSelectTotal());

        editTextAmount = (EditText) findViewById(R.id.editText2);
    }

    private void deleteEntry(final String ID, String DATE, String AMOUNT, String DETAILS) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Do you want to remove this entry from Wallet?\n\n" +
                DATE + "\n" + AMOUNT + "\n" + DETAILS);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete(ID);
                        populateListViewFromDB();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}