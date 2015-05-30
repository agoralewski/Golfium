package pl.goralewski.artur.golfium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.goralewski.artur.golfium.services.GPSTrackerService;

public class GameActivity extends Activity {

    private final String MY_TAG ="GameActivity";
    private final static String CONNECTIVITY = "android.net.conn.CONNECTIVITY_CHANGE";
    private final double ACCURACY_THRESHOLD = 100.0;
    private final String[] Status = {"out of service", "temporarily unavailable", "available"};

    private GoogleMap googleMap;

    private LocationManager mLocationManager;

    private ConnectivityManager connectivityManager;

    @InjectView(R.id.metresToTargetTextView)
    TextView metresToTargetTextView;
    @InjectView(R.id.numberOfHitsTextView)
    TextView numberOfHitsTextView;
    @InjectView(R.id.nameOfFieldTextView)
    TextView nameOfFieldTextView;
    @InjectView(R.id.numberOfParTextView)
    TextView numberOfParTextView;

    SharedPreferences allInfoOfCurrentStatePrefs;

    public Handler messageHandler;

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(GPSTrackerService.NOTIFICATION)) {
////                updateServiceStatus();
//            }
//            if (action.equals(CONNECTIVITY)) {
////                updateNetworkStatus();
//            }
//        }
//    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i(MY_TAG, "onCreate()");

        ButterKnife.inject(this);

        allInfoOfCurrentStatePrefs = getSharedPreferences(String.valueOf(R.string.allInfoOfCurrentStateFileName), MODE_PRIVATE);

//        registerReceiver(receiver, new IntentFilter(GPSTrackerService.NOTIFICATION));
//        registerReceiver(receiver, new IntentFilter(GameActivity.CONNECTIVITY));

        // Acquire reference to the LocationManager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager == null){
            // Stop here, we definitely need Location
            Toast.makeText(this, "This device doesn't support GPS or internet connection.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if(!mLocationManager.getProviders(true).contains(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "Localization on your phone is disabled.\n" +
                    "Please turn it on. GPS at least is required.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

//        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        locationer = new//todo

//        int pref_gps_updates = Integer.parseInt(allInfoOfCurrentStatePrefs.getString("pref_gps_updates", "30")); // seconds
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);

        messageHandler = new MessageHandler();

        //start GPSTrackerService
        Intent startService = new Intent(this, GPSTrackerService.class);
        startService.putExtra("MESSENGER", new Messenger(messageHandler));
        startService(startService);


        try {
            // Loading map
            initializeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            double latitude = 52.135044;
            double longitude = 21.066671;

            // lets place some 10 random markers
            for (int i = 0; i < 10; i++) {
                // random latitude and logitude
                double[] randomLocation = createRandLocation(latitude,
                        longitude);

                // Adding a marker
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(randomLocation[0], randomLocation[1]))
                        .title("Hello Maps " + i);

                Log.e("Random", "> " + randomLocation[0] + ", "
                        + randomLocation[1]);

                // changing marker color
                if (i == 0)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                if (i == 1)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                if (i == 2)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                if (i == 3)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                if (i == 4)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                if (i == 5)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                if (i == 6)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                if (i == 7)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                if (i == 8)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                if (i == 9)
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                googleMap.addMarker(marker);

                // Move the camera to last position with a zoom level
                if (i == 9) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(randomLocation[0],
                                    randomLocation[1])).zoom(15).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(MY_TAG, "onStart() The activity is visible and about to be started.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(MY_TAG, "onRestart() The activity is visible and about to be restarted. after onStop() before onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MY_TAG, "onResume() The activity is and has focus (it is now \"resumed\")");
        initializeMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MY_TAG, "onPause() Another activity is taking focus (this activity is about to be \"paused\")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(MY_TAG, "onStop() The activity is no longer visible (it is now \"stopped\")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(MY_TAG, "onDestroy() The activity is about to be destroyed.");
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * creating random postion around a location for testing purpose only
     */
    private double[] createRandLocation(double latitude, double longitude) {

        return new double[] { latitude + ((Math.random() - 0.5) / 500),
                longitude + ((Math.random() - 0.5) / 500),
                150 + ((Math.random() - 0.5) * 10) };
    }

    private void updateMapAndDistanceToTarget(Location location){
        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();

        //updating distanceToTarget
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude,
                allInfoOfCurrentStatePrefs.getFloat("holeLat", (float) latitude),
                allInfoOfCurrentStatePrefs.getFloat("holeLong", (float) longitude), results);
        Log.d(MY_TAG, "dystans juz po przeslaniu " + (int) results[0]);
        metresToTargetTextView.setText( ""+ (int) results[0] );

        //updatingMap

    }

    public class MessageHandler extends Handler {
        public final int NEW_LOCATION = 1;

        @Override
        public void handleMessage(Message message) {
            int state =  message.arg1;
            switch (state) {
                case NEW_LOCATION:
                    Log.d(MY_TAG, "obsluga wiadomosci");
                    updateMapAndDistanceToTarget(message.getData().<Location>getParcelable("NEW_LOCATION"));
                    break;
//                case SHOW:
//
//                    break;
            }
        }
    }
}
