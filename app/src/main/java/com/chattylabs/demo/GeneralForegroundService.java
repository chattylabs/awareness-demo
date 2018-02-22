package com.chattylabs.demo;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chattylabs.module.base.ServiceDelegate;

public class GeneralForegroundService extends Service {

    public static String START_FOREGROUND_SERVICE_COMMAND = "START_FOREGROUND_SERVICE_COMMAND";
    public static final int ID = 222;
    private static boolean connected;

    // Trial components
    ServiceDelegate serviceDelegate;

    public static void start(Context context) {
        Intent intent = new Intent(context, GeneralForegroundService.class);
        intent.putExtra(GeneralForegroundService.START_FOREGROUND_SERVICE_COMMAND, true);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, GeneralForegroundService.class);
        intent.putExtra(GeneralForegroundService.START_FOREGROUND_SERVICE_COMMAND, false);
        context.startService(intent);
    }

    public static boolean isConnected() {
        return connected;
    }

    public GeneralForegroundService() {
        //this.serviceDelegate = new NotificationListenerDelegate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connected = true;
        serviceDelegate.onCreate(getApplication(), this.getClass());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If Android kills the service, after creating the new service instance;
        // if there are not any pending start commands to be delivered to the service,
        // it will be called with a null intent object (START_STICKY)
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(START_FOREGROUND_SERVICE_COMMAND)) {
                if (extras.getBoolean(START_FOREGROUND_SERVICE_COMMAND, false)) {
                    startForeground(ID, new Notification());
                } else {
                    stopForeground(true);
                    stopSelf();
                }
            } else {
                serviceDelegate.onStartCommand(getApplication(), intent);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        serviceDelegate.onTrimMemory(getApplication(), level);
    }

    @Override
    public void onDestroy() {
        serviceDelegate.onDestroy(getApplication());
        connected = false;
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        serviceDelegate.onLowMemory(getApplication());
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
