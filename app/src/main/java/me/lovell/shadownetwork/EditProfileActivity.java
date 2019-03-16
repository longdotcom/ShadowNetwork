package me.lovell.shadownetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar menuBar;
    private EditText editstatus;
    private EditText editname;
    private EditText editlocation;
    private EditText editdob;
    private EditText editgender;
    private EditText editrelationship;
    private EditText editusrname;
    private Button editbtn;
    private CircleImageView editimg;
    private ProgressDialog progressBar;
    private StorageReference picRefMember;



    private DatabaseReference editdb;
    private FirebaseAuth menuAuth;
    private String loggedinUsr;
    final static int imageFromGal = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        menuAuth = FirebaseAuth.getInstance();
        loggedinUsr = menuAuth.getCurrentUser().getUid();
        editdb = FirebaseDatabase.getInstance().getReference().child("Users").child(loggedinUsr);
        picRefMember = FirebaseStorage.getInstance().getReference().child("Profile Images");

        menuBar = (Toolbar) findViewById(R.id.edittoolbr);
        setSupportActionBar(menuBar);
        getSupportActionBar().setTitle("Edit profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editstatus = (EditText) findViewById(R.id.editstatus);
        editname = (EditText) findViewById(R.id.editname);
        editlocation = (EditText) findViewById(R.id.editlocation);
        editdob = (EditText) findViewById(R.id.editbirth);
        editgender = (EditText) findViewById(R.id.editgender);
        editrelationship = (EditText) findViewById(R.id.editrelation);
        editusrname = (EditText) findViewById(R.id.editusername);
        editbtn = (Button) findViewById(R.id.editbtn);
        editimg = (CircleImageView) findViewById(R.id.editimage);
        progressBar = new ProgressDialog(this);



        editdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String editimgstr = dataSnapshot.child("profile_image").getValue().toString();
                    String editusrnmestr = dataSnapshot.child("user_name").getValue().toString();
                    String editnamestr = dataSnapshot.child("full_name").getValue().toString();
                    String editstatusstr = dataSnapshot.child("profileStatus").getValue().toString();
                    String editbirthstr = dataSnapshot.child("dob").getValue().toString();
                    String editgenderstr = dataSnapshot.child("gender").getValue().toString();
                    String editrelationstr = dataSnapshot.child("relationshipStatus").getValue().toString();
                    String editlocatestr = dataSnapshot.child("locationCountry").getValue().toString();

                    Picasso.with(EditProfileActivity.this).load(editimgstr).placeholder(R.drawable.profile_img).into(editimg);

                    editusrname.setText(editusrnmestr);
                    editname.setText(editnamestr);
                    editrelationship.setText(editrelationstr);
                    editgender.setText(editgenderstr);
                    editdob.setText(editbirthstr);
                    editlocation.setText(editlocatestr);
                    editstatus.setText(editstatusstr);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUsrAccnt();
            }
        });

        editimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent();
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, imageFromGal);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == imageFromGal && resultCode == RESULT_OK && data != null) {
            Uri picuri = data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult imageRes = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri statusUri = imageRes.getUri();

                StorageReference imglocation = picRefMember.child(loggedinUsr + ".jpg");

                imglocation.putFile(statusUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Picture uploaded successfully", Toast.LENGTH_LONG);

                            Task<Uri> getURIres = task.getResult().getMetadata().getReference().getDownloadUrl();

                            getURIres.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String retrieveUrl = uri.toString();

                                    editdb.child("profile_image").setValue(retrieveUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent ownint = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                                                startActivity(ownint);

                                                Toast.makeText(EditProfileActivity.this, "Success", Toast.LENGTH_LONG).show();
                                            } else {
                                                String msgErr = task.getException().getMessage();
                                                Toast.makeText(EditProfileActivity.this, "Something went wrong: " + msgErr, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(EditProfileActivity.this, "Error with image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkUsrAccnt() {
        String newusrnme = editusrname.getText().toString();
        String newname = editname.getText().toString();
        String newrelation = editrelationship.getText().toString();
        String newgender = editgender.getText().toString();
        String newdob = editdob.getText().toString();
        String newlocation = editlocation.getText().toString();
        String newstatus = editstatus.getText().toString();

        if(TextUtils.isEmpty(newusrnme)){
            Toast.makeText(this, "Enter new username", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newname)){
            Toast.makeText(this, "Enter new name", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newrelation)){
            Toast.makeText(this, "Enter relationship status", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newgender)){
            Toast.makeText(this, "Enter gender", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newdob)){
            Toast.makeText(this, "Enter date of birth", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newlocation)){
            Toast.makeText(this, "Enter location/country", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(newstatus)){
            Toast.makeText(this, "Enter profile status", Toast.LENGTH_LONG).show();
        }
        else{
            saveEditProfile(newusrnme, newname, newrelation, newgender, newdob, newlocation, newstatus);
        }

    }

    private void saveEditProfile(String newusrnme, String newname, String newrelation, String newgender, String newdob, String newlocation, String newstatus) {
        HashMap editMap = new HashMap();
        editMap.put("user_name", newusrnme);
        editMap.put("full_name", newname);
        editMap.put("relationshipStatus", newrelation);
        editMap.put("gender", newgender);
        editMap.put("dob", newdob);
        editMap.put("locationCountry", newlocation);
        editMap.put("profileStatus", newstatus);
        editdb.updateChildren(editMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){
                    goToMain();
                    Toast.makeText(EditProfileActivity.this, "Details updated", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private void goToMain() {
        Intent goToMainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToMainIntent);
        finish();
    }
}
