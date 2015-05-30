package pl.goralewski.artur.golfium.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Date;

import pl.goralewski.artur.golfium.dataManagement.DatabaseOpenHelper;
import pl.goralewski.artur.golfium.model.Coordinates;

/**
 * Created by Artur Goralewski on 24/05/2015.
 */
public class Locationer implements LocationListener {

    private Context context;
    SharedPreferences allInfoOfCurrentStatePrefs;

    private static final String DEBUG_TAG = "Locationer";
    private static final double ACCURACY_THRESHOLD = 100.0;
    private static final String[] Status = {"out of service", "temporarily unavailable", "available"};
    private Messenger messageHandler;

    public Locationer(Context context, SharedPreferences prefs, Messenger messageHandler) {
        this.context = context;
        this.allInfoOfCurrentStatePrefs = prefs;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(DEBUG_TAG, "onLocationChanged()");
        if ((location == null)||(location.getAccuracy() > ACCURACY_THRESHOLD)) {
        }
        else{
            insertLocationToDatabase(location);

            sendMessageToUpdateMap(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(DEBUG_TAG, provider + " disabled.");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(DEBUG_TAG, provider + " enabled.");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(DEBUG_TAG, provider + " statu changed" + status);
    }


    private void insertLocationToDatabase(Location location) {
        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();
        Long time = location.getTime();

        // put them into db
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
        databaseOpenHelper.addNewRow(new Coordinates(null, latitude, longitude, new Date(time),
                Integer.parseInt(allInfoOfCurrentStatePrefs.getString("holePlayId", "0"))));

        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude,
                allInfoOfCurrentStatePrefs.getFloat("holeLat", (float) latitude),
                allInfoOfCurrentStatePrefs.getFloat("holeLong", (float) longitude), results);
        Log.d(DEBUG_TAG, " " + (int) results[0]);
    }


    private void sendMessageToUpdateMap(Location location){
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putParcelable("NEW_LOCATION", location);
        message.setData(bundle);
        message.arg1 = 1;//NEW_KOCATION
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}