package pl.goralewski.artur.golfium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.goralewski.artur.golfium.dataManagement.DatabaseExporter;
import pl.goralewski.artur.golfium.dataManagement.DatabaseOpenHelper;
import pl.goralewski.artur.golfium.services.GPSTrackerService;

public class WelcomeActivity extends Activity {
    private final String MY_TAG = "WelcomeActivity";
    @InjectView(R.id.startButton)
    Button startButton;
    @InjectView(R.id.stopButton)
    Button stopButton;
    Button locationCheckButton;
    Button programYourNfcButton;
    SharedPreferences allInfoOfCurrentStatePrefs;
    @InjectView(R.id.exportDatabaseButton)
    Button exportDatabaseButton;
    @InjectView(R.id.googlesampleButton)
    Button googlesampleButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.i(MY_TAG, " onCreate");

        ButterKnife.inject(this);

        locationCheckButton = (Button)findViewById(R.id.locationCheckButton);
        programYourNfcButton= (Button)findViewById(R.id.NFCWriter);

        allInfoOfCurrentStatePrefs = getSharedPreferences(String.valueOf(R.string.allInfoOfCurrentStateFileName), MODE_PRIVATE);

        if(allInfoOfCurrentStatePrefs.getBoolean("inGame", false)) //chceck if we are still in game
        {
            stopButton.setClickable(true);
            stopButton.setTextColor(Color.parseColor("#ffffff"));
            startButton.setText("Resume game");
        }
        else
        {
            stopButton.setClickable(false);
            stopButton.setTextColor(Color.parseColor("#a6a6a6"));
            startButton.setText("Start game");
        }

        locationCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(WelcomeActivity.this, "", "Loading", true);

                final Handler closeHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (dialog != null) dialog.dismiss();
                    }
                };
                startActivity(new Intent(WelcomeActivity.this, LocationCheckActivity.class));
                closeHandler.sendEmptyMessageDelayed(0, 1000);
            }
        });

        programYourNfcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(WelcomeActivity.this, "", "Loading", true);

                final Handler closeHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (dialog != null) dialog.dismiss();
                    }
                };
                startActivity(new Intent(WelcomeActivity.this, NFCWriterActivity.class));
                closeHandler.sendEmptyMessageDelayed(0, 1000);
            }
        });
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
        if (allInfoOfCurrentStatePrefs.getBoolean("inGame", false)) //chceck if we are still in game
        {
            stopButton.setClickable(true);
            stopButton.setTextColor(Color.parseColor("#ffffff"));
            startButton.setText("Resume game");
        }
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

        stopService(new Intent(this, GPSTrackerService.class));

        Log.i(MY_TAG, "onDestroy() The activity is about to be destroyed.");
        SharedPreferences.Editor editor= allInfoOfCurrentStatePrefs.edit();
        editor.putBoolean("inGame", false);
        editor.commit();
    }

    @OnClick(R.id.startButton)
    public void onClickStartButton()
    {
        final ProgressDialog dialog = ProgressDialog.show(WelcomeActivity.this, "", "Loading", true);
        final Handler closeHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (dialog != null) dialog.dismiss();
            }
        };

//        File data_file = new File("golfium_game_data.txt");
        // FTYS here we should not just check if file exists, but also read from file to check if there is also ball ID and current hole ID.
        if (allInfoOfCurrentStatePrefs.getBoolean("inGame", false)) //chceck if we are still in game
        {
            startActivity(new Intent(WelcomeActivity.this, GameActivity.class));
            closeHandler.sendEmptyMessageDelayed(0, 1000);
        } else {
            startActivity(new Intent(WelcomeActivity.this, GameStartWizardActivity.class));
            closeHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @OnClick(R.id.stopButton)
    public void onClickStopButton()
    {
        //stop service
        Intent intent = new Intent(this, GPSTrackerService.class);
        stopService(intent);

        stopButton.setTextColor(Color.parseColor("#a6a6a6"));
        stopButton.setClickable(false);
        startButton.setText("Start game");
        SharedPreferences.Editor editor= allInfoOfCurrentStatePrefs.edit();
        editor.putBoolean("inGame", false);
        editor.commit();
    }

    @OnClick(R.id.exportDatabaseButton)
    public void onClickExportDatabaseButton()
    {
        DatabaseExporter databaseExporter = new DatabaseExporter(new DatabaseOpenHelper(getApplicationContext()).getReadableDatabase());
        try {
            databaseExporter.export(DatabaseOpenHelper.DB_NAME, "golfiumExport");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.googlesampleButton)
    public void onClickGoogleSampleButton() {
        startActivity(new Intent(WelcomeActivity.this, PolylineDemoActivity.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inURL) );
        startActivity(browse);
    }
}
