package ykim164cs242.tournamentor.Activity.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ykim164cs242.tournamentor.Activity.Client.ClientTournamentListActivity;
import ykim164cs242.tournamentor.Activity.Client.SelectChannelActivity;
import ykim164cs242.tournamentor.InformationStorage.AdminUserInfo;
import ykim164cs242.tournamentor.InformationStorage.ChannelInfo;
import ykim164cs242.tournamentor.InformationStorage.TeamInfo;
import ykim164cs242.tournamentor.InformationStorage.TournamentInfo;
import ykim164cs242.tournamentor.R;

/**
 * The AddNewAdminActivity class represents the screen where a user can
 * add an account for the Admin side and set up a channel.
 */

public class AddNewAdminActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputChannelName;
    private TextView loginSign;

    private ProgressDialog progressDialog;

    //Firebase Authentication Object

    private FirebaseAuth firebaseAuth;

    // Firebase Database references.
    DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

    private Date currentDate;
    private String dateInString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_admin);

        // created Date

        currentDate = Calendar.getInstance().getTime();
        dateInString = simpleDateFormat.format(currentDate);

        progressDialog = new ProgressDialog(AddNewAdminActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        submitButton = (Button) findViewById(R.id.create_user_button);
        inputEmail = (EditText) findViewById(R.id.enter_email_add);
        inputPassword = (EditText) findViewById(R.id.enter_password_add);
        inputChannelName = (EditText) findViewById(R.id.enter_channel_add);
        loginSign = (TextView) findViewById(R.id.login_sign);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        loginSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = new Intent(AddNewAdminActivity.this, AdminLoginActivity.class);
                startActivity(signInIntent);

            }
        });
    }

    /**
     * The registerUser function handles registration.
     * It registers the user on the Firebase DB with the email given.
     *
     */
    private void registerUser(){

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        final String channelName = inputChannelName.getText().toString();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success

                        if(task.isSuccessful()) {

                            Toast.makeText(AddNewAdminActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();

                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String adminID = firebaseUser.getUid();
                            String adminEmail = firebaseUser.getEmail();

                            AdminUserInfo adminUserInfo = new AdminUserInfo(adminID, adminEmail, channelName);
                            ChannelInfo channelInfo = new ChannelInfo(channelName, dateInString);

                            Intent successIntent = new Intent(AddNewAdminActivity.this, EditAdminActivity.class);

                            successIntent.putExtra("channelID", adminID);
                            rootReference.child("Channels").child(adminID).setValue(channelInfo);
                            startActivity(successIntent);

                        } else {

                            Toast.makeText(AddNewAdminActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
