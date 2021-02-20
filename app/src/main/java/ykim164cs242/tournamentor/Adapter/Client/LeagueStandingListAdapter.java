package ykim164cs242.tournamentor.Adapter.Client;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.ListItem.LeagueTableItem;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.R;

/**
 * MatchListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_match_list_items.
 */

public class LeagueStandingListAdapter extends BaseAdapter{

    private Context context;
    private List<LeagueTableItem> leagueTableList;

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;
    DatabaseReference teamReference;

    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;
    String gameID;

    public LeagueStandingListAdapter(Context context, List<LeagueTableItem> leagueTableList) {
        this.context = context;
        this.leagueTableList = leagueTableList;
    }

    @Override
    public int getCount() {
        return leagueTableList.size();
    }

    @Override
    public Object getItem(int position) {
        return leagueTableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Matches each TextView with the corresponding id, and stores the right information
     * by using setText function. It retrieves the correct repositoryName, userName,
     * and description based on the position of the item in the repoList.
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = View.inflate(context, R.layout.activity_league_table_item, null);

        userReference = rootReference.child("Users").child(deviceID).child("starredGames");
        teamReference = rootReference.child("Channels").child(channelID).child("tournaments")
                .child(tournamentName).child("teams");

        TextView ranking = (TextView) view.findViewById(R.id.standing_ranking);
        TextView teamName = (TextView) view.findViewById(R.id.standing_teamname);
        TextView gamesPlayed = (TextView) view.findViewById(R.id.standing_played);
        //TextView wins = (TextView) view.findViewById(R.id.standing_wins);
        //TextView draws = (TextView) view.findViewById(R.id.standing_draw);
        //TextView losses = (TextView) view.findViewById(R.id.standing_losses);
        //TextView goalScored = (TextView) view.findViewById(R.id.standing_goalscored);
        //TextView goalAgainst = (TextView) view.findViewById(R.id.standing_goalagainst);
        TextView goalDifference = (TextView) view.findViewById(R.id.standing_goaldiff);
        TextView points = (TextView) view.findViewById(R.id.standing_points);

        ranking.setText(Integer.toString(leagueTableList.get(position).getRank()));
        teamName.setText(leagueTableList.get(position).getTeamName());
        gamesPlayed.setText(Integer.toString(leagueTableList.get(position).getGamesPlayed()));



        //wins.setText(Integer.toString(leagueTableList.get(position).getWins()));
        //draws.setText(Integer.toString(leagueTableList.get(position).getDraws()));
        //losses.setText(Integer.toString(leagueTableList.get(position).getLosses()));
        //goalScored.setText(Integer.toString(leagueTableList.get(position).getGoalScored()));
        //goalAgainst.setText(Integer.toString(leagueTableList.get(position).getGoalAgasint()));
        goalDifference.setText(Integer.toString(leagueTableList.get(position).getGoalDifference()));
        points.setText(Integer.toString(leagueTableList.get(position).getPoints()));

        return view;
    }

}
