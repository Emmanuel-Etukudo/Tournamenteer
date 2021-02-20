package ykim164cs242.tournamentor.Activity.Client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ykim164cs242.tournamentor.Adapter.Client.TeamListAdapter;
import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.ListItem.TeamListItem;

/**
 * TeamListActivity represents a screen of displaying participating teams of the tournament.
 * The team information is fetched from the Firebase real-time database and displayed
 * in the ListView of the teams.
 */
public class TeamListActivity extends AppCompatActivity {

    ListView teamListView;

    private TeamListAdapter adapter;
    private List<TeamListItem> teamListItems;

    // Storages for parsed data from the real-time database
    private List<String> teamNameList;
    private List<String> foundationYearList;
    private List<String> captainNameList;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tournamentReference;
    DatabaseReference teamReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        String channelId = ClientMainActivity.passedInChannelID;
        String tournamentName = ClientMainActivity.passedInTournamentName;

        teamListView = (ListView) findViewById(R.id.team_listview);

        teamListItems = new ArrayList<>();
        teamNameList = new ArrayList<>();
        foundationYearList = new ArrayList<>();
        captainNameList = new ArrayList<>();

        tournamentReference = rootReference.child("Channels").child(channelId).child("tournaments").child(tournamentName);
        teamReference = tournamentReference.child("teams");

        adapter = new TeamListAdapter(this, teamListItems);
        teamListView.setAdapter(adapter);

    }

    /**
     * Fetches the data from the real-time database, stores into the pre-initialized storages,
     * and displays in the ListView. The onDataChange function runs everytime the data is
     * changed in the real-time database.
     */
    @Override
    protected void onStart() {
        super.onStart();

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
    }
}
