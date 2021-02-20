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
import ykim164cs242.tournamentor.ListItem.GoalDeatilItem;
import ykim164cs242.tournamentor.ListItem.ScoreTableItem;
import ykim164cs242.tournamentor.R;

/**
 * MatchListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_match_list_items.
 */

public class GoalDetailListAdapter extends BaseAdapter{

    private Context context;
    private List<GoalDeatilItem> goalDeatilItems;

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;
    DatabaseReference teamReference;

    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;


    public GoalDetailListAdapter(Context context, List<GoalDeatilItem> goalDeatilItems) {
        this.context = context;
        this.goalDeatilItems = goalDeatilItems;
    }

    @Override
    public int getCount() {
        return goalDeatilItems.size();
    }

    @Override
    public Object getItem(int position) {
        return goalDeatilItems.get(position);
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

        final View view = View.inflate(context, R.layout.activity_goaldetail_list_item, null);

        TextView detail = (TextView) view.findViewById(R.id.goal_details);
        detail.setText(goalDeatilItems.get(position).getDetail());

        return view;
    }

}
