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

    /**
     * Main intent filter action
     */
    String FENCE_ACTION = BuildConfig.APPLICATION_ID + ".FENCE_ACTION";

    /**
     * Custom actions
     */
    String START_IN_VEHICLE_FENCE = "startInVehicleFence";
    String STOP_IN_VEHICLE_FENCE = "stopInVehicleFence";

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
        int STARTING_ON_FOOT = 0;
        int STOPPING_ON_FOOT = 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({STARTING_ON_FOOT, STOPPING_ON_FOOT})
        @interface Items {}
    }

    /**
     * This class will contain the different callbacks needed to interact with a client.
     */
    class Callbacks {
        private OnErrorListener errorListener;
        private OnConnectedListener connectedListener;
        private OnDisconnectedListener disconnectedListener;
        private OnStartDrivingListener startDrivingListener;
        private OnStopDrivingListener stopDrivingListener;

        private Callbacks(Builder builder) {
            this.errorListener = builder.errorListener;
            this.connectedListener = builder.connectedListener;
            this.disconnectedListener = builder.disconnectedListener;
            this.startDrivingListener = builder.startDrivingListener;
            this.stopDrivingListener = builder.stopDrivingListener;
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

        public OnStartDrivingListener getStartDrivingListener() {
            return startDrivingListener != null ? startDrivingListener : ignored -> {};
        }

        public OnStopDrivingListener getStopDrivingListener() {
            return stopDrivingListener != null ? stopDrivingListener : ignored -> {};
        }

        /**
         * Proper Builder design pattern to handle multiple parameters in one place.
         */
        public static class Builder {
            private OnErrorListener errorListener;
            private OnConnectedListener connectedListener;
            private OnDisconnectedListener disconnectedListener;
            private OnStartDrivingListener startDrivingListener;
            private OnStopDrivingListener stopDrivingListener;

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

            public Builder setStartDrivingListener(OnStartDrivingListener startDrivingListener) {
                this.startDrivingListener = startDrivingListener;
                return this;
            }

            public Builder setStopDrivingListener(OnStopDrivingListener stopDrivingListener) {
                this.stopDrivingListener = stopDrivingListener;
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
    interface OnStartDrivingListener {
        void execute(FenceState fenceState);
    }
    interface OnStopDrivingListener {
        void execute(FenceState fenceState);
    }
}
