package com.upt.cti.labseter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by andreeagb on 12/3/2017.
 */

/*
 * This activity holds all the login data.
 */
public class SignupActivity extends Activity {

    private static final String TAG = "labster-login";

    // views
    private TextView tStatus;
    private TextView tDetail;
    private EditText eEmail;
    private EditText ePass;
    private Button bLogin;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RC_SIGN_IN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // views
        tStatus = (TextView) findViewById(R.id.tStatus);
        tDetail = (TextView) findViewById(R.id.tDetail);
        eEmail = (EditText) findViewById(R.id.eEmail);
        ePass = (EditText) findViewById(R.id.ePass);
        bLogin = (Button)findViewById(R.id.bGoogle);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
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

    // TODO(andreeagb): add here a create user form.
    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (!task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    */

    private boolean ok = true;
    private boolean signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        ok = true;
        if (!validateForm()) {
            return false;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        return ok;
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = eEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            eEmail.setError("Required.");
            valid = false;
        } else {
            eEmail.setError(null);
        }

        String password = ePass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ePass.setError("Required.");
            valid = false;
        } else {
            ePass.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            tStatus.setText("Email user: " + user.getEmail());
            tDetail.setText("Firebase user: " + user.getUid());

            findViewById(R.id.lSignIn).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
        } else {
            tStatus.setText("Signed out");
            tDetail.setText(null);

            findViewById(R.id.lSignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
        }
    }

    public void clicked(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                createAccount(eEmail.getText().toString(), ePass.getText().toString());
                break;
            case R.id.bSignIn:
                if (signIn(eEmail.getText().toString(), ePass.getText().toString())){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user", eEmail.getText().toString());
                    intent.putExtra("pass", ePass.getText().toString());
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
}

