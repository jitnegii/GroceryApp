package com.groceryapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.utility.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    FloatingActionButton imageBtn;
    CircleImageView imageView;
    EditText nameEdit, contactEdit;
    TextView addressText, updateBtnText;
    ImageView nameEditBtn, addressEditBtn, contactEditBtn;
    CardView updateBtn;
    UserViewModel userViewModel;
    ActivityResultLauncher<Intent> mGetContent;
    Uri uri;
    boolean profileChanged;
    UploadTask uploadTask;
    ProgressDialog pd;
    boolean changeToGrocer = false;
    boolean canGoBack = false;

    DatabaseReference database;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        FirebaseUtils.removePreviousReference();

        changeToGrocer = getIntent().getBooleanExtra("change", false);
        canGoBack = getIntent().getBooleanExtra("can_go_back", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");

            if (canGoBack || changeToGrocer)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canGoBack)
                    finish();
            }
        });

        initView();
        setOnClickListeners();
        pd = new ProgressDialog(this);

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData() != null) {
                                uri = result.getData().getData();

                                loadImage(uri, 3);


                            } else {
                                Log.e(TAG, "Image uri is null");
                            }
                        }
                    }
                });

        database = FirebaseUtils.getDatabaseRef();

    }


    private void loadImage(Uri uri, int tryLeft) {
        if (tryLeft == 0)
            return;
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(uri)
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        loadImage(uri, tryLeft - 1);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        setBitmap(resource);
                        return false;
                    }
                })
                .into(imageView);
    }

    private void initView() {
        imageBtn = findViewById(R.id.fab);
        imageView = findViewById(R.id.profile_image);
        nameEdit = findViewById(R.id.nameEdit);
        addressText = findViewById(R.id.locText);
        contactEdit = findViewById(R.id.contactEdit);
        nameEditBtn = findViewById(R.id.nameEditBtn);
        addressEditBtn = findViewById(R.id.addressEditBtn);
        contactEditBtn = findViewById(R.id.contactEditBtn);
        updateBtn = findViewById(R.id.updateProfile);
        updateBtnText = findViewById(R.id.update_button_text);

        if (changeToGrocer) {
            updateBtnText.setText("Change to grocer");
        }

        userViewModel.getUserLiveData(FirebaseUtils.getUserId()).observe(this, user -> {

            if (user == null)
                return;

            if (!changeToGrocer) {
                nameEdit.setText(user.user_name == null ? "" : user.user_name);

                if (user.number != null && !user.number.isEmpty()) {
                    contactEditBtn.setVisibility(View.GONE);
                    contactEditBtn.setClickable(false);
                    contactEdit.setFocusable(false);
                    contactEdit.setText(user.number);
                }

                String uri = user.photo;

                if (uri != null && !uri.isEmpty()) {
                    Glide.with(this)
                            .load(uri)
                            .into(imageView);
                } else {
                    Log.e("Uri", "null");
                }
            }

            addressText.setText(user.address == null ? "" : user.address);


        });

    }

    private void setOnClickListeners() {

        nameEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdit.setEnabled(true);
                nameEdit.requestFocus();

                AppUtils.showSoftKeyboard(ProfileActivity.this, v.getRootView());
            }
        });

        addressEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        contactEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactEdit.setEnabled(true);
                contactEdit.requestFocus();
                AppUtils.showSoftKeyboard(ProfileActivity.this, v.getRootView());
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                mGetContent.launch(intent);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!AppUtils.internetIsConnected(ProfileActivity.this)) {
                    ViewUtils.connectToInternetToast(ProfileActivity.this);
                    return;
                }

                String name = nameEdit.getText().toString().trim();
                String number = contactEdit.getText().toString().replace(" ", "");
                String address = addressText.getText().toString();

                number = number.replace("+", "");
                if (number.startsWith("91") && number.length() == 12)
                    number = number.replaceFirst("91", "");

                if (name.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Invalid name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (number.length() != 10 && TextUtils.isDigitsOnly(number)) {
                    Toast.makeText(ProfileActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                    return;
                }

                number = "+91" + number;

                if (changeToGrocer) {
                    if (address.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Address is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (uploadTask != null && uploadTask.isInProgress())
                    uploadTask.cancel();

                pd.setMessage("Updating");
                pd.setCancelable(false);
                pd.show();

                if (profileChanged && getBitmap() != null) {

                    updateDatabaseWithImg(name, number, getBitmap());
                } else {
                    updateDatabase(name, number, null);
                }

            }
        });
    }

    private void updateDatabaseWithImg(final String name, final String number, final Bitmap bitmap) {


        final StorageReference fileReference = FirebaseUtils.getStorageRef()
                .child("profile_images").child(System.currentTimeMillis() + ".jpeg");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] array = out.toByteArray();
        uploadTask = fileReference.putBytes(array);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUri = downloadUri.toString();

                    updateDatabase(name, number, imageUri);

                } else {
                    updateDatabase(name, number, null);
                    Log.e("Update image ", "Failed");
                }

            }
        });
    }

    private void updateDatabase(final String name, final String number, final String imageUri) {

//        HashMap<String, Object> map = new HashMap<>();
//        map.put("number", number);
//        map.put("user_name", name);

        User user = MyRoomDatabase.getUserDao(this).getUser(FirebaseUtils.getUserId());

        user.number = number;
        user.user_name = name;
        user.is_shop = changeToGrocer;

        if (imageUri != null && !imageUri.isEmpty())
            user.photo = imageUri;

        database.child("users")
                .child(FirebaseUtils.getUserId())
                .updateChildren(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userViewModel.updateUserName(FirebaseUtils.getUserId(), name);
                            userViewModel.updateNumber(FirebaseUtils.getUserId(), number);
                            userViewModel.updateIsShop(FirebaseUtils.getUserId(), changeToGrocer);

                            if (changeToGrocer) {
                                copyNodeToShop();
                            } else {
                                pd.dismiss();
                                finish();
                            }


                        } else {
                            Log.e("Update database", "Failed");
                            pd.dismiss();
                        }

                    }
                });

    }

    private void copyNodeToShop() {
        FirebaseUtils.getDatabaseRef("users")
                .child(FirebaseUtils.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseUtils.getDatabaseRef("shops")
                                .child(FirebaseUtils.getUserId())
                                .setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error != null) {
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            Log.e("Copying to shops", "Failed");
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                            Log.e("Copying to shops", "Success");
                                        }
                                        pd.dismiss();
                                        Intent intent = new Intent(ProfileActivity.this,GrocerActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteOldInsertNew(final String imageUri) {
        User user = userViewModel.getUser(FirebaseUtils.getUserId());
        if (user != null) {
            String uri = user.photo;

            if (uri != null && !uri.isEmpty()) {
                FirebaseUtils.getStorageInstance()
                        .getReferenceFromUrl(uri)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("Delete old image", "Success");
                        } else {
                            Log.e("Delete old image", "Failed");
                        }
                        userViewModel.updatePhoto(FirebaseUtils.getUserId(), imageUri);
                    }

                });
            } else {
                userViewModel.updatePhoto(FirebaseUtils.getUserId(), imageUri);
            }
        } else {
            Log.e(TAG, "User null");
        }

    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (bitmap != null) {
            profileChanged = true;
            imageView.setImageBitmap(bitmap);
            Log.e("BITMAP", "After compression " + bitmap.getByteCount());
        } else {
            Log.e("BITMAP", "After compression null");
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
        super.onDestroy();
    }
}