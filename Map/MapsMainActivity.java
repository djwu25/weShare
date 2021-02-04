package com.example.weshare.MapActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weshare.Forums.Forum;
import com.example.weshare.MainActivity;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private float DEFAULT_ZOOM = 12;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LatLng mDefaultLocation;
    private Marker m;
    private String title;
    private Double latitudeMap, longitudeMap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ArrayList<Forum> forumMapsList;
    private final double radius = 10.0/69.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);
        mMapView = findViewById(R.id.mapsMainView);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("forums");

        forumMapsList = new ArrayList<>();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationPermissionGranted = true;
        mDefaultLocation = new LatLng(38.89378, -77.1546637);
        getLocationPermission();
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
                            myRef.addChildEventListener( new ForumMapsChildEventListener() );
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

    public void switchHomePage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
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
        latitudeMap = 0.0;
        longitudeMap = 0.0;
        for( int i = 0; i < forumMapsList.size(); i++ )
        {
            latitudeMap = forumMapsList.get(i).getLat();
            longitudeMap = forumMapsList.get(i).getLon();
            title = forumMapsList.get(i).getTitle();
            createMarker( latitudeMap, longitudeMap, title );
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private Marker createMarker( Double latitude, Double longitude, String titleName )
    {
        return mMap.addMarker( new MarkerOptions().position( new LatLng( latitude, longitude ) )
            .title(titleName));
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

    class ForumMapsChildEventListener implements ChildEventListener
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            double lat = Math.abs(dataSnapshot.getValue(Forum.class).getLat());
            double lon = Math.abs(dataSnapshot.getValue(Forum.class).getLon());
            double latitude = Math.abs(mLastKnownLocation.getLatitude());
            double longitude = Math.abs(mLastKnownLocation.getLongitude());
            double radiusCheck = Math.sqrt(Math.pow(lat - latitude, 2) + Math.pow(lon - longitude, 2));

            if( radiusCheck <= radius ) {
                Forum forum = dataSnapshot.getValue(Forum.class);
                forumMapsList.add(forum);
                latitudeMap = forum.getLat();
                longitudeMap = forum.getLon();
                title = forum.getTitle();
                createMarker( latitudeMap, longitudeMap, title );
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Forum forum = dataSnapshot.getValue(Forum.class);
            forumMapsList.remove(forum);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
