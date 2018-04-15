package com.chattylabs.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chattylabs.sdk.android.awareness.AwarenessComponent;
import com.chattylabs.sdk.android.awareness.AwarenessModule;

public class OnUpgradeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("OnUpgradeReceiver", "Device upgraded!");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean on_foot = preferences.getBoolean("on_foot", false);
        boolean still = preferences.getBoolean("still", false);
        boolean on_vehicle = preferences.getBoolean("on_vehicle", false);
        boolean headset = preferences.getBoolean("headset", false);

        AwarenessComponent component = AwarenessModule.provideAwarenessComponent();

        if (on_foot) {
            component.register(
                    context,
                    new int[]{
                            AwarenessComponent.Fences.STARTING_ON_FOOT,
                            AwarenessComponent.Fences.STOPPING_ON_FOOT
                    });
        }
        if (still) {
            component.register(
                    context,
                    new int[]{
                            AwarenessComponent.Fences.STARTING_STILL,
                            AwarenessComponent.Fences.STOPPING_STILL
                    });
        }
        if (on_vehicle) {
            component.register(
                    context,
                    new int[]{
                            AwarenessComponent.Fences.STARTING_IN_VEHICLE,
                            AwarenessComponent.Fences.STOPPING_IN_VEHICLE
                    });
        }
        if (headset) {
            component.register(
                    context,
                    new int[]{
                            AwarenessComponent.Fences.PLUGGING_HEADPHONE,
                            AwarenessComponent.Fences.UNPLUGGING_HEADPHONE
                    });
        }
    }
}
