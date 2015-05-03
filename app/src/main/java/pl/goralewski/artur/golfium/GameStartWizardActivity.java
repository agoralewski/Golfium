package pl.goralewski.artur.golfium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class GameStartWizardActivity extends Activity {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome);
//        ((Button)findViewById(R.id.startButton)).setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View v) {
//                final ProgressDialog dialog = ProgressDialog.show(GameStartWizardActivity.this, "",
//                        "Loading", true);
//
//                final Handler closeHandler = new Handler() {
//                    public void handleMessage(Message msg) {
//                        if (dialog!=null) dialog.dismiss();
//                    }
//                };
//
//                startActivity(new Intent(GameStartWizardActivity.this, GameActivity.class));
//                closeHandler.sendEmptyMessageDelayed(0, 3000);
//            }
//        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start_wizard);

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
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
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
                                                //<Params,progress,result> okresnienie typow
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

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
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
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
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
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
                TextView mTextView1;
                mTextView1 = (TextView) findViewById(R.id.textViewWizard1);
                TextView mTextView2;
                mTextView2 = (TextView) findViewById(R.id.textViewWizard2);
                TextView mTextView3;
                mTextView3 = (TextView) findViewById(R.id.textViewWizard3);
                Button mButton1 = (Button)findViewById(R.id.startMyGameButton);
                String substr1;
                String substr2;
                // FTYS in this if we will ckeck if data_file exists - if not, do this (in activity onCreate, we delete data_file)
                if (mTextView3.getText().toString().equals("1/3")){
                    substr1=result.substring(0,13);
                    if (substr1.equals("GOLFIUM.ball.")){
                        mTextView1.setTextColor(Color.parseColor("#a6a6a6"));
                        mTextView2.setTextColor(Color.parseColor("#ffffff"));
                        substr2=result.substring(13);
                        mTextView1.setText("Ball ID: " + substr2);
                        mTextView3.setText("2/3");
                        //FTYS writing one line into data_file (rewriting whole file)
                    }
                }
                // FTYS in this if we will check if file exists and contains exactly one line (we will not check if it has 2/3)
                else if (mTextView3.getText().toString().equals("2/3")){
                    //FTYS rewrite screen, so it looks like at the end of previous if part
                    substr1=result.substring(0,13);
                    if (substr1.equals("GOLFIUM.hole.")){
                        mTextView2.setTextColor(Color.parseColor("#a6a6a6"));
                        mButton1.setClickable(true);
                        mButton1.setTextColor(Color.parseColor("#ffffff"));
                        substr2=result.substring(13);
                        mTextView2.setText("Hole ID: " + substr2);
                        mTextView3.setText("3");
                        //FTYS write hole ID as a second line in data_file
                    }
                }
            }
        }
    }
}
