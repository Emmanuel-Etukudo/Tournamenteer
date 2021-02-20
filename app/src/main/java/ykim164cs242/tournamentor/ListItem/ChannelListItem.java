package ykim164cs242.tournamentor.ListItem;

import java.util.Date;

/**
 * ChannelListItem class represents a component of the ListView in the Channel List Page.
 * It contains the id (or index), channelName, competitionTerm, hostOrganization names.
 */
public class ChannelListItem {

    private String id;
    private String channelName;
    private String createdDate;

    public ChannelListItem(String id, String channelName, String createdDate) {
        this.id = id;
        this.channelName = channelName;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
