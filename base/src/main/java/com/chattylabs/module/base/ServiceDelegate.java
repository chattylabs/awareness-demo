package com.chattylabs.module.base;

import android.app.Application;
import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.Intent;

public interface ServiceDelegate {
    <T extends Class<? extends Service>> void onCreate(Application application, T clazz);
    void onHandleIntent(Application application, Intent intent);
    void onTrimMemory(Application application, int level);
    void onLowMemory(Application application);
    void onDestroy(Application application);

    interface Trace {
        // Static values
        String TRUE = Boolean.TRUE.toString().toUpperCase();
        // Dynamic values
        String SERVICE_CREATED = "SERVICE_CREATED";
        String SERVICE_DESTROYED = "SERVICE_DESTROYED";
        String ON_LOW_MEMORY = "ON_LOW_MEMORY";
        String ON_TRIM_MEMORY = "ON_TRIM_MEMORY";
        String ERROR = "ERROR";
    }

    class Utils {
        public static String getTrimMemoryAsString(int level) {
            switch (level) {
                case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                    return "TRIM_MEMORY_BACKGROUND";
                case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                    return "TRIM_MEMORY_COMPLETE";
                case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                    return "TRIM_MEMORY_MODERATE";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                    return "TRIM_MEMORY_RUNNING_CRITICAL";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                    return "TRIM_MEMORY_RUNNING_LOW";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                    return "TRIM_MEMORY_RUNNING_MODERATE";
                case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                    return "TRIM_MEMORY_UI_HIDDEN";
                default:
                    return "TRIM_MEMORY_UNKNOWN";
            }
        }
    }
}
