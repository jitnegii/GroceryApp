package com.groceryapp.utility;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;

public class AppUtils {

    private static User user;
    public static User getCurrentUser(Context context){
        if(user!= null)
            return user;

        user = MyRoomDatabase.getUserDao(context).getUser(FirebaseUtils.getUser().getUid());

        if(user == null){
            FirebaseUtils.getDatabaseRef("users/"+FirebaseUtils.getUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    MyRoomDatabase.getUserDao(context).insertUser(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return user;
    }

    public static void signOut(Context context){
        user = null;
        getSignInClient(context).signOut();
        FirebaseUtils.signOut();
    }

    public static GoogleSignInClient getSignInClient(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }
}
