package com.groceryapp.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;

import java.util.HashMap;

public class ViewUtils {

    private static Dialog dialog;

    public static Dialog createUpdateUserNameDialog(Activity context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_username);

        CardView updateBtn = dialog.findViewById(R.id.update);
        EditText firstName = dialog.findViewById(R.id.firstName);
        EditText lastName = dialog.findViewById(R.id.lastName);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first = firstName.getText().toString().trim();
                String last = lastName.getText().toString().trim();

                if (first.length() > 0 && last.length() > 0) {
                    HashMap<String, Object> map = new HashMap<>();

                    final String name = first+" "+last;
                    map.put("user_name", name);


                    FirebaseUtils.getDatabaseRef("users").child( FirebaseUtils.getUser().getUid()).updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            MyRoomDatabase.databaseWriteExecutor.execute(() ->
                                    MyRoomDatabase.getUserDao(context).updateUserName(FirebaseUtils.getUser().getUid(), first + " " + last));
                            Log.d("UserDialog", "UserUpdated");

                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            FirebaseUtils.getUser().updateProfile(request);

                            dialog.dismiss();

                        }
                    });


                } else {
                    Toast.makeText(context, "Both fields are required", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return  dialog;

    }
}
