package ykim164cs242.tournamentor.Adapter.Client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.ListItem.LeagueTableItem;
import ykim164cs242.tournamentor.ListItem.ScoreTableItem;
import ykim164cs242.tournamentor.R;

/**
 * MatchListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_match_list_items.
 */

public class ScoreTableListAdapter extends BaseAdapter{

    private Context context;
    private List<ScoreTableItem> scoreTableItems;

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;
    DatabaseReference teamReference;

    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;
    String gameID;

    public ScoreTableListAdapter(Context context, List<ScoreTableItem> scoreTableItems) {
        this.context = context;
        this.scoreTableItems = scoreTableItems;
    }

    @Override
    public int getCount() {
        return scoreTableItems.size();
    }

    @Override
    public Object getItem(int position) {
        return scoreTableItems.get(position);
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

        final View view = View.inflate(context, R.layout.activity_score_table_item, null);

        userReference = rootReference.child("Users").child(deviceID).child("starredGames");
        teamReference = rootReference.child("Channels").child(channelID).child("tournaments")
                .child(tournamentName).child("teams");

        TextView rank = (TextView) view.findViewById(R.id.score_rank);
        TextView playerName = (TextView) view.findViewById(R.id.score_name);
        TextView teamName = (TextView) view.findViewById(R.id.score_teamname);
        TextView numGoals = (TextView) view.findViewById(R.id.score_numgoals);

        rank.setText(Integer.toString(scoreTableItems.get(position).getScoreRank()));
        playerName.setText(scoreTableItems.get(position).getPlayerName());
        teamName.setText(scoreTableItems.get(position).getTeamName());
        numGoals.setText(Integer.toString(scoreTableItems.get(position).getNumGoals()));

        return view;
    }

}
