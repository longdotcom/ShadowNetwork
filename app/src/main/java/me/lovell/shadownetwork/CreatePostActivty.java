package me.lovell.shadownetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class CreatePostActivty extends AppCompatActivity {


    private Toolbar menuTlBar;
    private ProgressDialog progressBar;

    private ImageButton chooseImgPost;
    private Button btnUpdatePosts;
    private EditText usrDescPost;
    private long newsFeedOrderedNewest = 0;
    private static final int img_picker = 1;
    private Uri uriImg;
    private String desc;
    private StorageReference imgPostRef;
    private DatabaseReference refUsr, refPost;
    private FirebaseAuth menuAuth;

    private String crntDateUpdate, crntTimeUpdate, randomName, urlDownloader, idCrntUser;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_activty);


        menuAuth = FirebaseAuth.getInstance();
        idCrntUser = menuAuth.getCurrentUser().getUid();

        imgPostRef = FirebaseStorage.getInstance().getReference();
        refUsr = FirebaseDatabase.getInstance().getReference().child("Users");
        refPost = FirebaseDatabase.getInstance().getReference().child("Posts");


        chooseImgPost = (ImageButton) findViewById(R.id.addimg);
        btnUpdatePosts = (Button) findViewById(R.id.addpost);
        usrDescPost =(EditText) findViewById(R.id.addtext);
        progressBar = new ProgressDialog(this);


        menuTlBar = (Toolbar) findViewById(R.id.uppsttlbr);
        setSupportActionBar(menuTlBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");


        chooseImgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                galleryAction();
            }
        });


        btnUpdatePosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });
    }



    private void ValidatePostInfo()
    {
        desc = usrDescPost.getText().toString();

        if(uriImg == null)
        {
            Toast.makeText(this, "Add a picture to your update", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(desc))
        {
            Toast.makeText(this, "Describe your picture", Toast.LENGTH_LONG).show();
        }
        else
        {
            progressBar.setTitle("Create new update");
            progressBar.setMessage("Your update is being added");
            progressBar.show();
            progressBar.setCanceledOnTouchOutside(true);

            uploadToDB();
        }
    }



    private void uploadToDB()
    {
        Calendar dateCalFord = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        crntDateUpdate = currentDate.format(dateCalFord.getTime());

        Calendar timeCalFord = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        crntTimeUpdate = currentTime.format(dateCalFord.getTime());

        randomName = crntDateUpdate + crntTimeUpdate;


        final StorageReference filePath = imgPostRef.child("Post Images").child(uriImg.getLastPathSegment() + randomName + ".jpg");

        filePath.putFile(uriImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override


            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {


                    urlDownloader = task.getResult().getDownloadUrl().toString();
                    //downloadUrl = filePath.getDownloadUrl().toString();
                    Toast.makeText(CreatePostActivty.this, "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();

                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(CreatePostActivty.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void SavingPostInformationToDatabase()
    {
        refPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    newsFeedOrderedNewest = dataSnapshot.getChildrenCount();
                }
                else{
                    newsFeedOrderedNewest = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        refUsr.child(idCrntUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    final String userFullName = dataSnapshot.child("full_name").getValue().toString();
                    final String userProfileImage = dataSnapshot.child("profile_image").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", idCrntUser);
                    postsMap.put("date", crntDateUpdate);
                    postsMap.put("time", crntTimeUpdate);
                    postsMap.put("description", desc);
                    postsMap.put("postimage", urlDownloader);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userFullName);
                    postsMap.put("newsFeedOrderedNewest", newsFeedOrderedNewest);
                    refPost.child(idCrntUser + randomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        goToNewsFeed();
                                        Toast.makeText(CreatePostActivty.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(CreatePostActivty.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void galleryAction()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, img_picker);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==img_picker && resultCode==RESULT_OK && data!=null)
        {
            uriImg = data.getData();
            chooseImgPost.setImageURI(uriImg);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            goToNewsFeed();
        }

        return super.onOptionsItemSelected(item);
    }



    private void goToNewsFeed()
    {
        Intent mainIntent = new Intent(CreatePostActivty.this, MainActivity.class);
        startActivity(mainIntent);
    }
}