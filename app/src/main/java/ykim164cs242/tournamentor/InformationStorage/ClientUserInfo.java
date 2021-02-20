package ykim164cs242.tournamentor.InformationStorage;

import java.util.List;

/**
 * UserInfo is a Model for users' profile data.
 * It contains githubID, name, avatarURL, numRepos,
 * numFollowers, numFollowing, bio, webstie, and createdDate
 * of the profile.
 */

public class ClientUserInfo {

    private String deviceID;
    private List<String> starredGames;

    private boolean allMatchNotification;
    private boolean followedMatchNotification;
    private boolean popUpOn;
    private boolean vibrationOn;
    private boolean soundOn;


    public ClientUserInfo(String deviceID, List<String> starredGames) {
        this.deviceID = deviceID;
        this.starredGames = starredGames;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public List<String> getStarredGames() {
        return starredGames;
    }

    public void setStarredGames(List<String> starredGames) {
        this.starredGames = starredGames;
    }

    public boolean isVibrationOn() {
        return vibrationOn;
    }

    public void setVibrationOn(boolean vibrationOn) {
        this.vibrationOn = vibrationOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isPopUpOn() {
        return popUpOn;
    }

    public void setPopUpOn(boolean popUpOn) {
        this.popUpOn = popUpOn;
    }

    public boolean isAllMatchNotification() {
        return allMatchNotification;
    }

    public void setAllMatchNotification(boolean allMatchNotification) {
        this.allMatchNotification = allMatchNotification;
    }

    public boolean isFollowedMatchNotification() {
        return followedMatchNotification;
    }

    public void setFollowedMatchNotification(boolean followedMatchNotification) {
        this.followedMatchNotification = followedMatchNotification;
    }
}
