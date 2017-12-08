package com.upt.cti.labseter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    // Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQ_SIGNIN = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup authentication
        mAuth = FirebaseAuth.getInstance();
        /*
        * Listen to any changes in the state of the user.
        * The listener  will fire when it is attached, and then every time a user logins in, out or his token expires.
        * The triggered method returns the currently logged in user (or null).
        */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    TextView tLoginDetail = (TextView) findViewById(R.id.tLoginDetail);
                    TextView tUser = (TextView) findViewById(R.id.tUser);
                    tLoginDetail.setText("Firebase ID: " + user.getUid());
                    tUser.setText("Email: " + user.getEmail());


                    AppState.get().setUserId(user.getUid());
                    attachDBListener(user.getUid());
                } else {
                    // If user not signed in, start new activity.
                    startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), REQ_SIGNIN);
                }
            }
        };
    }

    private void attachDBListener(String uid) {

        // setup firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        databaseReference.child("users").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            //...
        });
    }

    // retreive result from login activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNIN) {
            if (resultCode == RESULT_OK) {
                // get data from intent
                String user = data.getStringExtra("user");
                String pass = data.getStringExtra("pass");
                // ...
            } else if (resultCode == RESULT_CANCELED) {
                // data was not retrieved
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
