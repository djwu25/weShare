package com.example.weshare.MapActivities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weshare.Forums.ForumEditActivity;
import com.example.weshare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private float DEFAULT_ZOOM = 13;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Marker m;
    private LatLng mDefaultLocation;
    private String title, description, forumType, uId, user;
    private int position;
    private double lat, lon;
    private boolean anonymity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_edit);
        mMapView = findViewById(R.id.mapsEditView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationPermissionGranted = true;
        getLocationPermission();

        title = "";
        description = "";
        forumType = "Select...";
        position = 0;

        Bundle bundle = getIntent().getExtras();
        if (bundle.getInt("check") == 1) {
            title = bundle.getString("title");
            description = bundle.getString("description");
            forumType = bundle.getString("forumType");
            uId = bundle.getString("uId");
            position = bundle.getInt("position");
            lat = bundle.getDouble("lat");
            lon = bundle.getDouble("lon");
            user = bundle.getString("user");
            anonymity = bundle.getBoolean("anonymity");
        }
        mDefaultLocation = new LatLng(lat, lon);
    }


    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    private void getLocationPermission() {          //addeds
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void toEditForum(View view) {
        if (m == null) {
            Toast.makeText(getApplicationContext(), "No point selected. Select a point before editing marker.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ForumEditActivity.class);

        Bundle forumEdit = new Bundle();

        Double lat = m.getPosition().latitude;
        Double lon = m.getPosition().longitude;
        forumEdit.putDouble("latitude_value", lat);
        forumEdit.putDouble("longitude_value", lon);
        forumEdit.putString("title", title);
        forumEdit.putString("uId", uId);
        forumEdit.putString("description", description);
        forumEdit.putString("forumType", forumType);
        forumEdit.putInt("position", position);
        forumEdit.putString("username", user);
        forumEdit.putBoolean("anonymity", anonymity);
        forumEdit.putInt("check", 2);

        intent.putExtras(forumEdit);

        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateLocationUI();
        if (mLocationPermissionGranted == false)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));
        else
            getDeviceLocation();
        MarkerOptions moSet;
        if( mDefaultLocation != null )
        {
            moSet = new MarkerOptions().position(mDefaultLocation);
            m = mMap.addMarker(moSet);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions mo = new MarkerOptions().position(latLng);
                if (m != null)
                    m.remove();
                m = mMap.addMarker(mo);
                Toast.makeText(MapsEditActivity.this, "Marker Set", Toast.LENGTH_SHORT).show();
            }
        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}