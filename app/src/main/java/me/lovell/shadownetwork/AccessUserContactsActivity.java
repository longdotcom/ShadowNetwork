package me.lovell.shadownetwork;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


// READS USERS CONTACTS INTO ARRAY
// LOOPS ARRAY INTO FIREBASE DB

public class AccessUserContactsActivity extends AppCompatActivity {
    private static final String TAG = "FILTERTAG";
    private ListView contactNames;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    ImageButton importBtn = null;
   // ImageButton gotocreateshadow;

    private List<Contact> arrayContacts;

    private Integer count;
    private FirebaseAuth menuAuth;
    private String idCrntUser, userfullname;
    private DatabaseReference refContacts, ReferenceUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_access_user_contacts);
        Toolbar accessusercontacttoolbar = (Toolbar) findViewById(R.id.accessusercontacttoolbar);
        setSupportActionBar(accessusercontacttoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Access Contacts");

        menuAuth = FirebaseAuth.getInstance();
        idCrntUser = menuAuth.getCurrentUser().getUid();
        refContacts = FirebaseDatabase.getInstance().getReference().child("Contacts");


        // GET CURRENT USERS FULL NAME TO APPEND TO SAVE CONTACTS AS IDENTIFER
        ReferenceUsr = FirebaseDatabase.getInstance().getReference().child("Users").child(idCrntUser);

        // When current user retrieved, access their name for db identifier of contact
        ReferenceUsr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     userfullname = dataSnapshot.child("full_name").getValue().toString(); }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        contactNames = (ListView) findViewById(R.id.contactnames);

        int contactpermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS);

        if(contactpermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS); }

        importBtn = (ImageButton) findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(AccessUserContactsActivity.this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String[] pro = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    ContentResolver contentRes = getContentResolver();
                    Cursor cursor = contentRes.query(ContactsContract.Contacts.CONTENT_URI,
                            pro,
                            null,
                            null,
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

                                        // GRABS USERS CONTACTS FIELDS - NAME, EMAIL, PHONE
                                        // INTO ARRAY OF CONTACT OBJECTS
                                        arrayContacts = new ArrayList<Contact>();
                                        Cursor crsor =  contentRes.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                                        while (crsor.moveToNext()) {
                                            String phoneNum ="";
                                            String emailAdd = "";
                                            String contactId = crsor.getString(crsor.getColumnIndex(ContactsContract.Contacts._ID));

                                            // Contacts name
                                            String name = crsor.getString(crsor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                                            while (phones.moveToNext()) {
                                                // Contacts number
                                                phoneNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); }
                                            phones.close();

                                            // Find Email Addresses
                                            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
                                            while (emails.moveToNext()) {
                                                // Contacts email
                                                emailAdd = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)); }
                                            emails.close();

                                            Log.d(TAG, "                    " + name + " " + phoneNum + " " + emailAdd);

                                            // Array of logged in users contacts
                                            arrayContacts.add(new Contact(name, phoneNum, emailAdd));
                                        }
                                        crsor.close();


                                        // LOOPS ARRAY AND STORES TO DB FOR CURRENT LOGGED IN USERS CONTACTS
                                        count = 0;
                                        for(Contact x: arrayContacts){
                                            HashMap contactMap = new HashMap();
                                            contactMap.put("uid", userfullname);
                                            contactMap.put("name", x.getName());
                                            contactMap.put("email", x.getEmail());
                                            contactMap.put("phone", x.getPhone());

                                            // FOR EACH CONTACT SAVED UNDER LOGGED IN USERS NAME AND INCREMENT COUNT
                                            // For unique identifier of contacts belonging to who

                                            refContacts.child(userfullname +"-"+count.toString()).updateChildren(contactMap)

                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()) {
                                                                //goToNewsFeed();
                                                                //Toast.makeText(AccessUserContactsActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                                                // progressBar.dismiss();
                                                            } else {
                                                                Toast.makeText(AccessUserContactsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                                                                //progressBar.dismiss();
                                                            }
                                                        }
                                                    });

                                            count++;
                                        }



                    if (cursor != null) {
                        List<String> contactsArray = new ArrayList<String>();
                        while (cursor.moveToNext()) {
                            contactsArray.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))); }
                        cursor.close();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccessUserContactsActivity.this, R.layout.detail_contact, R.id.fullname, contactsArray);
                        contactNames.setAdapter(adapter);

                        Intent addfriends = new Intent(AccessUserContactsActivity.this, AddFriendsActivity.class);
                        startActivity(addfriends);
                        finish();
                    }

                } else {
                    Snackbar.make(view, "Cannot access contacts", Snackbar.LENGTH_INDEFINITE);

                }

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



}
