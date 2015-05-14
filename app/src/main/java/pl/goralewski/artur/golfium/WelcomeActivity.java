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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WelcomeActivity extends Activity {
    private final String MY_TAG = "WelcomeActivity";
    @InjectView(R.id.startButton)
    Button startButton;
    @InjectView(R.id.stopButton)
    Button stopButton;
    Button locationCheckButton;
    SharedPreferences allInfoOfCurrentState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.inject(this);

        locationCheckButton = (Button)findViewById(R.id.locationCheckButton);

        allInfoOfCurrentState = getSharedPreferences(String.valueOf(R.string.allInfoOfCurrentStateFileName), MODE_PRIVATE);

//        File data_file = new File("golfium_game_data.txt");

        if(allInfoOfCurrentState.getBoolean("inGame", false)) //chceck if we are still in game
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

//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final ProgressDialog dialog = ProgressDialog.show(WelcomeActivity.this, "",
//                        "Loading", true);
//
//                final Handler closeHandler = new Handler() {
//                    public void handleMessage(Message msg) {
//                        if (dialog != null) dialog.dismiss();
//                    }
//                };
//                File data_file = new File("golfium_game_data.txt");
//                // FTYS here we should not just check if file exists, but also read from file to check if there is also ball ID and current hole ID.
//                if (data_file.exists()) {
//                    startActivity(new Intent(WelcomeActivity.this, GameActivity.class));
//                    closeHandler.sendEmptyMessageDelayed(0, 1000);
//                } else {
//                    startActivity(new Intent(WelcomeActivity.this, GameStartWizardActivity.class));
//                    closeHandler.sendEmptyMessageDelayed(0, 1000);
//                }
//            }
//        });

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
        if (allInfoOfCurrentState.getBoolean("inGame", false)) //chceck if we are still in game
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
        SharedPreferences.Editor editor= allInfoOfCurrentState.edit();
        editor.putBoolean("inGame", false);
        editor.commit();
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
