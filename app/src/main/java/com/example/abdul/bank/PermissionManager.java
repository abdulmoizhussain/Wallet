package com.example.abdul.bank;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {
    /**
     * @param context     This must always be the activity context. Passing it getApplicationContext() might crash the app.
     * @param requestCode This is the code which you will receive inside onRequestPermissionsResult() when the permission is successfully granted.
     * @return When true, the permission has been granted by the user. Upon false it has already requested for the permission and then you should check the method: onRequestPermissionsResult().
     */
    public static Boolean checkWriteStoragePermission(Context context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permission is automatically granted on sdk<23 (sdk 23 is Marshmallow) upon installation.
            return true;
        }

        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // ask for the permissions when not granted:
        String[] permissionsToAsk = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions((Activity) context, permissionsToAsk, requestCode);
        return false;
    }

    /**
     * @param context     This must always be the activity context. Passing it getApplicationContext() might crash the app.
     * @param requestCode This is the code which you will receive inside onRequestPermissionsResult() when the permission is successfully granted.
     * @return When true, the permission has been granted by the user. Upon false it has already requested for the permission and then you should check the method: onRequestPermissionsResult().
     */
    public static Boolean checkReadStoragePermission(Context context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permission is automatically granted on sdk<23 (sdk 23 is Marshmallow) upon installation.
            return true;
        }

        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // ask for the permissions when not granted:
        String[] permissionsToAsk = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions((Activity) context, permissionsToAsk, requestCode);
        return false;
    }
}
