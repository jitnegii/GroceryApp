package com.groceryapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.groceryapp.database.entities.User;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AnimationUtils;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int TIME_OUT = 20000;
    GoogleSignInClient mGoogleSignInClient;
    CardView googleSignIn;
    CardView sendOTP;
    EditText editText;
    TextView otpBtn;
    static volatile boolean fetchedUser;
    ActivityResultLauncher<Intent> mGetContent;
    View rootView;
    String mVerificationId;
    boolean otpSent;
    ProgressDialog pd;
    final Handler handler = new Handler();
    final LoginCallback loginCallback = new LoginCallback(this);
    private UserViewModel userViewModel;
    private static boolean doubleBackToExitPressedOnce;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    Query userQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViews();

        setOnClickListener();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUtils.removePreviousReference();

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
                                else {
                                    Log.e(TAG, "signInResult:failed code= failed");

                                }

                            } catch (ApiException e) {

                                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

                            }
                        } else {
                            Log.d(TAG, "NOT_RESULT_OK");
                        }
                    }
                });


        fetchedUser = false;
        reference = FirebaseUtils.getDatabaseRef();


        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging In");
        pd.setCancelable(false);
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

                if (!AppUtils.internetIsConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                mGetContent.launch(signInIntent);
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP.startAnimation(AnimationUtils.getFadeIn(getApplicationContext()));

                if (!AppUtils.internetIsConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (otpSent) {

                    Log.d(TAG, "verifying code");
                    if (mVerificationId == null) {
                        Log.e(TAG, "verifyingID null");
                        return;
                    }

                    String code = editText.getText().toString().replace(" ", "");
                    if (TextUtils.isDigitsOnly(code) && !code.isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
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


    public void toggleOTPButton(Boolean next) {
        if (next) {
            otpBtn.setText(getString(R.string.verify));
            editText.setText("");
            editText.setHint(getString(R.string.otp));

        } else {
            otpBtn.setText(getString(R.string.send_otp));
            editText.setText("");
            editText.setHint(getString(R.string.phone_number));
            mVerificationId = null;
            otpSent = false;
        }
    }


    private void verifyPhoneNumber(String phoneNumber) {

        FirebaseAuthSettings firebaseAuthSettings = mAuth.getFirebaseAuthSettings();

        //firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, "123456");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
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

        Log.d(TAG, "signInWithGoogle: processing");

        if (pd != null)
            pd.show();

        handler.postDelayed(loginCallback, TIME_OUT);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            Log.d(TAG, "signInWithGoogle:success");

                            FirebaseUser fUser = task.getResult().getUser();

                            if (fUser == null)
                                Log.e(TAG, "user null");

                            reference.child("users")
                                    .child(fUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            fetchedUser = true;
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                userViewModel.insert(user);
                                                if (user.is_shop)
                                                    startGrocerActivity();
                                                else
                                                    startHomeActivity();
                                            } else {
                                                addUserToDatabase(fUser);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            Log.e(TAG, "Getting user from Firebase:failure", error.toException());
                                        }
                                    });

                        } else {

                            Log.e(TAG, "signInWithCredential:failure", task.getException());

                        }

                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        Log.d(TAG, "signInWithCredential: processing");

        if (pd != null)
            pd.show();

        handler.postDelayed(loginCallback, TIME_OUT);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "signInWithCredential:success ");

                        if (task.isSuccessful()) {

                            FirebaseUser fUser = task.getResult().getUser();

                            if (fUser == null)
                                Log.e(TAG, "user null");

                            reference.child("users")
                                    .child(fUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            fetchedUser = true;
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                userViewModel.insert(user);
                                                if (user.is_shop)
                                                    startGrocerActivity();
                                                else
                                                    startHomeActivity();
                                            } else {
                                                addUserToDatabase(fUser);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            Log.e(TAG, "Getting user from Firebase:failure", error.toException());
                                        }
                                    });

                        } else {

                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }


    private void addUserToDatabase(final FirebaseUser fUser) {

        Log.d(TAG, "Adding to database");

        User user = new User();
        user.user_name = fUser.getDisplayName();
        user.user_id = fUser.getUid();
        user.is_shop = false;
        user.email = fUser.getEmail();
        user.number = fUser.getPhoneNumber();
        user.photo = fUser.getPhotoUrl() == null ? "" : fUser.getPhotoUrl().toString();

        userViewModel.insert(user);

        reference.child("users").child(user.user_id).setValue(user);
        Log.e("Path", reference.toString());

        startHomeActivity();


    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void startGrocerActivity() {
        Intent intent = new Intent(LoginActivity.this, GrocerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (otpSent) {
            toggleOTPButton(false);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back once more to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {

        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(loginCallback);
        dismissProgressDialog();
        super.onStop();
    }

    public void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
        }
    }


    private static class LoginCallback implements Runnable {

        WeakReference<LoginActivity> weakContext;

        LoginCallback(LoginActivity activity) {
            weakContext = new WeakReference<>(activity);
        }

        @Override
        public void run() {


            if (!fetchedUser) {

                LoginActivity activity = weakContext.get();
                if (activity != null) {
                    AppUtils.signOut(activity);
                    activity.dismissProgressDialog();
                    activity.toggleOTPButton(false);
                    fetchedUser = false;
                    Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
