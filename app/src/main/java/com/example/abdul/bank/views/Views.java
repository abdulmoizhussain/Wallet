package com.example.abdul.bank.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.abdul.bank.AlertMessage;
import com.example.abdul.bank.R;
import com.example.abdul.bank.SPManager;
import com.example.abdul.bank.common.constants.Constants;

public class Views {
    public static void showSearchOptionsPopUp(final Context ctx) {
        final SPManager spManager = new SPManager(ctx);
        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.search_options, null);

        // SET RADIO BUTTON VALUE WHICH USER HAS SELECTED ALREADY. IF FIRST TIME, THEN SET A DEFAULT VALUE.
        String savedSearchTypeConstant = spManager.getSearchType();
        Integer searchOptionRId = Constants.SearchTypes.getRId(savedSearchTypeConstant);
        if (searchOptionRId == null) {
            searchOptionRId = R.id.radioBtnExactMatch;
        }
        ((RadioButton) dialogView.findViewById(searchOptionRId)).setChecked(true);

        // create alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        // find radio group and attach a value change listener.
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupSearchOptions);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedRadioButtonId) {
                String searchTypeConstant = Constants.SearchTypes.getConstant(checkedRadioButtonId);
                if (searchTypeConstant == null) {
                    String msg = "Unhandled search option selected: " + checkedRadioButtonId;
                    AlertMessage.show("Error!", msg, ctx, true);
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                // save the user selected value in the cache.
                spManager.setSearchType(searchTypeConstant);

                // show the selected value's text in a toast message.
                String text = ((RadioButton) dialogView.findViewById(checkedRadioButtonId)).getText().toString();
                Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();

                // dismiss the radio buttons dialog after selection.
                alertDialog.dismiss();
            }
        });

        // SHOW THE ALERT DIALOG WITH RADIO BUTTONS
        alertDialog.show();
    }
}
