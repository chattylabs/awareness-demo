package com.chattylabs.demo;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.chattylabs.module.awareness.AwarenessComponent;
import com.chattylabs.module.awareness.AwarenessComponentImpl;
import com.chattylabs.module.base.CsvWriter;
import com.chattylabs.module.base.ServiceDelegate;

public class AwarenessServiceDelegate implements ServiceDelegate {

    private CsvWriter csvWriter;
    private AwarenessComponent awarenessComponent;

    private void vibrateForStart(Context context) {
        Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        long[] patterns = new long[]{350, 300, 300, 1500};
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createWaveform(patterns, -1));
            }
            else {
                vibrator.vibrate(patterns, -1);
            }
        }
    }

    private void vibrateForStop(Context context) {
        Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else {
                vibrator.vibrate(3000);
            }
        }
    }

    private AwarenessComponent.Callbacks.Builder getGeneralCallbackBuilder(Context context) {
        return new AwarenessComponent.Callbacks.Builder()
                .setErrorListener(ex -> {
                    csvWriter.write(context, Pair.create(Trace.ERROR, ex.getMessage()));
                })
                .setStartInVehicleListener(fenceState -> {
                    vibrateForStart(context);
                    csvWriter.write(context, Pair.create(AwarenessComponent.LOG_STARTED_DRIVING, Trace.TRUE));
                })
                .setStopInVehicleListener(fenceState -> {
                    vibrateForStop(context);
                    csvWriter.write(context, Pair.create(AwarenessComponent.LOG_STOPPED_DRIVING, Trace.TRUE));
                })
                .setStartOnFootListener(fenceState -> {
                    Log.i("DEMO", "ON_FOOT START: " + AwarenessComponent.Utils.getFenceStateAsString(fenceState.getCurrentState()));
                })
                .setStopOnFootListener(fenceState -> {
                    Log.i("DEMO", "ON_FOOT STOP: " + AwarenessComponent.Utils.getFenceStateAsString(fenceState.getCurrentState()));
                })
                .setPluggingHeadphoneListener(fenceState -> {
                    String message = "Plugging Headphone: " + AwarenessComponent.Utils.getFenceStateAsString(fenceState.getCurrentState());
                    Log.i("DEMO", message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                })
                .setUnpluggingHeadphoneListener(fenceState -> {
                    String message = "Unplugging Headphone: " + AwarenessComponent.Utils.getFenceStateAsString(fenceState.getCurrentState());
                    Log.i("DEMO", message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public <T extends Class<? extends Service>> void onCreate(Application application, T clazz) {

        if (csvWriter == null) {
            csvWriter = new CsvWriter();
            initLog(application, AwarenessComponent.LOG_AWARENESS_FILENAME);
        }
        csvWriter.write(application, Pair.create(Trace.SERVICE_CREATED, Trace.TRUE));

        // The component
        if (awarenessComponent == null) {
            awarenessComponent = AwarenessComponent.Singleton.getInstance();
            if (awarenessComponent == null) {
                awarenessComponent = new AwarenessComponentImpl(application, clazz);
                csvWriter.write(application, Pair.create(AwarenessComponent.LOG_COMPONENT_STARTED, Trace.TRUE));
            }
        }
    }

    @Override
    public void onDestroy(Application application) {
        csvWriter.write(application, Pair.create(Trace.SERVICE_DESTROYED, Trace.TRUE));
        csvWriter.close(application);
        csvWriter = null;
    }

    @Override
    public void onHandleIntent(Application application, Intent intent) {
        if (awarenessComponent != null) awarenessComponent.process(intent, getGeneralCallbackBuilder(application).build());
    }

    @Override
    public void onTrimMemory(Application application, int level) {
        csvWriter.write(application, Pair.create(Trace.ON_TRIM_MEMORY, Utils.getTrimMemoryAsString(level)));
    }

    @Override
    public void onLowMemory(Application application) {
        csvWriter.write(application, Pair.create(Trace.ON_LOW_MEMORY, Trace.TRUE));
    }


    private void initLog(Context context, String activityString) {
        csvWriter.init(context, activityString, ";", "-",
                       AwarenessComponent.LOG_STARTED_DRIVING, AwarenessComponent.LOG_STOPPED_DRIVING, AwarenessComponent.LOG_COMPONENT_STARTED,
                       Trace.SERVICE_CREATED, Trace.SERVICE_DESTROYED, Trace.ON_TRIM_MEMORY, Trace.ON_LOW_MEMORY, Trace.ERROR);
    }
}
