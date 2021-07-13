package com.groceryapp.utility;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groceryapp.BuildConfig;

public class FirebaseUtils {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase database;

    private static FirebaseStorage firebaseStorage;

    public static FirebaseUser getUser() {
        return getAuth().getCurrentUser();

    }

    public static String getUserId() {

        if (getUser() == null)
            return null;
        return getUser().getUid();
    }

    public static FirebaseAuth getAuth() {
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        return mAuth;

    }


    public static FirebaseDatabase getDatabaseInstance() {
        if (database == null)
            database = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL);
        return database;

    }

    public static DatabaseReference getDatabaseRef() {
        return getDatabaseInstance().getReference();
    }

    public static DatabaseReference getDatabaseRef(String key) {
        return getDatabaseInstance().getReference(key);
    }

    public static void signOut() {
        getAuth().signOut();
        removePreviousReference();
    }

    public static void removePreviousReference() {
        mAuth = null;
        database = null;
        firebaseStorage = null;
    }

    public static Task<Void> setValue(String key, Object value) {
        return getDatabaseRef(key).setValue(value);
    }


    public static FirebaseStorage getStorageInstance() {
        if (firebaseStorage == null)
            firebaseStorage = FirebaseStorage.getInstance(BuildConfig.FIREBASE_STORAGE_URL);
        return firebaseStorage;

    }

    public static StorageReference getStorageRef() {
        return getStorageInstance().getReference();
    }
}
