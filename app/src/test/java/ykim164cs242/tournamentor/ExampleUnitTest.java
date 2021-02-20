package ykim164cs242.tournamentor;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ykim164cs242.tournamentor.Fragments.Client.LeagueTableFragment;
import ykim164cs242.tournamentor.ListItem.LeagueTableItem;
import ykim164cs242.tournamentor.ListItem.ScoreTableItem;
import ykim164cs242.tournamentor.Utils.DateHandler;
import ykim164cs242.tournamentor.Utils.LevenshteinDistance;

import static org.junit.Assert.*;

/**
 * Unit Test for the Tournamentor app. Test includes testing the right data from the
 * Firebase real-time database.
 */
public class ExampleUnitTest {

    private List<ScoreTableItem> scoreTableItems;
    private List<ScoreTableItem> rankedScoreTableItems;

    private List<LeagueTableItem> leagueTableItems;
    private List<LeagueTableItem> rankedLeagueTableItems;


    /**
     * checks if 01/05/2017 is a valid date -> should be invalid (already over)
     */
    @Test
    public void timeDifferenceTest() throws Exception {

        String time1 = "16 : 00";
        String time2 = "19 : 00";

        SimpleDateFormat format = new SimpleDateFormat("HH : mm");
        Date date1 = format.parse(time1);
        Date date2 = format.parse(time2);
        long difference = date2.getTime() - date1.getTime();

        //Long.toString(DateHandler.minuteDifference(timeStamp, matchList.get(position).getStartedTime()))

        assertTrue(TimeUnit.MILLISECONDS.toMinutes(difference) == 180);

        //assertTrue(!dateValidity);
    }

    /**
     * checks if 01/05/2017 is a valid date -> should be invalid (already over)
     */
    @Test
    public void checkDateVSToday1() throws Exception {

        String date = 1 + "-" + 5 + "-" + 2017;

        boolean dateValidity = DateHandler.isNotOver(date);

        assertTrue(!dateValidity);
    }

    /**
     * checks if 01/05/2018 is a valid date -> should be valid (not over)
     */
    @Test
    public void checkDateVSToday2() throws Exception {

        String date = 1 + "-" + 5 + "-" + 2018;

        boolean dateValidity = DateHandler.isNotOver(date);

        assertTrue(dateValidity);
    }

    /**
     * checks if 01/05/2018 is a valid date -> should be valid (today)
     */
    @Test
    public void checkDateVSToday3() throws Exception {

        String date = 12 + "-" + 9 + "-" + 2017;

        boolean dateValidity = DateHandler.isNotOver(date);

        assertTrue(dateValidity);
    }

    /**
     * checks if 01/05/2017 ~ 01/07/2017 is a valid date -> should be valid
     */
    @Test
    public void checkValidDateTest1() throws Exception {

        String start = 1 + "-" + 5 + "-" + 2017;
        String end = 1 + "-" + 7 + "-" + 2017;

        boolean dateValidity = DateHandler.isValidTerm(start, end);

        assertTrue(dateValidity);
    }

    /**
     * checks if 01/05/2017 ~ 01/03/2017 is a valid date -> should be invalid
     */
    @Test
    public void checkValidDateTest2() throws Exception {

        String start = 1 + "-" + 5 + "-" + 2017;
        String end = 1 + "-" + 3 + "-" + 2017;

        boolean dateValidity = DateHandler.isValidTerm(start, end);

        assertTrue(!dateValidity);
    }

    /**
     * checks if 01/05/2017 ~ 01/05/2017 is a valid date -> should be valid
     */
    @Test
    public void checkValidDateTest3() throws Exception {

        String start = 1 + "-" + 5 + "-" + 2017;
        String end = 1 + "-" + 5 + "-" + 2017;

        boolean dateValidity = DateHandler.isValidTerm(start, end);

        assertTrue(dateValidity);
    }

    /**
     * checks if 01/05/2017 ~ 01/12/2016 is a valid date -> should be invalid
     */
    @Test
    public void checkValidDateTest4() throws Exception {

        String start = 1 + "-" + 5 + "-" + 2017;
        String end = 1 + "-" + 12 + "-" + 2016;

        boolean dateValidity = DateHandler.isValidTerm(start, end);

        assertTrue(!dateValidity);
    }

    /**
     * checks if 02/05/2017 ~ 01/12/2017 is a valid date -> should be invalid
     */
    @Test
    public void checkValidDateTest5() throws Exception {

        String start = 2 + "-" + 5 + "-" + 2017;
        String end = 1 + "-" + 12 + "-" + 2017;

        boolean dateValidity = DateHandler.isValidTerm(start, end);

        assertTrue(!dateValidity);
    }

    /////////////////////////////////////////////////////////////// Week 3 Tests Below //////////////////////////////////////////////////////////////////////////////

    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTest() throws Exception {

        String name1 = "Steve";
        String name2 = "stev";

        // Add 5 players

        double similarity = LevenshteinDistance.similarity(name1, name2);

        // Player 2 is on the top rank
        assertTrue(similarity >= 0.46);
    }

    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTestWithLastName() throws Exception {

        String name1 = "yongjin kim";
        String name2 = "yongjin";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.60);
    }


    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTestWithSameLastName() throws Exception {

        String name1 = "bob wilson";
        String name2 = "steve wilson";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.55);
    }

    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTestWithSameFirstName() throws Exception {

        String name1 = "Collin williams";
        String name2 = "Collin Jackson";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.46);
    }

    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTestWithSameNameDifferentCase() throws Exception {

        String name1 = "Bryan";
        String name2 = "brYan";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.46);
    }

    /**
     * stringSimilarityTest tests whether two given names are similar
     */
    @Test
    public void stringSimilarityTestWithSameFirstName2() throws Exception {

        String name1 = "Sean Kim";
        String name2 = "Sean Liu";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.46);
    }

    /**
     * stringSimilarityTest tests whether two different names
     */
    @Test
    public void stringSimilarityTestWithDifferentNames() throws Exception {

        String name1 = "Jack Black";
        String name2 = "Kate Winslet";

        // Add 5 players

        double similarity = LevenshteinDistance.similarity(name1, name2);

        // Player 2 is on the top rank
        assertTrue(similarity <= 0.46);
    }

    /**
     * stringSimilarityTest tests whether two different names
     */
    @Test
    public void stringSimilarityTestSameNameNoSpace() throws Exception {

        String name1 = "yongjinkim";
        String name2 = "yong jin kim";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity >= 0.46);
    }

    /**
     * equalStringSimilarityTest tests whether two given names have similarity of 100%
     */
    @Test
    public void equalStringSimilarityTest() throws Exception {

        String name1 = "Steve";
        String name2 = "Steve";

        double similarity = LevenshteinDistance.similarity(name1, name2);

        assertTrue(similarity == 1.0);
    }

    /**
     * sortLeagueTableTestSamePointsDifferentGoals tests whether the sortLeagueTable function properly
     * sorts the table based on Points and GoalDifference in descending order.
     */
    @Test
    public void sortLeagueTableTestSamePointsDifferentGoals() throws Exception {

        leagueTableItems = new ArrayList<LeagueTableItem>();

        // Last two are goalDifference and Points respectively

        leagueTableItems.add(new LeagueTableItem(0, "Team A", 3, 3, 3, 3, 3, 3, 3, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team B", 3, 3, 3, 3, 3, 3, 9, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team C", 3, 3, 3, 3, 3, 3, -8, 5));
        leagueTableItems.add(new LeagueTableItem(0, "Team D", 3, 3, 3, 3, 3, 3, 2, 1));

        LeagueTableFragment.sortLeagueTable(leagueTableItems);

        assertTrue(leagueTableItems.get(0).getTeamName().equalsIgnoreCase("Team C"));

        assertTrue(leagueTableItems.get(3).getTeamName().equalsIgnoreCase("Team D"));
    }

    /**
     * sortLeagueTableTestSamePointsDifferentGoals tests whether the sortLeagueTable function properly
     * sorts the table based on Points and GoalDifference in descending order.
     */
    @Test
    public void sortLeagueTableTestSamePointsDifferentGoals2() throws Exception {

        leagueTableItems = new ArrayList<LeagueTableItem>();

        // Last two are goalDifference and Points respectively

        leagueTableItems.add(new LeagueTableItem(0, "Team A", 3, 3, 3, 3, 3, 3, -3, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team B", 3, 3, 3, 3, 3, 3, 9, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team C", 3, 3, 3, 3, 3, 3, -8, 5));
        leagueTableItems.add(new LeagueTableItem(0, "Team D", 3, 3, 3, 3, 3, 3, 2, 5));

        LeagueTableFragment.sortLeagueTable(leagueTableItems);

        assertTrue(leagueTableItems.get(0).getTeamName().equalsIgnoreCase("Team D"));

        assertTrue(leagueTableItems.get(3).getTeamName().equalsIgnoreCase("Team A"));
    }

    /////////////////////////////////////////////////////////////// Week 2 Tests Below //////////////////////////////////////////////////////////////////////////////

    /**
     * sortScoreTableTestUpgrade tests whether the sortScoreTable function properly
     * sorts the table based on the number of goals in descending order.
     */
    @Test
    public void sortScoreTableTest() throws Exception {

        scoreTableItems = new ArrayList<ScoreTableItem>();

        // Add 5 players

        scoreTableItems.add(new ScoreTableItem("Player 1", "Test Team", 4));
        scoreTableItems.add(new ScoreTableItem("Player 2", "Test Team", 8));
        scoreTableItems.add(new ScoreTableItem("Player 3", "Test Team", 1));
        scoreTableItems.add(new ScoreTableItem("Player 4", "Test Team", 3));
        scoreTableItems.add(new ScoreTableItem("Player 5", "Test Team", 6));

        sortScoreTable(scoreTableItems);

        // Player 2 is on the top rank
        assertTrue(scoreTableItems.get(0).getPlayerName().equalsIgnoreCase("Player 2"));
    }

    /**
     * Another sortScoreTableTest with more players
     */
    @Test
    public void sortScoreTableTest2() throws Exception {

        scoreTableItems = new ArrayList<ScoreTableItem>();

        // Add 5 players

        scoreTableItems.add(new ScoreTableItem("Player 1", "Test Team", 8));
        scoreTableItems.add(new ScoreTableItem("Player 2", "Test Team", 3));
        scoreTableItems.add(new ScoreTableItem("Player 3", "Test Team", 5));
        scoreTableItems.add(new ScoreTableItem("Player 4", "Test Team", 1));
        scoreTableItems.add(new ScoreTableItem("Player 5", "Test Team", 2));
        scoreTableItems.add(new ScoreTableItem("Player 6", "Test Team", 5));
        scoreTableItems.add(new ScoreTableItem("Player 7", "Test Team", 9));
        scoreTableItems.add(new ScoreTableItem("Player 8", "Test Team", 2));

        sortScoreTable(scoreTableItems);

        // Player 2 is on the top rank
        assertTrue(scoreTableItems.get(0).getPlayerName().equalsIgnoreCase("Player 7"));
    }

    /**
     * rankSortScoreTableTest tests whether the sortScoreTable function properly
     * sorts the table based on the number of goals in descending order and then give them the correct Rank.
     */
    @Test
    public void rankSortScoreTableTest() throws Exception {

        scoreTableItems = new ArrayList<ScoreTableItem>();
        rankedScoreTableItems = new ArrayList<>();

        // Add 5 players

        scoreTableItems.add(new ScoreTableItem("Player 1", "Test Team", 4));
        scoreTableItems.add(new ScoreTableItem("Player 2", "Test Team", 8));
        scoreTableItems.add(new ScoreTableItem("Player 3", "Test Team", 1));
        scoreTableItems.add(new ScoreTableItem("Player 4", "Test Team", 3));
        scoreTableItems.add(new ScoreTableItem("Player 5", "Test Team", 6));

        sortScoreTable(scoreTableItems);

        int currRank = 1;

        // Rank the sorted score table
        for(ScoreTableItem p : scoreTableItems) {
            rankedScoreTableItems.add(new ScoreTableItem(currRank, p.getPlayerName(), p.getTeamName(), p.getNumGoals()));
            currRank++;
        }

        // Player 2 is on the top rank
        assertTrue(rankedScoreTableItems.get(0).getPlayerName().equalsIgnoreCase("Player 2"));
        // Player 2 is rank 1
        assertTrue(rankedScoreTableItems.get(0).getScoreRank() == 1);
        // Player 3 is rank 5
        assertTrue(rankedScoreTableItems.get(4).getScoreRank() == 5);
    }

    /**
     * sortScoreTableTest tests whether the sortScoreTable function properly
     * sorts the table based on Points in descending order.
     */
    @Test
    public void sortLeagueTableTest() throws Exception {

        leagueTableItems = new ArrayList<LeagueTableItem>();

        // Add 5 players

        leagueTableItems.add(new LeagueTableItem(0, "Team A", 3, 3, 3, 3, 3, 3, 3, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team B", 3, 3, 3, 3, 3, 3, 3, 1));
        leagueTableItems.add(new LeagueTableItem(0, "Team C", 3, 3, 3, 3, 3, 3, 3, 6));
        leagueTableItems.add(new LeagueTableItem(0, "Team D", 3, 3, 3, 3, 3, 3, 3, 2));
        leagueTableItems.add(new LeagueTableItem(0, "Team E", 3, 3, 3, 3, 3, 3, 3, 8));

        sortLeagueTable(leagueTableItems);

        // Team E is on the top rank
        assertTrue(leagueTableItems.get(0).getTeamName().equalsIgnoreCase("Team E"));
        // Team B is the last place
        assertTrue(leagueTableItems.get(4).getTeamName().equalsIgnoreCase("Team B"));
    }

    /**
     * sortScoreTableTest with more teams.
     */
    @Test
    public void sortLeagueTableTest2() throws Exception {

        leagueTableItems = new ArrayList<LeagueTableItem>();

        // Add 5 players

        leagueTableItems.add(new LeagueTableItem(0, "Team A", 3, 3, 3, 3, 3, 3, 3, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team B", 3, 3, 3, 3, 3, 3, 3, 1));
        leagueTableItems.add(new LeagueTableItem(0, "Team C", 3, 3, 3, 3, 3, 3, 3, 6));
        leagueTableItems.add(new LeagueTableItem(0, "Team D", 3, 3, 3, 3, 3, 3, 3, 2));
        leagueTableItems.add(new LeagueTableItem(0, "Team E", 3, 3, 3, 3, 3, 3, 3, 0));
        leagueTableItems.add(new LeagueTableItem(0, "Team F", 3, 3, 3, 3, 3, 3, 3, 6));
        leagueTableItems.add(new LeagueTableItem(0, "Team G", 3, 3, 3, 3, 3, 3, 3, 12));
        leagueTableItems.add(new LeagueTableItem(0, "Team H", 3, 3, 3, 3, 3, 3, 3, 8));

        sortLeagueTable(leagueTableItems);

        // Team G is on the top rank
        assertTrue(leagueTableItems.get(0).getTeamName().equalsIgnoreCase("Team G"));
        // Team E is the last place
        assertTrue(leagueTableItems.get(7).getTeamName().equalsIgnoreCase("Team E"));
    }

    /**
     * rankSortLeagueTableTest tests whether the sortScoreTable function properly
     * sorts the table based on Points in descending order and then give them the correct Rank.
     */
    @Test
    public void rankSortLeagueTableTest() throws Exception {

        leagueTableItems = new ArrayList<LeagueTableItem>();
        rankedLeagueTableItems = new ArrayList<>();

        // Add 5 players

        leagueTableItems.add(new LeagueTableItem(0, "Team A", 3, 3, 3, 3, 3, 3, 3, 3));
        leagueTableItems.add(new LeagueTableItem(0, "Team B", 3, 3, 3, 3, 3, 3, 3, 1));
        leagueTableItems.add(new LeagueTableItem(0, "Team C", 3, 3, 3, 3, 3, 3, 3, 6));
        leagueTableItems.add(new LeagueTableItem(0, "Team D", 3, 3, 3, 3, 3, 3, 3, 2));
        leagueTableItems.add(new LeagueTableItem(0, "Team E", 3, 3, 3, 3, 3, 3, 3, 8));

        sortLeagueTable(leagueTableItems);

        int currRank = 1;

        // Rank the sorted league table
        for(LeagueTableItem p : leagueTableItems) {
            rankedLeagueTableItems.add(new LeagueTableItem(currRank, p.getTeamName(), p.getGamesPlayed(), p.getWins(),
                    p.getDraws(), p.getLosses(), p.getGoalScored(), p.getGoalAgasint(), p.getGoalDifference(), p.getPoints()));
            currRank++;
        }

        // Team E is on the top rank
        assertTrue(rankedLeagueTableItems.get(0).getTeamName().equalsIgnoreCase("Team E"));
        // Team B is the last place
        assertTrue(rankedLeagueTableItems.get(4).getTeamName().equalsIgnoreCase("Team B"));
        // Team E is rank 1
        assertTrue(rankedLeagueTableItems.get(0).getRank() == 1);
        // Team B is rank 5
        assertTrue(rankedLeagueTableItems.get(4).getRank() == 5);
    }

    public void sortScoreTable(List<ScoreTableItem> table) {
        Collections.sort(table, new Comparator<ScoreTableItem>(){
            public int compare(ScoreTableItem o1, ScoreTableItem o2){
                return o2.getNumGoals() - o1.getNumGoals();
            }
        });
    }

    public void sortLeagueTable(List<LeagueTableItem> table) {
        Collections.sort(table, new Comparator<LeagueTableItem>(){
            public int compare(LeagueTableItem o1, LeagueTableItem o2){
                return o2.getPoints() - o1.getPoints();
            }
        });
    }



}