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
        for (int fence : fences) {
            switch (fence) {
                case Fences.STARTING_ON_FOOT:
                    AwarenessFence startInVehicleFence = DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT);
                    builder = builder.addFence(START_IN_VEHICLE_FENCE, startInVehicleFence, pendingIntent);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    AwarenessFence stopInVehicleFence = DetectedActivityFence.stopping(DetectedActivityFence.ON_FOOT);
                    builder = builder.addFence(STOP_IN_VEHICLE_FENCE, stopInVehicleFence, pendingIntent);
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
                case Fences.STARTING_ON_FOOT:
                    builder.removeFence(START_IN_VEHICLE_FENCE);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    builder.removeFence(STOP_IN_VEHICLE_FENCE);
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
                case START_IN_VEHICLE_FENCE:
                    callbacks.getStartDrivingListener().execute(fenceState);
                    break;
                case STOP_IN_VEHICLE_FENCE:
                    callbacks.getStopDrivingListener().execute(fenceState);
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
