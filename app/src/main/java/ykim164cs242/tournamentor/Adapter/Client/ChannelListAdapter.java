package ykim164cs242.tournamentor.Adapter.Client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import ykim164cs242.tournamentor.ListItem.ChannelListItem;
import ykim164cs242.tournamentor.ListItem.TeamListItem;
import ykim164cs242.tournamentor.R;

/**
 * ChannelListAdapter represents an Adapter that can be used in the ListView of channels.
 * It puts in the right data inside the activity_channel_list_items.
 */

public class ChannelListAdapter extends BaseAdapter{

    Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private Context context;
    private List<ChannelListItem> channelList;

    public ChannelListAdapter(Context context, List<ChannelListItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
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

        View view = View.inflate(context, R.layout.activity_channel_list_item, null);

        TextView channelName = (TextView) view.findViewById(R.id.channel_name_item);
        TextView createdDate = (TextView) view.findViewById(R.id.comp_term_item);
        //TextView hostOrganization = (TextView) view.findViewById(R.id.host_name_item);

        // Set Texts for TextViews

        channelName.setText(channelList.get(position).getChannelName());
        createdDate.setText(channelList.get(position).getCreatedDate());
        //hostOrganization.setText(channelList.get(position).getHostOrganization());
        
        return view;
    }
}
