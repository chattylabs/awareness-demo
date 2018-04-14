package com.chattylabs.sdk.android.awareness;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

final class AwarenessComponentImpl implements AwarenessComponent {

    @Inject
    AwarenessComponentImpl() {
        Instance.referenceOf = new WeakReference<>(this);
    }

    @Override
    public String[] requiredPermissions() {
        return new String[0];
    }

    @Override
    public void register(@NonNull Context context, Fences fences,
                         @NonNull AwarenessListener... listeners) {
        PendingIntent pendingIntent = getIntentServicePendingIntent(context);
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        builder.addFence(fences.getKey(), fences.getFence(), pendingIntent);
        Awareness.getFenceClient(context).updateFences(builder.build())
                 .addOnSuccessListener(aVoid -> {
                     for (AwarenessListener l: listeners)
                         if (OnSuccess.class.isInstance(l)) {
                             ((OnSuccess) l).execute();
                             break;
                         }
                     Log.i(TAG, "Fence was successfully registered");
                 })
                 .addOnFailureListener(e -> {
                     for (AwarenessListener l: listeners)
                         if (OnError.class.isInstance(l)) {
                             ((OnError) l).execute(e);
                             break;
                         }
                     Log.i(TAG, "Fence could not be registered: " + e.getMessage());
                 });
    }

    @Override
    public void register(@NonNull Context context, @Fences.Items int[] fences,
                         @NonNull AwarenessListener... listeners) {
        PendingIntent pendingIntent = getIntentServicePendingIntent(context);
        Awareness.getFenceClient(context).updateFences(buildFencesToRegister(context, pendingIntent, fences))
                .addOnSuccessListener(aVoid -> {
                    for (AwarenessListener l: listeners)
                        if (OnSuccess.class.isInstance(l)) {
                            ((OnSuccess) l).execute();
                            break;
                        }
                    Log.i(TAG, "Fence was successfully registered");
                })
                .addOnFailureListener(e -> {
                    for (AwarenessListener l: listeners)
                        if (OnError.class.isInstance(l)) {
                            ((OnError) l).execute(e);
                            break;
                        }
                    Log.i(TAG, "Fence could not be registered: " + e.getMessage());
                });
    }

    @Override
    public void unregister(@NonNull Context context, String key,
                           @NonNull AwarenessListener... listeners) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        builder.removeFence(key);
        Awareness.getFenceClient(context).updateFences(builder.build())
                 .addOnSuccessListener(aVoid -> {
                     for (AwarenessListener l: listeners)
                         if (OnSuccess.class.isInstance(l)) {
                             ((OnSuccess) l).execute();
                             break;
                         }
                     Log.i(TAG, "Fence was successfully removed.");
                 })
                 .addOnFailureListener(e -> {
                     for (AwarenessListener l: listeners)
                         if (OnError.class.isInstance(l)) {
                             ((OnError) l).execute(e);
                             break;
                         }
                     Log.e(TAG, "Fence could not be removed: " + e.getMessage());
                 });
    }

    @Override
    public void unregister(@NonNull Context context, @Fences.Items int[] fences,
                           @NonNull AwarenessListener... listeners) {
        Awareness.getFenceClient(context).updateFences(buildFencesToUnregister(context, fences))
                 .addOnSuccessListener(aVoid -> {
                     for (AwarenessListener l: listeners)
                         if (OnSuccess.class.isInstance(l)) {
                             ((OnSuccess) l).execute();
                             break;
                         }
                     Log.i(TAG, "Fence was successfully removed.");
                 })
                 .addOnFailureListener(e -> {
                     for (AwarenessListener l: listeners)
                         if (OnError.class.isInstance(l)) {
                             ((OnError) l).execute(e);
                             break;
                         }
                     Log.e(TAG, "Fence could not be removed: " + e.getMessage());
                 });
    }

    private FenceUpdateRequest buildFencesToRegister(Context context, PendingIntent pendingIntent, @Fences.Items int[] fences) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        AwarenessFence awarenessFence;
        for (int fence : fences) {
            switch (fence) {
                case Fences.STARTING_IN_VEHICLE:
                    awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE);
                    builder.addFence(context.getPackageName() + Fences.Key.START_IN_VEHICLE_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STOPPING_IN_VEHICLE:
                    awarenessFence = DetectedActivityFence.stopping(DetectedActivityFence.IN_VEHICLE);
                    builder.addFence(context.getPackageName() + Fences.Key.STOP_IN_VEHICLE_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STARTING_ON_FOOT:
                    awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT);
                    builder.addFence(context.getPackageName() + Fences.Key.START_ON_FOOT_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    awarenessFence = DetectedActivityFence.stopping(DetectedActivityFence.ON_FOOT);
                    builder.addFence(context.getPackageName() + Fences.Key.STOP_ON_FOOT_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.DURING_FOOT:
                    awarenessFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
                    builder.addFence(context.getPackageName() + Fences.Key.DURING_FOOT_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.PLUGGING_HEADPHONE:
                    builder.addFence(context.getPackageName() + Fences.Key.PLUG_HEADPHONE_KEY, HeadphoneFence.pluggingIn(), pendingIntent);
                    break;
                case Fences.UNPLUGGING_HEADPHONE:
                    builder.addFence(context.getPackageName() + Fences.Key.UNPLUG_HEADPHONE_KEY, HeadphoneFence.unplugging(), pendingIntent);
                    break;
                case Fences.STARTING_STILL:
                    awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.STILL);
                    builder.addFence(context.getPackageName() + Fences.Key.START_STILL_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.STOPPING_STILL:
                    awarenessFence = DetectedActivityFence.stopping(DetectedActivityFence.STILL);
                    builder.addFence(context.getPackageName() + Fences.Key.STOP_STILL_KEY, awarenessFence, pendingIntent);
                    break;
                case Fences.DURING_STILL:
                    awarenessFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
                    builder.addFence(context.getPackageName() + Fences.Key.DURING_STILL_KEY, awarenessFence, pendingIntent);
                    break;
            }
        }
        return builder.build();
    }

    private FenceUpdateRequest buildFencesToUnregister(Context context, @Fences.Items int[] fences) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (int fence : fences) {
            switch (fence) {
                case Fences.STARTING_IN_VEHICLE:
                    builder.removeFence(context.getPackageName() + Fences.Key.START_IN_VEHICLE_KEY);
                    break;
                case Fences.STOPPING_IN_VEHICLE:
                    builder.removeFence(context.getPackageName() + Fences.Key.STOP_IN_VEHICLE_KEY);
                    break;
                case Fences.STARTING_ON_FOOT:
                    builder.removeFence(context.getPackageName() + Fences.Key.START_ON_FOOT_KEY);
                    break;
                case Fences.STOPPING_ON_FOOT:
                    builder.removeFence(context.getPackageName() + Fences.Key.STOP_ON_FOOT_KEY);
                    break;
                case Fences.DURING_FOOT:
                    builder.removeFence(context.getPackageName() + Fences.Key.DURING_FOOT_KEY);
                    break;
                case Fences.PLUGGING_HEADPHONE:
                    builder.removeFence(context.getPackageName() + Fences.Key.PLUG_HEADPHONE_KEY);
                    break;
                case Fences.UNPLUGGING_HEADPHONE:
                    builder.removeFence(context.getPackageName() + Fences.Key.UNPLUG_HEADPHONE_KEY);
                    break;
                case Fences.STARTING_STILL:
                    builder.removeFence(context.getPackageName() + Fences.Key.START_STILL_KEY);
                    break;
                case Fences.STOPPING_STILL:
                    builder.removeFence(context.getPackageName() + Fences.Key.STOP_STILL_KEY);
                    break;
                case Fences.DURING_STILL:
                    builder.removeFence(context.getPackageName() + Fences.Key.DURING_STILL_KEY);
                    break;
            }
        }
        return builder.build();
    }

    /**
     * Gets a PendingIntent Service to be sent for each fence.
     * <p>
     * An {@link IntentService} must be set with a {@link AwarenessComponent.Fences#ACTION_FENCE} {@code <intent-filter>}.
     */
    private PendingIntent getIntentServicePendingIntent(Context context) {
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 4234,
                                        new Intent(ACTION_FENCE).setPackage(context.getPackageName()), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
