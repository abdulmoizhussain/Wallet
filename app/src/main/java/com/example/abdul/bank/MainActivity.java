package com.example.abdul.bank;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
    private Button buttonStartDate, buttonEndDate;
    private static final DecimalFormat decimalFormat = new DecimalFormat("##,##,##,##,###.##");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);

        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);

        SPManager spManager = new SPManager(this);

        long startDateLong = spManager.getStartDate();
        if (startDateLong == 0) {
            startDate.add(Calendar.YEAR, -1);
        } else {
            startDate.setTime(new Date(startDateLong));
        }

        long endDateLong = spManager.getEndDate();
        if (endDateLong != 0) {
            endDate.setTime(new Date(endDateLong));
        }

        setStartDate();
        setEndDate();

        populateListViewFromDatabase();

//        String timeStamp = Utils.getTimeStamp(new Date());
//        String fileName = "wallet-backup-" + timeStamp + ".json";
//        AlertMessage.show(null, fileName, this);
//        exportToDownloadsFolder();
    }

    private void exportToDownloadsFolder(String serializedData, String fileNameWithExtension) {
        try {
            String backupFilePath = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath() + "/" + fileNameWithExtension;

            File file = new File(backupFilePath);

            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(serializedData);
            output.close();

            AlertMessage.show(null, "Backup has been saved to: " + backupFilePath, this);
        } catch (Exception e) {
            AlertMessage.show("Failed to Backup!", e.getMessage(), this);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void setStartDate() {
        String text = "StartDate: " + dateFormat.format(new Date(startDate.getTimeInMillis()));
        buttonStartDate.setText(text);
    }

    private void setEndDate() {
        String text = "EndDate: " + dateFormat.format(new Date(endDate.getTimeInMillis()));
        buttonEndDate.setText(text);
    }

    public void onClickTotal(View v) {
        setTotalAmount(new DBHelper(this));
    }

    public void onClickStartDate(View v) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new SPManager(MainActivity.this).setStartDate(startDate.getTimeInMillis());

                setStartDate();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                listener,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public void onClickEndDate(View v) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new SPManager(MainActivity.this).setEndDate(endDate.getTimeInMillis());

                setEndDate();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                listener,
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
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

        Cursor cursor = dbHelper.onSelectAll();

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_single_wallet_entry_layout,
                cursor,
                fromFieldNames,
                toViewIDs,
                0
        );

        ListView listView = findViewById(R.id.listViewWalletEntries);
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

        setTotalAmount(dbHelper);
    }

    private void setTotalAmount(DBHelper dbHelper) {
        long totalAmount = dbHelper.getTotalAmount(startDate.getTimeInMillis(), endDate.getTimeInMillis());
        dbHelper.close();

        TextView textViewTotal = findViewById(R.id.textViewTotalAmount);
        String total = String.format("%s %s", getResources().getString(R.string.total), decimalFormat.format(totalAmount));
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