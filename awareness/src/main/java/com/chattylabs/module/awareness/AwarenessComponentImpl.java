package com.chattylabs.module.awareness;

import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.common.api.GoogleApiClient;

public class AwarenessComponentImpl implements AwarenessComponent {

    private final GoogleApiClient googleApiClient;
    private Class<? extends Service> serviceClass;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static AwarenessComponentImpl getLocalInstance() {
        return (AwarenessComponentImpl) Singleton.INSTANCE;
    }

    public AwarenessComponentImpl(Application application, Class<? extends Service> serviceClass) {
        this.serviceClass = serviceClass;
        Singleton.INSTANCE = this;
        googleApiClient = new GoogleApiClient.Builder(application).addApi(Awareness.API).build();
        googleApiClient.connect();
    }

    @Override
    public String[] requiredPermissions() {
        return new String[0];
    }

    @Override
    public void register(@NonNull Context context, @Fences.Items int[] fences) {
        Awareness.FenceApi.updateFences(googleApiClient, buildFencesToRegister(getActivityDetectionPendingIntent(context), fences))
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence was successfully registered.");
                    } else {
                        Log.e(TAG, "Fence could not be registered: " + status);
                    }
                });
    }

    private FenceUpdateRequest buildFencesToRegister(PendingIntent pendingIntent, @Fences.Items int[] fences) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        AwarenessFence awarenessFence;
        for (int fence : fences) {
            switch (fence) {
                case Fences.STARTING_IN_VEHICLE:
                    awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE);
                    builder.addFence(Fences.START_IN_VEHICLE_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STOPPING_IN_VEHICLE:
                    awarenessFence = DetectedActivityFence.stopping(DetectedActivityFence.IN_VEHICLE);
                    builder.addFence(Fences.STOP_IN_VEHICLE_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STARTING_ON_FOOT:
                    awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT);
                    builder.addFence(Fences.START_ON_FOOT_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    awarenessFence = DetectedActivityFence.stopping(DetectedActivityFence.ON_FOOT);
                    builder.addFence(Fences.STOP_ON_FOOT_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.PLUGGING_HEADPHONE:
                    builder.addFence(Fences.PLUG_HEADPHONE_KEY, HeadphoneFence.pluggingIn(), pendingIntent);
                    break;
                case Fences.UNPLUGGING_HEADPHONE:
                    builder.addFence(Fences.UNPLUG_HEADPHONE_KEY, HeadphoneFence.unplugging(), pendingIntent);
                    break;
            }
        }
        return builder.build();
    }

    @Override
    public void unregister(@NonNull Context context, @Fences.Items int[] fences) {
        Awareness.FenceApi.updateFences(googleApiClient, buildFencesToUnregister(fences))
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence was successfully removed.");
                    } else {
                        Log.e(TAG, "Fence could not be removed: " + status);
                    }
                });
    }

    private FenceUpdateRequest buildFencesToUnregister(@Fences.Items int[] fences) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (int fence : fences) {
            switch (fence) {
                case Fences.STARTING_IN_VEHICLE:
                    builder.removeFence(Fences.START_IN_VEHICLE_KEY);
                    break;
                case Fences.STOPPING_IN_VEHICLE:
                    builder.removeFence(Fences.STOP_IN_VEHICLE_KEY);
                    break;
                case Fences.STARTING_ON_FOOT:
                    builder.removeFence(Fences.START_ON_FOOT_KEY);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    builder.removeFence(Fences.STOP_ON_FOOT_KEY);
                    break;
                case Fences.PLUGGING_HEADPHONE:
                    builder.removeFence(Fences.PLUG_HEADPHONE_KEY);
                    break;
                case Fences.UNPLUGGING_HEADPHONE:
                    builder.removeFence(Fences.UNPLUG_HEADPHONE_KEY);
                    break;
            }
        }
        return builder.build();
    }

    @Override
    public void process(@NonNull Intent intent, @NonNull Callbacks callbacks) {
        FenceState fenceState = FenceState.extract(intent);
        String fenceKey = fenceState.getFenceKey();
        if (!TextUtils.isEmpty(fenceKey)) {
            switch (fenceKey) {
                case Fences.START_IN_VEHICLE_KEY:
                    callbacks.getStartInVehicleListener().execute(fenceState);
                    break;
                case Fences.STOP_IN_VEHICLE_KEY:
                    callbacks.getStopInVehicleListener().execute(fenceState);
                    break;
                case Fences.START_ON_FOOT_KEY:
                    callbacks.getStartOnFootListener().execute(fenceState);
                    break;
                case Fences.STOP_ON_FOOT_KEY:
                    callbacks.getStopOnFootListener().execute(fenceState);
                    break;
                case Fences.PLUG_HEADPHONE_KEY:
                    callbacks.getPluggingHeadphoneListener().execute(fenceState);
                    break;
                case Fences.UNPLUG_HEADPHONE_KEY:
                    callbacks.getUnpluggingHeadphoneListener().execute(fenceState);
                    break;
            }
        }
    }

    /**
     * Gets a PendingIntent to be sent for each fence.
     *
     * An IntentService must be provided.
     */
    private PendingIntent getActivityDetectionPendingIntent(Context context) {
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, new Intent(context, serviceClass), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
