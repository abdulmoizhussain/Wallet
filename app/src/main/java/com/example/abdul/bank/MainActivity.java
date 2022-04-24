package com.example.abdul.bank;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_STORAGE = 200;
    private static final int REQUEST_CODE_READ_STORAGE = 201;
    private final Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat("##,##,##,##,###.##");
    private static final int[] TO_VIEW_IDs = new int[]{R.id.id_field, R.id.date_field, R.id.amount_field, R.id.details_field};
    private DBHelper dbHelper;
    private SPManager spManager;
    private SimpleCursorAdapter cursorAdapterWalletEntries;
    private Button buttonStartDate, buttonEndDate;
    private ListView listViewWalletEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// TODO:
        // Edit feature.
        // Import feature.
        // Floating button for an entry.
        // Google drive backup if possible.
        // Put icons with import/export options.

        dbHelper = new DBHelper(this);
        spManager = new SPManager(this);

        listViewWalletEntries = findViewById(R.id.listViewWalletEntries);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);

        // min/max values of Calendar.MILLISECOND (check java-doc of Example 4):
        // https://www.programcreek.com/java-api-examples/?class=java.util.Calendar&method=MILLISECOND
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);

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

        setTextStartDate();
        setTextEndDate();

        populateListViewItems();

        // Attaching listeners:
        findViewById(R.id.buttonClearSearchTerm).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Tap to clear search term.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        findViewById(R.id.buttonSearch).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Tap to find entries with the typed search term.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /**
     * Requires: Android 8 - Oreo.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openFilePickerDialogue(String fileName, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        Uri uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        if (R.id.option_export_data == item_id && PermissionManager.checkWriteStoragePermission(this, REQUEST_CODE_WRITE_STORAGE)) {
            exportData();
            return true;
        } else if (R.id.option_import_data == item_id && PermissionManager.checkReadStoragePermission(this, REQUEST_CODE_READ_STORAGE)) {
            importData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            exportData();
        } else if (requestCode == REQUEST_CODE_READ_STORAGE) {
            importData();
        } else if (Arrays.asList(permissions).contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertMessage.show("Permission Denied!", "Please provide WRITE-storage permission to export data.", this, false);
        } else if (Arrays.asList(permissions).contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertMessage.show("Permission Denied!", "Please provide READ-storage permission to import data from backup file.", this, false);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setTextStartDate() {
        buttonStartDate.setText(Utils.formatAsDateMonthYear(startDate));
    }

    private void setTextEndDate() {
        buttonEndDate.setText(Utils.formatAsDateMonthYear(endDate));
    }

    public void onClickStartDate(View v) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                spManager.setStartDate(startDate.getTimeInMillis());

                setTextStartDate();
                updateListViewItems();
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

                spManager.setEndDate(endDate.getTimeInMillis());

                setTextEndDate();
                updateListViewItems();
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

    private final SimpleCursorAdapter.ViewBinder viewBinderWalletEntries = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int columnIndexOfAmountColumn = 2;
            if (columnIndex == columnIndexOfAmountColumn) {
                long amount = cursor.getLong(columnIndexOfAmountColumn);
                ((TextView) view).setText(decimalFormat.format(amount));
                return true;
            }
            return false;
        }
    };

    private final AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            String dbId = ((TextView) view.findViewById(R.id.id_field)).getText().toString();
            String date = ((TextView) view.findViewById(R.id.date_field)).getText().toString();
            String amount = ((TextView) view.findViewById(R.id.amount_field)).getText().toString();
            String description = ((TextView) view.findViewById(R.id.details_field)).getText().toString();

            askUserToDeleteThisEntry(dbId, date, amount, description);
            return true;
        }
    };

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String date = ((TextView) view.findViewById(R.id.date_field)).getText().toString();
            String amount = ((TextView) view.findViewById(R.id.amount_field)).getText().toString();
            String description = ((TextView) view.findViewById(R.id.details_field)).getText().toString();

            String msg = String.format(Locale.US,
                    "Amount: %s\n\nDetails: %s",
                    amount, description);

            AlertMessage.showSelectable(date, msg, MainActivity.this, true);
        }
    };

    private void populateListViewItems() {
        cursorAdapterWalletEntries = new SimpleCursorAdapter(
                this,
                R.layout.list_view_single_wallet_entry_layout,
                dbHelper.getAllInDescOrder(startDate, endDate),
                DBHelper.SELECTED_COLUMNS,
                TO_VIEW_IDs,
                0
        );
        cursorAdapterWalletEntries.setViewBinder(viewBinderWalletEntries);

        listViewWalletEntries.setAdapter(cursorAdapterWalletEntries);
        listViewWalletEntries.setLongClickable(true);
        listViewWalletEntries.setClickable(true);
        listViewWalletEntries.setOnItemClickListener(onItemClickListener);
        listViewWalletEntries.setOnItemLongClickListener(onItemLongClickListener);

        setTotalAmount();
    }

    private void updateListViewItems() {
        cursorAdapterWalletEntries.changeCursor(dbHelper.getAllInDescOrder(startDate, endDate));
        setTotalAmount();
    }

    private void setTotalAmount() {
        long totalAmount = dbHelper.getTotalAmount(startDate, endDate);

        String total = String.format("%s %s", getResources().getString(R.string.total), decimalFormat.format(totalAmount));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(total);
        }
    }

    private void askUserToDeleteThisEntry(final String id, String date, String amount, String description) {
        String msg = String.format(Locale.US,
                "Do you want to remove this entry from Wallet?\n\n%s\n\n%s\n\n%s",
                date, amount, description);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteOneById(id);
                updateListViewItems();

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

    private void exportData() {
        try {
            String dbRecordsSerialized = dbHelper.getAllSerialized();

            String fileName = "wallet-backup-" + Utils.getTimeStamp(new Date()) + ".json";

            String backupFilePath = exportToDownloadsFolder(dbRecordsSerialized, fileName);

            AlertMessage.show("Backup has been saved to: ", backupFilePath, this, false);

        } catch (JSONException | IOException ex) {
            AlertMessage.show(
                    "Failed to Backup !",
                    ex.getMessage(),
                    this,
                    false
            );
        }
    }

    private void importData() {
        Toast.makeText(this, "Not implemented yet.", Toast.LENGTH_SHORT).show();
    }

    private String exportToDownloadsFolder(String serializedData, String fileNameWithExtension) throws IOException {
        String backupFileAbsolutePath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + fileNameWithExtension;

        File file = new File(backupFileAbsolutePath);

        Writer output = new BufferedWriter(new FileWriter(file));
        output.write(serializedData);
        output.close();

        return backupFileAbsolutePath;
    }
}