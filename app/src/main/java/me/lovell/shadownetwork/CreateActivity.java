package me.lovell.shadownetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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


public class CreateActivity extends AppCompatActivity {

    private EditText createusername;
    private EditText fullname;
    private EditText country;
    private Button saveButton;
    private CircleImageView profilePic;
    private ProgressDialog progressBar;

    private StorageReference picRefMember;
    final static int imageFromGal = 1;

    private FirebaseAuth menuAuth;
    private DatabaseReference loggedInRef;
    String loggedInID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        menuAuth = FirebaseAuth.getInstance();
        loggedInID = menuAuth.getCurrentUser().getUid();
        loggedInRef = FirebaseDatabase.getInstance().getReference().child("Users").child(loggedInID);
        picRefMember = FirebaseStorage.getInstance().getReference().child("Profile Images");

        createusername = (EditText) findViewById(R.id.createUsername);
        fullname = (EditText) findViewById(R.id.createName);
        country = (EditText) findViewById(R.id.createCountry);
        profilePic = (CircleImageView) findViewById(R.id.createImage);
        saveButton = (Button) findViewById(R.id.createBtn);
        progressBar = new ProgressDialog(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfileInfo();
            }
        });

        // for uploading profile image
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent();
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, imageFromGal);
            }
        });

        loggedInRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profile_image")){
                        String img = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.with(CreateActivity.this).load(img).placeholder(R.drawable.profile_img).into(profilePic);

                    }
                    else{

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // method for cropping and uploading image to profile
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

                StorageReference imglocation = picRefMember.child(loggedInID + ".jpg");

                imglocation.putFile(statusUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateActivity.this, "Picture uploaded successfully", Toast.LENGTH_LONG);

                            Task<Uri> getURIres = task.getResult().getMetadata().getReference().getDownloadUrl();

                            getURIres.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String retrieveUrl = uri.toString();

                                    loggedInRef.child("profile_image").setValue(retrieveUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent ownint = new Intent(CreateActivity.this, CreateActivity.class);
                                                startActivity(ownint);

                                                Toast.makeText(CreateActivity.this, "Success", Toast.LENGTH_LONG).show();
                                            } else {
                                                String msgErr = task.getException().getMessage();
                                                Toast.makeText(CreateActivity.this, "Something went wrong: " + msgErr, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(CreateActivity.this, "Error with image", Toast.LENGTH_LONG).show();
            }
        }
    }


    // set up profile attributes
    private void createProfileInfo() {
        String profileUsername = createusername.getText().toString();
        String profileFullname = fullname.getText().toString();
        String profileCountry = country.getText().toString();

        if (TextUtils.isEmpty(profileUsername)) {
            Toast.makeText(this, "Username can not be blank", Toast.LENGTH_LONG);
        } else if (TextUtils.isEmpty(profileFullname)) {
            Toast.makeText(this, "Full name can not be blank", Toast.LENGTH_LONG);
        } else if (TextUtils.isEmpty(profileCountry)) {
            Toast.makeText(this, "Country can not be blank", Toast.LENGTH_LONG);
        } else {
            progressBar.setTitle("Setting up account");
            progressBar.setMessage("This won't take long..");
            progressBar.show();
            progressBar.setCanceledOnTouchOutside(true);

            HashMap loggedInMap = new HashMap();
            loggedInMap.put("user_name", profileUsername);
            loggedInMap.put("full_name", profileFullname);
            loggedInMap.put("locationCountry", profileCountry);
            loggedInMap.put("profileStatus", "Status");
            loggedInMap.put("gender", "n/a");
            loggedInMap.put("dob", "n/a");
            loggedInMap.put("relationshipStatus", "n/a");
            loggedInRef.updateChildren(loggedInMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        goToMain();
                        Toast.makeText(CreateActivity.this, "Your profile has been set up", Toast.LENGTH_LONG).show();
                        progressBar.dismiss();
                    } else {
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(CreateActivity.this, "Problem setting up account" + errorMsg, Toast.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }
                }
            });
        }

    }

    // redirect to news feed once profile set up
    private void goToMain() {
        Intent goToMainIntent = new Intent(CreateActivity.this, MainActivity.class);
        goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToMainIntent);
        finish();
    }
}
