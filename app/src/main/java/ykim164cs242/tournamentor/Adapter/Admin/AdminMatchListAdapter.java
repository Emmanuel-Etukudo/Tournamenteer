package ykim164cs242.tournamentor.Adapter.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ykim164cs242.tournamentor.Activity.Admin.AdminAddMatchActivity;
import ykim164cs242.tournamentor.Activity.Admin.AdminAddTournamentAddTeamActivity;
import ykim164cs242.tournamentor.Activity.Admin.AdminMatchListActivity;
import ykim164cs242.tournamentor.Activity.Admin.EditAdminActivity;
import ykim164cs242.tournamentor.Activity.Common.StartMenuActivity;
import ykim164cs242.tournamentor.InformationStorage.GameInfo;
import ykim164cs242.tournamentor.InformationStorage.PlayerInfo;
import ykim164cs242.tournamentor.ListItem.AdminMatchListItem;
import ykim164cs242.tournamentor.ListItem.AdminTeamListItem;
import ykim164cs242.tournamentor.ListItem.MatchListItem;
import ykim164cs242.tournamentor.ListItem.ScoreTableItem;
import ykim164cs242.tournamentor.R;
import ykim164cs242.tournamentor.Utils.DateHandler;
import ykim164cs242.tournamentor.Utils.LevenshteinDistance;

/**
 * AdminMatchListAdapter represents an Adapter that can be used in the ListView of matches.
 * It puts in the right data inside the activity_match_list_items.
 */

public class AdminMatchListAdapter extends BaseAdapter{

    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String scoredTeam;
    String rightScorer;
    Boolean scorerExists;

    private String startTime;
    private String diff;

    HashMap<String, Integer> goalHashMap;
    HashMap<String, Integer> teamInfoHashMap;

    private boolean firstGameStartCheck = false;
    private String gameStartedTime;

    private Context context;
    private List<AdminMatchListItem> adminMatchList;

    private String notificationMessage;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    public AdminMatchListAdapter(Context context, List<AdminMatchListItem> adminMatchList) {
        this.context = context;
        this.adminMatchList = adminMatchList;
    }

    @Override
    public int getCount() {
        return adminMatchList.size();
    }

    @Override
    public Object getItem(int position) {
        return adminMatchList.get(position);
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Game ID Formatting
        final String gameID = adminMatchList.get(position).getGameDate() + adminMatchList.get(position).getGameTime() + adminMatchList.get(position).getTeamA() + adminMatchList.get(position).getTeamB();

        // Database references

        final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference liveReference = rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                .child(adminMatchList.get(position).getTournamentName()).child("games").child(gameID).child("live");
        final DatabaseReference tournamentReference = rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                .child(adminMatchList.get(position).getTournamentName());
        final DatabaseReference scorerReference = tournamentReference.child("scorers");
        final DatabaseReference gamesReference = tournamentReference.child("games");
        final DatabaseReference teamReference = tournamentReference.child("teams");

        final View view = View.inflate(context, R.layout.activity_admin_match_list_item, null);
        TextView fieldName = (TextView) view.findViewById(R.id.comp_name);
        final TextView gameTime = (TextView) view.findViewById(R.id.game_time);
        final TextView[] gameDate = {(TextView) view.findViewById(R.id.game_date)};
        final TextView teamA = (TextView) view.findViewById(R.id.team_a);
        final TextView teamB = (TextView) view.findViewById(R.id.team_b);
        final TextView scoreA = (TextView) view.findViewById(R.id.score_a);
        TextView scoreB = (TextView) view.findViewById(R.id.score_b);
        final Switch liveSwitch = (Switch) view.findViewById(R.id.live_status_switch);

        ImageView deleteButton = (ImageView) view.findViewById(R.id.delete_game_button);
        ImageView editButton = (ImageView) view.findViewById(R.id.edit_game_button);

        // HashMap for counting number of goals of scorers
        goalHashMap = new HashMap<>();

        scorerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Fires every single time the scorerReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String playerName = " ";

                    if(snapshot.child("playerName").getValue() != null) {playerName = snapshot.child("playerName").getValue().toString();}
                    else {playerName = "test";}

                    int goals = Integer.parseInt(snapshot.child("goals").getValue().toString());

                    // Correctly maps the number of goals to a scorer
                    goalHashMap.put(playerName, goals);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { /* BLANK */}
        });

        // Store all team status into this HashMap to store the correct current data

        teamInfoHashMap = new HashMap<>();

        teamReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Fires every single time the scorerReference updates in the Real-time DB
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    int wins = Integer.parseInt(snapshot.child("wins").getValue().toString());
                    int losses = Integer.parseInt(snapshot.child("losses").getValue().toString());
                    int draws = Integer.parseInt(snapshot.child("draws").getValue().toString());
                    int GS = Integer.parseInt(snapshot.child("goalScored").getValue().toString());
                    int GA = Integer.parseInt(snapshot.child("goalAgainst").getValue().toString());
                    int GD = Integer.parseInt(snapshot.child("goalDifference").getValue().toString());
                    int played = Integer.parseInt(snapshot.child("gamesPlayed").getValue().toString());
                    int pts = Integer.parseInt(snapshot.child("point").getValue().toString());

                    // Correctly maps info to the keys
                    teamInfoHashMap.put(snapshot.getKey() + "wins", wins);
                    teamInfoHashMap.put(snapshot.getKey() + "losses", losses);
                    teamInfoHashMap.put(snapshot.getKey() + "draws", draws);
                    teamInfoHashMap.put(snapshot.getKey() + "GS", GS);
                    teamInfoHashMap.put(snapshot.getKey() + "GD", GD);
                    teamInfoHashMap.put(snapshot.getKey() + "GA", GA);
                    teamInfoHashMap.put(snapshot.getKey() + "played", played);
                    teamInfoHashMap.put(snapshot.getKey() + "pts", pts);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { /* BLANK */}
        });

        // Manage buttons

        Button updateScoreButton = (Button) view.findViewById(R.id.update_score_button);
        Button endGameButton = (Button) view.findViewById(R.id.end_game_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder yesNoBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View yesNoDialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                yesNoBuilder.setView(yesNoDialogView);
                yesNoBuilder.setTitle("Are you sure?");
                final AlertDialog yesNoDialog = yesNoBuilder.create();
                yesNoDialog.show();

                final TextView question = (TextView) yesNoDialogView.findViewById(R.id.yes_no_question);
                question.setText("Delete this match?");

                Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Do action

                        rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                                .child(adminMatchList.get(position).getTournamentName()).child("games").child(gameID).getRef().removeValue();
                        yesNoDialog.dismiss();
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        yesNoDialog.dismiss();

                    }
                });


            }
        });


        // Edit Game Button - only editable when the game is not LIVE
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean[] editedLiveStatus = new boolean[1];
                editedLiveStatus[0] = adminMatchList.get(position).isLive();

                if(!editedLiveStatus[0]) {

                    final AlertDialog.Builder editBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View editDialogView = inflater.inflate(R.layout.dialog_edit_match, null);

                    editBuilder.setView(editDialogView);
                    editBuilder.setTitle("Edit Game Info");
                    final AlertDialog editDialog = editBuilder.create();
                    editDialog.show();

                    final EditText editFieldName = (EditText) editDialogView.findViewById(R.id.edit_field_name);
                    final TextView editMatchDate = (TextView) editDialogView.findViewById(R.id.edit_match_date);
                    final TextView editMatchTime = (TextView) editDialogView.findViewById(R.id.edit_match_time);
                    final EditText editTeamA = (EditText) editDialogView.findViewById(R.id.edit_team_A);
                    final EditText editTeamB = (EditText) editDialogView.findViewById(R.id.edit_team_B);
                    final EditText editScoreA = (EditText) editDialogView.findViewById(R.id.edit_score_A);
                    final EditText editScoreB = (EditText) editDialogView.findViewById(R.id.edit_score_B);
                    final Button editButton = (Button) editDialogView.findViewById(R.id.edit_game_button);

                    editFieldName.setText(adminMatchList.get(position).getFieldName());

                    editMatchTime.setText(adminMatchList.get(position).getGameTime());

                    editMatchDate.setText(adminMatchList.get(position).getGameDate());
                    editTeamA.setText(adminMatchList.get(position).getTeamA());
                    editTeamB.setText(adminMatchList.get(position).getTeamB());
                    editScoreA.setText(Integer.toString(adminMatchList.get(position).getScoreA()));
                    editScoreB.setText(Integer.toString(adminMatchList.get(position).getScoreB()));

                    final String[] editedDate = new String[1];
                    final String[] editedTime = new String[1];
                    final String[] editedFieldName = new String[1];
                    final String[] editedTeamA = new String[1];
                    final String[] editedTeamB = new String[1];
                    final int[] editedScoreA = new int[1];
                    final int[] editedScoreB = new int[1];


                    editedTime[0] = adminMatchList.get(position).getGameTime();
                    editedDate[0] = adminMatchList.get(position).getGameDate();
                    editedScoreA[0] = adminMatchList.get(position).getScoreA();
                    editedScoreB[0] = adminMatchList.get(position).getScoreB();


                    // Date selector dialog

                    editMatchDate.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog dialog = new DatePickerDialog(v.getRootView().getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                    });

                    // Time selector dialog

                    editMatchTime.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {

                            Calendar calendar = Calendar.getInstance();
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int min = calendar.get(Calendar.MINUTE);

                            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getRootView().getContext(), timeSetListener, hour, min, true);
                            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            timePickerDialog.show();

                        }
                    });

                    dateSetListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month += 1;
                            String date = month + "-" + dayOfMonth + "-" + year;
                            editMatchDate.setText(date);

                            editedDate[0] = date;

                        }
                    };

                    timeSetListener = new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            String time = hourOfDay + " : " + minute;
                            editMatchTime.setText(time);
                            editedTime[0] = time;

                        }
                    };


                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            editedFieldName[0] = editFieldName.getText().toString();
                            editedTeamA[0] = editTeamA.getText().toString();
                            editedTeamB[0] = editTeamB.getText().toString();
                            editedScoreA[0] = Integer.parseInt(editScoreA.getText().toString());
                            editedScoreB[0] = Integer.parseInt(editScoreB.getText().toString());

                            // Game ID Format is date + time + teamA + teamB
                            String gameID = editedDate[0] + editedTime[0] + editedTeamA[0] + editedTeamB[0]; // + gameTime

                            // See if the id is different
                            if(!adminMatchList.get(position).getId().equalsIgnoreCase(gameID)) {
                                rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments").child(adminMatchList.get(position).getTournamentName()).child("games").child(adminMatchList.get(position).getId()).getRef().removeValue();
                            }

                            // Adds the gameInfo to the Databse
                            GameInfo gameInfo = new GameInfo(gameID, editedFieldName[0], editedTime[0], editedDate[0], editedTeamA[0], editedTeamB[0], Integer.toString(editedScoreA[0]), Integer.toString(editedScoreB[0]), false, adminMatchList.get(position).getTournamentName(), "0", adminMatchList.get(position).isOver());
                            rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments").child(adminMatchList.get(position).getTournamentName()).child("games").child(gameID).setValue(gameInfo);

                            // Passes in the tournamentName
                            //intent.putExtra("tournamentName", tournamentName);
                            //startActivity(intent);

                            editDialog.dismiss();

                        }
                    });
                } else {
                    Toast.makeText(v.getRootView().getContext(), "You cannot edit a live game.", Toast.LENGTH_LONG).show();
                }
            }
        });

        updateScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adminMatchList.get(position).isOver()) {

                    Toast.makeText(context, "This game is already over! Use the EDIT button to make necessary changes.", Toast.LENGTH_SHORT).show();


                } else if(!adminMatchList.get(position).isLive()) {

                    Toast.makeText(context, "The game has to be LIVE to get its score updated.", Toast.LENGTH_SHORT).show();


                } else {


                    // Opens an update score dialog

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View dialogView = inflater.inflate(R.layout.dialog_update_score, null);

                    Button udpdateScoreButton = (Button) dialogView.findViewById(R.id.update_score_button);

                    RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group_scored);
                    RadioButton teamAradio = (RadioButton) dialogView.findViewById(R.id.teamA_score);
                    RadioButton teamBradio = (RadioButton) dialogView.findViewById(R.id.teamB_score);

                    teamAradio.setText(adminMatchList.get(position).getTeamA());
                    teamBradio.setText(adminMatchList.get(position).getTeamB());

                    // DB reference for scores
                    final DatabaseReference gameReference = rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments")
                            .child(adminMatchList.get(position).getTournamentName()).child("games").child(gameID);

                    // Select which team scored

                    teamAradio.setOnClickListener(
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    scoredTeam = adminMatchList.get(position).getTeamA();
                                };
                            }
                    );

                    teamBradio.setOnClickListener(
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    scoredTeam = adminMatchList.get(position).getTeamB();
                                }
                            }
                    );

                    // Input name of the scorer

                    final EditText inputScorer = (EditText) dialogView.findViewById(R.id.input_scorer);

                    builder.setView(dialogView);
                    builder.setTitle("Update Score");
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    udpdateScoreButton.setOnClickListener(
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {


                                    final String scorer = inputScorer.getText().toString();

                                    // Update score of team A

                                    // If the input name is similar to the one already in the scorer list
                                    if(scoredTeam.equalsIgnoreCase(adminMatchList.get(position).getTeamA())) {

                                        if(similarNameExists(goalHashMap, scorer).first) {

                                            final AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getRootView().getContext());
                                            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                            View dialogView2 = inflater.inflate(R.layout.dialog_select_scorer, null);
                                            builder2.setView(dialogView2);
                                            builder2.setTitle("Were you looking for...");

                                            Button okButton = (Button) dialogView2.findViewById(R.id.ok_button_scorer);

                                            RadioGroup radioGroup = (RadioGroup) dialogView2.findViewById(R.id.radio_group_scorer_select);
                                            RadioButton scorer1Radio = (RadioButton) dialogView2.findViewById(R.id.scorer1);
                                            RadioButton scorer2Radio = (RadioButton) dialogView2.findViewById(R.id.scorer2);

                                            scorer1Radio.setText(similarNameExists(goalHashMap, scorer).second);

                                            // Select the right scorer

                                            // Existing Scorer
                                            scorer1Radio.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            rightScorer = similarNameExists(goalHashMap, scorer).second;
                                                            scorerExists = true;

                                                        };
                                                    }
                                            );

                                            // New Scorer
                                            scorer2Radio.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            rightScorer = scorer;
                                                            scorerExists = false;
                                                        }
                                                    }
                                            );

                                            final AlertDialog dialog2 = builder2.create();
                                            dialog2.show();

                                            okButton.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {

                                                            if(scorerExists) {

                                                                int currentGoal = goalHashMap.get(rightScorer);
                                                                currentGoal++;
                                                                scorerReference.child(adminMatchList.get(position).getTeamA() + rightScorer).child("goals").setValue(currentGoal);

                                                                // Update scoring details

                                                                scorerReference.child(adminMatchList.get(position).getTeamA() + rightScorer).child("scoreInfo")
                                                                        .child(adminMatchList.get(position).getGameDate())
                                                                        .child("details")
                                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamB() + " on " +
                                                                        adminMatchList.get(position).getGameDate());


                                                            } else {

                                                                PlayerInfo newScorer = new PlayerInfo(adminMatchList.get(position).getTeamA() + rightScorer, rightScorer, adminMatchList.get(position).getTeamA(), 1);
                                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).setValue(newScorer);

                                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).child("scoreInfo")
                                                                        .child(adminMatchList.get(position).getGameDate())
                                                                        .child("details")
                                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamB() + " on " +
                                                                                adminMatchList.get(position).getGameDate());

                                                            }

                                                            int currentScore = adminMatchList.get(position).getScoreA();
                                                            currentScore++;
                                                            gameReference.child("scoreA").setValue(Integer.toString(currentScore));

                                                            // Format for notification message

                                                            tournamentReference.child("notificationTitle").setValue(setNotificationTitle(
                                                                    adminMatchList.get(position).getTeamA(), adminMatchList.get(position).getTeamB(), adminMatchList.get(position).getScoreA() + 1,
                                                                    adminMatchList.get(position).getScoreB(), scorer));

                                                            tournamentReference.child("notificationBody").setValue(setNotificationBody(adminMatchList.get(position).getTeamA(), scorer));
                                                            tournamentReference.child("currentlyUpdatedGame").setValue(adminMatchList.get(position).getId());


                                                            dialog2.dismiss();
                                                        }
                                                    }
                                            );

                                            // No similar name exists

                                        } else {

                                            final PlayerInfo scorerInfo = new PlayerInfo(scorer + adminMatchList.get(position).getTeamA(), (inputScorer.getText().toString()), adminMatchList.get(position).getTeamA(), 1);
                                            int currentScore = adminMatchList.get(position).getScoreA();
                                            currentScore++;

                                            gameReference.child("scoreA").setValue(Integer.toString(currentScore));

                                            gameReference.child("scorers").child(scorer).setValue(scorerInfo);

                                            scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).child("scoreInfo")
                                                    .child(adminMatchList.get(position).getGameDate())
                                                    .child("details")
                                                    .setValue("Scored against " + adminMatchList.get(position).getTeamB() + " on " +
                                                            adminMatchList.get(position).getGameDate());

                                            // if the scorer is already in the HashMap -> update number of goals

                                            if(goalHashMap.containsKey(scorer)) {

                                                int currentGoal = goalHashMap.get(scorer);
                                                currentGoal++;
                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).child("goals").setValue(currentGoal);

                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).child("scoreInfo")
                                                        .child(adminMatchList.get(position).getGameDate())
                                                        .child("details")
                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamB() + " on " +
                                                                adminMatchList.get(position).getGameDate());

                                            } else {

                                                // if the scorer is not in the HashMap -> add a new scorer with goal = 1

                                                PlayerInfo newScorer = new PlayerInfo(adminMatchList.get(position).getTeamA() + scorer, scorer, adminMatchList.get(position).getTeamA(), 1);
                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).setValue(newScorer);

                                                scorerReference.child(adminMatchList.get(position).getTeamA() + scorer).child("scoreInfo")
                                                        .child(adminMatchList.get(position).getGameDate())
                                                        .child("details")
                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamB() + " on " +
                                                                adminMatchList.get(position).getGameDate());
                                            }

                                            tournamentReference.child("notificationTitle").setValue(setNotificationTitle(
                                                    adminMatchList.get(position).getTeamA(), adminMatchList.get(position).getTeamB(), adminMatchList.get(position).getScoreA() + 1,
                                                    adminMatchList.get(position).getScoreB(), scorer));

                                            tournamentReference.child("notificationBody").setValue(setNotificationBody(adminMatchList.get(position).getTeamA(), scorer));
                                            tournamentReference.child("currentlyUpdatedGame").setValue(adminMatchList.get(position).getId());

                                        }


                                        // Update score of teamB

                                    } else if(scoredTeam.equalsIgnoreCase(adminMatchList.get(position).getTeamB())) {

                                        // If the input name is similar to the one already in the scorer list

                                        if(similarNameExists(goalHashMap, scorer).first) {

                                            final AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getRootView().getContext());
                                            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                            View dialogView2 = inflater.inflate(R.layout.dialog_select_scorer, null);
                                            builder2.setView(dialogView2);
                                            builder2.setTitle("Were you looking for...");

                                            Button okButton = (Button) dialogView2.findViewById(R.id.ok_button_scorer);

                                            RadioGroup radioGroup = (RadioGroup) dialogView2.findViewById(R.id.radio_group_scorer_select);
                                            RadioButton scorer1Radio = (RadioButton) dialogView2.findViewById(R.id.scorer1);
                                            RadioButton scorer2Radio = (RadioButton) dialogView2.findViewById(R.id.scorer2);

                                            scorer1Radio.setText(similarNameExists(goalHashMap, scorer).second);

                                            // Select the right scorer

                                            // Existing Scorer
                                            scorer1Radio.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            rightScorer = similarNameExists(goalHashMap, scorer).second;
                                                            scorerExists = true;

                                                        };
                                                    }
                                            );

                                            // New Scorer
                                            scorer2Radio.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            rightScorer = scorer;
                                                            scorerExists = false;

                                                        }
                                                    }
                                            );

                                            final AlertDialog dialog2 = builder2.create();
                                            dialog2.show();

                                            okButton.setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {

                                                            if(scorerExists) {

                                                                int currentGoal = goalHashMap.get(rightScorer);
                                                                currentGoal++;
                                                                scorerReference.child(adminMatchList.get(position).getTeamB() + rightScorer).child("goals").setValue(currentGoal);

                                                                scorerReference.child(adminMatchList.get(position).getTeamB() + rightScorer).child("scoreInfo")
                                                                        .child(adminMatchList.get(position).getGameDate())
                                                                        .child("details")
                                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamA() + " on " +
                                                                                adminMatchList.get(position).getGameDate());

                                                            } else {

                                                                PlayerInfo newScorer = new PlayerInfo(adminMatchList.get(position).getTeamB() + rightScorer, rightScorer, adminMatchList.get(position).getTeamB(), 1);
                                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).setValue(newScorer);

                                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).child("scoreInfo")
                                                                        .child(adminMatchList.get(position).getGameDate())
                                                                        .child("details")
                                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamA() + " on " +
                                                                                adminMatchList.get(position).getGameDate());

                                                            }

                                                            int currentScore = adminMatchList.get(position).getScoreB();
                                                            currentScore++;
                                                            gameReference.child("scoreB").setValue(Integer.toString(currentScore));

                                                            tournamentReference.child("notificationTitle").setValue(setNotificationTitle(
                                                                    adminMatchList.get(position).getTeamA(), adminMatchList.get(position).getTeamB(), adminMatchList.get(position).getScoreA(),
                                                                    adminMatchList.get(position).getScoreB() + 1, scorer));

                                                            tournamentReference.child("notificationBody").setValue(setNotificationBody(adminMatchList.get(position).getTeamB(), scorer));
                                                            tournamentReference.child("currentlyUpdatedGame").setValue(adminMatchList.get(position).getId());


                                                            dialog2.dismiss();
                                                        }
                                                    }
                                            );


                                            // No similar name exists
                                        } else {

                                            final PlayerInfo scorerInfo = new PlayerInfo(scorer + adminMatchList.get(position).getTeamB(), (inputScorer.getText().toString()), adminMatchList.get(position).getTeamB(), 1);
                                            int currentScore = adminMatchList.get(position).getScoreB();
                                            currentScore++;
                                            gameReference.child("scoreB").setValue(Integer.toString(currentScore));

                                            gameReference.child("scorers").child(scorer).setValue(scorerInfo);

                                            // if the scorer is already in the HashMap -> update number of goals

                                            if(goalHashMap.containsKey(scorer)) {

                                                int currentGoal = goalHashMap.get(scorer);
                                                currentGoal++;
                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).child("goals").setValue(currentGoal);

                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).child("scoreInfo")
                                                        .child(adminMatchList.get(position).getGameDate())
                                                        .child("details")
                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamA() + " on " +
                                                                adminMatchList.get(position).getGameDate());

                                            } else {

                                                // if the scorer is not in the HashMap -> add a new scorer with goal = 1

                                                PlayerInfo newScorer = new PlayerInfo(adminMatchList.get(position).getTeamB() + scorer, scorer, adminMatchList.get(position).getTeamB(), 1);
                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).setValue(newScorer);

                                                scorerReference.child(adminMatchList.get(position).getTeamB() + scorer).child("scoreInfo")
                                                        .child(adminMatchList.get(position).getGameDate())
                                                        .child("details")
                                                        .setValue("Scored against " + adminMatchList.get(position).getTeamA() + " on " +
                                                                adminMatchList.get(position).getGameDate());
                                            }

                                            tournamentReference.child("notificationTitle").setValue(setNotificationTitle(
                                                    adminMatchList.get(position).getTeamA(), adminMatchList.get(position).getTeamB(), adminMatchList.get(position).getScoreA(),
                                                    adminMatchList.get(position).getScoreB() + 1, scorer));

                                            tournamentReference.child("notificationBody").setValue(setNotificationBody(adminMatchList.get(position).getTeamB(), scorer));
                                            tournamentReference.child("currentlyUpdatedGame").setValue(adminMatchList.get(position).getId());

                                        }
                                    }

                                    dialog.dismiss();

                                };
                            }
                    );

                    // Determine who is winning. It immediately updates the win, draw, loss since it's showing a live game.

                }
            }
        });

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adminMatchList.get(position).isOver()) {

                    Toast.makeText(context, "This game is already over!", Toast.LENGTH_SHORT).show();
                } else {


                    // End game: change time to FT (Full Time) and set live to false


                    final AlertDialog.Builder yesNoBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    final View yesNoDialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                    yesNoBuilder.setView(yesNoDialogView);
                    yesNoBuilder.setTitle("Are you sure?");
                    final AlertDialog yesNoDialog = yesNoBuilder.create();
                    yesNoDialog.show();

                    final TextView question = (TextView) yesNoDialogView.findViewById(R.id.yes_no_question);
                    question.setText("The Game cannot become LIVE again once it's ended.");

                    Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                    Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                gamesReference.child(gameID).child("over").setValue(true);

                                // Do action
                                adminMatchList.get(position).setLive(false);
                                liveReference.setValue(false);
                                gamesReference.child(gameID).child("gameTime").setValue("FT");
                                gameTime.setText("FT");

                                // Update League Table

                                // If team A won
                                if (adminMatchList.get(position).getScoreA() > adminMatchList.get(position).getScoreB()) {

                                    String teamA = adminMatchList.get(position).getTeamA();
                                    String teamB = adminMatchList.get(position).getTeamB();
                                    int scoreA = adminMatchList.get(position).getScoreA();
                                    int scoreB = adminMatchList.get(position).getScoreB();

                                    // Update Team A
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("wins").setValue((teamInfoHashMap.get(teamA + "wins")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamA + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalScored").setValue((teamInfoHashMap.get(teamA + "GS")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalAgainst").setValue((teamInfoHashMap.get(teamA + "GA")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalDifference").setValue(teamInfoHashMap.get(teamA + "GD")  + (scoreA - scoreB));

                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("point").setValue((teamInfoHashMap.get(teamA + "pts")) + 3);

                                    // Update Team B
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("losses").setValue((teamInfoHashMap.get(teamB + "losses")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamB + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalScored").setValue((teamInfoHashMap.get(teamB + "GS")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalAgainst").setValue((teamInfoHashMap.get(teamB + "GA")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalDifference").setValue(teamInfoHashMap.get(teamB + "GD")  + (scoreB - scoreA));

                                } else if(adminMatchList.get(position).getScoreA() < adminMatchList.get(position).getScoreB()) {

                                    // team B won
                                    String teamA = adminMatchList.get(position).getTeamA();
                                    String teamB = adminMatchList.get(position).getTeamB();
                                    int scoreA = adminMatchList.get(position).getScoreA();
                                    int scoreB = adminMatchList.get(position).getScoreB();

                                    // Update Team A
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("losses").setValue((teamInfoHashMap.get(teamA + "losses")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamA + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalScored").setValue((teamInfoHashMap.get(teamA + "GS")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalAgainst").setValue((teamInfoHashMap.get(teamA + "GA")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalDifference").setValue(teamInfoHashMap.get(teamA + "GD")  + (scoreA - scoreB));

                                    // Update Team B

                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("wins").setValue((teamInfoHashMap.get(teamB + "wins")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamB + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalScored").setValue((teamInfoHashMap.get(teamB + "GS")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalAgainst").setValue((teamInfoHashMap.get(teamB + "GA")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalDifference").setValue(teamInfoHashMap.get(teamB + "GD")  + (scoreB - scoreA));
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("point").setValue((teamInfoHashMap.get(teamB + "pts")) + 3);

                                } else if(adminMatchList.get(position).getScoreA() == adminMatchList.get(position).getScoreB()) {

                                    // If game is tied
                                    String teamA = adminMatchList.get(position).getTeamA();
                                    String teamB = adminMatchList.get(position).getTeamB();
                                    int scoreA = adminMatchList.get(position).getScoreA();
                                    int scoreB = adminMatchList.get(position).getScoreB();

                                    // Update Team A
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("draws").setValue((teamInfoHashMap.get(teamA + "draws")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamA + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalScored").setValue((teamInfoHashMap.get(teamA + "GS")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("goalAgainst").setValue((teamInfoHashMap.get(teamA + "GA")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamA()).child("point").setValue((teamInfoHashMap.get(teamA + "pts")) + 1);

                                    // Update Team B

                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("draws").setValue((teamInfoHashMap.get(teamB + "draws")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("gamesPlayed").setValue((teamInfoHashMap.get(teamB + "played")) + 1);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalScored").setValue((teamInfoHashMap.get(teamB + "GS")) + scoreB);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("goalAgainst").setValue((teamInfoHashMap.get(teamB + "GA")) + scoreA);
                                    teamReference.child(adminMatchList.get(position).getTeamB()).child("point").setValue((teamInfoHashMap.get(teamB + "pts")) + 1);

                                }

                            yesNoDialog.dismiss();
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            yesNoDialog.dismiss();

                        }
                    });



                }


            }
         });

        // Set Texts for TextViews

        fieldName.setText(adminMatchList.get(position).getFieldName());
        gameTime.setText(adminMatchList.get(position).getGameTime());
        gameDate[0].setText(adminMatchList.get(position).getGameDate());
        teamA.setText(adminMatchList.get(position).getTeamA());
        teamB.setText(adminMatchList.get(position).getTeamB());
        scoreA.setText(Integer.toString(adminMatchList.get(position).getScoreA()));
        scoreB.setText(Integer.toString(adminMatchList.get(position).getScoreB()));

        final DatabaseReference matchReference = tournamentReference.child("Test Tournament").child("Matches").child(adminMatchList.get(position).getGameDate() + " "
                + adminMatchList.get(position).getTeamA() + " vs "
        + adminMatchList.get(position).getTeamB()).child("isStarred");

        // Live Status: Control the switch (set based on live status) Does not reset when changing other switch buttons

        if(adminMatchList.get(position).isLive()) {

            liveSwitch.setChecked(true);

        } else {

            liveSwitch.setChecked(false);

    }

    // Live Status : Blink effect if game is live

        liveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    if(adminMatchList.get(position).isOver()) {

                        Toast.makeText(context, "This game is already over!", Toast.LENGTH_SHORT).show();
                        liveSwitch.setChecked(false);

                    } else {

                        if(firstGameStartCheck == false) {
                            String timeStamp = new SimpleDateFormat("HH : mm").format(new Date());
                            gamesReference.child(gameID).child("startedTime").setValue(timeStamp);
                            gameStartedTime = timeStamp;
                            firstGameStartCheck = true;
                        }

                        // If game live is checked
                        Toast.makeText(context, "Game Begins!", Toast.LENGTH_SHORT).show();
                        adminMatchList.get(position).setLive(true);
                        liveReference.setValue(true);

                        // Set start time

                        String currTime = new SimpleDateFormat("HH:mm").format(new Date());

                        gameTime.setTextSize(14);
                        gameTime.setText("Game Started : " + "\n" + gameStartedTime);
                        //gamesReference.child(gameID).child("currGameTime").setValue(currTime);

                    }


                } else {
                    // Pause game
                    Toast.makeText(context, "Game is now paused. Press 'End Game' to officially end the game", Toast.LENGTH_SHORT).show();
                    adminMatchList.get(position).setLive(false);
                    liveReference.setValue(false);
                }
            }
        });
        return view;
    }

    /*
    * This function checks whether a similar name exists in the scorer database
    */
    private Pair<Boolean, String> similarNameExists(HashMap<String, Integer> scorerHashMap, String inputName) {

        Iterator it = scorerHashMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            // If the inputName is similar to one of the names in the database (or HashMap that contains the info of the DB)
            if(LevenshteinDistance.similarity((String) pair.getKey(), inputName) >= 0.46

                    // 0.46 because it has been tested in the Unit Test that 0.46 is appropriate for most seemingly similar names
                    // and disregard same names
                    && (!((String)pair.getKey()).equalsIgnoreCase(inputName))) {

                return new Pair<>(true, (String) pair.getKey());
            }
        }
        return new Pair<>(false, inputName);
    }

    /**
     * Following two functions is a String formatter for
     * the notifications sent out to the client users
     * when the game score is updated.
     */

    private String setNotificationTitle(String teamA, String teamB, int scoreA, int scoreB, String scorer) {

        return "[ " + scorer + " ] " + teamA + " " + scoreA + " : " + scoreB + " " + teamB;

    }

    private String setNotificationBody(String team, String scorer) {

        return "Goal by " + scorer + " (" + team + ") " + "!";
    }

}
