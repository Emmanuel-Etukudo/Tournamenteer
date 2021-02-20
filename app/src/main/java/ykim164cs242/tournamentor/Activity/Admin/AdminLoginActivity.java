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

import ykim164cs242.tournamentor.R;

/**
 * The AdminLoginActivity class represents the screen where a user can
 * login to manage the channel and the tournaments.
 */
public class AdminLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText inputEmail;
    private EditText inputPassword;
    private TextView loginText;

    private ProgressDialog progressDialog;

    //Firebase Authentication Object

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        progressDialog = new ProgressDialog(AdminLoginActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = (Button) findViewById(R.id.login_button);
        inputEmail = (EditText) findViewById(R.id.enter_email);
        inputPassword = (EditText) findViewById(R.id.enter_password);

        loginText = (TextView) findViewById(R.id.newaccount_text);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIdIntent = new Intent(AdminLoginActivity.this, AddNewAdminActivity.class);

                startActivity(newIdIntent);

            }
        });
    }

    private void registerUser(){

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Signing in. Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            // start new activity
                            finish();
                            Intent successIntent = new Intent(AdminLoginActivity.this, EditAdminActivity.class);
                            startActivity(successIntent);

                        } else {
                            Toast.makeText(getBaseContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
