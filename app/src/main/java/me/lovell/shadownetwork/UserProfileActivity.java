package me.lovell.shadownetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

// LOGGED IN USERS OWN PROFILE
public class UserProfileActivity extends AppCompatActivity {

    private TextView mystatus;
    private TextView myname;
    private TextView mylocation;
    private TextView mydob;
    private TextView mygender;
    private TextView myrelationship;
    private TextView myusrname;
    private CircleImageView myprofileimg;
    private DatabaseReference myprofiledb;
    private DatabaseReference noOfFriends;
    private FirebaseAuth menuAuth;
    private int friendcnter;
    private Toolbar uppermenubar;

    private Button friendnumberbtn;

    private String loggedinusrID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        menuAuth = FirebaseAuth.getInstance();
        loggedinusrID = menuAuth.getCurrentUser().getUid();
        myprofiledb = FirebaseDatabase.getInstance().getReference().child("Users").child(loggedinusrID);
        noOfFriends = FirebaseDatabase.getInstance().getReference().child("Friends");

        mystatus = (TextView) findViewById(R.id.usrprfsts);
        myusrname = (TextView) findViewById(R.id.usrusrname);
        myname = (TextView) findViewById(R.id.usrprfname);
        mylocation = (TextView) findViewById(R.id.usrlocation);
        mydob = (TextView) findViewById(R.id.usrdob);
        mygender = (TextView) findViewById(R.id.usrgender);
        myrelationship = (TextView) findViewById(R.id.usrrelation);
        myprofileimg = (CircleImageView) findViewById(R.id.usrprfpic);
        friendnumberbtn = (Button) findViewById(R.id.friendnumberbtn);

        uppermenubar = (Toolbar) findViewById(R.id.usrprfbar);
        setSupportActionBar(uppermenubar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        friendnumberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFriends();
            }
        });

        noOfFriends.child(loggedinusrID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    friendcnter = (int) dataSnapshot.getChildrenCount();
                    friendnumberbtn.setText(friendcnter + " friends");
                }else{
                    friendnumberbtn.setText("No Friends");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myprofiledb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myimgstr = dataSnapshot.child("profile_image").getValue().toString();
                    String myusrnmestr = dataSnapshot.child("user_name").getValue().toString();
                    String mynamestr = dataSnapshot.child("full_name").getValue().toString();
                    String mystatusstr = dataSnapshot.child("profileStatus").getValue().toString();
                    String mybirthstr = dataSnapshot.child("dob").getValue().toString();
                    String mygenderstr = dataSnapshot.child("gender").getValue().toString();
                    String myrelationstr = dataSnapshot.child("relationshipStatus").getValue().toString();
                    String mylocatestr = dataSnapshot.child("locationCountry").getValue().toString();

                    Picasso.with(UserProfileActivity.this).load(myimgstr).placeholder(R.drawable.profile_img).into(myprofileimg);

                    myusrname.setText(myusrnmestr);
                    myname.setText(mynamestr);
                    myrelationship.setText("Relationship status: "+myrelationstr);
                    mygender.setText(mygenderstr);
                    mydob.setText("D.O.B: "+mybirthstr);
                    mylocation.setText("Location: "+mylocatestr);
                    mystatus.setText(mystatusstr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    private void listFriends() {

        Intent gotofriendslist = new Intent(UserProfileActivity.this, FriendListActivity.class);
        gotofriendslist.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(gotofriendslist);
        finish();

    }
}
