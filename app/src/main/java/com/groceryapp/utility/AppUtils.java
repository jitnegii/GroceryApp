package com.groceryapp.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.groceryapp.activities.LoginActivity;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppUtils {


    public static boolean internetIsConnected(Context context) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

                return activeNetworkInfo != null
                        && (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE);

            } else {

                Network network = cm.getActiveNetwork();
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);

                return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }


        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return false;
    }

    public static void signOut(Context context) {
        getSignInClient(context).signOut();
        FirebaseUtils.signOut();

    }

    public static GoogleSignInClient getSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        view.clearFocus();
        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }


    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputManager.showSoftInput(view,0);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }


}
