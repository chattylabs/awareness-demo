package com.chattylabs.demo;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.chattylabs.module.awareness.AwareComponent;
import com.chattylabs.module.awareness.AwareComponentImpl;
import com.chattylabs.module.base.CsvWriter;
import com.chattylabs.module.base.ServiceDelegate;

public class AwarenessServiceDelegate implements ServiceDelegate {

    private static CsvWriter csvWriter;

    private AwareComponent awareComponent;

    public static AwareComponent.Callbacks.Builder getGeneralCallbackBuilder(Context context) {
        return new AwareComponent.Callbacks.Builder()
                .setErrorListener(ex -> {
                    csvWriter.write(Pair.create("ERROR", ex.getMessage()));
                })
                .setStartedDrivingListener(() -> {
                    csvWriter.write(Pair.create("STARTED_DRIVING", Trace.TRUE));
                })
                .setStoppedDrivingListener(() -> {
                    csvWriter.write(Pair.create("STOPPED_DRIVING", Trace.TRUE));
                });
    }

    public AwarenessServiceDelegate() {
        awareComponent = AwareComponent.Singleton.getInstance();
    }

    @Override
    public <T extends Class<? extends Service>> void onCreate(Application application, T clazz) {

        if (csvWriter == null) {
            csvWriter = new CsvWriter(application);
            initLog("AWARE_COMPONENT");
        }
        csvWriter.write(Pair.create(Trace.SERVICE_CREATED, Trace.TRUE));

        // The component
        if (awareComponent == null) {
            awareComponent = AwareComponent.Singleton.getInstance();
            if (awareComponent == null) {
                awareComponent = new AwareComponentImpl(application, clazz);
                awareComponent.start(application, getGeneralCallbackBuilder(application).build());
                csvWriter.write(Pair.create("COMPONENT_STARTED", Trace.TRUE));
            }
        }
    }

    @Override
    public void onDestroy(Application application) {
        if (awareComponent != null) {
            awareComponent.stop(application);
            awareComponent = null;
        }
        csvWriter.write(Pair.create(Trace.SERVICE_DESTROYED, Trace.TRUE));
        csvWriter.close();
        csvWriter = null;
    }

    @Override
    public void onStartCommand(Application application, Intent intent) {
        if (awareComponent != null) awareComponent.process(intent);
    }

    @Override
    public void onTrimMemory(Application application, int level) {
        csvWriter.write(Pair.create(Trace.ON_TRIM_MEMORY, Utils.getTrimMemoryAsString(level)));
    }

    @Override
    public void onLowMemory(Application application) {
        csvWriter.write(Pair.create(Trace.ON_LOW_MEMORY, Trace.TRUE));
    }


    private void initLog(String activity) {
        csvWriter.init(activity, ";", "-",
                "STARTED_DRIVING", "STOPPED_DRIVING",
                Trace.SERVICE_CREATED, Trace.SERVICE_DESTROYED, Trace.ON_TRIM_MEMORY, Trace.ON_LOW_MEMORY,
                "ERROR", "COMPONENT_STARTED");
    }
}
