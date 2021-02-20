package ykim164cs242.tournamentor.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import ykim164cs242.tournamentor.Adapter.Admin.TournamentListAdapter;
import ykim164cs242.tournamentor.InformationStorage.TournamentInfo;
import ykim164cs242.tournamentor.ListItem.TournamentListItem;
import ykim164cs242.tournamentor.R;

/**
 * The AdminTournamentListActivity class represents the Activity that displays the tournament list.
 * The Admin user can add new tournament and manage the tournament by clicking on the
 * item of the ListView.
 */
public class AdminTournamentListActivity extends AppCompatActivity {

    ListView tournamentListView;

    private String startDate;
    private String endDate;
    private List<String> dateArray;
    private FirebaseUser firebaseUser;

    private TournamentListAdapter adapter;
    private List<TournamentListItem> tournamentListItems;

    private Button addButton;
    private TextView emptyText;
    private String channelID;

    // Storages for parsed data from the real-time database
    private List<String> tournamentNameList;
    private List<String> termList;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tournamentTermReference;
    DatabaseReference channelsReference = rootReference.child("Channels");

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tournament_list);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, AdminLoginActivity.class));
        }

        firebaseUser = firebaseAuth.getCurrentUser();

        channelID = firebaseUser.getUid();

        tournamentListView = (ListView) findViewById(R.id.manage_tournament_listview);

        tournamentListItems = new ArrayList<>();
        tournamentNameList = new ArrayList<>();
        termList = new ArrayList<>();
        dateArray =  new ArrayList<>();

        addButton = (Button) findViewById(R.id.add_tournament_button);

        // Move to add screen
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTournamentIntent = new Intent(AdminTournamentListActivity.this, AdminAddTournamentActivity.class);
                addTournamentIntent.putExtra("channelID", firebaseUser.getUid());
                startActivity(addTournamentIntent);
            }
        });

        // ListView item click listener
        // Move to Tournament Managemenet Option Screen
        tournamentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = tournamentNameList.get(position);

                Intent intent = new Intent(AdminTournamentListActivity.this, AdminTournamentMenuActivity.class);
                intent.putExtra("tournamentName", name);
                startActivity(intent);

            }
        });

        adapter = new TournamentListAdapter(this, tournamentListItems);
        tournamentListView.setAdapter(adapter);

    }

    /**
     * Fetches the data from the real-time database, stores into the pre-initialized storages,
     * and displays in the ListView. The onDataChange function runs everytime the data is
     * changed in the real-time database.
     */
    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference theChannelReference = channelsReference.child(firebaseUser.getUid()).child("tournaments");

        theChannelReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the storages for redrawing of the ListView
                tournamentNameList.clear();
                termList.clear();

                // Fires every single time the channelReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    tournamentListItems.clear();
                    tournamentNameList.add(snapshot.child("name").getValue().toString());
                    if(snapshot.child("term").exists()) {
                        termList.add(snapshot.child("term").getValue().toString());
                    }
                }

                for(int i = 0; i < tournamentNameList.size(); i++) {
                    tournamentListItems.add(new TournamentListItem(i, tournamentNameList.get(i), termList.get(i)));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
