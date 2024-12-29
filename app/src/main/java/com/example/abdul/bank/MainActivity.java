package com.example.abdul.bank;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.abdul.bank.common.constants.ActivityRequestCode;
import com.example.abdul.bank.common.constants.Constants;
import com.example.abdul.bank.common.utils.DateUtil;
import com.example.abdul.bank.modelscore.WalletCore;
import com.example.abdul.bank.views.Views;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat("##,##,##,##,###.##");
    private static final int[] TO_VIEW_IDs = new int[]{R.id.id_field, R.id.date_field, R.id.amount_field, R.id.details_field};
    private EditText editTextSearchTerm;
    private DBHelper dbHelper;
    private SPManager spManager;
    private SimpleCursorAdapter cursorAdapterWalletEntries;
    private Button buttonStartDate, buttonEndDate;
    private ListView listViewWalletEntries;

    @Override
    protected void onStop() {
        super.onStop();
        dbHelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        // source: https://stackoverflow.com/a/31596288/8075004
        // source: https://stackoverflow.com/a/3142471/8075004
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Toast.makeText(this, "Press Home to exit.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        searchForTheTermAndUpdateListView();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// TODO:
        // Edit feature.
        // Floating button for an entry.
        // Google drive backup if possible.

        dbHelper = new DBHelper(this);
        spManager = new SPManager(this);

        listViewWalletEntries = findViewById(R.id.listViewWalletEntries);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);

        editTextSearchTerm = findViewById(R.id.editTextSearchTerm);

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
        editTextSearchTerm.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchForTheTermAndUpdateListView();
                    shiftFocusFromEditTextAndHideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        Button buttonClearSearchTerm = findViewById(R.id.buttonClearSearchTerm);
        buttonClearSearchTerm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Tap to clear search term.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        buttonClearSearchTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearchTerm.getText().clear();
                searchForTheTermAndUpdateListView();
                shiftFocusFromEditTextAndHideSoftKeyboard();
            }
        });

        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForTheTermAndUpdateListView();
                shiftFocusFromEditTextAndHideSoftKeyboard();
            }
        });
        buttonSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Views.showSearchOptionsPopUp(MainActivity.this);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode.IMPORT_FILE && resultCode == RESULT_OK) {
            importDataStep2_ReadFromUri(data);
        } else if (requestCode == ActivityRequestCode.CREATE_EXPORT_FILE && resultCode == RESULT_OK) {
            ExportData.exportData_ToUserSelectedDirectory(this, data, dbHelper);
        }
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

        if (R.id.option_export_data == item_id && PermissionManager.checkWriteStoragePermission(this, ActivityRequestCode.WRITE_STORAGE)) {
            ExportData.exportData_Step1(this, dbHelper);
            return true;
        } else if (R.id.option_import_data == item_id && PermissionManager.checkReadStoragePermission(this, ActivityRequestCode.READ_STORAGE)) {
            importDataStep1_OpenFilePicker();
            return true;
        } else if (R.id.option_delete_all == item_id) {
            askAndDeleteAllWalletEntries();
        } else if (R.id.option_about_app == item_id) {
            // something here
            AboutApp.showAsAlertPopUp(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivityRequestCode.WRITE_STORAGE) {
            ExportData.exportData_Step1(this, dbHelper);
        } else if (requestCode == ActivityRequestCode.READ_STORAGE) {
            importDataStep1_OpenFilePicker();
        } else if (Arrays.asList(permissions).contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertMessage.show("Permission Denied!", "Please provide WRITE-storage permission to export data.", this, false);
        } else if (Arrays.asList(permissions).contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertMessage.show("Permission Denied!", "Please provide READ-storage permission to import data from backup file.", this, false);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setTextStartDate() {
        buttonStartDate.setText(DateUtil.formatAsDateMonthYear(startDate));
    }

    private void setTextEndDate() {
        buttonEndDate.setText(DateUtil.formatAsDateMonthYear(endDate));
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
                searchForTheTermAndUpdateListView();
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
                searchForTheTermAndUpdateListView();
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

    private static final int columnIndexOfDateLongColumn = 1;
    private static final int columnIndexOfAmountColumn = 2;
    private final SimpleCursorAdapter.ViewBinder viewBinderWalletEntries = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (columnIndex == columnIndexOfDateLongColumn) {
                long dateLong = cursor.getLong(columnIndexOfDateLongColumn);
                ((TextView) view).setText(DateUtil.formatIn12HourFormat(dateLong));
                return true;
            } else if (columnIndex == columnIndexOfAmountColumn) {
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
            String rowId = ((TextView) view.findViewById(R.id.id_field)).getText().toString();
            String date = ((TextView) view.findViewById(R.id.date_field)).getText().toString();
            String amount = ((TextView) view.findViewById(R.id.amount_field)).getText().toString();
            String description = ((TextView) view.findViewById(R.id.details_field)).getText().toString();

            displayEditOrViewDialogue(rowId, date, amount, description);
        }
    };

    private void populateListViewItems() {
        cursorAdapterWalletEntries = new SimpleCursorAdapter(
                this,
                R.layout.list_view_single_wallet_entry_layout,
                dbHelper.searchInDescOrder(editTextSearchTerm, spManager.getSearchType(), startDate, endDate),
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

    private void searchForTheTermAndUpdateListView() {
        cursorAdapterWalletEntries.changeCursor(dbHelper.searchInDescOrder(editTextSearchTerm, spManager.getSearchType(), startDate, endDate));
        setTotalAmount();
    }

    private void setTotalAmount() {
        long totalAmount = dbHelper.getTotalAmount(editTextSearchTerm, spManager.getSearchType(), startDate, endDate);

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
                searchForTheTermAndUpdateListView();

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

    private void displayEditOrViewDialogue(final String id, String date, String amount, final String description) {
        // sources:
        // https://stackoverflow.com/a/18799229/8075004
        // https://stackoverflow.com/a/9470361/8075004
        // https://stackoverflow.com/a/45964439/8075004
        // https://stackoverflow.com/a/16998245/8075004

        String msg = String.format(Locale.US,
                "Amount: %s\n\nDetails: %s",
                amount, description);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        EditText editText = new EditText(MainActivity.this);
        editText.setLayoutParams(layoutParams);
        editText.setKeyListener(null);
        editText.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            editText.setBackground(null);
        }
        editText.setText(msg);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(date);
        alertDialog.setView(editText);
        alertDialog.setCancelable(true);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (Message) null);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, AddOrSubtract.class);
                intent.putExtra(Constants.WALLET_ID, id);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    private void importDataStep1_OpenFilePicker() {
        // code sources:
        // https://stackoverflow.com/a/36558378/8075004
        // https://stackoverflow.com/a/67639241/8075004
        // https://commonsware.com/blog/2016/03/15/how-consume-content-uri.html
        // https://stackoverflow.com/a/61343993/8075004
        // https://developer.android.com/training/data-storage/shared/documents-files#java

        String action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;

        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // why not directly selecting JSON files?
        // reason: https://stackoverflow.com/a/34402101/8075004
        intent.setType("text/plain"); // application/json

        // When need to pass multiple mime-types:
        // https://stackoverflow.com/a/33117677/8075004
        // https://stackoverflow.com/a/23426753/8075004
        // https://stackoverflow.com/q/28978581/8075004

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }

        startActivityForResult(Intent.createChooser(intent, "Select the backup file"), ActivityRequestCode.IMPORT_FILE);
    }

    private void importDataStep2_ReadFromUri(Intent intent) {
        try {
            ContentResolver contentResolver = getContentResolver();

            Uri uri = intent.getData();
            assert uri != null;

            String scheme = uri.getScheme();
            assert scheme != null;

            long fileSizeInBytes = 0;

            switch (scheme) {
                case "file":
                    String filePath = uri.getPath();
                    filePath = filePath == null ? "" : filePath;

                    String extension = "";
                    int index = filePath.lastIndexOf('.');
                    if (index > -1) {
                        extension = filePath.substring(index);
                    }
                    if (!extension.equals(".txt")) {
                        AlertMessage.show("Error", "Please select a \".txt\" file.", this, false);
                        return;
                    }

                    File file = new File(filePath);
                    fileSizeInBytes = file.length();
                    break;
                case "content":
                    Cursor cursor = contentResolver.query(uri, new String[]{OpenableColumns.SIZE}, null, null, null);
                    assert cursor != null;
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (columnIndex != -1 && cursor.moveToFirst() /* cursor.getCount() */) {
                        fileSizeInBytes = cursor.getLong(columnIndex);
                    }
                    cursor.close();
                    break;
                default:
                    AlertMessage.show("Error!", "Unhandled Uri scheme: " + scheme, this, false);
                    break;
            }

            if (fileSizeInBytes > (10 * 1000000)) {
                AlertMessage.show("Error!", "File size is greater than 10 MB. Please select correct file.", this, false);
                return;
            }

            InputStream inputStream = contentResolver.openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line).append('\n');
            }
            String serializedData = stringBuilder.toString();

            JSONArray jsonArray = new JSONArray(serializedData);
            int jsonArrayLength = jsonArray.length();

            List<WalletCore> walletCoreList = new ArrayList<>();

            for (int index = 0; index < jsonArrayLength; index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                WalletCore walletCore = new WalletCore();

                walletCore.dateString = jsonObject.getString("date_string");
                walletCore.dateLong = jsonObject.getLong("date_long");
                walletCore.amount = jsonObject.getLong("amount");
                walletCore.details = jsonObject.getString("details");

                walletCoreList.add(walletCore);
            }

            dbHelper.insertMany(walletCoreList);

            searchForTheTermAndUpdateListView();

            AlertMessage.show("Success!", "Backup has been restored successfully.", this, false);

        } catch (NullPointerException ex) {
            AlertMessage.show("Error", ex.toString(), this, false);
        } catch (Exception ex) {
            AlertMessage.show("Error", ex.toString(), this, false);
        }
    }

    private void shiftFocusFromEditTextAndHideSoftKeyboard() {
        // source: https://stackoverflow.com/a/39884008/8075004
        editTextSearchTerm.clearFocus();

        // source: https://stackoverflow.com/a/54759383/8075004
        // https://stackoverflow.com/q/4165414/8075004
        InputMethodManager inputMethodManager = (InputMethodManager) (MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE));
        inputMethodManager.hideSoftInputFromWindow(editTextSearchTerm.getWindowToken(), 0);

        findViewById(R.id.linearLayoutParent).requestFocus();
    }

    private void askAndDeleteAllWalletEntries() {
        String msg = "Are you sure you want to delete all the wallet entries?\nThis can't be undone.\nYou should create a backup first.";

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteAllEntriesFromWallet();
                searchForTheTermAndUpdateListView();
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