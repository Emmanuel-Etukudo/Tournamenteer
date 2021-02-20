package ykim164cs242.tournamentor.Activity.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ykim164cs242.tournamentor.Adapter.Client.TeamListAdapter;
import ykim164cs242.tournamentor.InformationStorage.GameInfo;
import ykim164cs242.tournamentor.ListItem.TeamListItem;
import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.Utils.DateHandler;

/**
 * AdminAddMatchActivity represents a screen where the Admin can add a new
 * game in the tournament. Based on the final setting, it updates
 * the Firebase database.
 */
public class AdminAddMatchActivity extends AppCompatActivity {

    // For Team Selecting Dialog
    ListView teamListView;

    private TeamListAdapter adapter;
    private List<TeamListItem> teamListItems;

    // Storages for parsed data from the real-time database
    private List<String> teamNameList;
    private List<String> foundationYearList;
    private List<String> captainNameList;
;
    private EditText inputFieldName;
    private TextView inputMatchDate;
    private TextView inputMatchTime;
    private TextView inputTeamA;
    private TextView inputTeamB;
    private Button submitButton;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String tournamentName;
    private String fieldName;
    private String gameDate;
    private String gameTime;
    private String teamA;
    private String teamB;

    String channelId;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference teamReference;
    DatabaseReference tournamentReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_match);

        // Gets the tournamentName from the previous Activity
        try {
            Intent intent = getIntent();

            tournamentName = intent.getStringExtra("tournamentName");

        } catch(Exception e) {
            e.printStackTrace();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inputFieldName = (EditText) findViewById(R.id.input_field_name);
        inputMatchDate = (TextView) findViewById(R.id.input_match_date);
        inputMatchTime = (TextView) findViewById(R.id.input_match_time);
        inputTeamA = (TextView) findViewById(R.id.input_team_A);
        inputTeamB = (TextView) findViewById(R.id.input_team_B);
        submitButton = (Button) findViewById(R.id.add_game_button);

        channelId = firebaseUser.getUid();
        tournamentReference = rootReference.child("Channels").child(channelId).child("tournaments").child(tournamentName);
        teamReference = tournamentReference.child("teams");
        // Date selector dialog

        inputMatchDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AdminAddMatchActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // Time selector dialog

        inputMatchTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminAddMatchActivity.this, timeSetListener, hour, min, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();


            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = month + "-" + dayOfMonth + "-" + year;

                if(!DateHandler.isNotOver(date)) {

                    Toast.makeText(getBaseContext(), "This date is already over", Toast.LENGTH_SHORT).show();

                } else {

                    inputMatchDate.setText(date);
                    gameDate = date;

                }
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time = hourOfDay + " : " + minute;
                inputMatchTime.setText(time);
                gameTime = time;

            }
        };

        inputTeamA.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder addTemaBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View editTeamDialogView = inflater.inflate(R.layout.dialog_select_team, null);
                addTemaBuilder.setView(editTeamDialogView);
                addTemaBuilder.setTitle("Select Team A");
                final AlertDialog addTeamDialog = addTemaBuilder.create();
                addTeamDialog.show();

                teamListView = (ListView) editTeamDialogView.findViewById(R.id.select_team_listview);

                teamListItems = new ArrayList<>();
                teamNameList = new ArrayList<>();
                foundationYearList = new ArrayList<>();
                captainNameList = new ArrayList<>();

                teamReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Clear the storages for redrawing of the ListView
                        teamNameList.clear();
                        foundationYearList.clear();
                        captainNameList.clear();

                        // Fires every single time the channelReference updates in the Real-time DB
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            teamListItems.clear();
                            teamNameList.add(snapshot.child("teamName").getValue().toString());
                            foundationYearList.add(snapshot.child("foundationYear").getValue().toString());
                            captainNameList.add(snapshot.child("captainName").getValue().toString());
                        }

                        for(int i = 0; i < teamNameList.size(); i++) {
                            teamListItems.add(new TeamListItem(i, teamNameList.get(i), foundationYearList.get(i), captainNameList.get(i)));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // To prevent double selection
                        if(teamNameList.get(position) != null && teamNameList.get(position).equalsIgnoreCase(inputTeamB.getText().toString())) {
                            Toast.makeText(getBaseContext(), "This team is already selected.", Toast.LENGTH_LONG).show();
                        } else {
                            String teamName = teamNameList.get(position);
                            inputTeamA.setText(teamName);
                            teamA = teamName;
                            addTeamDialog.dismiss();
                        }
                    }
                });

                adapter = new TeamListAdapter(getBaseContext(), teamListItems);
                teamListView.setAdapter(adapter);


            }
        });

        inputTeamB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder addTemaBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View editTeamDialogView = inflater.inflate(R.layout.dialog_select_team, null);
                addTemaBuilder.setView(editTeamDialogView);
                addTemaBuilder.setTitle("Select Team B");
                final AlertDialog addTeamDialog = addTemaBuilder.create();
                addTeamDialog.show();

                teamListView = (ListView) editTeamDialogView.findViewById(R.id.select_team_listview);

                teamListItems = new ArrayList<>();
                teamNameList = new ArrayList<>();
                foundationYearList = new ArrayList<>();
                captainNameList = new ArrayList<>();

                teamReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Clear the storages for redrawing of the ListView
                        teamNameList.clear();
                        foundationYearList.clear();
                        captainNameList.clear();

                        // Fires every single time the channelReference updates in the Real-time DB
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            teamListItems.clear();
                            teamNameList.add(snapshot.child("teamName").getValue().toString());
                            foundationYearList.add(snapshot.child("foundationYear").getValue().toString());
                            captainNameList.add(snapshot.child("captainName").getValue().toString());
                        }

                        for(int i = 0; i < teamNameList.size(); i++) {
                            teamListItems.add(new TeamListItem(i, teamNameList.get(i), foundationYearList.get(i), captainNameList.get(i)));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // To prevent double selection
                        if(teamNameList.get(position) != null && teamNameList.get(position).equalsIgnoreCase(inputTeamA.getText().toString())) {
                            Toast.makeText(getBaseContext(), "This team is already selected.", Toast.LENGTH_LONG).show();
                        } else {
                            String teamName = teamNameList.get(position);
                            inputTeamB.setText(teamName);
                            teamB = teamName;
                            addTeamDialog.dismiss();
                        }

                    }
                });

                adapter = new TeamListAdapter(getBaseContext(), teamListItems);
                teamListView.setAdapter(adapter);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fieldName = inputFieldName.getText().toString();
                teamA = inputTeamA.getText().toString();
                teamB = inputTeamB.getText().toString();

                // Game ID Format is date + teamA + teamB
                String gameID = gameDate + gameTime + teamA + teamB;

                // Adds the gameInfo to the Databse
                GameInfo gameInfo = new GameInfo(gameID, fieldName, gameTime, gameDate, teamA, teamB, "0", "0", false, tournamentName, "0", false);
                rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments").child(tournamentName).child("games").child(gameID).setValue(gameInfo);

                Intent intent = new Intent(AdminAddMatchActivity.this, AdminMatchListActivity.class);

                // Passes in the tournamentName
                intent.putExtra("tournamentName", tournamentName);
                startActivity(intent);

            }
        });


    }

}
