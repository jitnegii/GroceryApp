package com.groceryapp.utility;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.groceryapp.R;

public class FirebaseUtils {

    private static String databaseUrl;
    private static FirebaseUser user;
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase database;

    public static FirebaseUser getUser() {
        if(user == null)
            user = getAuth().getCurrentUser();

        return user;
    }

    public static String getUserId() {
        return getUser().getUid();
    }

    public static FirebaseAuth getAuth() {
        if(mAuth==null)
            mAuth = FirebaseAuth.getInstance();

        return mAuth;
    }

    public static void setDatabaseUrl(Context context){
        if(databaseUrl==null || databaseUrl.isEmpty())
            databaseUrl = "https://"+context.getString(R.string.project_id)+"-default-rtdb.asia-southeast1.firebasedatabase.app/";;
    }

    public static FirebaseDatabase getDatabase() {
        if(database==null) {
            database = FirebaseDatabase.getInstance(databaseUrl);
        }

        return database;
    }

    public static DatabaseReference getDatabaseRef(){
        return getDatabase().getReference();
    }
    public static DatabaseReference getDatabaseRef(String key){
        return getDatabase().getReference(key);
    }

    public static void signOut(){
        getAuth().signOut();
        removePreviousReference();
    }

    public static void removePreviousReference(){
        mAuth=null;
        user=null;
        database=null;
    }

    public static Task<Void> setValue(String key, Object value){
      return getDatabaseRef(key).setValue(value);
    }
}
