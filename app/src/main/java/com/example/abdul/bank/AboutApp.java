package com.example.abdul.bank;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

public class AboutApp {
    public static void showAsAlertPopUp(final Context ctx) {
        String version = String.format(Locale.US, " v%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.about_app, null);

        ((TextView) dialogView.findViewById(R.id.textViewVersion)).setText(version);

        dialogView.findViewById(R.id.imageViewCopySupportEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String support_email = ctx.getResources().getString(R.string.support_email);
                copyToClipboard(ctx, support_email);
                Toast.makeText(ctx, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        dialogView.findViewById(R.id.imageViewCopyGithubIssuesUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String github_issues_url = ctx.getResources().getString(R.string.github_issues_url);
                copyToClipboard(ctx, github_issues_url);
                Toast.makeText(ctx, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private static void copyToClipboard(Context context, String textToCopy) {
        ClipData clipData = ClipData.newPlainText("label", textToCopy);

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clipData);
    }
}
