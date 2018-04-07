package com.chattylabs.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Pair;

import com.chattylabs.demo.awareness.R;
import com.chattylabs.sdk.android.awareness.AwarenessComponent;
import com.chattylabs.sdk.android.awareness.AwarenessModule;
import com.chattylabs.sdk.android.core.CsvWriter;
import com.chattylabs.sdk.android.core.ServiceDelegate;

import static com.chattylabs.sdk.android.awareness.AwarenessComponent.*;

public class AwarenessServiceDelegate implements ServiceDelegate {

    private static final String CHANNEL_ID = "CHANNEL_1";
    public static final String LOG_STARTED_DRIVING = "LOG_STARTED_DRIVING";
    public static final String LOG_STOPPED_DRIVING = "LOG_STOPPED_DRIVING";
    public static final String LOG_COMPONENT_STARTED = "LOG_COMPONENT_STARTED";
    private CsvWriter csvWriter;
    private AwarenessComponent awarenessComponent;

    private void vibrateForStart(Context context) {
        Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        long[] patterns = new long[]{0, 200, 100, 200, 100, 200, 700, 200, 100, 200, 100, 200};
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
                vibrator.vibrate(VibrationEffect.createOneShot(2100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else {
                vibrator.vibrate(2100);
            }
        }
    }

    private void clearNotification(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }

    private void createNotification(Context context, String content, int id) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel_name", importance);
            // Register the channel with the system
            NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (n != null) n.createNotificationChannel(channel);
        }

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
    }

    @Override
    public <T extends Class<? extends Service>> void onCreate(Application application, T clazz) {

        if (csvWriter == null) {
            csvWriter = CsvWriter.getInstance();
            initLog(application, "LOG_AWARENESS_FILENAME");
        }
        csvWriter.write(application, Pair.create(Trace.SERVICE_CREATED, Trace.TRUE));

        // The component
        if (awarenessComponent == null) {
            awarenessComponent = AwarenessModule.provideAwarenessComponent();
            csvWriter.write(application, Pair.create(LOG_COMPONENT_STARTED, Trace.TRUE));
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
        Fences.State state = extract(intent);
        switch (state.getFence()) {
            case Fences.STARTING_IN_VEHICLE:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    vibrateForStart(application);
                    csvWriter.write(application, Pair.create(LOG_STARTED_DRIVING, Trace.TRUE));
                    createNotification(application, "STARTING_IN_VEHICLE", Fences.STARTING_IN_VEHICLE);
                }
                break;
            case Fences.STOPPING_IN_VEHICLE:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    clearNotification(application, Fences.STARTING_IN_VEHICLE);
                }
                break;
            case Fences.STARTING_ON_FOOT:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    vibrateForStart(application);
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    createNotification(application, "STARTING_ON_FOOT", Fences.STARTING_ON_FOOT);
                }
                break;
            case Fences.STOPPING_ON_FOOT:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    clearNotification(application, Fences.STARTING_ON_FOOT);
                }
                break;
            case Fences.PLUGGING_HEADPHONE:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    vibrateForStart(application);
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    createNotification(application, "PLUGGING_HEADPHONE", Fences.PLUGGING_HEADPHONE);
                }
                break;
            case Fences.UNPLUGGING_HEADPHONE:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    clearNotification(application, Fences.PLUGGING_HEADPHONE);
                }
                break;
            case Fences.STARTING_STILL:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    vibrateForStart(application);
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    createNotification(application, "STARTING_STILL", Fences.STARTING_STILL);
                }
                break;
            case Fences.STOPPING_STILL:
                if (state.getCurrentState() == Fences.State.TRUE) {
                    //csvWriter.write(application, Pair.create(LOG_STOPPED_DRIVING, Trace.TRUE));
                    clearNotification(application, Fences.STARTING_STILL);
                }
                break;
        }
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
        csvWriter.init(activityString, ";", "-");
        csvWriter.addHeaders(context, LOG_STARTED_DRIVING, LOG_STOPPED_DRIVING, LOG_COMPONENT_STARTED,
                             Trace.SERVICE_CREATED, Trace.SERVICE_DESTROYED, Trace.ON_TRIM_MEMORY, Trace.ON_LOW_MEMORY, Trace.ERROR);
    }
}
