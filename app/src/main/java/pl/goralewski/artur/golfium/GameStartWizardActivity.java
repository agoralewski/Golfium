package pl.goralewski.artur.golfium;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GameStartWizardActivity extends Activity {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public final String MY_TAG = "GameStartWizardActivity";
    private NfcAdapter mNfcAdapter;

    @InjectView(R.id.textViewWizard1)
    TextView textViewWizard1;
    @InjectView(R.id.textViewWizard2)
    TextView textViewWizard2;
    @InjectView(R.id.textViewWizard3)
    TextView textViewWizard3;
    @InjectView(R.id.startMyGameButton)
    Button startMyGameButton;
    SharedPreferences allInfoOfCurrentStatePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start_wizard);
        Log.i(MY_TAG, "onCreate()");

        ButterKnife.inject(this);

        allInfoOfCurrentStatePrefs = getSharedPreferences(String.valueOf(R.string.allInfoOfCurrentStateFileName), MODE_PRIVATE);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC on your phone is disabled.\nPlease turn it on.", Toast.LENGTH_LONG).show();
            finish();
        }
        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(MY_TAG, "onStart() The activity is visible and about to be started.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startMyGameButton.setClickable(false);
        Log.i(MY_TAG, "onRestart() The activity is visible and about to be restarted. after onStop() before onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MY_TAG, "onResume() The activity is and has focus (it is now \"resumed\")");
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MY_TAG, "onPause() Another activity is taking focus (this activity is about to be \"paused\")");
        mNfcAdapter.disableForegroundDispatch(this);
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
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }




    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called.
         * In this case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            Log.d(MY_TAG, "mime type: " + type + " expected: " + MIME_TEXT_PLAIN);
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); //obtain a Tag object from the intent,
                                                                           // which will contain the payload and allow you to enumerate the tag's technologies:
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(MY_TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @OnClick(R.id.startMyGameButton)
    public void onClickStartMyGameButton()
    {
        final ProgressDialog dialog = ProgressDialog.show(GameStartWizardActivity.this, "", "Loading", true);
        final Handler closeHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (dialog != null) dialog.dismiss();
            }
        };

//        File data_file = new File("golfium_game_data.txt");
        // FTYS here we should not just check if file exists, but also read from file to check if there is also ball ID and current hole ID.
        SharedPreferences.Editor editor = allInfoOfCurrentStatePrefs.edit();
        editor.putBoolean("inGame", true);
        editor.commit();

        startActivity(new Intent(GameStartWizardActivity.this, GameActivity.class));
        closeHandler.sendEmptyMessageDelayed(0, 1000);
                                                //todo
//        startMyGameButton.setClickable(false);//prawdopodobnie potrzebne żeby nie robić nowego activity po jednym cofnięciu
                                                //albo lepiej z game activity przejść odrazu do welcomeActivity
    }

                                                //<Params,progress,result> okresnienie typow
    private class NdefReaderTask extends AsyncTask<Tag, Integer, String> {

        @Override
        protected void onPreExecute() { //executes in UI thread before AsyncTask is running. it set up the AsyncTask
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Tag... params) { //main body of background thread
                                                         //ma parametry and results results of type result zaznaczonym w < x, x, x>
                                                         //w trakcie wykonywania może zawołac metodę publishProgress(progress values)
                                                         //w UI thread w metodzie onProgressUpdate reaguje na progress - też zaimplementowana w AsyncTask
            Tag tag = params[0];
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                Log.d(MY_TAG, "type name format: " + ndefRecord.getTnf());
                Log.d(MY_TAG, "mime type: " + ndefRecord.toMimeType());
                //not used
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(MY_TAG, "Unsupported Encoding", e);
                    }
                }
                //used in my application
                if (ndefRecord.getTnf() == NdefRecord.TNF_MIME_MEDIA && MIME_TEXT_PLAIN.equals(ndefRecord.toMimeType())) {
                    Log.d(MY_TAG, "data from NFC tag: " + new String(ndefRecord.getPayload()));
                        return new String(ndefRecord.getPayload());
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */
            byte[] payload = record.getPayload();
            // Get the Text Encoding
            String textEncoding;
            if((payload[0] & 128) == 0)
                textEncoding = "UTF-8";
            else
                textEncoding = "UTF-16";
            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"
            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) { //będzie wywołane jak się skończy doInBackground() chyba w UI thread
            if (result != null) {
                //result contains NFC text
                String[] dataFromTag = result.split(":");//GOLFIUM:FIELD:x:HOLE:x:LAT:xx.xxxx:LON:xx.xxxx
                // FTYS in this if we will ckeck if data_file exists - if not, do this (in activity onCreate, we delete data_file)
                if(dataFromTag[0].equals("GOLFIUM"))
                {
                    if (textViewWizard3.getText().toString().equals("1/3"))
                    {
                        if (dataFromTag[3].equals("BALL"))
                        {
                            textViewWizard1.setTextColor(Color.parseColor("#a6a6a6"));
                            textViewWizard2.setTextColor(Color.parseColor("#ffffff"));
                            textViewWizard1.setText("Ball ID: " + dataFromTag[4]);
                            textViewWizard3.setText("2/3");

                            SharedPreferences.Editor editor = allInfoOfCurrentStatePrefs.edit();
                            editor.putString("ballId", dataFromTag[4]);
                            editor.commit();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), R.string.WrongNfcTagNotBall, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (textViewWizard3.getText().toString().equals("2/3"))
                    {
                        if (dataFromTag[3].equals("HOLE"))
                        {
                            textViewWizard2.setTextColor(Color.parseColor("#a6a6a6"));
                            startMyGameButton.setClickable(true);
                            startMyGameButton.setTextColor(Color.parseColor("#ffffff"));
                            textViewWizard2.setText("Hole ID: " + dataFromTag[4]);
                            textViewWizard3.setText("3/3");

                            SharedPreferences.Editor editor = allInfoOfCurrentStatePrefs.edit();
                            editor.putString("holeId", dataFromTag[4]);
                            editor.putFloat("holeLat", new Float(dataFromTag[6]));
                            editor.putFloat("holeLong", new Float(dataFromTag[8]));
                            editor.commit();
                        }
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
