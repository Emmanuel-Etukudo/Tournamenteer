package ykim164cs242.tournamentor.Fragments.Client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.Adapter.Client.MatchListAdapter;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.R;

/**
 * The StarredMatchListTab class represents the tab fragment that displays the STARRED match list.
 * It only fetches the data with "isStarred = true" from the Firebase real-time database
 * and displays it in the ListView.
 */
public class StarredMatchListTab extends Fragment {

    ListView matchListView;

    private MatchListAdapter adapter;
    private List<MatchListItem> matchListItems;
    private List<String> userStarredMatchList;

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
    private List<Boolean> isStarredList;
    private List<String> startedTimeList;

    String deviceID = ClientMainActivity.passedInDeviceID;

    // Firebase database reference
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tournamentReference;
    DatabaseReference matchReference;
    DatabaseReference userReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_match_list, container, false);
        matchListView = (ListView) view.findViewById(R.id.match_list);

        matchListItems = new ArrayList<>();
        matchIDList = new ArrayList<>();
        fieldNameList = new ArrayList<>();
        gameTimeList = new ArrayList<>();
        gameDateList = new ArrayList<>();
        teamAList = new ArrayList<>();
        scoreAList = new ArrayList<>();
        teamBList = new ArrayList<>();
        scoreBList = new ArrayList<>();
        isLiveList = new ArrayList<>();
        isStarredList = new ArrayList<>();
        startedTimeList = new ArrayList<>();

        //temp starred storage
        userStarredMatchList = new ArrayList<>();

        String channelId = ClientMainActivity.passedInChannelID;
        String tournamentName = ClientMainActivity.passedInTournamentName;

        tournamentReference = rootReference.child("Channels").child(channelId).child("tournaments").child(tournamentName);
        matchReference = tournamentReference.child("games");
        userReference = rootReference.child("Users").child(deviceID).child("starredGames");

        adapter = new MatchListAdapter(getContext(), matchListItems);
        matchListView.setAdapter(adapter);

        return view;
    }

    /**
     * Fetches the data from the real-time database, stores into the pre-initialized storages,
     * and displays in the ListView. The onDataChange function runs everytime the data is
     * changed in the real-time database.
     */
    @Override
    public void onStart() {

        super.onStart();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userData) {

                userStarredMatchList.clear();

                for(DataSnapshot userDataSnapshot : userData.getChildren()) {

                    userStarredMatchList.add(userDataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        matchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearCurrentList();

                // Fires every single time the channelReference updates in the Real-time DB
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    matchListItems.clear();

                    // ID Format: Date + TeamA + vs + TeamB
                    matchIDList.add(snapshot.child("id").getValue().toString());

                    fieldNameList.add(snapshot.child("fieldName").getValue().toString());
                    gameTimeList.add(snapshot.child("gameTime").getValue().toString());
                    gameDateList.add(snapshot.child("gameDate").getValue().toString());
                    teamAList.add(snapshot.child("teamA").getValue().toString());
                    scoreAList.add(Integer.parseInt(snapshot.child("scoreA").getValue().toString()));
                    teamBList.add(snapshot.child("teamB").getValue().toString());
                    scoreBList.add(Integer.parseInt(snapshot.child("scoreB").getValue().toString()));
                    isLiveList.add((boolean)snapshot.child("live").getValue());
                    startedTimeList.add(snapshot.child("startedTime").getValue().toString());

                    if(userStarredMatchList.contains(snapshot.child("id").getValue().toString())) {
                        isStarredList.add(true);
                    } else {
                        isStarredList.add(false);
                    }
                }

                for(int i = 0; i < fieldNameList.size(); i++) {

                    // Fetches ONLY the match with "isStarred = true"
                    if(isStarredList.get(i) == true) {
                        matchListItems.add(new MatchListItem(matchIDList.get(i), fieldNameList.get(i), gameTimeList.get(i), gameDateList.get(i), teamAList.get(i),
                                teamBList.get(i),scoreAList.get(i),scoreBList.get(i), isLiveList.get(i), isStarredList.get(i), startedTimeList.get(i)));
                    }
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
        isStarredList.clear();
    }



}
