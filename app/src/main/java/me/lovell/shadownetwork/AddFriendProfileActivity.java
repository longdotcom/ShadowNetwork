package me.lovell.shadownetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

// FOUND USERS PROFILES TO ADD AS FRIEND
// ALLOWS TO VIEW PROFILE FROM USERS FRIENDS LIST
public class AddFriendProfileActivity extends AppCompatActivity {

    private TextView addstatus;
    private TextView addname;
    private TextView addlocation;
    private TextView adddob;
    private TextView addgender;
    private TextView addrelationship;
    private TextView addusername;
    private CircleImageView addprfleimg;
    private Button reqFrndSndBtn;
    private Button reqFrndDecBtn;
    private Toolbar addfriendsprofilebar;
    private DatabaseReference ReferenceFrndReq;
    private DatabaseReference ReferenceUsr;
    private DatabaseReference ReferenceFrnd;
    private FirebaseAuth menuAuth;
    private String getDate;
    private String STATUS;
    private String idOfSndr;
    private String idOfRecr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_profile);
        menuAuth = FirebaseAuth.getInstance();
        idOfSndr = menuAuth.getCurrentUser().getUid();
        idOfRecr = getIntent().getExtras().get("foundTheUser").toString();
        ReferenceUsr = FirebaseDatabase.getInstance().getReference().child("Users");
        ReferenceFrndReq = FirebaseDatabase.getInstance().getReference().child("PendingRequests");
        ReferenceFrnd = FirebaseDatabase.getInstance().getReference().child("Friends");
        addfriendsprofilebar = (Toolbar) findViewById(R.id.addfriendsprofilebar);
        setSupportActionBar(addfriendsprofilebar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Friend");
        addstatus = (TextView) findViewById(R.id.addfriendstatus);
        addusername = (TextView) findViewById(R.id.addfriendusername);
        addname = (TextView) findViewById(R.id.addfriendfullname);
        addlocation = (TextView) findViewById(R.id.addfriendlocation);
        adddob = (TextView) findViewById(R.id.addfrienddob);
        addgender = (TextView) findViewById(R.id.addfriendgender);
        addrelationship = (TextView) findViewById(R.id.addfriendrelationstatus);
        addprfleimg = (CircleImageView) findViewById(R.id.addfriendprflepic);
        reqFrndSndBtn = (Button) findViewById(R.id.addfriendbtn);
        reqFrndDecBtn = (Button) findViewById(R.id.declinefriendbtn);

        STATUS = "notadded";

        ReferenceUsr.child(idOfRecr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myimgstr = dataSnapshot.child("profile_image").getValue().toString();
                    String myusrnmestr = dataSnapshot.child("user_name").getValue().toString();
                    String mynamestr = dataSnapshot.child("full_name").getValue().toString();
                    String mystatusstr = dataSnapshot.child("profileStatus").getValue().toString();
                    String mybirthstr = dataSnapshot.child("dob").getValue().toString();
                    String mygenderstr = dataSnapshot.child("gender").getValue().toString();
                    String myrelationstr = dataSnapshot.child("relationshipStatus").getValue().toString();
                    String mylocatestr = dataSnapshot.child("locationCountry").getValue().toString();
                    Picasso.with(AddFriendProfileActivity.this).load(myimgstr).placeholder(R.drawable.profile_img).into(addprfleimg);
                    addusername.setText(myusrnmestr);
                    addname.setText(mynamestr);
                    addrelationship.setText("Relationship status: " + myrelationstr);
                    addgender.setText("Gender: " + mygenderstr);
                    adddob.setText("D.O.B: " + mybirthstr);
                    addlocation.setText("Location: " + mylocatestr);
                    addstatus.setText(mystatusstr);
                    ReferenceFrndReq.child(idOfSndr).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(idOfRecr)) {
                                String typeofrequest = dataSnapshot.child(idOfRecr).child("typeofrequest").getValue().toString();
                                if (typeofrequest.equals("sending")) {
                                    STATUS = "sendingrequest";
                                    reqFrndSndBtn.setText("Cancel friend request");
                                    reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                    reqFrndDecBtn.setEnabled(false);
                                }
                                else if(typeofrequest.equals("receiving")){
                                    STATUS = "receivingrequest";
                                    reqFrndSndBtn.setText("Accept friend");
                                    reqFrndDecBtn.setVisibility(View.VISIBLE);
                                    reqFrndDecBtn.setEnabled(true);
                                    reqFrndDecBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ReferenceFrndReq.child(idOfSndr).child(idOfRecr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ReferenceFrndReq.child(idOfRecr).child(idOfSndr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    reqFrndSndBtn.setEnabled(true);
                                                                    STATUS = "notadded";
                                                                    reqFrndSndBtn.setText("Send friendship request");
                                                                    reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                                    reqFrndDecBtn.setEnabled(false);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    ReferenceFrnd.child(idOfSndr).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(idOfRecr)){
                                                STATUS = "addedfriends";
                                                reqFrndSndBtn.setText("Unfriend");
                                                reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                reqFrndDecBtn.setEnabled(false);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        reqFrndDecBtn.setVisibility(View.INVISIBLE);
        reqFrndDecBtn.setEnabled(false);
        if (!idOfSndr.equals(idOfRecr)) {
            reqFrndSndBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reqFrndSndBtn.setEnabled(false);
                    if (STATUS.equals("notadded")) {
                        ReferenceFrndReq.child(idOfSndr).child(idOfRecr).child("typeofrequest").setValue("sending").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ReferenceFrndReq.child(idOfRecr).child(idOfSndr).child("typeofrequest").setValue("receiving").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reqFrndSndBtn.setEnabled(true);
                                                STATUS = "sendingrequest";
                                                reqFrndSndBtn.setText("Cancel friend request");
                                                reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                reqFrndDecBtn.setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    if (STATUS.equals("sendingrequest")) {
                        ReferenceFrndReq.child(idOfSndr).child(idOfRecr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ReferenceFrndReq.child(idOfRecr).child(idOfSndr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reqFrndSndBtn.setEnabled(true);
                                                STATUS = "notadded";
                                                reqFrndSndBtn.setText("Send friendship request");
                                                reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                reqFrndDecBtn.setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    if(STATUS.equals("receivingrequest")){
                        Calendar todayCal = Calendar.getInstance();
                        SimpleDateFormat todayDate = new SimpleDateFormat("dd-MM-yyyy");
                        getDate = todayDate.format(todayCal.getTime());
                        ReferenceFrnd.child(idOfSndr).child(idOfRecr).child("date").setValue(getDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ReferenceFrnd.child(idOfRecr).child(idOfSndr).child("date").setValue(getDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                ReferenceFrndReq.child(idOfSndr).child(idOfRecr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            ReferenceFrndReq.child(idOfRecr).child(idOfSndr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        reqFrndSndBtn.setEnabled(true);
                                                                        STATUS = "addedfriends";
                                                                        reqFrndSndBtn.setText("Unfriend ");
                                                                        reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                                        reqFrndDecBtn.setEnabled(false);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    if(STATUS.equals("addedfriends")){
                        ReferenceFrnd.child(idOfSndr).child(idOfRecr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ReferenceFrnd.child(idOfRecr).child(idOfSndr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reqFrndSndBtn.setEnabled(true);
                                                STATUS = "notadded";
                                                reqFrndSndBtn.setText("Send friendship request");
                                                reqFrndDecBtn.setVisibility(View.INVISIBLE);
                                                reqFrndDecBtn.setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        } else {
            reqFrndDecBtn.setVisibility(View.INVISIBLE);
            reqFrndSndBtn.setVisibility(View.INVISIBLE);
        }
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

}
