package com.groceryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.groceryapp.R;
import com.groceryapp.fragments.GrocersInfoFragment;
import com.groceryapp.fragments.SettingFragment;
import com.groceryapp.utility.AppUtils;

public class InfoActivity extends AppCompatActivity implements SettingFragment.ClickListener {

    SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadSettingFrag();

    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
        }
    }

    private void loadSettingFrag() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, getSettingFragment())
                .commit();
    }

    private SettingFragment getSettingFragment() {
        if (settingFragment == null)
            settingFragment = new SettingFragment(this);
        return settingFragment;
    }

    @Override
    public void onClick(int actionId) {
        Intent intent;
        switch (actionId) {
            case 0:
                intent = new Intent(InfoActivity.this, ProfileActivity.class);
                intent.putExtra("can_go_back", true);
                startActivity(intent);
                break;
            case 1:
//                loadFragment(new GrocersInfoFragment());
                intent = new Intent(InfoActivity.this, ProfileActivity.class);
                intent.putExtra("can_go_back", true);
                intent.putExtra("change",true);
                startActivity(intent);
                break;
            case 2:
                AppUtils.signOut(this);
                intent = new Intent(InfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);

        if (fragment instanceof GrocersInfoFragment) {
            loadSettingFrag();
            return;
        }

        super.onBackPressed();
    }
}