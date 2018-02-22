package com.chattylabs.module.awareness;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chattylabs.module.base.RequiredPermissions;

public interface AwareComponent extends RequiredPermissions {
    String TAG = "AwareComponent".substring(0, 23);

    String FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + ".FENCE_RECEIVER_ACTION";

    String START_IN_VEHICLE_FENCE = "startInVehicleFence";
    String STOP_IN_VEHICLE_FENCE = "stopInVehicleFence";

    /**
     * To use only the last instance.
     */
    class Singleton {
        static AwareComponent INSTANCE;

        @Nullable
        public static AwareComponent getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Convenient method to deal with Android lifecycle
     */
    void attach(@NonNull Callbacks callbacks);

    /**
     * Convenient method to deal with Android lifecycle
     */
    void detach();

    /**
     * Initializes the Driving Detection service.
     *
     * Deliberately created that way to have into account unit testing purposes.
     */
    void start(@NonNull Context context, @NonNull Callbacks callbacks);

    /**
     * Stops the services
     */
    void stop(@NonNull Context context);

    /**
     * Process the retrieved intent
     */
    void process(Intent intent);

    /**
     * This class will contain the different callbacks needed to interact with a client.
     */
    class Callbacks {
        private OnErrorListener errorListener;
        private OnConnectedListener connectedListener;
        private OnDisconnectedListener disconnectedListener;
        private OnStartedDrivingListener startedDrivingListener;
        private OnStoppedDrivingListener stoppedDrivingListener;

        private Callbacks(Builder builder) {
            this.errorListener = builder.errorListener;
            this.connectedListener = builder.connectedListener;
            this.disconnectedListener = builder.disconnectedListener;
            this.startedDrivingListener = builder.startedDrivingListener;
            this.stoppedDrivingListener = builder.stoppedDrivingListener;
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

        public OnStartedDrivingListener getStartedDrivingListener() {
            return startedDrivingListener != null ? startedDrivingListener : () -> {};
        }

        public OnStoppedDrivingListener getStoppedDrivingListener() {
            return stoppedDrivingListener != null ? stoppedDrivingListener : () -> {};
        }

        /**
         * Proper Builder design pattern to handle multiple parameters in one place.
         */
        public static class Builder {
            private OnErrorListener errorListener;
            private OnConnectedListener connectedListener;
            private OnDisconnectedListener disconnectedListener;
            private OnStartedDrivingListener startedDrivingListener;
            private OnStoppedDrivingListener stoppedDrivingListener;

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

            public Builder setStartedDrivingListener(OnStartedDrivingListener startedDrivingListener) {
                this.startedDrivingListener = startedDrivingListener;
                return this;
            }

            public Builder setStoppedDrivingListener(OnStoppedDrivingListener stoppedDrivingListener) {
                this.stoppedDrivingListener = stoppedDrivingListener;
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

    interface OnStartedDrivingListener {
        void execute();
    }
    interface OnStoppedDrivingListener {
        void execute();
    }
}
