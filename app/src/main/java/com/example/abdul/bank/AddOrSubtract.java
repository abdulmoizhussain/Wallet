package com.example.abdul.bank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddOrSubtract extends AppCompatActivity {
	EditText amount,details;
	TextView date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_or_subtract);
		
		date = (TextView)findViewById(R.id.textView3);
		date.setText(getDate());
		date.append("-"+getMonth()+"-"+getYear()+"-"+getTime());
//		amount.setFocusable(false);
		
	}
	
	public void add (View v) {
		amount = (EditText)findViewById(R.id.editText2);
		if (!amount.getText().toString().isEmpty()) {
			details = (EditText)findViewById(R.id.editText);
			DBHelper mDBHelper = new DBHelper(this);
			if ( mDBHelper.onInsert (date.getText().toString(),
					amount.getText().toString(), details.getText().toString()))
				Toast.makeText(this, amount.getText().toString()+" added to Bank", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, "Error! Cannot add", Toast.LENGTH_LONG).show();
			mDBHelper.close();
			amount.setText(null);
			details.setText(null);
			goBack();
		}
	}
	
	public void subtract (View v) {
		amount = (EditText)findViewById(R.id.editText2);
		if (!amount.getText().toString().isEmpty()) {
			details = (EditText)findViewById(R.id.editText);
			DBHelper mDBHelper = new DBHelper(this);
			if ( mDBHelper.onInsert (date.getText().toString(),
					"-"+amount.getText().toString(), details.getText().toString()))
				Toast.makeText(this, amount.getText().toString()+" subtracted from Bank", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, "Error! Cannot subtract", Toast.LENGTH_LONG).show();
			
			mDBHelper.close();
			amount.setText(null);
			details.setText(null);
			goBack();
		}
	}
	
	private void goBack() {
		finish();
		startActivity(new Intent(this, MainActivity.class));
	}
	
	private String getYear () {
		return new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
	}
	
	private String getDate () {
		return new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
	}
	
	private String getMonth () {
		return new SimpleDateFormat("MMM", Locale.getDefault()).format(new Date());
	}
	
	private String getTime () {
		return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
	}
	
}
