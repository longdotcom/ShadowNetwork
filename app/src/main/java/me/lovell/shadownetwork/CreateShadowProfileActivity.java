package me.lovell.shadownetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// On page load, reads all users contacts from db
// hashes them by mobile number to find similar contacts
// looks for majority used name/email of each to create shadow profile

// BUG - LOGS USER OUT, LOGS INTO LAST CREATED SHADOW PROFILE - SOLVED

public class CreateShadowProfileActivity extends AppCompatActivity {
    private static final String TAG = "FILTERTAG";
    private List<String> names;
    private List<String> emails;
    private FirebaseAuth menuAuth;
    private DatabaseReference editdb;
    private FirebaseUser user;
    private String userUid;
    private String originalUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shadow_profile);



        menuAuth = FirebaseAuth.getInstance();

        originalUserEmail = menuAuth.getCurrentUser().getEmail();


        Toolbar shadowprofiletoolbar = (Toolbar) findViewById(R.id.shadowprofiletoolbar);
        setSupportActionBar(shadowprofiletoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Creating Shadow Profiles");

        // ACCESS FIREBASE DB TO GRAB ALL CONTACTS
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference friendsRef = rootRef.child("Contacts");

        // Event listener to check activity loaded
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // hashmap/arraylist data structure to list all users contacts by phone number
                ListMultimap<String, Contact> mobileKey = ArrayListMultimap.create();

                // for each contact found, add to listmultimap
                for(DataSnapshot x : dataSnapshot.getChildren()) {
                    Contact c = x.getValue(Contact.class);
                    mobileKey.put(c.getPhone(), c);
                }

                // loops through each mobile number in hashmap
                // grabs arraylist of contacts linked to that number to compare
                for (String key : mobileKey.keySet()) {
                    // print which mobile number is currently being compared
                    Log.d(TAG, "Phone number: " + key);
                    // each contact associated with the current number
                    List<Contact> contactlist = mobileKey.get(key);
                    // put each name and email into seperate lists
                    // count most occurances to find highest probability of fields to create profile
                    names = new ArrayList<String>();
                    emails = new ArrayList<String>();
                    // string to show which users created shadow profile
                    String createdBy = "Created by: ";
                        for(Contact x : contactlist)
                        {   // add each name and email to seperate arrays
                            Log.d(TAG, x.getName() + " " + x.getEmail() + " " + x.getUid());
                            names.add(x.getName());
                            emails.add(x.getEmail());
                            createdBy += x.getUid()+", ";

                        }

                    // for current mobile number, find most occuring name to create shadow profile from
                    String mostRepeatedName
                            = names.stream()
                            .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                            .entrySet()
                            .stream()
                            .max(Comparator.comparing(Map.Entry::getValue))
                            .get()
                            .getKey();

                    Log.d(TAG, "Most common name: " + mostRepeatedName);

                    // for current mobile number, find most occuring email to create shadow profile from
                    String mostRepeatedEmail
                            = emails.stream()
                            .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                            .entrySet()
                            .stream()
                            .max(Comparator.comparing(Map.Entry::getValue))
                            .get()
                            .getKey();

                    Log.d(TAG, "Most common email: " + mostRepeatedEmail);
                    Log.d(TAG, " ");

                    Log.d(TAG, createdBy );
                    Log.d(TAG, " ");


                    String finalCreatedBy = createdBy;

                    // create shadow profile
                    try {
                        // create shadow profile account login with the email and generic password
                        menuAuth.createUserWithEmailAndPassword(mostRepeatedEmail, "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            // create profile for shadow profile
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // retrieve shadow profile account UID to modify their profile

                                try {
                                    user = task.getResult().getUser();
                                    userUid = user.getUid();
                                    editdb = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);
                                    // create profile for shadow profile
                                    // using highest probability name
                                    // highest probability email
                                    // status is concatination of all users contact came from
                                    HashMap editMap = new HashMap();
                                    editMap.put("user_name", mostRepeatedEmail);
                                    editMap.put("full_name", mostRepeatedName);
                                    editMap.put("relationshipStatus", "SHADOW");
                                    editMap.put("gender", "SHADOW");
                                    editMap.put("dob", "SHADOW");
                                    editMap.put("locationCountry", "SHADOW");
                                    editMap.put("profileStatus", finalCreatedBy);
                                    editMap.put("profile_image", "https://firebasestorage.googleapis.com/v0/b/shadownetwork-f6f21.appspot.com/o/Profile%20Images%2Fshadow.jpg?alt=media&token=373ea451-89f8-4129-91be-4886690edf72");
                                    editdb.updateChildren(editMap);
                                    //Log.d(TAG, "onComplete: uid=" + user.getUid());
                                }catch(Exception e){}
                            }
                        });
                    }catch (Exception e){
                        Log.d(TAG, "User exists already");
                    }


                }

                menuAuth.signInWithEmailAndPassword(originalUserEmail, "password");
                // once shadow profiles generated, go back to main activity
                Intent intent = new Intent(CreateShadowProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        friendsRef.addListenerForSingleValueEvent(eventListener);
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