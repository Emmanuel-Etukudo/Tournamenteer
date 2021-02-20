package ykim164cs242.tournamentor.ListItem;

/**
 * ScoreTableItem class represents a component of the ListView in the ScoreTable
 */
public class ScoreTableItem {

    private int scoreRank;
    private String playerName;
    private String teamName;
    private int numGoals;

    public ScoreTableItem(String playerName, String teamName, int numGoals) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.numGoals = numGoals;
    }

    public ScoreTableItem(int scoreRank, String playerName, String teamName, int numGoals) {
        this.scoreRank = scoreRank;
        this.playerName = playerName;
        this.teamName = teamName;
        this.numGoals = numGoals;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getNumGoals() {
        return numGoals;
    }

    public void setNumGoals(int numGoals) {
        this.numGoals = numGoals;
    }

    public int getScoreRank() {
        return scoreRank;
    }

    public void setScoreRank(int scoreRank) {
        this.scoreRank = scoreRank;
    }
}
