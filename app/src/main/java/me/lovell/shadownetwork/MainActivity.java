package me.lovell.shadownetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private NavigationView nvVw;
    private DrawerLayout layoutdrw;
    private RecyclerView usrPostList;
    private Toolbar menuToolbar;
    private ActionBarDrawerToggle toggleBar;
    private FirebaseAuth menuAuth;
    private DatabaseReference loggedInRef;
    private DatabaseReference refPst;
    private CircleImageView picNavBar;
    private TextView usrNameNavBar;

    String crnUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);

        // check connectivity to firebase to stop crashes
        menuAuth = FirebaseAuth.getInstance();

        // check if user is logged in
        try{
            crnUsr = menuAuth.getCurrentUser().getUid();
        }
        catch (Exception e) {

        }
        loggedInRef = FirebaseDatabase.getInstance().getReference().child("Users");
        refPst = FirebaseDatabase.getInstance().getReference().child("Posts");

        menuToolbar = (Toolbar) findViewById(R.id.mainPageToolbar);
        setSupportActionBar(menuToolbar);
        getSupportActionBar().setTitle("Home");


        // main view in activity main
        layoutdrw = (DrawerLayout) findViewById(R.id.drawableLayout);

        // hamburger menu for left sided menu
        toggleBar = new ActionBarDrawerToggle(MainActivity.this, layoutdrw, R.string.open, R.string.close);
        layoutdrw.addDrawerListener(toggleBar);
        toggleBar.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // nav menu side bar
        nvVw = (NavigationView) findViewById(R.id.nvVw);


        // list view populate
        usrPostList = (RecyclerView) findViewById(R.id.userPostList);
        usrPostList.setHasFixedSize(true);
        LinearLayoutManager managerLayoutLinear = new LinearLayoutManager(this);
        managerLayoutLinear.setReverseLayout(true);
        managerLayoutLinear.setStackFromEnd(true);
        usrPostList.setLayoutManager(managerLayoutLinear);


        // add header to nav side swipe bar
        View naviView = nvVw.inflateHeaderView(R.layout.navheader);

        // left side bar logged in user details
        picNavBar = (CircleImageView) naviView.findViewById(R.id.navBarPrImg);
        usrNameNavBar = (TextView) naviView.findViewById(R.id.navUsrName);


        // set side menu bar image and name
        loggedInRef.child(crnUsr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("full_name")){
                        String fl_name = dataSnapshot.child("full_name").getValue().toString();
                        usrNameNavBar.setText(fl_name);

                    }
                    if(dataSnapshot.hasChild("profile_image")){
                        String prfimg = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.with(MainActivity.this).load(prfimg).placeholder(R.drawable.profile_img).into(picNavBar);

                    }
                    else{

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // listener for clicks within side swipe menu
        nvVw.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                usrMnuSlct(menuItem);
                return false;
            }
        });

        // top right new post button


        showUsrsPsts();
    }

    // Adapter for listview to populate news feed with users posts
    private void showUsrsPsts() {

        Query newsFeedOrderedNewest = refPst.orderByChild("newsFeedOrderedNewest");

        FirebaseRecyclerAdapter<Posts, ViewPostHandler> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, ViewPostHandler>
                (

                Posts.class,
                R.layout.layoutposts,
                ViewPostHandler.class,
                        newsFeedOrderedNewest


        ) {
            @Override
            protected void populateViewHolder(ViewPostHandler viewHolder, Posts model, int position) {

                final String idStatus = getRef(position).getKey();

                viewHolder.setFullName(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                viewHolder.setPostImage(getApplicationContext(), model.getPostimage());

                viewHolder.viewM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent deleteStatus = new Intent(MainActivity.this, DeletePost.class);
                        deleteStatus.putExtra("idStatus", idStatus);
                        startActivity(deleteStatus);
                    }
                });
            }
        };
        usrPostList.setAdapter(firebaseRecyclerAdapter);
    }

    // For recycle view to display users posts on news feed
    public static class ViewPostHandler extends RecyclerView.ViewHolder{
        View viewM;

        public ViewPostHandler(View viewItem){
            super(viewItem);
            viewM = viewItem;
        }

        public void setFullName(String fullName){
            TextView username = (TextView) viewM.findViewById(R.id.name_profile_post);
            username.setText(fullName);
        }

        public void setProfileimage(Context cntx, String profileimage){
            CircleImageView image = (CircleImageView) viewM.findViewById(R.id.image_profile_post);
            Picasso.with(cntx).load(profileimage).into(image);
        }
        public void setTime(String time){
            TextView TimePost = (TextView) viewM.findViewById(R.id.time_post);
            TimePost.setText("  " + time);
        }
        public void setDate(String date){
            TextView DatePost = (TextView) viewM.findViewById(R.id.date_post);
            DatePost.setText("  " + date);
        }
        public void setDescription(String description){
            TextView DescriptionPost = (TextView) viewM.findViewById(R.id.description_post);
            DescriptionPost.setText(description);
        }
        public void setPostImage(Context cntx2, String postimage){
            ImageView ImagePost = (ImageView) viewM.findViewById(R.id.image_post);
            Picasso.with(cntx2).load(postimage).into(ImagePost);
        }
    }



    //check users logged in or not on start
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser loggedinUser = menuAuth.getCurrentUser();

        // if user not signed in, send them to sign up home/start screen
        if(loggedinUser == null){

            signInActivity();

        }
        else{

        // user signed in, validate them and allow to set up profile
            userChecker();

        }
    }

    // check user ID when already signed in
    private void userChecker() {

        final String signedInUserId = menuAuth.getCurrentUser().getUid();

        loggedInRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // set up account profile
                if(!dataSnapshot.hasChild(signedInUserId)){
                    createActivityForUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    // hamburger menu selected
    // when clicked will drag the left nav bar over
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggleBar.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // action performed for clicked side swipe menu, for intent and new views
    private void usrMnuSlct(MenuItem menuItem) {

        switch(menuItem.getItemId())
        {
            case R.id.navPost:
                createPostActivity();
                break;

            case R.id.navProfile:
                goToMyProfile();
                break;

            case R.id.navFriends:
                listFriends();
                break;

            case R.id.navFindFriends:
                findfriends();
                break;

            case R.id.navSettings:
                editprofile();
                break;

            case R.id.navLogout:
                menuAuth.signOut();
                signInActivity();
                break;

            case R.id.createShadow:
                createshadow();
                break;
        }

    }

    private void createshadow() {
        Intent createshadowintent = new Intent(MainActivity.this, CreateShadowProfileActivity.class);
        startActivity(createshadowintent);
        finish();
    }


    // redirecr user to sign in if not logged in
    private void listFriends() {

        Intent gotofriendslist = new Intent(MainActivity.this, FriendListActivity.class);
        startActivity(gotofriendslist);
        finish();

    }

    private void createPostActivity() {
        Intent goToCreatePostActivity = new Intent(MainActivity.this, CreatePostActivty.class);
        startActivity(goToCreatePostActivity);
        finish();
    }

    // redirecr user to sign in if not logged in
    private void signInActivity() {

        Intent signinIntent = new Intent(MainActivity.this, SignInActivity.class);

        startActivity(signinIntent);
        finish();

    }

    private void goToMyProfile() {
        Intent myprofileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(myprofileIntent);
        finish();
    }

    private void editprofile() {

        Intent editprofile = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(editprofile);
        finish();

    }

    private void findfriends() {

        Intent findfriends = new Intent(MainActivity.this, AccessUserContactsActivity.class);
        startActivity(findfriends);
        finish();

    }

    // signed in user redirected to set up profile attributes
    private void createActivityForUser() {
        Intent createActivityIntent = new Intent(MainActivity.this,  CreateActivity.class);
        startActivity(createActivityIntent);
        finish();
    }

}
