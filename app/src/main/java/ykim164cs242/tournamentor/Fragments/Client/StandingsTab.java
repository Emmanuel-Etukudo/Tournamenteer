package ykim164cs242.tournamentor.Fragments.Client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ykim164cs242.tournamentor.Adapter.Client.MatchListAdapter;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.R;

/**
 * StandingsTab contains two fragments that displays the league talbe and score table.
 * To be implemented in Week 2.
 */
public class StandingsTab extends Fragment {

    ListView matchListView;

    private MatchListAdapter adapter;
    private List<MatchListItem> matchListItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_standings, container, false);

        LeagueTableFragment standingsFragment = new LeagueTableFragment();
        ScoreTableFragment scorerFragment = new ScoreTableFragment();

        // Places two fragments

        FragmentManager topFragmentManager = getChildFragmentManager();
        topFragmentManager.beginTransaction()
                .replace(R.id.top_layout, standingsFragment, standingsFragment.getTag())
                .commit();

        FragmentManager bottomFragmentManager = getChildFragmentManager();
        bottomFragmentManager.beginTransaction()
                .replace(R.id.bottom_layout, scorerFragment, scorerFragment.getTag())
                .commit();

        return view;

    }

}
