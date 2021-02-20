package ykim164cs242.tournamentor.InformationStorage;

/**
 * UserInfo is a Model for users' profile data.
 * It contains githubID, name, avatarURL, numRepos,
 * numFollowers, numFollowing, bio, webstie, and createdDate
 * of the profile.
 */

public class TeamInfo {

    private String ID;
    private String teamName;
    private String foundationYear;
    private String captainName;
    private String participatingTournament;

    private int gamesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalScored;
    private int goalAgainst;
    private int goalDifference;
    private int point;


    public TeamInfo(String ID, String teamName, String foundationYear, String captainName, String participatingTournament) {
        this.ID = ID;
        this.teamName = teamName;
        this.foundationYear = foundationYear;
        this.captainName = captainName;
        this.participatingTournament = participatingTournament;

        this.gamesPlayed = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalScored = 0;
        this.goalAgainst = 0;
        this.goalDifference = goalScored - goalAgainst;
        this.point = 0;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getFoundationYear() {
        return foundationYear;
    }

    public void setFoundationYear(String foundationYear) {
        this.foundationYear = foundationYear;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public String getParticipatingTournament() {
        return participatingTournament;
    }

    public void setParticipatingTournament(String participatingTournament) {
        this.participatingTournament = participatingTournament;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getGoalScored() {
        return goalScored;
    }

    public void setGoalScored(int goalScored) {
        this.goalScored = goalScored;
    }

    public int getGoalAgainst() {
        return goalAgainst;
    }

    public void setGoalAgainst(int goalAgainst) {
        this.goalAgainst = goalAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
