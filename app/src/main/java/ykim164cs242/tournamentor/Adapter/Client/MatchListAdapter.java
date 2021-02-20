package ykim164cs242.tournamentor.Adapter.Client;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.InformationStorage.ClientUserInfo;
import ykim164cs242.tournamentor.InformationStorage.GameInfo;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.ListItem.TournamentListItem;
import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.Utils.DateHandler;

/**
 * MatchListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_match_list_items.
 */

public class MatchListAdapter extends BaseAdapter{

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference matchReference;
    DatabaseReference userReference;
    private Context context;
    private List<MatchListItem> matchList;

    Boolean isStarred;


    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;
    String gameID;

    private String startTime;
    private String diff;

    List<String> starredGameList;

    public MatchListAdapter(Context context, List<MatchListItem> matchList) {
        this.context = context;
        this.matchList = matchList;
    }

    @Override
    public int getCount() {
        return matchList.size();
    }

    @Override
    public Object getItem(int position) {
        return matchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Matches each TextView with the corresponding id, and stores the right information
     * by using setText function. It retrieves the correct repositoryName, userName,
     * and description based on the position of the item in the repoList.
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = View.inflate(context, R.layout.activity_match_list_item, null);
        TextView fieldName = (TextView) view.findViewById(R.id.comp_name);
        TextView gameTime = (TextView) view.findViewById(R.id.game_time);
        TextView gameDate = (TextView) view.findViewById(R.id.game_date);
        TextView teamA = (TextView) view.findViewById(R.id.team_a);
        TextView teamB = (TextView) view.findViewById(R.id.team_b);
        TextView scoreA = (TextView) view.findViewById(R.id.score_a);
        TextView scoreB = (TextView) view.findViewById(R.id.score_b);
        TextView liveStatus = (TextView) view.findViewById(R.id.live_status);

        // Favorite Star

        final ImageView starred = (ImageView) view.findViewById(R.id.starred);

        // Set Texts for TextViews

        fieldName.setText(matchList.get(position).getFieldName());
        gameTime.setText(matchList.get(position).getGameTime());
        gameDate.setText(matchList.get(position).getGameDate());
        teamA.setText(matchList.get(position).getTeamA());
        teamB.setText(matchList.get(position).getTeamB());
        scoreA.setText(Integer.toString(matchList.get(position).getScoreA()));
        scoreB.setText(Integer.toString(matchList.get(position).getScoreB()));

        startTime = matchList.get(position).getStartedTime();

        // dummy data for refreshing the real-time DB

        matchReference = rootReference.child("Channels").child(channelID).child("tournaments")
                .child(tournamentName).child("games").child(matchList.get(position).getId()).child("dummy");

        // needs unique device ID to personalize 'starred' games
        userReference = rootReference.child("Users").child(deviceID).child("starredGames");

        // Live Status : Blink effect if game is live

        if(matchList.get(position).isLive()) {

            // Set Start Time : Start time is recorded when the admin creates a game.

            String timeStamp = new SimpleDateFormat("HH : mm").format(new Date());
            try {
                diff = Long.toString(DateHandler.minuteDifference(startTime, timeStamp));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            liveStatus.setText("Game Started: " + startTime + "   " + "[ LIVE ]");//"Current Game Time : "+ diff + "'");

            //Blink effect

            gameTime.setText(Integer.toString(Integer.parseInt(diff) + 1) + "'");
            ObjectAnimator textBlinker = ObjectAnimator.ofInt(gameTime, "textColor", Color.GREEN, Color.TRANSPARENT);
            textBlinker.setDuration(800);
            textBlinker.setEvaluator(new ArgbEvaluator());
            textBlinker.setRepeatCount(ValueAnimator.INFINITE);
            textBlinker.setRepeatMode(ValueAnimator.REVERSE);
            textBlinker.start();


        } else {
            liveStatus.setText("");
        }

        // Starred Status: Turns star into Yellow if the game is starred

       if(matchList.get(position).isStarred()) {
           starred.setColorFilter(Color.argb(255, 255, 214, 51));
       } else {
           starred.setColorFilter(Color.argb(255, 255, 255, 255)); // White
       }

        starredGameList = new ArrayList<>();


        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchList.get(position).isStarred()) {

                    // when the Colored star is clicked, change color back to white and set the "Starred" value to false in both data and real-time database

                    gameID = matchList.get(position).getId();

                    // Removes the starredGame from the list of the user with the deviceID
                    userReference.child(gameID).getRef().removeValue();
                    matchReference.setValue(generateRandomString());
                    matchList.get(position).setStarred(false);
                    starred.setColorFilter(Color.argb(255, 255, 255, 255)); // White
                }
                else {

                    // when the unColored star is clicked, change color to yellow and set the "Starred" value to true in both data and real-time database

                    gameID = matchList.get(position).getId();
                    starredGameList.add(gameID);

                    // add the ID of the game to the list of starredGames
                    userReference.child(gameID).setValue(gameID);

                    matchReference.setValue(generateRandomString());
                    matchList.get(position).setStarred(true);
                    starred.setColorFilter(Color.argb(255, 255, 214, 51)); // Yellow
                }
            }
        });

        return view;
    }

    // random String generator for adding a dummy data for refreshing the real-time DB
    public static String generateRandomString() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

}
