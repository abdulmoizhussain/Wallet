package com.example.abdul.bank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddOrSubtract extends AppCompatActivity {
    private EditText editTextAmount, editTextDetails;
    private TextView textViewDate;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_subtract);

        textViewDate = findViewById(R.id.textViewDate);

        date = new Date();
        String timeStamp = String.format("%s-%s-%s-%s", getDate(date), getMonth(date), getYear(date), getTime(date));
        textViewDate.setText(timeStamp);
    }

    public void add(View v) {
        editTextAmount = findViewById(R.id.editTextAmount);
        if (editTextAmount.getText().toString().isEmpty()) {
            return;
        }
        editTextDetails = findViewById(R.id.editTextDetails);
        DBHelper dbHelper = new DBHelper(this);

        boolean insertionResult = dbHelper.insertOne(
                textViewDate.getText().toString(),
                date.getTime(),
                editTextAmount.getText().toString(),
                editTextDetails.getText().toString()
        );
        String msg = insertionResult ? editTextAmount.getText().toString() + " added to Bank" : "Error! Cannot add";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        dbHelper.close();
        editTextAmount.setText(null);
        editTextDetails.setText(null);
        goBack();
    }

    public void subtract(View v) {
        editTextAmount = findViewById(R.id.editTextAmount);
        if (editTextAmount.getText().toString().isEmpty()) {
            return;
        }

        editTextDetails = findViewById(R.id.editTextDetails);
        DBHelper dbHelper = new DBHelper(this);
        boolean insertionResult = dbHelper.insertOne(
                textViewDate.getText().toString(),
                date.getTime(),
                "-" + editTextAmount.getText().toString(),
                editTextDetails.getText().toString()
        );

        String message = insertionResult ? editTextAmount.getText().toString() + " subtracted from Bank" : "Error! Cannot subtract";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        dbHelper.close();
        editTextAmount.setText(null);
        editTextDetails.setText(null);
        goBack();
    }

    private void goBack() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private String getYear(Date date) {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
    }

    private String getDate(Date date) {
        return new SimpleDateFormat("dd", Locale.getDefault()).format(date);
    }

    private String getMonth(Date date) {
        return new SimpleDateFormat("MMM", Locale.getDefault()).format(date);
    }

    private String getTime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }
}
