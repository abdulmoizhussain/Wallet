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
    EditText editTextAmount, editTextDetails;
    TextView textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_subtract);

        textViewDate = (TextView) findViewById(R.id.textView3);

        Date date = new Date();
        String timeStamp = String.format("%s-%s-%s-%s", getDate(date), getMonth(date), getYear(date), getTime(date));
        textViewDate.setText(timeStamp);
    }

    public void add(View v) {
        editTextAmount = (EditText) findViewById(R.id.editText2);
        if (!editTextAmount.getText().toString().isEmpty()) {
            editTextDetails = (EditText) findViewById(R.id.editText);
            DBHelper mDBHelper = new DBHelper(this);
            if (mDBHelper.onInsert(textViewDate.getText().toString(),
                    editTextAmount.getText().toString(), editTextDetails.getText().toString()))
                Toast.makeText(this, editTextAmount.getText().toString() + " added to Bank", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Error! Cannot add", Toast.LENGTH_LONG).show();
            mDBHelper.close();
            editTextAmount.setText(null);
            editTextDetails.setText(null);
            goBack();
        }
    }

    public void subtract(View v) {
        editTextAmount = (EditText) findViewById(R.id.editText2);
        if (editTextAmount.getText().toString().isEmpty()) {
            return;
        }

        editTextDetails = (EditText) findViewById(R.id.editText);
        DBHelper dbHelper = new DBHelper(this);
        boolean insertionResult = dbHelper.onInsert(textViewDate.getText().toString(), "-" + editTextAmount.getText().toString(), editTextDetails.getText().toString());

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
