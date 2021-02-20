package ykim164cs242.tournamentor.Activity.Client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.provider.Settings.Secure;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ykim164cs242.tournamentor.Adapter.Client.ChannelListAdapter;
import ykim164cs242.tournamentor.InformationStorage.ClientUserInfo;
import ykim164cs242.tournamentor.InformationStorage.GameInfo;
import ykim164cs242.tournamentor.InformationStorage.TournamentInfo;
import ykim164cs242.tournamentor.ListItem.ChannelListItem;
import ykim164cs242.tournamentor.R;

/**
 * SelectChannelActivity represents a screen of selecting existing tournament channels.
 * The channel information is fetched from the Firebase real-time database and
 * displayed in the ListView of the channel.
 */

public class SelectChannelActivity extends AppCompatActivity {

    ListView channelListView;

    private ChannelListAdapter adapter;
    private List<ChannelListItem> channelListItems;

    // Storages for parsed data from the real-time database
    private List<String> channelIDList;
    private List<String> channelNameList;
    private List<String> termList;

    // Realtime database reference
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference channelReference = rootReference.child("Channels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);

        final String deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        addUserToDB(deviceID);

        channelListView = (ListView) findViewById(R.id.channel_listview);
        channelIDList = new ArrayList<>();
        channelListItems = new ArrayList<>();
        channelNameList = new ArrayList<>();
        termList = new ArrayList<>();

        adapter = new ChannelListAdapter(this, channelListItems);
        channelListView.setAdapter(adapter);

        // onClickHandler for each list item. It moves to the corresponding channel passing the tournamentName data to the next activity

        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent intent = new Intent(SelectChannelActivity.this, ClientTournamentListActivity.class);
               String key = channelIDList.get(position);
               intent.putExtra("channelID", key);
                intent.putExtra("deviceID", deviceID);

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
    protected void onStart() {
        super.onStart();

        channelReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the storages for redrawing of the ListView
                channelNameList.clear();
                termList.clear();

                // Fires every single time the channelReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    channelListItems.clear();
                    channelIDList.add(snapshot.getKey());
                    channelNameList.add(snapshot.child("channelName").getValue().toString());
                    termList.add(snapshot.child("createdDate").getValue().toString());
                }

                for(int i = 0; i < channelNameList.size(); i++) {
                    channelListItems.add(new ChannelListItem(channelIDList.get(i), channelNameList.get(i), termList.get(i)));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addUserToDB(String deviceID) {

        final ClientUserInfo clientUserInfo =  new ClientUserInfo(deviceID, null);

        DatabaseReference userReference = rootReference.child("Users");

        rootReference.child("Users").child(deviceID).setValue(clientUserInfo);

    }
}
