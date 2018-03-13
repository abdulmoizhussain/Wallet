package com.example.abdul.bank;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import java.text.SimpleDateFormat;

import android.os.Build;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
	TextView date,total;
	EditText amount;
	Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//date = (TextView)findViewById(R.id.textView3);
		populateListViewFromDB();
	}
	
	public void addOrSubtract (View v) {
		//stopManagingCursor(cursor);
		//cursor.close();
		startActivity(new Intent(this, AddOrSubtract.class));
	}

	//
//	public void add (View v) {
//		amount = (EditText)findViewById(R.id.editText2);
//		if (!amount.getText().toString().isEmpty()) {
//			details = (EditText)findViewById(R.id.editText);
//			DBHelper mDBHelper = new DBHelper(this);
//			if ( mDBHelper.onInsert (date.getText().toString(),
//					amount.getText().toString(), details.getText().toString()))
//				Toast.makeText(this, amount.getText().toString()+" added to Bank", Toast.LENGTH_LONG).show();
//			else
//				Toast.makeText(this, "Error! Cannot add", Toast.LENGTH_LONG).show();
//			mDBHelper.close();
//			populateListViewFromDB();
//			amount.setText(null); details.setText(null);
//		}
//	}
//
//	public void subtract (View v) {
//		amount = (EditText)findViewById(R.id.editText2);
//		if (!amount.getText().toString().isEmpty()) {
//			details = (EditText)findViewById(R.id.editText);
//			DBHelper mDBHelper = new DBHelper(this);
//			if ( mDBHelper.onInsert (date.getText().toString(),
//					"-"+amount.getText().toString(), details.getText().toString()))
//				Toast.makeText(this, amount.getText().toString()+" subtracted from Bank", Toast.LENGTH_LONG).show();
//			else
//				Toast.makeText(this, "Error! Cannot subtract", Toast.LENGTH_LONG).show();
//
//			mDBHelper.close();
//			populateListViewFromDB();
//			amount.setText(null); details.setText(null);
//		}
//	}
	
	public void delete (String ID) {
		DBHelper dbHelper = new DBHelper(this);
		dbHelper.onDelete(ID);
		dbHelper.close();
	}
	
	private void populateListViewFromDB () {
		DBHelper mDBHelper = new DBHelper(this);
		String[] fromFieldNames = new String[] { DBHelper.KEY_ID, DBHelper.KEY_DATE, DBHelper.KEY_AMOUNT, DBHelper.KEY_DETAILS};
		int[] toViewIDs = new int[] { R.id.id_field, R.id.date_field, R.id.amount_field, R.id.details_field };
		
		cursor = mDBHelper.onSelectAll();
		mDBHelper.close();
		//startManagingCursor(cursor);
		SimpleCursorAdapter myCursorAdapter =
				new SimpleCursorAdapter (
						this,// Context
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
				// TODO Auto-generated method stub
				//Log.v("long clicked","pos: " + pos);
				
				//to get the textView strings from listView layout.
				String ID = ((TextView) arg1.findViewById(R.id.id_field)).getText().toString();
				String DATE = ((TextView) arg1.findViewById(R.id.date_field)).getText().toString();
				String AMOUNT = ((TextView) arg1.findViewById(R.id.amount_field)).getText().toString();
				String DETAILS = ((TextView) arg1.findViewById(R.id.details_field)).getText().toString();
				
				deleteEntry(ID,DATE,AMOUNT,DETAILS);
				ID=AMOUNT=DETAILS=DATE=null;
				return true;
			}
		});
		
		total = (TextView)findViewById(R.id.textView16);
		total.setText(getResources().getString(R.string.total));
		total.append(" "+ mDBHelper.onSelectTotal());
		
		amount = (EditText)findViewById(R.id.editText2);
//		amount.clearFocus();
		/*if ( amount.isFocused() )
			amount.clearFocus();*/
	}
	
	private void deleteEntry (final String ID, String DATE, String AMOUNT, String DETAILS) {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage("Do you want to remove this entry from Wallet?\n\n"+
				DATE+"\n"+AMOUNT+"\n"+DETAILS);
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