package me.lovell.shadownetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText usrEmail, usrPassword;
    private TextView createAcntLink;
    private FirebaseAuth menuAuth;
    private ProgressDialog progressDisplay;
    private TextView resetpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        menuAuth = FirebaseAuth.getInstance();
        createAcntLink = (TextView) findViewById(R.id.loginCreate);
        usrEmail = (EditText) findViewById(R.id.loginEmail);
        usrPassword = (EditText) findViewById(R.id.loginPassword);
        loginBtn = (Button) findViewById(R.id.loginBTN);
        progressDisplay = new ProgressDialog(this);
        resetpassword = (TextView) findViewById(R.id.resetpassword);
        // send from text to sign up
        createAcntLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(SignInActivity.this, RegisteringActivity.class );
                startActivity(signupIntent);
                finish();
            }
        });
        // button once user enters log in details
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signinEmail = usrEmail.getText().toString();
                String signinPassword = usrPassword.getText().toString();
                if(TextUtils.isEmpty(signinEmail))
                {
                    Toast.makeText(SignInActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(signinPassword)){
                    Toast.makeText(SignInActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDisplay.setTitle("Logging in");
                    progressDisplay.setMessage("This won't take long..");
                    progressDisplay.show();
                    progressDisplay.setCanceledOnTouchOutside(true);
                    menuAuth.signInWithEmailAndPassword(signinEmail, signinPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // redirect to main logged in view
                                Intent goMainIntent = new Intent(SignInActivity.this, MainActivity.class);
                                goMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(goMainIntent);
                                finish();
                                progressDisplay.dismiss();
                            }
                            else{
                                String errMessage = task.getException().getMessage();
                                Toast.makeText(SignInActivity.this, "Error signing in: " + errMessage, Toast.LENGTH_LONG).show();
                                progressDisplay.dismiss();
                            }
                        }
                    });
                }
            }
        });
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset = new Intent(SignInActivity.this, PasswordResetActivity.class);
                startActivity(reset);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser loggedinUser = menuAuth.getCurrentUser();
        // if user already logged in, send to main feed screen
        if(loggedinUser != null){
            Intent goToMainIntent = new Intent(SignInActivity.this, MainActivity.class);
            goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToMainIntent);
            finish();
        }
    }

    // takes field log in details and checks database


    // redirect logged in user to main logged in view feed

    // open the sign up view activity

}
