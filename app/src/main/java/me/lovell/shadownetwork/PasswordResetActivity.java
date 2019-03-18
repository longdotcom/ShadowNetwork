package me.lovell.shadownetwork;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private TextView resettext;
    private EditText resetemail;
    private Button resetbtn;
    private Toolbar resettlbar;
    private FirebaseAuth menuAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        menuAuth = FirebaseAuth.getInstance();
        resettlbar = (Toolbar) findViewById(R.id.resettlbar);
        setSupportActionBar(resettlbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request new password");
        resettext = (TextView) findViewById(R.id.resettext);
        resetemail = (EditText) findViewById(R.id.resetemail);
        resetbtn = (Button) findViewById(R.id.resetbtn);
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = resetemail.getText().toString();
                if(TextUtils.isEmpty(getEmail)){
                    Toast.makeText(PasswordResetActivity.this, "Email field is blank", Toast.LENGTH_LONG);
                }else{
                    menuAuth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordResetActivity.this, "Reset email has been sent.", Toast.LENGTH_LONG);
                                Intent loginAgain = new Intent(PasswordResetActivity.this, SignInActivity.class);
                                startActivity(loginAgain);
                            }
                            else{
                                Toast.makeText(PasswordResetActivity.this, "Something went wrong", Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            }
        });
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
}