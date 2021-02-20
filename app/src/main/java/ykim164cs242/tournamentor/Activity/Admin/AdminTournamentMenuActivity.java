package ykim164cs242.tournamentor.Activity.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ykim164cs242.tournamentor.Activity.Common.StartMenuActivity;
import ykim164cs242.tournamentor.ListItem.AdminMatchListItem;
import ykim164cs242.tournamentor.R;

/**
 * The AdminTournamentMenuActivity class represents the menus for managing
 * an existing tournament. It conatins editting, adding games, and deleting.
 */
public class AdminTournamentMenuActivity extends AppCompatActivity {

    private String tournamentName;
    private String startDate;
    private String endDate;

    private List<String> dateArray = new ArrayList<>();

    private TextView tournamentNameTextView;
    private Button addTournamentButton;
    private Button setGameButton;
    private Button editTournamentButton;
    private Button deleteTournamentButton;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference channelsReference = rootReference.child("Channels");
    DatabaseReference tournamentTermReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tournament_menu);

        try {
            Intent intent = getIntent();

            // Tournament name
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
            tournamentName = intent.getStringExtra("tournamentName");

        } catch(Exception e) {
            e.printStackTrace();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tournamentNameTextView = (TextView) findViewById(R.id.tournament_name_option);
        addTournamentButton = (Button) findViewById(R.id.add_tournament_button);
        setGameButton = (Button) findViewById(R.id.set_game_schedule_button);
        editTournamentButton = (Button) findViewById(R.id.edit_tournament_button);
        deleteTournamentButton = (Button) findViewById(R.id.delete_tournament_button);

        tournamentNameTextView.setText(tournamentName);

        tournamentTermReference = channelsReference.child(firebaseUser.getUid()).child("tournaments")
                .child(tournamentName).child("term");

        // Go back to add tournament menu
        addTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminTournamentMenuActivity.this, AdminTournamentListActivity.class);
                startActivity(intent);
            }
        });

        // Go to add match menu
        setGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminTournamentMenuActivity.this, AdminMatchListActivity.class);
                intent.putExtra("tournamentName", tournamentName);
                startActivity(intent);
            }
        });

        // Edit tournament menu
        editTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Parse date

                Intent intent = new Intent(AdminTournamentMenuActivity.this, AdminAddTournamentActivity.class);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                intent.putExtra("tournamentName", tournamentName);

                startActivity(intent);
            }
        });

        // Delete this tournament
        deleteTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder yesNoBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View yesNoDialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                yesNoBuilder.setView(yesNoDialogView);
                yesNoBuilder.setTitle("Are you sure?");
                final AlertDialog yesNoDialog = yesNoBuilder.create();
                yesNoDialog.show();

                final TextView question = (TextView) yesNoDialogView.findViewById(R.id.yes_no_question);
                question.setText("Delete this tournament?");

                Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Do action
                        // Delete tournament data from DB

                        rootReference.child("Channels").child(firebaseUser.getUid()).child("tournaments").child(tournamentName).getRef().removeValue();

                        Intent intent = new Intent(AdminTournamentMenuActivity.this, AdminTournamentListActivity.class);
                        startActivity(intent);

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

    }

}
