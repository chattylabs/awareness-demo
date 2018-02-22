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

import com.chattylabs.module.base.Preconditions;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.common.api.GoogleApiClient;

public class AwareComponentImpl implements AwareComponent {

    private final GoogleApiClient googleApiClient;
    private Class<? extends Service> foregroundServiceClass;
    private Callbacks callbacks;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static AwareComponentImpl getLocalInstance() {
        return (AwareComponentImpl) Singleton.INSTANCE;
    }

    public AwareComponentImpl(Application application, Class<? extends Service> foregroundServiceClass) {
        this.foregroundServiceClass = foregroundServiceClass;
        Singleton.INSTANCE = this;
        googleApiClient = new GoogleApiClient.Builder(application)
                .addApi(Awareness.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public String[] requiredPermissions() {
        return new String[0];
    }

    @Override
    public void attach(@NonNull Callbacks callbacks) {

    }

    @Override
    public void detach() {

    }

    @Override
    public void start(@NonNull Context context, @NonNull Callbacks callbacks) {

        Preconditions.checkNotNull(callbacks);

        this.callbacks = callbacks;

        AwarenessFence startInVehicleFence = DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT);
        AwarenessFence stopInVehicleFence = DetectedActivityFence.stopping(DetectedActivityFence.ON_FOOT);
        Awareness.FenceApi.updateFences(googleApiClient, new FenceUpdateRequest.Builder()
                .addFence(START_IN_VEHICLE_FENCE, startInVehicleFence, getActivityDetectionPendingIntent(context))
                .addFence(STOP_IN_VEHICLE_FENCE, stopInVehicleFence, getActivityDetectionPendingIntent(context))
                .build())
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence was successfully registered.");
                    } else {
                        Log.e(TAG, "Fence could not be registered: " + status);
                    }
                });
    }

    @Override
    public void stop(@NonNull Context context) {
        Awareness.FenceApi.updateFences(googleApiClient, new FenceUpdateRequest.Builder()
                .removeFence(START_IN_VEHICLE_FENCE)
                .removeFence(STOP_IN_VEHICLE_FENCE)
                .build())
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence was successfully removed.");
                    } else {
                        Log.e(TAG, "Fence could not be removed: " + status);
                    }
                });
    }

    @Override
    public void process(Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        String fenceKey = fenceState.getFenceKey();
        if (!TextUtils.isEmpty(fenceKey) && fenceState.getCurrentState() == FenceState.TRUE) {
            switch (fenceState.getFenceKey()) {
                case START_IN_VEHICLE_FENCE:
                    if (this.callbacks != null)
                        this.callbacks.getStartedDrivingListener().execute();
                    break;
                case STOP_IN_VEHICLE_FENCE:
                    if (this.callbacks != null)
                        this.callbacks.getStoppedDrivingListener().execute();
                    break;
            }
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     *
     * A Foreground Service must be provided.
     */
    private PendingIntent getActivityDetectionPendingIntent(Context context) {
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, new Intent(context, foregroundServiceClass), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
