package com.chattylabs.sdk.android.awareness;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chattylabs.sdk.android.core.RequiredPermissions;
import com.chattylabs.sdk.android.core.Tag;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public interface AwarenessComponent extends RequiredPermissions {
    String TAG = Tag.make(AwarenessComponent.class);

    String SDK_PACKAGE = "com.chattylabs.sdk.android.awareness";

    /**
     * Intent filter actions
     */
    String ACTION_FENCE = SDK_PACKAGE + ".action.FENCE";

    /**
     * Intent extras
     */

    /**
     * TODO: documentation...
     */
    class Instance {
        transient static WeakReference<AwarenessComponent> referenceOf;
        static AwarenessComponent getInstanceOf() {
            synchronized (Instance.class) {
                if ((referenceOf == null) || (referenceOf.get() == null))
                {
                    Log.w(TAG, "New reference of WeakReference<AwarenessComponent>");
                    return new AwarenessComponentImpl();
                }
                return referenceOf.get();
            }
        }
        private Instance(){}
    }

    /**
     * Initializes and registers a client to the service.
     */
    void register(@NonNull Context context, Fences fences, @NonNull AwarenessListener... listeners);

    /**
     * Initializes and registers a client to the service.
     */
    void register(@NonNull Context context, @Fences.Items int[] fences, @NonNull AwarenessListener... listeners);

    /**
     * Initializes and registers a client to the service.
     */
    void unregister(@NonNull Context context, String key, @NonNull AwarenessListener... listeners);

    /**
     * Stops and unregisters the client from the service.
     */
    void unregister(@NonNull Context context, @Fences.Items int[] fences, @NonNull AwarenessListener... listeners);

    /**
     * Creates a compatible {@link Fences} object from a {@link AwarenessFence} fence.
     * <br/>
     * Useful when creating a combined fence using Nested trees of {@code AND}, {@code OR} and {@code NOT}.
     */
    static Fences from(@NonNull AwarenessFence fence, @NonNull String key) {
        return new Fences() {
            @Override
            public AwarenessFence getFence() {
                return fence;
            }

            @Override
            public String getKey() {
                return key;
            }
        };
    }

    /**
     * Extracts the {@link Fences} from the incoming intent.
     */
    static Fences.State extract(@NonNull Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        String fenceKey = fenceState.getFenceKey();
        return new Fences.State() {

            @Override
            public boolean isFence(@Fences.Items int fence) {
                return fence == getFence();
            }

            @Override
            public boolean isState(@Fences.State.Value int state) {
                return state == getCurrentState();
            }

            @Override
            public String getFenceKey() {
                return fenceKey;
            }

            @Fences.Items
            @Override
            public int getFence() {
                String packageName = intent.getPackage() != null ? intent.getPackage() : "";
                switch (fenceKey.replace(packageName, "")) {
                    case Fences.Key.START_IN_VEHICLE_KEY:
                        return Fences.STARTING_IN_VEHICLE;
                    case Fences.Key.STOP_IN_VEHICLE_KEY:
                        return Fences.STOPPING_IN_VEHICLE;
                    case Fences.Key.START_ON_FOOT_KEY:
                        return Fences.STARTING_ON_FOOT;
                    case Fences.Key.STOP_ON_FOOT_KEY:
                        return Fences.STOPPING_ON_FOOT;
                    case Fences.Key.PLUG_HEADPHONE_KEY:
                        return Fences.PLUGGING_HEADPHONE;
                    case Fences.Key.UNPLUG_HEADPHONE_KEY:
                        return Fences.UNPLUGGING_HEADPHONE;
                    case Fences.Key.START_STILL_KEY:
                        return Fences.STARTING_STILL;
                    case Fences.Key.STOP_STILL_KEY:
                        return Fences.STOPPING_STILL;
                    case Fences.Key.DURING_STILL_KEY:
                        return Fences.DURING_STILL;
                    case Fences.Key.DURING_FOOT_KEY:
                        return Fences.DURING_FOOT;
                    default: return Fences.UNKNOWN;
                }
            }

            @Override @Fences.State.Value
            public int getCurrentState() {
                return fenceState.getCurrentState();
            }

            @Override @Fences.State.Value
            public int getPreviousState() {
                return fenceState.getPreviousState();
            }

            @Override
            public long getLastFenceUpdateTimeMillis() {
                return fenceState.getLastFenceUpdateTimeMillis();
            }
        };
    }

    /**
     * Fences types wrapped over the {@link Fences}
     */
    interface Fences {

        AwarenessFence getFence();

        String getKey();

        /**
         * Custom fences
         */
        int UNKNOWN = -1;
        int STARTING_ON_FOOT = 1;
        int STOPPING_ON_FOOT = 2;
        int DURING_FOOT = 10;
        int STARTING_IN_VEHICLE = 3;
        int STOPPING_IN_VEHICLE = 4;
        int PLUGGING_HEADPHONE = 5;
        int UNPLUGGING_HEADPHONE = 6;
        int STARTING_STILL = 7;
        int STOPPING_STILL = 8;
        int DURING_STILL = 9;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({UNKNOWN, STARTING_ON_FOOT, STOPPING_ON_FOOT, STARTING_IN_VEHICLE,
                        STOPPING_IN_VEHICLE, PLUGGING_HEADPHONE, UNPLUGGING_HEADPHONE,
                        STARTING_STILL, STOPPING_STILL, DURING_STILL, DURING_FOOT})
        @interface Items {}

        /**
         * Fences keys
         */
        class Key {
            final static String START_IN_VEHICLE_KEY = ".awareness.key.startInVehicleFence";
            final static String STOP_IN_VEHICLE_KEY = ".awareness.key.stopInVehicleFence";
            final static String START_ON_FOOT_KEY = ".awareness.key.startOnFootFence";
            final static String STOP_ON_FOOT_KEY = ".awareness.key.stopOnFootFence";
            final static String DURING_FOOT_KEY = ".awareness.key.duringFootFence";
            final static String PLUG_HEADPHONE_KEY = ".awareness.key.plugHeadphoneFence";
            final static String UNPLUG_HEADPHONE_KEY = ".awareness.key.unplugHeadphoneFence";
            final static String START_STILL_KEY = ".awareness.key.startStillFence";
            final static String STOP_STILL_KEY = ".awareness.key.stopStillFence";
            final static String DURING_STILL_KEY = ".awareness.key.duringStillFence";
        }

        interface State {
            int UNKNOWN = 0;
            int FALSE = 1;
            int TRUE = 2;

            @Retention(RetentionPolicy.SOURCE)
            @IntDef({UNKNOWN, FALSE, TRUE})
            @interface Value {}

            boolean isFence(@Fences.Items int fence);

            boolean isState(@Fences.State.Value int state);

            String getFenceKey();

            @Fences.Items
            int getFence();

            @Fences.State.Value
            int getCurrentState();

            @Fences.State.Value
            int getPreviousState();

            long getLastFenceUpdateTimeMillis();
        }
    }

    /**
     * Utilities related to this component
     */

    static String getFenceStateAsString(int state) {
        switch (state) {
            case FenceState.TRUE:
                return "TRUE";
            case FenceState.FALSE:
                return "FALSE";
            case FenceState.UNKNOWN:
                return "UNKNOWN";
        }
        return "UNKNOWN_STATE";
    }

    static String getFenceAsString(int state) {
        switch (state) {
            case Fences.STARTING_ON_FOOT:
                return "STARTING_ON_FOOT";
            case Fences.STOPPING_ON_FOOT:
                return "STOPPING_ON_FOOT";
            case Fences.DURING_FOOT:
                return "DURING_FOOT";
            case Fences.STARTING_IN_VEHICLE:
                return "STARTING_IN_VEHICLE";
            case Fences.STOPPING_IN_VEHICLE:
                return "STOPPING_IN_VEHICLE";
            case Fences.PLUGGING_HEADPHONE:
                return "PLUGGING_HEADPHONE";
            case Fences.UNPLUGGING_HEADPHONE:
                return "UNPLUGGING_HEADPHONE";
            case Fences.STARTING_STILL:
                return "STARTING_STILL";
            case Fences.STOPPING_STILL:
                return "STOPPING_STILL";
            case Fences.DURING_STILL:
                return "DURING_STILL";
        }
        return "UNKNOWN";
    }

    /**
     * Handles SharedPreferences keys related to this component
     */
    interface Preferences {
    }

    /**
     * Listeners
     */

    interface AwarenessListener {}

    interface OnSuccess extends AwarenessListener {
        void execute();
    }
    interface OnError extends AwarenessListener {
        void execute(Exception e);
    }
}
