package ykim164cs242.tournamentor.Adapter.Admin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ykim164cs242.tournamentor.ListItem.ChannelListItem;
import ykim164cs242.tournamentor.ListItem.TournamentListItem;
import ykim164cs242.tournamentor.R;

/**
 * TournamentListAdapter represents an Adapter that can be used in the ListView of tournaments.
 * It puts in the right data inside the activity_admin_tournament_list_item
 */

public class TournamentListAdapter extends BaseAdapter{

    private Context context;
    private List<TournamentListItem> tournamentList;

    public TournamentListAdapter(Context context, List<TournamentListItem> tournamentList) {
        this.context = context;
        this.tournamentList = tournamentList;
    }

    @Override
    public int getCount() {
        return tournamentList.size();
    }

    @Override
    public Object getItem(int position) {
        return tournamentList.get(position);
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

        View view = View.inflate(context, R.layout.activity_admin_tournament_list_item, null);

        TextView tournamentName = (TextView) view.findViewById(R.id.tournament_name_item);
        TextView tournamentTerm = (TextView) view.findViewById(R.id.tournament_term_item);

        // Set Texts for TextViews

        tournamentName.setText(tournamentList.get(position).getTournamentName());
        tournamentTerm.setText(tournamentList.get(position).getCompetitionTerm());
        
        return view;
    }
}
