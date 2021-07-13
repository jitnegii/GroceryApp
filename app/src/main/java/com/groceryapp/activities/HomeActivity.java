package com.groceryapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.groceryapp.R;
import com.groceryapp.adapters.ViewPagerAdapter;
import com.groceryapp.database.entities.User;
import com.groceryapp.fragments.BucketFragment;
import com.groceryapp.fragments.HistoryFragment;
import com.groceryapp.fragments.OrderFragment;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    //    TextView nameView;
    private UserViewModel userViewModel;
    private ImageView imageBtn;
    private AppBarLayout appBarLayout;
    private TextView locText;
    ViewPagerAdapter adapter;
    ViewPager2 viewPager;
    BottomNavigationView navigationView;
    private static boolean doubleBackToExitPressedOnce;

    //    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        init();
        OnClickListeners();


        User user = userViewModel.getUser(FirebaseUtils.getUserId());

        if (user != null) {

            String name = user.user_name;
            if (name == null || name.isEmpty()) {
                Log.d(TAG, "Name " + (name == null ? "NULL" : name));
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }

        userViewModel.getUserLiveData(FirebaseUtils.getUserId()).observe(this, liveUser -> {

            if (liveUser != null) {
                if (liveUser.address != null && !liveUser.address.isEmpty()) {
                    locText.setText(liveUser.address);
                }
            }
        });

    }

    private void init() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        imageBtn = findViewById(R.id.profile_image);
        locText = findViewById(R.id.locText);
        appBarLayout = findViewById(R.id.appBarLayout);
        navigationView = findViewById(R.id.bottomNavigation);

        Fragment fragment = new Fragment(R.layout.layout_no_internet);

        adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new OrderFragment());
        adapter.addFragment(new BucketFragment());
        adapter.addFragment(new HistoryFragment("users"));
        adapter.addFragment(fragment);


        viewPager = findViewById(R.id.viewPager);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2, false);

        locText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.order) {
                    viewPager.setCurrentItem(0, false);
                    return true;
                } else if (item.getItemId() == R.id.bucket) {
                    appBarLayout.setExpanded(true, true);
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (item.getItemId() == R.id.history) {
                    appBarLayout.setExpanded(true, true);
                    viewPager.setCurrentItem(2, false);
                    return true;
                }
                return false;
            }
        });


    }

    private void OnClickListeners() {

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
                startActivity(intent);
                //finish();
            }

        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
//        noInternetLayout.setVisibility(AppUtils.internetIsConnected(this) ? View.GONE : View.VISIBLE);
        if (!AppUtils.internetIsConnected(this)) {
            viewPager.setCurrentItem(3, false);
            navigationView.setVisibility(View.GONE);
        } else {
            if (navigationView.getSelectedItemId() == R.id.order) {
                viewPager.setCurrentItem(0, false);
            } else if (navigationView.getSelectedItemId() == R.id.bucket) {
                viewPager.setCurrentItem(1, false);
            } else {
                viewPager.setCurrentItem(2, false);
            }
            navigationView.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() != 0 && viewPager.getCurrentItem() < 4) {
            navigationView.setSelectedItemId(R.id.order);
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
}
