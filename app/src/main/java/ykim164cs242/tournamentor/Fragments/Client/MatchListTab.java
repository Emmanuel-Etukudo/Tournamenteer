package ykim164cs242.tournamentor.Fragments.Client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

import ykim164cs242.tournamentor.Activity.Client.ClientMainActivity;
import ykim164cs242.tournamentor.Activity.Client.ClientTournamentListActivity;
import ykim164cs242.tournamentor.Activity.Client.SelectChannelActivity;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.Adapter.Client.MatchListAdapter;
import ykim164cs242.tournamentor.R;

/**
 * The MatchListTab class represents the tab fragment that displays the match list.
 * It fetches the data from the Firebase real-time database and displays it in the
 * ListView.
 */

public class MatchListTab extends Fragment {

    ListView matchListView;

    private MatchListAdapter adapter;
    private List<MatchListItem> matchListItems;
    private List<String> userStarredMatchList;
    private SwipeRefreshLayout swipeLayout;

    // Storages for parsed JSON data (repoName, userName, description of repositories)
    private List<String> matchIDList;
    private List<String> fieldNameList;
    private List<String> gameTimeList;
    private List<String> gameDateList;
    private List<String> teamAList;
    private List<Integer> scoreAList;
    private List<String> teamBList;
    private List<Integer> scoreBList;
    private List<Boolean> isLiveList;
    private List<Boolean> isStarredList;
    private List<String> startedTimeList;

    private List<String> notificationList;
    private List<Boolean> notificationChangedList;

    private int currenetScoreA;
    private int currentScoreB;

    // Notification
    private int notificationCounter;
    private int starNotificationCounter;
    NotificationCompat.Builder notification;

    // User notification status

    private boolean allMatchSelected;
    private boolean followedMatchSelected;
    private boolean headerOn;
    private boolean vibrationOn;
    private boolean soundOn;

    // Firebase database reference
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tournamentReference;
    DatabaseReference matchReference;
    DatabaseReference userReference;
    DatabaseReference userNotificationReference;
    String deviceID = ClientMainActivity.passedInDeviceID;
    String channelID = ClientMainActivity.passedInChannelID;
    String tournamentName = ClientMainActivity.passedInTournamentName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.tab_match_list, container, false);
        matchListView = (ListView) view.findViewById(R.id.match_list);

        matchListItems = new ArrayList<>();
        matchIDList = new ArrayList<>();
        fieldNameList = new ArrayList<>();
        gameTimeList = new ArrayList<>();
        gameDateList = new ArrayList<>();
        teamAList = new ArrayList<>();
        scoreAList = new ArrayList<>();
        teamBList = new ArrayList<>();
        scoreBList = new ArrayList<>();
        isLiveList = new ArrayList<>();
        isStarredList = new ArrayList<>();
        startedTimeList = new ArrayList<>();

        notificationList = new ArrayList<>();
        notificationChangedList = new ArrayList<>();

        //temp starred storage
        userStarredMatchList = new ArrayList<>();

        String channelId = ClientMainActivity.passedInChannelID;
        final String tournamentName = ClientMainActivity.passedInTournamentName;

        // Set Database reference path

        tournamentReference = rootReference.child("Channels").child(channelId).child("tournaments").child(tournamentName);
        matchReference = tournamentReference.child("games");
        userReference = rootReference.child("Users").child(deviceID).child("starredGames");
        userNotificationReference = rootReference.child("Users").child(deviceID);

        // Swipe Refresh Settings

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {


            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                view.refreshDrawableState();
                swipeLayout.setRefreshing(false);
            }
        });

        // Refresh by clicking
        matchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String matchID = matchIDList.get(position);
                matchReference.child(matchID).child("dummy").setValue(generateRandomString());

            }
        });

        // Initialize user notification settings

        userNotificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // If no notification is selected
                if(!(boolean)dataSnapshot.child("allMatchNotification").getValue() &&
                        !(boolean)dataSnapshot.child("followedMatchNotification").getValue()) {

                    allMatchSelected = false;
                    followedMatchSelected = false;
                    headerOn = false;
                    vibrationOn = false;
                    soundOn = false;

                }

                // sync with database
                if((boolean)dataSnapshot.child("allMatchNotification").getValue()) {

                    allMatchSelected = true;
                    followedMatchSelected = false;

                }

                if((boolean)dataSnapshot.child("followedMatchNotification").getValue()) {
                    allMatchSelected = false;
                    followedMatchSelected = true;
                }

                if((boolean)dataSnapshot.child("popUpOn").getValue()) {
                    headerOn = true;
                } else {
                    headerOn = false;
                }

                if((boolean)dataSnapshot.child("vibrationOn").getValue()) {
                    vibrationOn = true;
                } else {
                    vibrationOn = false;
                }

                if((boolean)dataSnapshot.child("soundOn").getValue()) {
                    soundOn = true;
                } else {
                    soundOn = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Prevents firing the notification on the first access to the screen
        notificationCounter = 0;

        notification = new NotificationCompat.Builder(getContext());

        // Remove when the notification is checked
        notification.setAutoCancel(true);

        DatabaseReference notificationReference = rootReference.child("Channels").child(ClientMainActivity.passedInChannelID).child("tournaments")
                .child(ClientMainActivity.passedInTournamentName);

        adapter = new MatchListAdapter(getContext(), matchListItems);
        matchListView.setAdapter(adapter);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userData) {

                starNotificationCounter = 0;
                userStarredMatchList.clear();

                for(DataSnapshot userDataSnapshot : userData.getChildren()) {

                    userStarredMatchList.add(userDataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { /* BLANK */}

        });

        tournamentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(allMatchSelected) {

                    // Send notification for all matches

                    if(notificationCounter > 0) {
                        notifyGoals(getView(), dataSnapshot.child("notificationBody").getValue().toString(), dataSnapshot.child("notificationTitle").getValue().toString(), headerOn, vibrationOn, soundOn);
                    }

                } else if(followedMatchSelected) {

                    // Only send notification if a game is followed by the user

                    if(notificationCounter > 0 && starNotificationCounter > 0) {

                        for(String str : userStarredMatchList) {

                            String s = dataSnapshot.child("currentlyUpdatedGame").toString();
                            if(dataSnapshot.child("currentlyUpdatedGame").getValue() == null) {
                                break;

                            } else if(dataSnapshot.child("currentlyUpdatedGame").getValue().toString().equalsIgnoreCase(str)) {
                                notifyGoals(getView(), dataSnapshot.child("notificationBody").getValue().toString(), dataSnapshot.child("notificationTitle").getValue().toString(), headerOn, vibrationOn, soundOn);
                                dataSnapshot.child("currentlyUpdatedGame").getRef().removeValue();
                            }

                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        matchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearCurrentList();

                // Increment to prevent running notification on the first run

                notificationCounter++;
                starNotificationCounter++;

                // Fires every single time the channelReference updates in the Real-time DB
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    matchListItems.clear();

                    // ID Format: Date + TeamA + vs + TeamB
                    matchIDList.add(snapshot.child("id").getValue().toString());

                    fieldNameList.add(snapshot.child("fieldName").getValue().toString());
                    gameTimeList.add(snapshot.child("gameTime").getValue().toString());
                    gameDateList.add(snapshot.child("gameDate").getValue().toString());
                    teamAList.add(snapshot.child("teamA").getValue().toString());
                    scoreAList.add(Integer.parseInt(snapshot.child("scoreA").getValue().toString()));
                    teamBList.add(snapshot.child("teamB").getValue().toString());
                    scoreBList.add(Integer.parseInt(snapshot.child("scoreB").getValue().toString()));
                    isLiveList.add((boolean)snapshot.child("live").getValue());
                    startedTimeList.add(snapshot.child("startedTime").getValue().toString());

                    if(userStarredMatchList.contains(snapshot.child("id").getValue().toString())) {
                        isStarredList.add(true);
                    } else {
                        isStarredList.add(false);
                    }
                }

                for(int i = 0; i < fieldNameList.size(); i++) {
                    matchListItems.add(new MatchListItem(matchIDList.get(i), fieldNameList.get(i), gameTimeList.get(i), gameDateList.get(i), teamAList.get(i),
                            teamBList.get(i),scoreAList.get(i),scoreBList.get(i), isLiveList.get(i), isStarredList.get(i), startedTimeList.get(i)));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    /**
     * clearCurrentList clears items inside the data storage for data-redrawing
     */
    public void clearCurrentList() {

        startedTimeList.clear();
        matchIDList.clear();
        fieldNameList.clear();
        gameTimeList.clear();
        gameDateList.clear();
        teamAList.clear();
        scoreAList.clear();
        teamBList.clear();
        scoreBList.clear();
        isLiveList.clear();
        isStarredList.clear();
    }

    /**
     * notifyGoals configures the type of the notification based on the user's setting
     */
    public void notifyGoals(View view, String body, String title, boolean headerOn, boolean vibrateOn, boolean soundOn) {

        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(title);
        notification.setContentText(body);

        // Vibrates only when the setting is on
        if(vibrateOn) notification.setVibrate(new long[] { 1000, 1000 });
        else notification.setVibrate(null);

        // Pop-up only when the settings is on
        if(headerOn) notification.setPriority(NotificationManager.IMPORTANCE_HIGH);
        else notification.setPriority(NotificationManager.IMPORTANCE_DEFAULT);

        // Sound only when the setting is on
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(soundOn) notification.setSound(alarmSound);
        else notification.setSound(null);

        //When notification clicked
        Intent intent = new Intent(getContext(), SelectChannelActivity.class);
        // Give access to intents
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pendingIntent);

        // Builds notification and issues it = sending it out to device
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(4444, notification.build());
    }

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
