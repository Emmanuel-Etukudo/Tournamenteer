package ykim164cs242.tournamentor.Activity.Common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ykim164cs242.tournamentor.Activity.Admin.AdminLoginActivity;
import ykim164cs242.tournamentor.Activity.Client.SelectChannelActivity;
import ykim164cs242.tournamentor.R;

/**
 * StartMenuActivity represents a screen of selecting the mode of the application.
 * It lets the user to choose either the Admin Mode: Mode for setting up channels for
 * tournaments and managing them or the Join Channel Mode: Mode for joining tournament
 * channel as a participant of the tournament.
 */

public class StartMenuActivity extends AppCompatActivity {

    Button joinChannelButton;
    Button adminModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        joinChannelButton = (Button) findViewById(R.id.join_channel_button);
        adminModeButton = (Button) findViewById(R.id.admin_mode_button);
        // Moves to the Channel Selection Screen

        joinChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent channelIntent = new Intent(StartMenuActivity.this, SelectChannelActivity.class);
                startActivity(channelIntent);
            }
        });

        adminModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(StartMenuActivity.this, AdminLoginActivity.class);
                startActivity(adminIntent);
            }
        });
    }
}
