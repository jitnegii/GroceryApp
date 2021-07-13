package com.groceryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void updateUI(final FirebaseUser fUser) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;

                if (fUser == null) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    Log.e(TAG, "User NULL");
                } else {

                    User user = MyRoomDatabase.getUserDao(SplashActivity.this).getUser(fUser.getUid());

                    if (user == null || !user.is_shop) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, GrocerActivity.class);
                    }
                    Log.e(TAG, "User " + (user == null ? "NULL" : user.user_name));

                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }


}
