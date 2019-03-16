package me.lovell.shadownetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendsActivity extends AppCompatActivity {

    private Toolbar uppermenubar;
    private ImageButton BtnSearch;
    private EditText inptSearchTxt;
    private RecyclerView ResultFromSearch;
    private DatabaseReference databaseUsers;
    private ImageButton gotocreateshadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        BtnSearch = (ImageButton) findViewById(R.id.btnsearch);
        inptSearchTxt = (EditText) findViewById(R.id.inputsearchusers);

       // gotocreateshadow = (ImageButton) findViewById(R.id.gotocreateshadowbtn);

//        gotocreateshadow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent shadowintent = new Intent(AddFriendsActivity.this, CreateShadowProfileActivity.class);
//                startActivity(shadowintent);
//                finish();
//            }
//        });


        ResultFromSearch = (RecyclerView) findViewById(R.id.outputfromsearch);
        ResultFromSearch.setHasFixedSize(true);
        ResultFromSearch.setLayoutManager(new LinearLayoutManager(this));


        uppermenubar = (Toolbar) findViewById(R.id.addfriendsbar);
        setSupportActionBar(uppermenubar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add friends");


        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String inputusrsearch =  inptSearchTxt.getText().toString();


                findfriendaction(inputusrsearch);
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


    private void findfriendaction(String inputusrsearch) {

        Query querysearch = databaseUsers.orderByChild("full_name").startAt(inputusrsearch).endAt(inputusrsearch + "\uf8ff");

        FirebaseRecyclerAdapter<FoundFriends,FoundFriendsHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FoundFriends, FoundFriendsHolder>
                (
                FoundFriends.class,
                R.layout.foundfriends,
                FoundFriendsHolder.class,
                        querysearch
                 )
        {
            @Override
            protected void populateViewHolder(FoundFriendsHolder viewHolder, FoundFriends model, final int position) {

                viewHolder.setFull_name(model.getFull_name());
                viewHolder.setProfileStatus(model.getProfileStatus());
                viewHolder.setProfile_image(getApplicationContext(), model.getProfile_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String foundTheUser = getRef(position).getKey();

                        Intent goAddUser = new Intent (AddFriendsActivity.this, AddFriendProfileActivity.class);
                        goAddUser.putExtra("foundTheUser", foundTheUser);
                        startActivity(goAddUser);
                    }
                });
            }
        };

        ResultFromSearch.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FoundFriendsHolder extends  RecyclerView.ViewHolder{
        View mView;

        public FoundFriendsHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setProfile_image(Context cntx, String profile_image){
            CircleImageView usrimge = (CircleImageView) mView.findViewById(R.id.foundusrimg);
            Picasso.with(cntx).load(profile_image).placeholder(R.drawable.profile_img).into(usrimge);
        }

        public void setFull_name (String full_name){
            TextView usrname = (TextView) mView.findViewById(R.id.foundusrname);
            usrname.setText(full_name);
        }

        public void setProfileStatus (String profileStatus){
            TextView usrstatus = (TextView) mView.findViewById(R.id.foundusrstatus);
            usrstatus.setText(profileStatus);
        }
    }
}
