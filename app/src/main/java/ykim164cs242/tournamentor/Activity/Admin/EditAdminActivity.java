package ykim164cs242.tournamentor.Activity.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
import ykim164cs242.tournamentor.ListItem.TeamListItem;
import ykim164cs242.tournamentor.R;

/**
 * The EditAdminActivity class represents the screen where a user can
 * change channelName, signOut, and delete account.
 *
 */
public class EditAdminActivity extends AppCompatActivity {

    private TextView adminChannelName;
    private String theChannelname;

    private Button editInfoButton;
    private Button goToManageTournament;
    private Button signOutButton;
    private Button deleteUserButton;

    //Firebase Authentication Object

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    AuthCredential credential;

    // Storages for parsed data from the real-time database
    private List<String> channelNameString;
    private String channelString;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference channelsReference = rootReference.child("Channels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        try {
            Intent intent = getIntent();

            // Tournament name and term
            theChannelname = intent.getStringExtra("channelID");

        } catch(Exception e) {
            e.printStackTrace();
        }

        channelNameString = new ArrayList<>();

        credential = EmailAuthProvider.getCredential("user@example.com", "password1234");

        adminChannelName = (TextView) findViewById(R.id.admin_channel_name);

        editInfoButton = (Button) findViewById(R.id.edit_admin_info);
        goToManageTournament = (Button) findViewById(R.id.manage_tournament_button);
        signOutButton = (Button) findViewById(R.id.admin_signout_button);
        deleteUserButton = (Button) findViewById(R.id.delete_channel_button);

        DatabaseReference theChannelReference = channelsReference.child(firebaseUser.getUid());
        theChannelReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                channelString = dataSnapshot.child("channelName").getValue().toString();
                channelNameString.add(channelString);
                if(!channelNameString.isEmpty()){
                    adminChannelName.setText(channelNameString.get(0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder editNameBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View editNameDialogView = inflater.inflate(R.layout.dialog_edit_channel_name, null);
                editNameBuilder.setView(editNameDialogView);
                editNameBuilder.setTitle("Edit Channel Name");
                final AlertDialog editNameDialog = editNameBuilder.create();
                editNameDialog.show();

                final EditText nameInput = (EditText) editNameDialogView.findViewById(R.id.edit_channel_name);
                nameInput.setText(theChannelname);

                Button submitButton = (Button) editNameDialogView.findViewById(R.id.change_channel_name_submit);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        adminChannelName.setText(nameInput.getText().toString());
                        channelsReference.child(firebaseUser.getUid()).child("channelName").setValue(nameInput.getText().toString());
                        editNameDialog.dismiss();

                    }
                });

            }
        });

        goToManageTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go To Manage Tournament
                Intent intent = new Intent(EditAdminActivity.this, AdminTournamentListActivity.class);
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
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
                question.setText("Sign out from this account?");

                Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Do action
                        firebaseAuth.signOut();
                        // Go Back To Main Menu
                        Intent intent = new Intent(EditAdminActivity.this, StartMenuActivity.class);
                        Toast.makeText(getBaseContext(), "Signed Out", Toast.LENGTH_SHORT).show();
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

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Add credential and confirmation

                final AlertDialog.Builder yesNoBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View yesNoDialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                yesNoBuilder.setView(yesNoDialogView);
                yesNoBuilder.setTitle("Are you sure?");
                final AlertDialog yesNoDialog = yesNoBuilder.create();
                yesNoDialog.show();

                final TextView question = (TextView) yesNoDialogView.findViewById(R.id.yes_no_question);
                question.setText("Delete this account?");

                Button yesButton = (Button) yesNoDialogView.findViewById(R.id.yes_button);
                Button noButton = (Button) yesNoDialogView.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Do action

                        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            // Go Back To Main Menu
                                            Intent intent = new Intent(EditAdminActivity.this, StartMenuActivity.class);
                                            channelsReference.child(firebaseUser.getUid()).getRef().removeValue();
                                            Toast.makeText(getBaseContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                            startActivity(intent);

                                        }
                                    }
                                });

                            }
                        });

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
