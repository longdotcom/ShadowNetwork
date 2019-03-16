package me.lovell.shadownetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class DeletePost extends AppCompatActivity {

    private ImageView imgPost;
    private Button btnDelete;
    private TextView deleteDesc;
    private Toolbar deletefriendsprofilebar;
    private DatabaseReference refClck;
    private FirebaseAuth menuAuth;

    private String idStatus;
    private String crntUsrID;
    private String dbCrntUsr;
    private String desc;
    private String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_post);

        menuAuth = FirebaseAuth.getInstance();
        crntUsrID = menuAuth.getCurrentUser().getUid();

        idStatus = getIntent().getExtras().get("idStatus").toString();
        refClck = FirebaseDatabase.getInstance().getReference().child("Posts").child(idStatus);

        imgPost = (ImageView) findViewById(R.id.image_status);
        deleteDesc = (TextView) findViewById(R.id.delete_text);
        //status delete
        btnDelete = (Button) findViewById(R.id.status_delete);
        deletefriendsprofilebar = (Toolbar) findViewById(R.id.deletefriendsprofilebar);
        setSupportActionBar(deletefriendsprofilebar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnDelete.setVisibility(View.INVISIBLE);

        refClck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    desc = dataSnapshot.child("description").getValue().toString();
                    img = dataSnapshot.child("postimage").getValue().toString();
                    dbCrntUsr = dataSnapshot.child("uid").getValue().toString();

                    deleteDesc.setText(desc);
                    Picasso.with(DeletePost.this).load(img).into(imgPost);

                    if(crntUsrID.equals(dbCrntUsr)){

                        btnDelete.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erasePost();
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

    private void erasePost() {
        refClck.removeValue();
        backToNewsFeed();
        Toast.makeText(this, "Successfully deleted", Toast.LENGTH_LONG);

    }

    private void backToNewsFeed()
    {
        Intent mainIntent = new Intent(DeletePost.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
