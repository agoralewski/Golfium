package pl.goralewski.artur.golfium;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Formatka extends ActionBarActivity {
    private final String MY_TAG = "Formatka";
    /*
    restore saved state
    set content view
    initialize UI elements
    link UI elements to code actions
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //savedInstanceState - wszystko co aplikacja mogła przechować po poprzedniej activity
//        setContentView(R.layout.activity_my);
//  Initialize UI elements
//  final EditText addrText = (EditText) findViewById(R.id.location);

//    to daje przycisk który kieruje do nowego activity kontaktów gdzie pobierzemy namiar gps albo lokacji od kontaktu "pole golfowe A"
//    final Button button = (Button) findViewById(R.id.mapButton);
//    button.setOnClickListener(new Button.OnClickListener() {
//        // Called when user clicks the Show Map button
//        @Override
//        public void onClick(View v) {
//            try {
//// Create Intent object for picking data from Contacts database
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        CONTACTS_CONTENT_URI);
//// Use intent to start Contacts application
//// Variable PICK_CONTACT_REQUEST identifies this operation
//                startActivityForResult(intent, PICK_CONTACT_REQUEST);
//            } catch (Exception e) {
//// Log any error messages to LogCat using Log.e()
//                Log.e(MY_TAG, e.toString());
//            }
//        }
//    });

    }

//    to będzie do odebrania rezultatu z activity pobiera geolokacje z kontaktu
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        // Ensure that this call is the result of a successful PICK_CONTACT_REQUEST request
//        if (resultCode == Activity.RESULT_OK
//                && requestCode == PICK_CONTACT_REQUEST) {
//
//            // These details are covered in the lesson on ContentProviders
//            ContentResolver cr = getContentResolver();
//            Cursor cursor = cr.query(data.getData(), null, null, null, null);
//
//            if (null != cursor && cursor.moveToFirst()) {
//                String id = cursor
//                        .getString(cursor.getColumnIndex(CONTACTS_ID));
//                String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE
//                        + " = ?";
//                String[] whereParameters = new String[] { id,
//                        STRUCTURED_POSTAL_CONTENT_ITEM_TYPE };
//                Cursor addrCur = cr.query(DATA_CONTENT_URI, null, where,
//                        whereParameters, null);
//                if (null != addrCur && addrCur.moveToFirst()) {
//                    String formattedAddress = addrCur
//                            .getString(addrCur
//                                    .getColumnIndex(STRUCTURED_POSTAL_FORMATTED_ADDRESS));
//                    if (null != formattedAddress) {
//
//                        // Process text for network transmission
//                        formattedAddress = formattedAddress.replace(' ', '+');
//
//                        // Create Intent object for starting Google Maps application
//                        Intent geoIntent = new Intent(
//                                android.content.Intent.ACTION_VIEW,
//                                Uri.parse("geo:0,0?q=" + formattedAddress));
//
//                        // Use the Intent to start Google Maps application using Activity.startActivity()
//                        startActivity(geoIntent);
//                    }
//                }
//                if (null != addrCur)
//                    addrCur.close();
//            }
//            if (null != cursor)
//                cursor.close();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(MY_TAG, "The activity is visible and about to be started.");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(MY_TAG, "The activity is visible and about to be restarted.");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MY_TAG, "The activity is and has focus (it is now \"resumed\")");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MY_TAG,
                "Another activity is taking focus (this activity is about to be \"paused\")");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(MY_TAG, "The activity is no longer visible (it is now \"stopped\")");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(MY_TAG, "The activity is about to be destroyed.");
    }
}
