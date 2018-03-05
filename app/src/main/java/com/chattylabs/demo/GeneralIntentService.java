package com.chattylabs.demo;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chattylabs.module.base.ServiceDelegate;

public class GeneralIntentService extends IntentService {
    public static final String TAG = "GeneralIntentService";

    private static boolean connected;

    // Component Delegate
    ServiceDelegate serviceDelegate;

    public static boolean isConnected() {
        return connected;
    }

    public GeneralIntentService() {
        super(TAG);
        this.serviceDelegate = new AwarenessServiceDelegate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connected = true;
        serviceDelegate.onCreate(getApplication(), getClass());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            serviceDelegate.onHandleIntent(getApplication(), intent);
        }
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
