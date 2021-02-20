package ykim164cs242.tournamentor.Activity.Common;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ykim164cs242.tournamentor.R;

/**
 * The MainActivity class represents the starting screen of the application.
 * It displays the logo of the Tournamentor for SPLASH_TIME milliseconds.
 * Then it changes the screen to the StartMenuActivity.
 */

public class MainActivity extends AppCompatActivity {

    // Determines how long the image will be displayed for

    private static int SPLASH_TIME = 3000; // Milliseconds, 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Automatically moves to the StartMenuActivity after 3 seconds

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent nextIntent = new Intent(MainActivity.this, StartMenuActivity.class);
                startActivity(nextIntent);
                finish();
            }
        }, SPLASH_TIME);
    }
}
