package ykim164cs242.tournamentor.Fragments.Client;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.Adapter.Client.GoalDetailListAdapter;
import ykim164cs242.tournamentor.Adapter.Client.LeagueStandingListAdapter;
import ykim164cs242.tournamentor.Adapter.Client.ScoreTableListAdapter;
import ykim164cs242.tournamentor.InformationStorage.PlayerInfo;
import ykim164cs242.tournamentor.ListItem.GoalDeatilItem;
import ykim164cs242.tournamentor.ListItem.LeagueTableItem;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.ListItem.ScoreTableItem;
import ykim164cs242.tournamentor.R;

/**
 * Fragment for the scorer table. To be implemented in Week 2
 */
public class ScoreTableFragment extends Fragment {

    ListView scoreTableListView;

    private ScoreTableListAdapter adapter;
    private List<ScoreTableItem> scoreTableItems;

    private GoalDetailListAdapter goalDetailListAdapter;
    private List<GoalDeatilItem> scoreDetailItems;

    // Storages for parsed data from the real-time database

    private List<String> playerNameList;
    private List<String> teamNameList;
    private List<Integer> numGoalsList;
    private List<String> infoList;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tournamentReference;
    DatabaseReference scorerReference;

    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;

    public ScoreTableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scorer, container, false);

        scoreTableListView = (ListView) view.findViewById(R.id.score_listview);

        playerNameList = new ArrayList<>();
        numGoalsList = new ArrayList<>();
        teamNameList = new ArrayList<>();
        scoreTableItems = new ArrayList<>();
        scoreDetailItems = new ArrayList<>();
        infoList = new ArrayList<>();

        adapter = new ScoreTableListAdapter(getContext(), scoreTableItems);
        scoreTableListView.setAdapter(adapter);

        tournamentReference = rootReference.child("Channels").child(channelID).child("tournaments").child(tournamentName);
        scorerReference = tournamentReference.child("scorers");

        scoreTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // Displays Team Stats Dialog

                final AlertDialog.Builder goalDeatailBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View scorerInfoView = inflater.inflate(R.layout.dialog_goal_detail_list, null);
                goalDeatailBuilder.setView(scorerInfoView);
                goalDeatailBuilder.setTitle("Scorer Information");
                final AlertDialog scorerInfoDialog = goalDeatailBuilder.create();
                scorerInfoDialog.show();

                final ListView detailListView = (ListView) scorerInfoView.findViewById(R.id.goal_details_listview);

                goalDetailListAdapter = new GoalDetailListAdapter(getContext(), scoreDetailItems);
                detailListView.setAdapter(goalDetailListAdapter);

                final TextView scorerName = (TextView) scorerInfoView.findViewById(R.id.scorer_detail_name);
                final TextView scorerTeam = (TextView) scorerInfoView.findViewById(R.id.scorer_detail_teamname);
                final TextView numGoals = (TextView) scorerInfoView.findViewById(R.id.scorer_totalgoals);

                scorerName.setText(scoreTableItems.get(position).getPlayerName());
                scorerTeam.setText("( " + scoreTableItems.get(position).getTeamName() + " )");
                numGoals.setText("Total Goals : " + Integer.toString( scoreTableItems.get(position).getNumGoals() ));

                String scorerID = scoreTableItems.get(position).getTeamName() + scoreTableItems.get(position).getPlayerName();

                scorerReference.child(scorerID).child("scoreInfo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        infoList.clear();
                        scoreDetailItems.clear();

                        // Fires every single time the channelReference updates in the Real-time DB
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            scoreDetailItems.clear();

                            infoList.add(snapshot.child("details").getValue().toString());
                        }

                        for(int i = 0; i < infoList.size(); i++) {
                            scoreDetailItems.add(new GoalDeatilItem(infoList.get(i)));
                        }

                        goalDetailListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        return view;//inflater.inflate(R.layout.fragment_standings, container, false);
    }

    /**
     * Fetches the data from the real-time database, stores into the pre-initialized storages,
     * and displays in the ListView. The onDataChange function runs everytime the data is
     * changed in the real-time database.
     */
    @Override
    public void onStart() {
        super.onStart();

        scorerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the storages for redrawing of the ListView

                playerNameList.clear();
                teamNameList.clear();
                numGoalsList.clear();

                // Fires every single time the channelReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    scoreTableItems.clear();

                    playerNameList.add(snapshot.child("playerName").getValue().toString());
                    teamNameList.add(snapshot.child("teamName").getValue().toString());
                    numGoalsList.add(Integer.parseInt(snapshot.child("goals").getValue().toString()));

                }

                List<ScoreTableItem> tempScoreTableItem = new ArrayList<ScoreTableItem>();

                for(int i = 0; i < teamNameList.size(); i++) {
                    tempScoreTableItem.add(new ScoreTableItem(i + 1, playerNameList.get(i), teamNameList.get(i), numGoalsList.get(i)));
                }

                // sorts the tempScoreTableItem based on numGoals

                sortScoreTable(tempScoreTableItem);

                // Giving the right Rank for the sorted items

                int currRank = 1;

                for(ScoreTableItem p : tempScoreTableItem) {
                    scoreTableItems.add(new ScoreTableItem(currRank, p.getPlayerName(), p.getTeamName(), p.getNumGoals()));
                    currRank++;
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * sortScoreTable sorts the ScoreTableItem object
     * based on one of its fields (numGoals in this case).
     */
    public void sortScoreTable(List<ScoreTableItem> table) {
        Collections.sort(table, new Comparator<ScoreTableItem>(){
            public int compare(ScoreTableItem o1, ScoreTableItem o2){
                return o2.getNumGoals() - o1.getNumGoals();
            }
        });
    }
}
