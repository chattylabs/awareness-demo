package com.chattylabs.module.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public abstract class PermissionsHelper {

    public static void check(Activity activity, String[] permissions) {
        if (needsPermissions(activity.getApplicationContext(), permissions)) {

            // Should we show an explanation?
            if (needsExplanation(activity, permissions)) {

                ActivityCompat.requestPermissions(activity, permissions, 10);

                // TODO
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity, permissions, 10);
            }
        }
    }

    private static boolean needsPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    private static boolean needsExplanation(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }
        return false;
    }
}
