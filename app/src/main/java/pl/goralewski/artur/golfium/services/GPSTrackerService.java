package pl.goralewski.artur.golfium.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import pl.goralewski.artur.golfium.R;


/**
 * Created by Artur Góralewski on 07/05/2015.
 */
public class GPSTrackerService extends IntentService implements LocationListener {
    private final String MY_TAG = "GPSTrackerService";
    public static final String NOTIFICATION = "fr.herverenault.selfhostedgpstracker";

    public static boolean isRunning;
    public static Calendar runningSince;
    public Calendar stoppedOn;

    private SharedPreferences preferences;
    private String urlText;
    private LocationManager mLocationManager;
    private int pref_gps_updates;
    private long latestUpdate;
    private int pref_max_run_time;

    public GPSTrackerService() {
        super("GPSTrackerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MY_TAG, "in onCreate, init GPS stuff");

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager == null){
            // Stop here, we definitely need Location
            Toast.makeText(this, "This device doesn't support GPS or internet connection.", Toast.LENGTH_LONG).show();
            return;
        }
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            onProviderDisabled(LocationManager.GPS_PROVIDER);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("stoppedOn", 0);
        editor.commit();
        pref_gps_updates = Integer.parseInt(preferences.getString("pref_gps_updates", "30")); // seconds
        pref_max_run_time = Integer.parseInt(preferences.getString("pref_max_run_time", "24")); // hours
        urlText = preferences.getString("URL", "");
        if(urlText!=null)
            if (urlText.contains("?")) {
                urlText = urlText + "&";
            } else {
                urlText = urlText + "?";
            }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pref_gps_updates * 1000, 1, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(MY_TAG, "in onHandleIntent, run for maximum time set in preferences");
        new GPSTrackerRequest().execute(urlText + "tracker=start");

        isRunning = true;
        runningSince = Calendar.getInstance();
        Intent i = new Intent(NOTIFICATION);
        sendBroadcast(i);

        Notification notification = new Notification(R.drawable.ic_notif, getText(R.string.toast_service_running), System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, SelfHostedGPSTrackerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.toast_service_running), pendingIntent);
        startForeground(R.id.logo, notification);

        long endTime = System.currentTimeMillis() + pref_max_run_time*60*60*1000;
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(60*1000); // note: when device is sleeping, it may last up to 5 minutes or more
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        // (user clicked the stop button, or max run time has been reached)
        Log.d(MY_TAG, "in onDestroy, stop listening to the GPS");
        new GPSTrackerRequest().execute(urlText + "tracker=stop");

        mLocationManager.removeUpdates(this);

        isRunning = false;
        stoppedOn = Calendar.getInstance();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("stoppedOn", stoppedOn.getTimeInMillis());
        editor.commit();

        Intent intent = new Intent(NOTIFICATION);
        sendBroadcast(intent);
    }

	/* -------------- GPS stuff -------------- */

    @Override
    public void onLocationChanged(Location location) {
        Log.d(MY_TAG, "in onLocationChanged, latestUpdate == " + latestUpdate);

        if ((System.currentTimeMillis() - latestUpdate) < pref_gps_updates*1000) {
            return;
        } else {
            latestUpdate = System.currentTimeMillis();
        }
        new GPSTrackerRequest().execute(urlText + "lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Localization on your phone is disabled.\n" +
                "Please turn it on. GPS at least is required.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}