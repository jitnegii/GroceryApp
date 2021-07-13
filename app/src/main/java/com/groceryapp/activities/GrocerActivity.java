package com.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.groceryapp.R;
import com.groceryapp.adapters.ViewPagerAdapter;
import com.groceryapp.fragments.AddItemFragment;
import com.groceryapp.fragments.HistoryFragment;
import com.groceryapp.fragments.OrderFragment;
import com.groceryapp.fragments.RequestFragment;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;

public class GrocerActivity extends AppCompatActivity {

    private static final String TAG = GrocerActivity.class.getSimpleName();
    //    TextView nameView;
    private UserViewModel userViewModel;
    private ImageView imageBtn;
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
        setContentView(R.layout.activity_grocer);


        init();
        OnClickListeners();



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
        navigationView = findViewById(R.id.bottomNavigation);

        Fragment fragment = new Fragment(R.layout.layout_no_internet);

        adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new AddItemFragment());
        adapter.addFragment(new RequestFragment());
        adapter.addFragment(new HistoryFragment("shops"));
        adapter.addFragment(fragment);


        viewPager = findViewById(R.id.viewPager);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, false);

        locText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrocerActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.items) {
                    viewPager.setCurrentItem(0, false);
                    return true;
                } else if (item.getItemId() == R.id.request) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                }else if (item.getItemId() == R.id.history){
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
                Intent intent = new Intent(GrocerActivity.this, InfoActivity.class);
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

        if(!AppUtils.internetIsConnected(this)){
            viewPager.setCurrentItem(4,false);
            navigationView.setVisibility(View.GONE);
        }else {
            if(navigationView.getSelectedItemId()==R.id.items){
                viewPager.setCurrentItem(0,false);
            }else if(navigationView.getSelectedItemId()==R.id.request){
                viewPager.setCurrentItem(1,false);
            }else if(navigationView.getSelectedItemId()==R.id.history){
                viewPager.setCurrentItem(1,false);
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

        if (viewPager.getCurrentItem() != 0 && viewPager.getCurrentItem()<4) {
            navigationView.setSelectedItemId(R.id.items);
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