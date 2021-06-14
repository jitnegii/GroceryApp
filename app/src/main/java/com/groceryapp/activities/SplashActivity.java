package com.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;
import com.groceryapp.utility.FirebaseUtils;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        updateUI(FirebaseUtils.getUser());
    }

    private void updateUI(final FirebaseUser user) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;

                if (user == null) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    Log.e(TAG,"User NULL");
                } else {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                    Log.e(TAG,"User "+ user.getDisplayName());
                }

                startActivity(intent);
                finish();
            }
        }, 1000);
    }


}
