package com.example.abdul.bank;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abdul.bank.common.constants.Constants;
import com.example.abdul.bank.common.utils.CalendarUtil;
import com.example.abdul.bank.common.utils.DateUtil;
import com.example.abdul.bank.modelscore.WalletCore;

import java.util.Calendar;
import java.util.Date;

public class AddOrSubtract extends AppCompatActivity {
    private String walletIdToEdit;
    private EditText editTextAmount, editTextDetails;
    private TextView textViewDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_subtract);

        textViewDate = findViewById(R.id.textViewDate);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDetails = findViewById(R.id.editTextDetails);

        calendar = Calendar.getInstance();

        populateFieldsIfThisIsAnEditCall();
        formatAndSetTimeStamp(calendar);
    }

    private void populateFieldsIfThisIsAnEditCall() {
        walletIdToEdit = getIntent().getStringExtra(Constants.WALLET_ID);
        if (walletIdToEdit == null) {
            calendar.setTime(new Date());
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        WalletCore walletCore = dbHelper.getOneById(walletIdToEdit);
        dbHelper.close();

        editTextAmount.setText(walletCore.amountString().replace("-", ""));
        editTextDetails.setText(walletCore.details);
        calendar.setTime(new Date(walletCore.dateLong));
    }

    private void formatAndSetTimeStamp(Calendar calendar) {
        String timeStamp = DateUtil.formatIn12HourFormat(calendar);
        textViewDate.setText(timeStamp);
    }

    public void onClickAdd(View v) {
        addOrSubtract(true);
    }

    public void onClickSubtract(View v) {
        addOrSubtract(false);
    }

    private void addOrSubtract(boolean add) {
        String amountStr = editTextAmount.getText().toString();

        if (amountStr.isEmpty()) {
            return;
        }

        String amountStrToInsert = (add ? "" : "-") + amountStr;
        boolean dbResult;

        DBHelper dbHelper = new DBHelper(this);
        if (walletIdToEdit == null) {
            dbResult = dbHelper.insertOne(
                    textViewDate.getText().toString(),
                    CalendarUtil.trimSeconds(calendar).getTimeInMillis(),
                    amountStrToInsert,
                    editTextDetails.getText().toString()
            );
        } else {
            dbResult = dbHelper.updateOne(
                    walletIdToEdit,
                    textViewDate.getText().toString(),
                    CalendarUtil.trimSeconds(calendar).getTimeInMillis(),
                    amountStrToInsert,
                    editTextDetails.getText().toString()
            );
        }

        dbHelper.close();

        String msg = dbResult ? (add ? "+" : "-") + amountStr : "Error! Failed to add/subtract";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        editTextAmount.setText(null);
        editTextDetails.setText(null);
        goBack();
    }


    public void onClickTextViewDate(View view) {
        final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                formatAndSetTimeStamp(calendar);
            }
        };

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                formatAndSetTimeStamp(calendar);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddOrSubtract.this,
                        timeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );
                timePickerDialog.show();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void goBack() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
