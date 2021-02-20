package ykim164cs242.tournamentor.Adapter.Client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.ListItem.TeamListItem;

/**
 * TeamListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_team_list_item.
 */

public class TeamListAdapter extends BaseAdapter{

    private Context context;
    private List<TeamListItem> teamList;

    public TeamListAdapter(Context context, List<TeamListItem> teamList) {
        this.context = context;
        this.teamList = teamList;
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public Object getItem(int position) {
        return teamList.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(context, R.layout.activity_team_list_item, null);

        TextView teamName = (TextView) view.findViewById(R.id.team_name_item);
        TextView foundationYear = (TextView) view.findViewById(R.id.team_foundation_year_item);
        TextView captainName = (TextView) view.findViewById(R.id.team_captain_item);


        // Set Texts for TextViews

        teamName.setText(teamList.get(position).getTeamName());
        foundationYear.setText(teamList.get(position).getFoundationYear());
        captainName.setText(teamList.get(position).getCaptainName());
        
        return view;
    }
}
