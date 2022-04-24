package com.example.abdul.bank;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

public class AlertMessage {
    public static void show(String title, String message, Context context, boolean cancelable) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (Message) null);
        alertDialog.setCancelable(cancelable);
        alertDialog.show();
    }

    public static void showSelectable(String title, String message, Context context, boolean cancelable) {
        // sources:
        // https://stackoverflow.com/a/18799229/8075004
        // https://stackoverflow.com/a/9470361/8075004
        // https://stackoverflow.com/a/45964439/8075004
        // https://stackoverflow.com/a/16998245/8075004

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        EditText editText = new EditText(context);
        editText.setLayoutParams(layoutParams);
        editText.setKeyListener(null);
        editText.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            editText.setBackground(null);
        }
        editText.setText(message);

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setView(editText);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (Message) null);
        alertDialog.setCancelable(cancelable);
        alertDialog.show();
    }
}
