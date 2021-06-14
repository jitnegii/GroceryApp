package com.groceryapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.groceryapp.database.entities.User;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AnimationUtils;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    GoogleSignInClient mGoogleSignInClient;
    CardView googleSignIn;
    CardView sendOTP;
    EditText editText;
    TextView otpBtn;
    ActivityResultLauncher<Intent> mGetContent;
    View rootView;
    String mVerificationId;
    boolean otpSent;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViews();

        setOnClickListener();

        mGoogleSignInClient = AppUtils.getSignInClient(this);

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK) {
                            Log.d(TAG, "RESULT_OK");
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);

                                if (account != null)
                                    firebaseAuthWithGoogle(account.getIdToken());

                            } catch (ApiException e) {

                                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

                            }
                        }
                    }
                });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }


    private void initViews() {

        rootView = findViewById(R.id.rootView);
        googleSignIn = findViewById(R.id.googleSignIn);
        otpBtn = findViewById(R.id.OTP_btn);
        editText = findViewById(R.id.editView);
        sendOTP = findViewById(R.id.sendOTP);

    }

    private void setOnClickListener() {

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn.startAnimation(AnimationUtils.getFadeIn(getApplicationContext()));
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                mGetContent.launch(signInIntent);
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP.startAnimation(AnimationUtils.getFadeIn(getApplicationContext()));

                if (otpSent) {

                    Log.d(TAG,"verifying code");
                    if (mVerificationId == null) {
                        Log.e(TAG,"verifyingID null");
                        return;
                    }

                    String code = editText.getText().toString().replace(" ", "");
                    if (TextUtils.isDigitsOnly(code) && !code.isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                        signInWithPhoneAuthCredential(credential);
                    }else {
                        Toast.makeText(LoginActivity.this,"Wrong OTP",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String number = editText.getText().toString().replace(" ", "");
                    if (TextUtils.isDigitsOnly(number) && number.length() == 10) {
                        number = "+91" + number;
                        verifyPhoneNumber(number);
                        toggleOTPButton(true);
                        otpSent = true;
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void toggleOTPButton(Boolean next) {
        if (next) {
            otpBtn.setText(getString(R.string.verify));
            editText.setText("");
            editText.setHint(getString(R.string.otp));

        } else {
            otpBtn.setText(getString(R.string.send_otp));
            editText.setText("");
            editText.setHint(getString(R.string.phone_number));
        }
    }


    private void verifyPhoneNumber(String phoneNumber) {

        FirebaseAuthSettings firebaseAuthSettings = FirebaseAuth.getInstance().getFirebaseAuthSettings();

        //firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, "123456");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {

                                Log.d(TAG, "onVerificationCompleted:" + credential);

                                if (credential != null)
                                    signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {

                                Log.w(TAG, "onVerificationFailed", e);

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                                    Toast.makeText(LoginActivity.this, "Invalid otp", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Invalid credentials", e);
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    Log.e(TAG, "Too many requests", e);
                                }


                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Log.d(TAG, "onCodeSent:" + verificationId);

                                mVerificationId = verificationId;

                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseUtils.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithGoogle:success");

                            FirebaseUtils.setDatabaseUrl(LoginActivity.this);
                            FirebaseUtils.getDatabaseRef("users").child(FirebaseUtils.getUserId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);

                                            if (user == null) {
                                                addUserToDatabase(true);
                                            }else {
                                                userViewModel.insert(user);
                                                startHomeActivity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Snackbar.make(rootView, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseUtils.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUtils.setDatabaseUrl(LoginActivity.this);
                            FirebaseUtils.getDatabaseRef("users").child(FirebaseUtils.getUserId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);

                                            if (user == null) {
                                                addUserToDatabase(false);
                                            }else{
                                                startHomeActivity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void addUserToDatabase(boolean signInWithEmail) {
        Log.d(TAG, "signInWithCredential:success");
        FirebaseUser fUser = FirebaseUtils.getUser();

        User user = new User();
        user.setUserName(signInWithEmail ? fUser.getDisplayName() : "");
        user.setUser_id(fUser.getUid());
        user.setNumberOrEmail(signInWithEmail ? fUser.getEmail() : fUser.getPhoneNumber());
        user.setPhotoUri(fUser.getPhotoUrl() == null ? "" : fUser.getPhotoUrl().toString());

        HashMap<String, Object> userMap = new HashMap<>();

        Log.d(TAG, "URL " + fUser.getPhotoUrl());

        userMap.put("user_id", user.getUser_id());
        userMap.put("number_or_email", user.getNumberOrEmail());
        userMap.put("user_name", user.getUserName());
        userMap.put("photoUrl", user.getPhotoUri());


        FirebaseUtils.getDatabaseRef("users").child(user.getUser_id()).setValue(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userViewModel.insert(user);
                            Log.d(TAG, "Added " + user.getUserName());
                            startHomeActivity();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Update failed");
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (otpSent) {
            toggleOTPButton(false);
            mVerificationId = null;
            otpSent = false;
            return;
        }
        super.onBackPressed();
    }
}
