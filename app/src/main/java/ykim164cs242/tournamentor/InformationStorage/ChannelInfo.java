package ykim164cs242.tournamentor.InformationStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * UserInfo is a Model for users' profile data.
 * It contains githubID, name, avatarURL, numRepos,
 * numFollowers, numFollowing, bio, webstie, and createdDate
 * of the profile.
 */

public class ChannelInfo {


    private String channelName;
    private String createdDate;

    public ChannelInfo(String channelName, String createdDate) {
        this.channelName = channelName;
        this.createdDate = createdDate;
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
