package ykim164cs242.tournamentor.Activity.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ykim164cs242.tournamentor.Activity.Client.ClientTournamentListActivity;
import ykim164cs242.tournamentor.Activity.Client.SelectChannelActivity;
import ykim164cs242.tournamentor.Adapter.Admin.AdminMatchListAdapter;
import ykim164cs242.tournamentor.Adapter.Client.MatchListAdapter;
import ykim164cs242.tournamentor.ListItem.AdminMatchListItem;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.R;

/**
 * The MatchListTab class represents the Activity that displays the match list.
 * The Admin user can add new matches and manage the status of the match.
 * All status will be updated to the DB immediately. Each Item of the ListView
 * are handled in the AdminMatchListAdapter class.
 */
public class AdminMatchListActivity extends AppCompatActivity {

    ListView matchListView;

    private Button addMatchButton;

    private AdminMatchListAdapter adapter;
    private List<AdminMatchListItem> adminMatchListItems;

    // Storages for parsed JSON data (repoName, userName, description of repositories)
    private List<String> matchIDList;
    private List<String> fieldNameList;
    private List<String> gameTimeList;
    private List<String> gameDateList;
    private List<String> teamAList;
    private List<Integer> scoreAList;
    private List<String> teamBList;
    private List<Integer> scoreBList;
    private List<Boolean> isLiveList;
    private List<String> startedTimeList;
    private List<Boolean> isOverList;

    private String tournamentName;

    // Firebase database reference
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference matchReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_match_list);

        try {
            Intent intent = getIntent();
            // Tournament name
            tournamentName = intent.getStringExtra("tournamentName");

        } catch(Exception e) {
            e.printStackTrace();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        matchListView = (ListView) findViewById(R.id.admin_match_listview);

        adminMatchListItems = new ArrayList<>();
        matchIDList = new ArrayList<>();
        fieldNameList = new ArrayList<>();
        gameTimeList = new ArrayList<>();
        gameDateList = new ArrayList<>();
        teamAList = new ArrayList<>();
        scoreAList = new ArrayList<>();
        teamBList = new ArrayList<>();
        scoreBList = new ArrayList<>();
        isLiveList = new ArrayList<>();
        startedTimeList = new ArrayList<>();
        isOverList = new ArrayList<>();

        adapter = new AdminMatchListAdapter(this, adminMatchListItems);
        matchListView.setAdapter(adapter);

        addMatchButton = (Button) findViewById(R.id.add_admin_game_button);

        // Moves to the add match screen
        addMatchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMatchListActivity.this, AdminAddMatchActivity.class);
                intent.putExtra("tournamentName", tournamentName);
                startActivity(intent);
            }
        });
    }

    /**
     * Fetches the data from the real-time database, stores into the pre-initialized storages,
     * and displays in the ListView. The onDataChange function runs everytime the data is
     * changed in the real-time database.
     */
    @Override
    public void onStart() {
        super.onStart();

        matchReference = rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                .child(tournamentName).child("games");

        matchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearCurrentList();

                if(dataSnapshot.child("games").getValue() == null) {
                    adminMatchListItems.clear();
                }

                // Fires every single time the channelReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    adminMatchListItems.clear();

                    // ID Format: Date + TeamA + vs + TeamB
                    matchIDList.add(snapshot.child("id").getValue().toString());
                    fieldNameList.add(snapshot.child("fieldName").getValue().toString());

                    // If not live show game time

                    //if(!(boolean)snapshot.child("live").getValue())
                    gameTimeList.add(snapshot.child("gameTime").getValue().toString());

                    // If live show current game time

                    //else gameTimeList.add("Game Started : " + snapshot.child("startedTime").getValue().toString());

                    gameDateList.add(snapshot.child("gameDate").getValue().toString());
                    teamAList.add(snapshot.child("teamA").getValue().toString());
                    scoreAList.add(Integer.parseInt(snapshot.child("scoreA").getValue().toString()));
                    teamBList.add(snapshot.child("teamB").getValue().toString());
                    scoreBList.add(Integer.parseInt(snapshot.child("scoreB").getValue().toString()));
                    isLiveList.add((boolean)snapshot.child("live").getValue());
                    startedTimeList.add(snapshot.child("startedTime").getValue().toString());
                    isOverList.add((boolean)snapshot.child("over").getValue());
                }

                for(int i = 0; i < fieldNameList.size(); i++) {
                    adminMatchListItems.add(new AdminMatchListItem(matchIDList.get(i), fieldNameList.get(i), gameTimeList.get(i), gameDateList.get(i), teamAList.get(i),
                            teamBList.get(i),scoreAList.get(i),scoreBList.get(i), isLiveList.get(i), tournamentName, startedTimeList.get(i), isOverList.get(i)));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * clearCurrentList clears items inside the data storage for data-redrawing
     */
    public void clearCurrentList() {

        isOverList.clear();
        startedTimeList.clear();
        matchIDList.clear();
        fieldNameList.clear();
        gameTimeList.clear();
        gameDateList.clear();
        teamAList.clear();
        scoreAList.clear();
        teamBList.clear();
        scoreBList.clear();
        isLiveList.clear();

    }
}
