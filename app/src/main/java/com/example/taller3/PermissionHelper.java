package com.example.taller3;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    public static void requestPermission(Activity activity, String[] permissions, int permission_code) {
        if (!hasPermission(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, permission_code);
        }
    }

    public static void requestPermission(Activity activity, String permission, int permission_code) {
        if (!hasPermission(activity, new String[]{permission})) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permission_code);
        }
    }

    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasPermission(Activity activity, String[] permissions) {
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) !=
                    PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }
}