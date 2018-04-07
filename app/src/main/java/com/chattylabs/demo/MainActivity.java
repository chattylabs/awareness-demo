package com.chattylabs.demo;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.chattylabs.demo.awareness.R;
import com.chattylabs.sdk.android.awareness.AwarenessComponent;
import com.chattylabs.sdk.android.awareness.AwarenessModule;
import com.chattylabs.sdk.android.core.PermissionsHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean on_foot = preferences.getBoolean("on_foot", false);
        boolean still = preferences.getBoolean("still", false);
        boolean on_vehicle = preferences.getBoolean("on_vehicle", false);
        boolean headset = preferences.getBoolean("headset", false);

        TextView status = findViewById(R.id.status);

        AwarenessComponent component = AwarenessModule.provideAwarenessComponent();

        Button on_foot_btn = findViewById(R.id.on_foot);
        on_foot_btn.setText(on_foot ? "Stop foot detection" : "Start foot detection");
        on_foot_btn.setBackground(getBackground(on_foot));
        on_foot_btn.setTag(on_foot);
        on_foot_btn.setOnClickListener(v -> {
            boolean b = (boolean) on_foot_btn.getTag();
            preferences.edit().putBoolean("on_foot", !b).apply();
            on_foot_btn.setTag(!b);
            on_foot_btn.setText(!b ? "Stop foot detection" : "Start foot detection");
            on_foot_btn.setBackground(getBackground(!b));
            if (!b) {
                component.register(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_ON_FOOT,
                                AwarenessComponent.Fences.STOPPING_ON_FOOT
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\non_foot successfully registered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with on_foot: " + e.getMessage()));
            }
            else {
                component.unregister(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_ON_FOOT,
                                AwarenessComponent.Fences.STOPPING_ON_FOOT
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\non_foot successfully unregistered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with on_foot: " + e.getMessage()));
            }
        });

        Button still_btn = findViewById(R.id.still);
        still_btn.setText(still ? "Stop still detection" : "Start still detection");
        still_btn.setBackground(getBackground(still));
        still_btn.setTag(still);
        still_btn.setOnClickListener(v -> {
            boolean b = (boolean) still_btn.getTag();
            preferences.edit().putBoolean("still", !b).apply();
            still_btn.setTag(!b);
            still_btn.setText(!b ? "Stop still detection" : "Start still detection");
            still_btn.setBackground(getBackground(!b));
            if (!b) {
                component.register(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_STILL,
                                AwarenessComponent.Fences.STOPPING_STILL
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\nstill successfully registered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with still: " + e.getMessage()));
            }
            else {
                component.unregister(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_STILL,
                                AwarenessComponent.Fences.STOPPING_STILL
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\nstill successfully unregistered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with still: " + e.getMessage()));
            }
        });

        Button on_vehicle_btn = findViewById(R.id.on_vehicle);
        on_vehicle_btn.setText(on_vehicle ? "Stop vehicle detection" : "Start vehicle detection");
        on_vehicle_btn.setBackground(getBackground(on_vehicle));
        on_vehicle_btn.setTag(on_vehicle);
        on_vehicle_btn.setOnClickListener(v -> {
            boolean b = (boolean) on_vehicle_btn.getTag();
            preferences.edit().putBoolean("on_vehicle", !b).apply();
            on_vehicle_btn.setTag(!b);
            on_vehicle_btn.setText(!b ? "Stop vehicle detection" : "Start vehicle detection");
            on_vehicle_btn.setBackground(getBackground(!b));
            if (!b) {
                component.register(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_IN_VEHICLE,
                                AwarenessComponent.Fences.STOPPING_IN_VEHICLE
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\non_vehicle successfully registered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with on_vehicle: " + e.getMessage()));
            }
            else {
                component.unregister(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.STARTING_IN_VEHICLE,
                                AwarenessComponent.Fences.STOPPING_IN_VEHICLE
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\non_vehicle successfully unregistered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with on_vehicle: " + e.getMessage()));
            }
        });

        Button headset_btn = findViewById(R.id.headset);
        headset_btn.setText(headset ? "Stop headset detection" : "Start headset detection");
        headset_btn.setBackground(getBackground(headset));
        headset_btn.setTag(headset);
        headset_btn.setOnClickListener(v -> {
            boolean b = (boolean) headset_btn.getTag();
            preferences.edit().putBoolean("headset", !b).apply();
            headset_btn.setTag(!b);
            headset_btn.setText(!b ? "Stop headset detection" : "Start headset detection");
            headset_btn.setBackground(getBackground(!b));
            if (!b) {
                component.register(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.PLUGGING_HEADPHONE,
                                AwarenessComponent.Fences.UNPLUGGING_HEADPHONE
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\nheadset successfully registered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with headset: " + e.getMessage()));
            }
            else {
                component.unregister(
                        this,
                        new int[]{
                                AwarenessComponent.Fences.PLUGGING_HEADPHONE,
                                AwarenessComponent.Fences.UNPLUGGING_HEADPHONE
                        }, (AwarenessComponent.OnSuccess) () ->
                                status.setText(status.getText() + "\nheadset successfully unregistered"),
                        (AwarenessComponent.OnError) e ->
                                status.setText(status.getText() + "\nError with headset: " + e.getMessage()));
            }
        });

        PermissionsHelper.check(this, component.requiredPermissions());
    }

    private Drawable getBackground(boolean selected) {
        if (selected) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            TypedArray ta = obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            return drawableFromTheme;
        } else {
            return getTheme().getDrawable(android.R.drawable.btn_default);
        }
    }
}
