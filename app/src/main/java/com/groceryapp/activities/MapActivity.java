package com.groceryapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.groceryapp.R;
import com.groceryapp.adapters.SearchedItemAdapter;
import com.groceryapp.database.entities.User;
import com.groceryapp.models.UserViewModel;
import com.groceryapp.utility.AppUtils;
import com.groceryapp.utility.FirebaseUtils;
import com.groceryapp.utility.ViewUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapActivity.class.getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOC_PERMISSION_RQST_CODE = 1001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private final LatLng DEFAULT_LAT_LONG = new LatLng(29.746188, 78.468560);

    private Geocoder geocoder;

    ImageView backNav;
    CardView updateBtn;
    EditText searchBar;
    FloatingActionButton myLoc;
    ListView searchedList;
    UserViewModel userViewModel;
    TextView locText;


    private GoogleMap map;
    private Marker currentMarker;
    private LatLng currentLatlong;
    private String currLoc;
    FusedLocationProviderClient fusedLoc;

    private LocationManager locationManager;

    private GetLocationDownloadTask locationDownloadTask;
    private UpdateThread updateThread;
    private FindLocationNameTask findLocationNameTask;
    FirebaseUtils firebaseUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
        firebaseUtils = new FirebaseUtils();

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        fusedLoc = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    private void initView() {

        backNav = findViewById(R.id.backNav);
        myLoc = findViewById(R.id.myLoc);
        updateBtn = findViewById(R.id.updateLoc);
        searchBar = findViewById(R.id.searchBar);
        searchBar.setSingleLine(true);
        searchedList = findViewById(R.id.searchedList);
        locText = findViewById(R.id.locText);

        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    String[] permission = {FINE_LOCATION, COARSE_LOCATION};

                    ActivityCompat.requestPermissions(MapActivity.this,
                            permission,
                            LOC_PERMISSION_RQST_CODE);

                    return;
                }

                if (isLocationServiceEnabled()) {
                    map.setMyLocationEnabled(true);
                    getDeviceLocation();
                } else {
                    getLocationPermission();
                }

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Updating loc reached");

                if (currentLatlong == null) {
                    Toast.makeText(MapActivity.this, "Mark delivery location", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!AppUtils.internetIsConnected(MapActivity.this)) {
                    ViewUtils.connectToInternetToast(MapActivity.this);
                    return;
                }

                Log.e(TAG, "Updating loc");


                if (updateThread != null) {
                    updateThread.interrupt();
                    updateThread = null;
                }
                updateThread = new UpdateThread(MapActivity.this, currentLatlong, currLoc);
                updateThread.start();

            }
        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    AppUtils.hideSoftKeyboard(MapActivity.this, getCurrentFocus());

                    if (!AppUtils.internetIsConnected(MapActivity.this)) {
                        ViewUtils.connectToInternetToast(MapActivity.this);
                    }

                    return true;
                }

                return false;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchedList.setVisibility(View.VISIBLE);
                String location = s.toString();
                Log.d(TAG, "geoLocate: searched: " + location);
                geoLocate(location);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map ready");

        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                if (latLng == null)
                    return;

                if (currentMarker != null)
                    currentMarker.remove();

                if (!AppUtils.internetIsConnected(MapActivity.this)) {
                    ViewUtils.connectToInternetToast(MapActivity.this);
                    return;
                }


                moveCamera(latLng, null);

            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        User user = userViewModel.getUser(firebaseUtils.getUserId());

        if (user != null) {

            if (user.latitude != null && !user.latitude.isEmpty()) {
                currentLatlong = new LatLng(Double.parseDouble(user.latitude)
                        , Double.parseDouble(user.longitude));

                moveCamera(currentLatlong, null);


            } else {
                moveCamera(DEFAULT_LAT_LONG, "Kotdwara");
            }

        } else {
            moveCamera(DEFAULT_LAT_LONG, "Kotdwara");
        }


    }

    private boolean isServiceOK() {
        Log.d(TAG, "Checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            if (dialog != null)
                dialog.show();
        } else {
            Toast.makeText(this, "Can't use map", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void geoLocate(String location) {

        if (!AppUtils.internetIsConnected(MapActivity.this))
            return;

        if (locationDownloadTask != null) {
            locationDownloadTask.cancel(true);
            locationDownloadTask = null;
        }

        locationDownloadTask = new GetLocationDownloadTask(MapActivity.this, searchedList);
        locationDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);


    }

    private void getDeviceLocation() {
        Log.d(TAG, "Getting device location");

        try {
            if (isLocPermissionGranted()) {
                final Task<Location> location = fusedLoc.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found location");
                            Location currentLoc = (Location) task.getResult();


                            if (currentLoc != null) {
                                currentLatlong = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                                moveCamera(currentLatlong, null);
                            }

                        } else {
                            Log.d(TAG, "Current location null");
                            Toast.makeText(MapActivity.this, "Unable to find location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        } catch (SecurityException s) {
            Log.e(TAG, "Security exception");
        }
    }

    private String getDeviceLocationName(LatLng latLng) {
        if (latLng == null)
            return null;

        List<Address> address = new ArrayList<>();
        int tryLeft = 2;

        while (tryLeft-- > 0) {
            try {
                address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                if (address.size() > 0)
                    break;
            } catch (IOException e) {
                Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
            }
        }

        if (address.size() == 0)
            return null;

        return address.get(0).getFeatureName();
    }


    private void moveCamera(LatLng latLng, String title) {

        if (map == null)
            return;

        Log.d(TAG, "MoveCamera to lat: " + latLng.latitude + " long: " + latLng.longitude);


        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        if (findLocationNameTask != null) {
            findLocationNameTask.cancel(true);
            findLocationNameTask = null;
        }


        if (title == null) {

            findLocationNameTask = new FindLocationNameTask(MapActivity.this);
            findLocationNameTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, latLng);

        } else {
            locText.setText(title);
            currLoc = title;
        }

        if (currentMarker != null)
            currentMarker.remove();
        currentLatlong = latLng;

        addMarker(latLng, title);

    }

    private void addMarker(LatLng latLng, String title) {


        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(title);


        currentMarker = map.addMarker(marker);

    }


    private boolean isLocPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }


    private void getLocationPermission() {

        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(MapActivity.this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);


                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(MapActivity.this,
                                                LOC_PERMISSION_RQST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;

                    }
                }
            }
        });
    }

    private boolean isLocationServiceEnabled() {

        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }


    @Override
    protected void onStop() {
        if (locationDownloadTask != null) {
            locationDownloadTask.cancel(true);
            locationDownloadTask = null;
            searchedList.setVisibility(View.GONE);
        }

        if (updateThread != null) {
            updateThread.interrupt();
            updateThread = null;
        }

        if (findLocationNameTask != null) {
            findLocationNameTask.cancel(true);
            findLocationNameTask = null;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (locationDownloadTask != null) {
            locationDownloadTask.cancel(true);
            locationDownloadTask = null;
        }

        if (updateThread != null) {
            updateThread.interrupt();
            updateThread = null;
        }

        if (findLocationNameTask != null) {
            findLocationNameTask.cancel(true);
            findLocationNameTask = null;
        }


        super.onDestroy();
    }

    static class GetLocationDownloadTask extends AsyncTask<String, List<Address>, Void> {

        private SearchedItemAdapter adapter;
        private final WeakReference<ListView> searchedListWeakRef;
        private final WeakReference<MapActivity> weakContext;
        private final Geocoder geocoder;

        public GetLocationDownloadTask(MapActivity context, ListView listView) {
            searchedListWeakRef = new WeakReference<>(listView);
            weakContext = new WeakReference<>(context);
            geocoder = new Geocoder(context.getApplicationContext());
        }

        @Override
        protected Void doInBackground(String... location) {


            Log.d(TAG, "geoLocate: searched: " + Thread.currentThread());
            List<Address> list;

            Log.e(TAG, "Location " + location[0]);
            int tryLeft = 2;
            try {
                while (tryLeft-- > 0) {

                    if (isCancelled())
                        return null;

                    list = geocoder.getFromLocationName(location[0] + " Uttarakhand", 3);
                    Log.d(TAG, "geoLocate: searched: " + list.size());
                    onProgressUpdate(list);
                }
            } catch (IOException e) {
                Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(List<Address>... lists) {

            if (isCancelled())
                return;

            List<Address> list = lists[0];

            ListView weakSearchedList = searchedListWeakRef.get();

            MapActivity mapActivity = weakContext.get();
            if (mapActivity != null) {
                mapActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size() > 0) {
                            Log.d(TAG, "Size = " + list.size());

                            adapter = new SearchedItemAdapter(list);
                            adapter.setListener(new SearchedItemAdapter.LocOnClickListener() {
                                @Override
                                public void onClick(Address address) {
                                    if (weakSearchedList != null) {
                                        weakSearchedList.setVisibility(View.GONE);

                                        mapActivity.moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                                                address.getLocality());
                                    }

                                    cancel(true);
                                }
                            });

                            if (weakSearchedList != null)
                                weakSearchedList.setAdapter(adapter);
                        }
                    }
                });
            }

            super.onProgressUpdate(lists);
        }

    }

    static class UpdateThread extends Thread {

        WeakReference<MapActivity> weakActivity;
        Geocoder geocoder;
        LatLng latLng;
        String currLoc;

        public UpdateThread(MapActivity activity, final LatLng latLng, final String curLoc) {
            weakActivity = new WeakReference<>(activity);
            geocoder = new Geocoder(activity.getApplicationContext());
            this.latLng = latLng;
            this.currLoc = curLoc;
        }

        @Override
        public void run() {

            String address = null;

            List<Address> addressList = new ArrayList<>();
            int tryLeft = 2;

            if (currLoc == null) {
                while (tryLeft-- > 0) {

                    if (isInterrupted()) {
                        Log.e(TAG, "Update Task interrupted");
                        return;
                    }

                    try {
                        addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        if (addressList.size() > 0)
                            break;
                    } catch (IOException e) {
                        Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
                    }
                }

                if (addressList.size() > 0)
                    address = addressList.get(0).getLocality();
            } else {
                address = currLoc;
            }


            if (address == null)
                address = "Kotdwara";

            Log.d(TAG, "Updating Location = " + address);

            Map<String, Object> map = new HashMap<>();

            String lat = String.valueOf(latLng.latitude);
            String lon = String.valueOf(latLng.longitude);

            map.put("latitude", lat);
            map.put("longitude", lon);
            map.put("address", address);

            String finalAddress = address;

            if (isInterrupted()) {
                Log.e(TAG, "Update Task interrupted");
                return;
            }

            MapActivity activity = weakActivity.get();

            if (activity != null) {
                Log.e(TAG, "Update profile successful");
                activity.userViewModel.updateAddress(FirebaseUtils.getUserId(), finalAddress);
                activity.userViewModel.updateLocation(FirebaseUtils.getUserId(), lat, lon);
                activity.finish();
            }
        }
    }

    static class FindLocationNameTask extends AsyncTask<LatLng, Void, String> {

        WeakReference<MapActivity> weakActivity;
        private Geocoder geoCoder;

        FindLocationNameTask(MapActivity activity) {
            weakActivity = new WeakReference<>(activity);
            geoCoder = new Geocoder(activity.getApplicationContext());
        }

        @Override
        protected String doInBackground(LatLng... latLngs) {

            LatLng latLng = latLngs[0];

            if (latLng == null)
                return null;

            List<Address> address = new ArrayList<>();
            int tryLeft = 2;

            while (tryLeft-- > 0) {

                if (isCancelled())
                    return null;

                try {
                    address = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    if (address.size() > 0)
                        break;
                } catch (IOException e) {
                    Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
                }
            }

            if (address.size() == 0)
                return null;

            return address.get(0).getLocality();
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if (isCancelled())
                return;

            MapActivity activity = weakActivity.get();

            if (activity != null) {

                if (s != null) {
                    activity.locText.setText(s);
                    activity.currLoc = s;
                } else {
                    activity.locText.setText("Unknown");
                    activity.currLoc = null;
                }
            }

        }
    }

}