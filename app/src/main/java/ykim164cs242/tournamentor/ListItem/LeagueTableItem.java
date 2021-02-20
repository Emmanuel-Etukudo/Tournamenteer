package ykim164cs242.tournamentor.ListItem;

/**
 * LeagueTableItem class represents a component of the ListView in the LeagueTable.
 */
public class LeagueTableItem {

    private int rank;
    private String teamName;
    private int gamesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalScored;
    private int goalAgasint;
    private int goalDifference;
    private int points;

    public LeagueTableItem(int rank, String teamName, int gamesPlayed, int wins, int draws, int losses, int goalScored, int goalAgasint, int goalDifference, int points) {
        this.rank = rank;
        this.teamName = teamName;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalScored = goalScored;
        this.goalAgasint = goalAgasint;
        this.goalDifference = goalDifference;
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public int getGoalAgasint() {
        return goalAgasint;
    }

    public void setGoalAgasint(int goalAgasint) {
        this.goalAgasint = goalAgasint;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
