package ykim164cs242.tournamentor.ListItem;

/**
 * AdminTeamListItem class represents a component of the ListView in the Team List Page.
 * It contains the id (or index), teamName, foundationYear, and the captainName.
 */
public class AdminTeamListItem {

    private int id;
    private String teamName;
    private String foundationYear;
    private String captainName;
    private String participatingTournament;

    public AdminTeamListItem(int id, String teamName, String foundationYear, String captainName, String participatingTournament) {
        this.id = id;
        this.teamName = teamName;
        this.foundationYear = foundationYear;
        this.captainName = captainName;
        this.participatingTournament = participatingTournament;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    // Constructor

}
