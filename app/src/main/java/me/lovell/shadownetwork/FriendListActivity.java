package me.lovell.shadownetwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

// SHOWS FRIENDS LIST IN MY FRIENDS
// USES FRIEND OBJECT TO POPULATE LIST VIEW AND VIEW HOLDER ADAPTER
public class FriendListActivity extends AppCompatActivity {

    private RecyclerView ListOfFrnds;
    private DatabaseReference RefOfFrnd, RefOfUsr;
    private FirebaseAuth menuAuth;
    private String loggedInUsrId;
    private Toolbar frndlistbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        menuAuth = FirebaseAuth.getInstance();
        loggedInUsrId = menuAuth.getCurrentUser().getUid();
        RefOfFrnd = FirebaseDatabase.getInstance().getReference().child("Friends").child(loggedInUsrId);
        RefOfUsr = FirebaseDatabase.getInstance().getReference().child("Users");

        frndlistbar = (Toolbar) findViewById(R.id.frndlistbar);
        setSupportActionBar(frndlistbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");
        ListOfFrnds = (RecyclerView) findViewById(R.id.list_of_friends);
        ListOfFrnds.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ListOfFrnds.setLayoutManager(linearLayoutManager);

        showUsrsFriends();

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

    private void showUsrsFriends() {
        FirebaseRecyclerAdapter<Friend, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(

                Friend.class,
                R.layout.foundfriends,
                FriendViewHolder.class,
                RefOfFrnd

        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, Friend model, int position) {

                viewHolder.setDate(model.getDate());

                final String iDofUsr = getRef(position).getKey();

                RefOfUsr.child(iDofUsr).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final String full_name = dataSnapshot.child("full_name").getValue().toString();
                            final String profile_image = dataSnapshot.child("profile_image").getValue().toString();

                            viewHolder.setFull_name(full_name);
                            viewHolder.setProfile_image(getApplicationContext(), profile_image);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]{
                                            full_name + "'s Profile",
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendListActivity.this);
                                    builder.setTitle("View profile");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which == 0){
                                                Intent goToProfile = new Intent(FriendListActivity.this, AddFriendProfileActivity.class);
                                                goToProfile.putExtra("foundTheUser", iDofUsr);
                                                startActivity(goToProfile);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        ListOfFrnds.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setProfile_image(Context cntx, String profile_image){
            CircleImageView friendimge = (CircleImageView) mView.findViewById(R.id.foundusrimg);
            Picasso.with(cntx).load(profile_image).placeholder(R.drawable.profile_img).into(friendimge);
        }

        public void setFull_name (String full_name){
            TextView friendusrname = (TextView) mView.findViewById(R.id.foundusrname);
            friendusrname.setText(full_name);
        }

        public void setDate (String date){
            TextView frienddate = (TextView) mView.findViewById(R.id.foundusrstatus);
            frienddate.setText("Added on: " + date);
        }
    }
}
