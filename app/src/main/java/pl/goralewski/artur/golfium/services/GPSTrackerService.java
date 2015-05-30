package pl.goralewski.artur.golfium.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import pl.goralewski.artur.golfium.GameActivity;
import pl.goralewski.artur.golfium.R;

/**
 * Created by Artur Goralewski on 07/05/2015.
 */

public class GPSTrackerService extends Service /*implements LocationListener*/ {
    private final String MY_TAG = "GPSTrackerService";
    public static final int NOTIFICATION = R.string.app_name;
    private static final long ONE_SEC = 1000;
    private static final long TWO_SEC = 2 * ONE_SEC;
    private static final long THIRTY_SEC = 30 * ONE_SEC;
    private static final long ONE_MIN = 60 * ONE_SEC;
    private static final long TWO_MIN = 2 *ONE_MIN ;

    public static boolean isRunning;
    public static Calendar runningSince;
    public Calendar stoppedOn;

    private SharedPreferences allInfoOfCurrentStatePrefs;

    private LocationManager mLocationManager;
    private Locationer gpsLocationer, networkLocationer;
    private NotificationCompat.Builder builder;

    private NotificationManager mNotificationManager;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Messenger messageHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            gpsLocationer = new Locationer(getBaseContext(), allInfoOfCurrentStatePrefs, messageHandler);
            networkLocationer = new Locationer(getBaseContext(), allInfoOfCurrentStatePrefs, messageHandler);
//	          gpslistener = new gpsStatusListener();
//	          mLocationManager.addGpsStatusListener(gpslistener);
//	          mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 5, gpsLocationer);
//	          mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 5, gpsLocationer);
////	          Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Criteria criteria = new Criteria();
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String providerFine = mLocationManager.getBestProvider(criteria, true);

            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String providerCoarse = mLocationManager.getBestProvider(criteria, true);

            if (providerCoarse != null) {
                mLocationManager.requestLocationUpdates(providerCoarse, THIRTY_SEC, 5, networkLocationer);
            }
            if (providerFine != null) {
                mLocationManager.requestLocationUpdates(providerFine, TWO_SEC, 0, gpsLocationer);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MY_TAG, "in onCreate, init GPS stuff");

        // getting prefs
        allInfoOfCurrentStatePrefs = getSharedPreferences(String.valueOf(R.string.allInfoOfCurrentStateFileName), MODE_PRIVATE);

        // getting LocationManager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager == null){
            // Stop here, we definitely need Location
            Toast.makeText(this, "This device doesn't support GPS or internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        // getting NotificationManager
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();


        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "local service is started ", Toast.LENGTH_SHORT).show();
        Log.d(MY_TAG, "local service is started ");

        Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId; //actually don't use it but leave as it is
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.d(MY_TAG, "in onHandleIntent, run for maximum time set in preferences");
//        new GPSTrackerRequest().execute(urlText + "tracker=start");
//
//        isRunning = true;
//        runningSince = Calendar.getInstance();
//        Intent i = new Intent(NOTIFICATION);
//        sendBroadcast(i);
//
//        Notification notification = new Notification(R.mipmap.ic_launcher, getText(R.string.toast_service_running), System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, GameActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.toast_service_running), pendingIntent);
//        startForeground(R.id.logo, notification);
//
//        long endTime = System.currentTimeMillis() + pref_max_run_time*60*60*1000;
//        while (System.currentTimeMillis() < endTime) {
//            try {
//                Thread.sleep(60*1000); // note: when device is sleeping, it may last up to 5 minutes or more
//            } catch (Exception e) {
//            }
//        }
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        // (user clicked the stop button, or max run time has been reached)
        Log.d(MY_TAG, "in onDestroy, stop listening to the GPS");
        mNotificationManager.cancel(NOTIFICATION);
        mLocationManager.removeUpdates(gpsLocationer);
        mLocationManager.removeUpdates(networkLocationer);

        // Tell the user we stopped.
        Toast.makeText(this, "local service is stopped", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Location tracking";
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GameActivity.class), 0);

        builder = new NotificationCompat.Builder(getBaseContext())
                .setContentTitle("You are being tracked...")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true);

        Notification notification = builder.getNotification();

        // Send the notification.
        mNotificationManager.notify(NOTIFICATION, notification);
    }

/* -------------- GPS stuff -------------- */


//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d(MY_TAG, "in onLocationChanged, latestUpdate == " + latestUpdate);
//
//
//
//        if ((System.currentTimeMillis() - latestUpdate) < pref_gps_updates*1000) {
//            return;
//        } else {
//            latestUpdate = System.currentTimeMillis();
//        }
//        new GPSTrackerRequest().execute(urlText + "lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
//    }

}
