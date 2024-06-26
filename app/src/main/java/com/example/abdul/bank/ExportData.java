package com.example.abdul.bank;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.example.abdul.bank.common.constants.ActivityRequestCode;
import com.example.abdul.bank.common.utils.DateUtil;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;

public class ExportData {
    private static String fileName;

    /**
     * @param context  This context should be called from an Activity
     * @param dbHelper DBHelper to grab database entries.
     */
    public static void exportData_Step1(Context context, DBHelper dbHelper) {
        String fileName = "wallet-v" + BuildConfig.VERSION_CODE + "-" + DateUtil.formatForFileNameTimeStamp(new Date()) + ".json.txt";

        ExportData.fileName = fileName;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            toDownloadsDirectory_Step1(context, dbHelper, fileName);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        ((Activity) context).startActivityForResult(intent, ActivityRequestCode.CREATE_EXPORT_FILE);
    }

    public static void exportData_ToUserSelectedDirectory(Context context, Intent intent, DBHelper dbHelper) {
        // source: https://stackoverflow.com/a/75134879/8075004
        // String fileName = intent.getStringExtra(Intent.EXTRA_TITLE);

        try {
            Uri uri = intent.getData();
            assert uri != null;

            String scheme = uri.getScheme();
            assert scheme != null;
            if (scheme.equals("content")) {
                ContentResolver contentResolver = context.getContentResolver();
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                assert outputStream != null;
                String dbRecordsSerialized = dbHelper.getAllSerialized();

                // source: https://stackoverflow.com/q/4069028/8075004
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.write(dbRecordsSerialized);
                printWriter.close();

                AlertMessage.show("Backup has been saved as: ", fileName, context, false);
            } else {
                AlertMessage.show(
                        "ERROR",
                        String.format(Locale.US, "Invalid scheme: %s. Please contact support.", scheme),
                        context,
                        false);
            }
        } catch (JSONException | FileNotFoundException ex) {
            AlertMessage.show(
                    "Failed to Backup !",
                    ex.getMessage(),
                    context,
                    false
            );
        } catch (Exception ex) {
            AlertMessage.show(
                    "Failed to Backup !",
                    ex.getMessage(),
                    context,
                    false
            );
        }
    }

    private static void toDownloadsDirectory_Step1(Context context, DBHelper dbHelper, String fileName) {
        try {
            String dbRecordsSerialized = dbHelper.getAllSerialized();

            String backupFilePath = toDownloadsDirectory_Step2(dbRecordsSerialized, fileName);

            AlertMessage.show("Backup has been saved to: ", backupFilePath, context, false);

        } catch (JSONException | IOException ex) {
            AlertMessage.show(
                    "Failed to Backup !",
                    ex.getMessage(),
                    context,
                    false
            );
        }
    }

    private static String toDownloadsDirectory_Step2(String serializedData, String fileNameWithExtension) throws IOException {
        String backupFileAbsolutePath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + fileNameWithExtension;

        File file = new File(backupFileAbsolutePath);
        Writer output = new BufferedWriter(new FileWriter(file));
        output.write(serializedData);
        output.close();

        return backupFileAbsolutePath;
    }
}
