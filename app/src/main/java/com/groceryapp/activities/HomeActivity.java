package com.groceryapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.groceryapp.R;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.utility.ViewUtils;

public class HomeActivity extends AppCompatActivity {

    TextView nameView;
    private UserViewModel userViewModel;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        nameView = findViewById(R.id.name);
//        CardView button = findViewById(R.id.signOutBtn);
//
//        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
//
//        FirebaseUtils.removePreviousReference();
//        FirebaseUtils.setDatabaseUrl(this);
//
//        userViewModel.getUser(FirebaseUtils.getUserId()).observe(this,user -> {
//            if(user==null)
//                return;
//
//            String name = user.getUserName();
//            if (name == null || name.isEmpty()) {
//                dialog = ViewUtils.createUpdateUserNameDialog(this);
//                dialog.show();
//            } else
//                nameView.setText("Name " + name);
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppUtils.signOut(HomeActivity.this);
//                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(dialog != null){
            dialog.dismiss();
            dialog=null;
        }
        super.onDestroy();
    }
}
