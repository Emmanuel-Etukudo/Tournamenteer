package ykim164cs242.tournamentor.ListItem;

/**
 * TournamentListItem class represents a component of the ListView in the Tournament List Page.
 * It contains the id (or index), channelName, competitionTerm, hostOrganization names.
 */
public class TournamentListItem {

    private int id;
    private String tournamentName;
    private String competitionTerm;

    // Constructor

    public TournamentListItem(int id, String channelName, String competitionTerm) {
        this.id = id;
        this.tournamentName = channelName;
        this.competitionTerm = competitionTerm;

    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCompetitionTerm() {
        return competitionTerm;
    }

    public void setCompetitionTerm(String competitionTerm) {
        this.competitionTerm = competitionTerm;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
}
