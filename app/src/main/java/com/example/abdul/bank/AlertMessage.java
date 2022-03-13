package com.example.abdul.bank;

import android.content.Context;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;

public class AlertMessage {
    public static void show(String title, String message, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", (Message) null);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
