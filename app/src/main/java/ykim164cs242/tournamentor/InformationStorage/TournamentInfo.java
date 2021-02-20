package ykim164cs242.tournamentor.InformationStorage;

import java.util.List;

/**
 * UserInfo is a Model for users' profile data.
 * It contains githubID, name, avatarURL, numRepos,
 * numFollowers, numFollowing, bio, webstie, and createdDate
 * of the profile.
 */

public class TournamentInfo {

    private String name;
    private String term;
    private List<TeamInfo> teams;
    private List<GameInfo> games;


    public TournamentInfo(String name, String term, List<TeamInfo> teams, List<GameInfo> games) {
        this.name = name;
        this.term = term;
        this.teams = teams;
        this.games = games;
    }

    public TournamentInfo(String name, String term) {
        this.name = name;
        this.term = term;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<TeamInfo> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamInfo> teams) {
        this.teams = teams;
    }

    public List<GameInfo> getGames() {
        return games;
    }

    public void setGames(List<GameInfo> games) {
        this.games = games;
    }
}
