package com.example.abdul.bank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddOrSubtract extends AppCompatActivity {
    EditText editTextAmount, editTextDetails;
    TextView textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_subtract);

        textViewDate = findViewById(R.id.textViewDate);

        textViewDate.setText(Utils.formatTo12HourDateTime(new Date()));
    }

    public void add(View v) {
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        if (editTextAmount.getText().toString().isEmpty()) {
            return;
        }
        editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        DBHelper dbHelper = new DBHelper(this);

        boolean insertionResult = dbHelper.onInsert(
                textViewDate.getText().toString(),
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
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        if (editTextAmount.getText().toString().isEmpty()) {
            return;
        }

        editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        DBHelper dbHelper = new DBHelper(this);
        boolean insertionResult = dbHelper.onInsert(
                textViewDate.getText().toString(),
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
}
