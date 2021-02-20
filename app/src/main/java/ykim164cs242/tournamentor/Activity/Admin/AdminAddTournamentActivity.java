package ykim164cs242.tournamentor.Activity.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ykim164cs242.tournamentor.InformationStorage.TournamentInfo;
import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.Utils.DateHandler;

/**
 * AdminAddTournamentActivity represents a screen where the Admin can add a new
 * tournament in the channel. Based on the final setting, it updates
 * the Firebase database. It requires tournamentName and term.
 */
public class AdminAddTournamentActivity extends AppCompatActivity {


    private EditText inputName;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private DatePickerDialog.OnDateSetListener startDateSetListener;
    private DatePickerDialog.OnDateSetListener endDateSetListener;

    private Button nextButton;
    private String channelID;
    private String tournamentName;

    private String startDate;
    private String endDate;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tournament);
        nextButton = (Button) findViewById(R.id.next_button);
        inputName = (EditText) findViewById(R.id.add_tournament_name);

        // passed in data

        try {
            Intent intent = getIntent();
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
            channelID = intent.getStringExtra("channelID");
            tournamentName = intent.getStringExtra("tournamentName");
        } catch(Exception e) {
            e.printStackTrace();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        startDateTextView = (TextView) findViewById(R.id.start_date);
        endDateTextView = (TextView) findViewById(R.id.end_date);

        inputName.setText(tournamentName);

        // Start Date selector dialog
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AdminAddTournamentActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, startDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // End Date selector dialog
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AdminAddTournamentActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, endDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = month + "-" + dayOfMonth + "-" + year;

                if(endDate != null && !DateHandler.isValidTerm(date, endDate)) {

                    // Invalid date message

                    Toast.makeText(getBaseContext(), "Invalid Term! The start date must be come before or be same as the end date", Toast.LENGTH_SHORT).show();

                } else if(!DateHandler.isNotOver(date)) {

                    Toast.makeText(getBaseContext(), "This date is already over", Toast.LENGTH_SHORT).show();

                } else {
                    startDateTextView.setText(date);
                    startDate = date;
                }
            }
        };

        endDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = month + "-" + dayOfMonth + "-" + year;

                if(startDate != null && !DateHandler.isValidTerm(startDate, date)) {

                    // Invalid date message

                    Toast.makeText(getBaseContext(), "Invalid Term! The end date must be come after or be same as the start date", Toast.LENGTH_SHORT).show();

                } else if(!DateHandler.isNotOver(date)) {

                    Toast.makeText(getBaseContext(), "This date is already over", Toast.LENGTH_SHORT).show();

                } else {

                    endDateTextView.setText(date);
                    endDate = date;
                }
            }
        };

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addTeamIntent = new Intent(AdminAddTournamentActivity.this, AdminAddTournamentAddTeamActivity.class);

                String nametoPass = inputName.getText().toString();
                String datetoPass = startDate + " ~ " + endDate;
                addTeamIntent.putExtra("startDate", startDate);
                addTeamIntent.putExtra("endDate", endDate);
                addTeamIntent.putExtra("inputName", nametoPass);
                addTeamIntent.putExtra("inputDate", datetoPass);

                TournamentInfo tempTournament = new TournamentInfo(nametoPass, datetoPass, null, null);
                rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments").child(nametoPass).setValue(tempTournament);

                // Adds the tournament info to the DB and moves to the team add screen

                startActivity(addTeamIntent);

            }
        });

    }
}
