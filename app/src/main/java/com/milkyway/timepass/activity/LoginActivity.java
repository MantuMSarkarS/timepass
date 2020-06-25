package com.milkyway.timepass.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.milkyway.timepass.R;
import com.milkyway.timepass.databinding.ActivityLoginBinding;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginActivity<FacebookLoginActivity> extends AppCompatActivity {


    private static final String EMAIL = "email";
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "profile";
    ActivityLoginBinding mBinding;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setActivity(this);
        mBinding.forgetPswd.setPaintFlags(mBinding.forgetPswd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBinding.register.setPaintFlags(mBinding.forgetPswd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBinding.manualLogin.setOnClickListener(v -> {
            if (!textInputEmail() | !textInputPassword()) {
                Toast.makeText(this, "Fill requird fileds", Toast.LENGTH_SHORT).show();
                return;
            } else {
                mFirebaseAuth.signInWithEmailAndPassword(mBinding.textInputEmail.getEditText().getText().toString().trim(), mBinding.textInputPassword.getEditText().getText().toString().trim())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
        mBinding.register.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        mBinding.forgetPswd.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        };


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mBinding.signInButton.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
            }
        });
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        mBinding.fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                handleFacebookAccessToken(loginResult.getAccessToken());

                            }
                        });
                        Bundle bundle = new Bundle();
                        bundle.putString("fields", "first_name,last_name,email,id");
                        graphRequest.setParameters(bundle);
                        graphRequest.executeAsync();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "User has not log in", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public boolean textInputEmail() {
        String email = mBinding.textInputEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            mBinding.textInputEmail.setError("Email field can't be empty");
            return false;
        } else {
            mBinding.textInputEmail.setError(null);
            return true;
        }
    }

    public boolean textInputPassword() {
        String email = mBinding.textInputPassword.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            mBinding.textInputPassword.setError("Password field can't be empty");
            return false;
        } else {
            mBinding.textInputPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void signIn() {
        Intent signintent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signintent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            if (user != null) {
                                String first_name = user.getDisplayName();
                                String email = user.getEmail();
                                String id = user.getUid();
                                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("name", first_name);
                                intent.putExtra("email", email);
                                intent.putExtra("image", image_url);
                                startActivity(intent);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
 /*   private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }*/

}
