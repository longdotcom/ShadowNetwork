package me.lovell.shadownetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisteringActivity extends AppCompatActivity {
    private EditText regEmail, regPassword, regconfirmPassword;
    private Button regButton;
    private FirebaseAuth menuAuth;
    private ProgressDialog progressBar;
    private Toolbar regisbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);
        menuAuth = FirebaseAuth.getInstance();
        regEmail = (EditText) findViewById(R.id.regEmail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regconfirmPassword = (EditText) findViewById(R.id.regConfirm);
        regButton = (Button) findViewById(R.id.regSignUp);
        progressBar = new ProgressDialog(this);
        regisbar = (Toolbar) findViewById(R.id.regisbar);
        setSupportActionBar(regisbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regemail = regEmail.getText().toString();
                String regpass = regPassword.getText().toString();
                String regconfirmpass = regconfirmPassword.getText().toString();
                if(TextUtils.isEmpty(regemail)){
                    Toast.makeText(RegisteringActivity.this, "Enter email address", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(regpass)){
                    Toast.makeText(RegisteringActivity.this, "Enter password", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(regconfirmpass)){
                    Toast.makeText(RegisteringActivity.this, "Confirm password", Toast.LENGTH_LONG).show();
                }
                else if(!regpass.equals(regconfirmpass)){
                    Toast.makeText(RegisteringActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
                else{
                    progressBar.setTitle("Setting up account");
                    progressBar.setMessage("This won't take long..");
                    progressBar.show();
                    progressBar.setCanceledOnTouchOutside(true);
                    menuAuth.createUserWithEmailAndPassword(regemail, regpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent createIntent = new Intent(RegisteringActivity.this, CreateActivity.class);
                                createIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(createIntent);
                                finish();
                                Toast.makeText(RegisteringActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                            }
                            else{
                                String error = task.getException().getMessage();
                                Toast.makeText(RegisteringActivity.this, "Something went wrong: " + error, Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
    // checks if user signed in already to bypass register screen
    // sends them to main feed main activity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser loggedinUser = menuAuth.getCurrentUser();
    // if user signed in, send them to main activity/news feed
         if(loggedinUser != null){
             Intent goToMainIntent = new Intent(RegisteringActivity.this, MainActivity.class);
             goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(goToMainIntent);
             finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    // redirect logged in user to main logged in view feed

}
