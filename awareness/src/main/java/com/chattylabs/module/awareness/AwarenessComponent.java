package com.chattylabs.module.awareness;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chattylabs.module.base.RequiredPermissions;
import com.google.android.gms.awareness.fence.FenceState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface AwarenessComponent extends RequiredPermissions {
    String TAG = "AwarenessComponent";

    // Log constants
    String LOG_AWARENESS_FILENAME = "AWARENESS_COMPONENT";
    String LOG_STARTED_DRIVING = "LOG_STARTED_DRIVING";
    String LOG_STOPPED_DRIVING = "LOG_STOPPED_DRIVING";
    String LOG_COMPONENT_STARTED = "LOG_COMPONENT_STARTED";

    /**
     * To use only the last instance.
     */
    class Singleton {
        static AwarenessComponent INSTANCE;

        @Nullable
        public static AwarenessComponent getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Initializes and registers a client to the service.
     *
     * Deliberately created that way to have into account unit testing purposes.
     */
    void register(@NonNull Context context, @Fences.Items int[] fences);

    /**
     * Stops and unregisters the client from the service.
     */
    void unregister(@NonNull Context context, @Fences.Items int[] fences);

    /**
     * Processes the incoming intent.
     */
    void process(@NonNull Intent intent, @NonNull Callbacks callbacks);

    /**
     * Fences types
     */
    interface Fences {

        /**
         * Main intent filter action
         */
        String ACTION = BuildConfig.APPLICATION_ID + ".FENCE_ACTION";

        /**
         * Custom actions
         */
        int STARTING_ON_FOOT = 1;
        int STOPPING_ON_FOOT = 2;
        int STARTING_IN_VEHICLE = 3;
        int STOPPING_IN_VEHICLE = 4;
        int PLUGGING_HEADPHONE = 5;
        int UNPLUGGING_HEADPHONE = 6;

        /**
         * Custom actions
         */
        String START_IN_VEHICLE_KEY = "startInVehicleFenceKey";
        String STOP_IN_VEHICLE_KEY = "stopInVehicleFenceKey";
        String START_ON_FOOT_KEY = "startOnFootFenceKey";
        String STOP_ON_FOOT_KEY = "stopOnFootFenceKey";
        String PLUG_HEADPHONE_KEY = "plugHeadphoneFenceKey";
        String UNPLUG_HEADPHONE_KEY = "unplugHeadphoneFenceKey";

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({STARTING_ON_FOOT, STOPPING_ON_FOOT, STARTING_IN_VEHICLE, STOPPING_IN_VEHICLE, PLUGGING_HEADPHONE, UNPLUGGING_HEADPHONE})
        @interface Items {}
    }

    /**
     * This class will contain the different callbacks needed to interact with a client.
     */
    class Callbacks {
        private OnErrorListener errorListener;
        private OnConnectedListener connectedListener;
        private OnDisconnectedListener disconnectedListener;
        private OnStartInVehicleListener startInVehicleListener;
        private OnStopInVehicleListener stopInVehicleListener;
        private OnStartOnFootListener startOnFootListener;
        private OnStopOnFootListener stopOnFootListener;
        private OnPluggingHeadphoneListener pluggingHeadphoneListener;
        private OnUnpluggingHeadphoneListener unpluggingHeadphoneListener;

        private Callbacks(Builder builder) {
            this.errorListener = builder.errorListener;
            this.connectedListener = builder.connectedListener;
            this.disconnectedListener = builder.disconnectedListener;
            this.startInVehicleListener = builder.startInVehicleListener;
            this.stopInVehicleListener = builder.stopInVehicleListener;
            this.startOnFootListener = builder.startOnFootListener;
            this.stopOnFootListener = builder.stopOnFootListener;
            this.pluggingHeadphoneListener = builder.pluggingHeadphoneListener;
            this.unpluggingHeadphoneListener = builder.unpluggingHeadphoneListener;
        }

        public OnErrorListener getErrorListener() {
            return errorListener != null ? errorListener : ex -> {};
        }

        public OnConnectedListener getConnectedListener() {
            return connectedListener != null ? connectedListener : () -> {};
        }

        public OnDisconnectedListener getDisconnectedListener() {
            return disconnectedListener != null ? disconnectedListener : () -> {};
        }

        public OnStartInVehicleListener getStartInVehicleListener() {
            return startInVehicleListener != null ? startInVehicleListener : ignored -> {};
        }

        public OnStopInVehicleListener getStopInVehicleListener() {
            return stopInVehicleListener != null ? stopInVehicleListener : ignored -> {};
        }

        public OnStartOnFootListener getStartOnFootListener() {
            return startOnFootListener != null ? startOnFootListener : ignored -> {};
        }

        public OnStopOnFootListener getStopOnFootListener() {
            return stopOnFootListener != null ? stopOnFootListener : ignored -> {};
        }

        public OnPluggingHeadphoneListener getPluggingHeadphoneListener() {
            return pluggingHeadphoneListener != null ? pluggingHeadphoneListener : ignored -> {};
        }

        public OnUnpluggingHeadphoneListener getUnpluggingHeadphoneListener() {
            return unpluggingHeadphoneListener != null ? unpluggingHeadphoneListener : ignored -> {};
        }

        /**
         * Proper Builder design pattern to handle multiple parameters in one place.
         */
        public static class Builder {
            private OnErrorListener errorListener;
            private OnConnectedListener connectedListener;
            private OnDisconnectedListener disconnectedListener;
            private OnStartInVehicleListener startInVehicleListener;
            private OnStopInVehicleListener stopInVehicleListener;
            private OnStartOnFootListener startOnFootListener;
            private OnStopOnFootListener stopOnFootListener;
            private OnPluggingHeadphoneListener pluggingHeadphoneListener;
            private OnUnpluggingHeadphoneListener unpluggingHeadphoneListener;

            public Builder setErrorListener(OnErrorListener errorListener) {
                this.errorListener = errorListener;
                return this;
            }

            public Builder setConnectedListener(OnConnectedListener connectedListener) {
                this.connectedListener = connectedListener;
                return this;
            }

            public Builder setDisconnectedListener(OnDisconnectedListener disconnectedListener) {
                this.disconnectedListener = disconnectedListener;
                return this;
            }

            public Builder setStartInVehicleListener(OnStartInVehicleListener startInVehicleListener) {
                this.startInVehicleListener = startInVehicleListener;
                return this;
            }

            public Builder setStopInVehicleListener(OnStopInVehicleListener stopInVehicleListener) {
                this.stopInVehicleListener = stopInVehicleListener;
                return this;
            }

            public Builder setStartOnFootListener(OnStartOnFootListener startOnFootListener) {
                this.startOnFootListener = startOnFootListener;
                return this;
            }

            public Builder setStopOnFootListener(OnStopOnFootListener stopOnFootListener) {
                this.stopOnFootListener = stopOnFootListener;
                return this;
            }

            public Builder setPluggingHeadphoneListener(OnPluggingHeadphoneListener pluggingHeadphoneListener) {
                this.pluggingHeadphoneListener = pluggingHeadphoneListener;
                return this;
            }

            public Builder setUnpluggingHeadphoneListener(OnUnpluggingHeadphoneListener unpluggingHeadphoneListener) {
                this.unpluggingHeadphoneListener = unpluggingHeadphoneListener;
                return this;
            }

            public Callbacks build() {
                // TODO: throw in case of any required type is missing
                return new Callbacks(this);
            }
        }
    }

    /**
     * Handles Utilities related to this component
     */
    class Utils {
        public static String getFenceStateAsString(int state) {
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
    }

    /**
     * Handles SharedPreferences keys related to this component
     */
    interface Preferences {
    }

    /**
     * The callbacks to interact with the Driving Detection service.
     *
     * Declared as single interfaces so we can easily make use of lambda expressions and isolate each callback.
     */
    interface OnErrorListener {
        void execute(Exception ex);
    }
    interface OnConnectedListener {
        void execute();
    }
    interface OnDisconnectedListener {
        void execute();
    }
    interface OnStartOnFootListener {
        void execute(FenceState fenceState);
    }
    interface OnStopOnFootListener {
        void execute(FenceState fenceState);
    }
    interface OnStartInVehicleListener {
        void execute(FenceState fenceState);
    }
    interface OnStopInVehicleListener {
        void execute(FenceState fenceState);
    }
    interface OnPluggingHeadphoneListener {
        void execute(FenceState fenceState);
    }
    interface OnUnpluggingHeadphoneListener {
        void execute(FenceState fenceState);
    }
}
