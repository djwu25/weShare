package com.example.weshare.Forums;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weshare.R;
import com.example.weshare.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ForumViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Forum forum;
    private int positionFind;
    private double lat, lon;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private float DEFAULT_ZOOM = 10;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Marker m;
    private LatLng mDefaultLocation;
    private String title, description, uId, type, user, email, compare;
    private Bundle bundle;
    private ArrayList<User> usersList;
    private DatabaseReference myRefUser;
    private boolean anonymity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);
        mMapView = findViewById(R.id.viewForumMap);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        usersList = new ArrayList<>();

        myRefUser = FirebaseDatabase.getInstance().getReference("users");
        myRefUser.addChildEventListener( new ViewUserChildEventListener() );

        bundle = getIntent().getExtras();
        switch( bundle.getInt("check") ){
            case 1: {
                title = bundle.getString("title");
                description = bundle.getString("description");
                uId = bundle.getString("uId");
                type = bundle.getString("type");
                positionFind = bundle.getInt("position");
                lat = bundle.getDouble("lat");
                lon = bundle.getDouble("lon");
                user = bundle.getString("username");
                anonymity = bundle.getBoolean("anonymity");
                break;
            }
            case 2: {
                title = bundle.getString("titleCrime");
                description = bundle.getString("descriptionCrime");
                uId = bundle.getString("uIdCrime");
                type = bundle.getString("typeCrime");
                positionFind = bundle.getInt("positionCrime");
                lat = bundle.getDouble("latCrime");
                lon = bundle.getDouble("lonCrime");
                user = bundle.getString("userCrime");
                anonymity = bundle.getBoolean("anonymityCrime");
                break;
            }
            case 3: {
                title = bundle.getString("titleJobs");
                description = bundle.getString("descriptionJobs");
                uId = bundle.getString("uIdJobs");
                type = bundle.getString("type");
                positionFind = bundle.getInt("positionJobs");
                lat = bundle.getDouble("latJobs");
                lon = bundle.getDouble("lonJobs");
                user = bundle.getString("userJobs");
                anonymity = bundle.getBoolean("anonymityJobs");
                break;
            }
            case 4: {
                title = bundle.getString("titleNews");
                description = bundle.getString("descriptionNews");
                uId = bundle.getString("uIdNews");
                type = bundle.getString("typeNews");
                positionFind = bundle.getInt("positionNews");
                lat = bundle.getDouble("latNews");
                lon = bundle.getDouble("lonNews");
                user = bundle.getString("userNews");
                anonymity = bundle.getBoolean("anonymityNews");
                break;
            }
        }
        mDefaultLocation = new LatLng(lat, lon);

        forum = new Forum(title, description, uId, type, positionFind, lat, lon, user, anonymity);

        TextView titleAdd = (TextView)findViewById(R.id.titleView);
        TextView descriptionAdd = (TextView)findViewById(R.id.descriptionView);
        TextView forumTypeAdd = (TextView)findViewById(R.id.forumTypeView);
        TextView userInput = (TextView)findViewById(R.id.userNameInput);

        titleAdd.setText(forum.getTitle());
        descriptionAdd.setText(forum.getDescription());
        forumTypeAdd.setText(forum.getType());
        if( anonymity )
            userInput.setText("By: Anonymous");
        else
            userInput.setText("By: " + forum.getUsername() );
    }

    public void switchEditActivity( View view )
    {
        if( user.equals(forum.getUsername()) ) {
            Intent intent = new Intent(this, ForumEditActivity.class);
            Bundle newBundle = new Bundle();
            newBundle.putString("titleEdit", forum.getTitle());
            newBundle.putString("descriptionEdit", forum.getDescription());
            newBundle.putString("uIdEdit", forum.getuId());
            newBundle.putString("typeEdit", forum.getType());
            newBundle.putInt("getPosition", positionFind);
            newBundle.putDouble("getLat", lat);
            newBundle.putDouble("getLon", lon);
            newBundle.putString("getUsername", user);
            newBundle.putBoolean("getAnonymity", anonymity);
            newBundle.putInt("check", 1);
            intent.putExtras(newBundle);
            startActivity(intent);
        } else {
            Toast.makeText( this, "Cannot edit someone else's post.", Toast.LENGTH_SHORT).show();
        }
    }

    private void username()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        email = userEmail.getEmail();
        for( int i = 0; i < usersList.size(); i++) {
            compare = usersList.get(i).getEmail();
            if (email.equals(compare)) {
                user = usersList.get(i).getUsername();
                return;
            }
        }
    }

    public void switchBackActivity( View view )
    {
        Intent intent = new Intent( this, ForumMainActivity.class);

        switch(bundle.getInt("check"))
        {
            case 1: {
                intent = new Intent( this, ForumMainActivity.class);
                break;
            }
            case 2: {
                intent = new Intent( this, CrimeForumActivity.class);
                break;
            }
            case 3: {
                intent = new Intent( this, JobListingForumActivity.class);
                break;
            }
            case 4: {
                intent = new Intent( this, LocalNewsForumActivity.class);
                break;
            }
        }
        startActivity(intent);
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
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateLocationUI();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        MarkerOptions mo = new MarkerOptions().position(mDefaultLocation);
        m = mMap.addMarker(mo);
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

    class ViewUserChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            usersList.add(dataSnapshot.getValue(User.class));
            ForumViewActivity.this.username();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            usersList.remove(dataSnapshot.getValue(User.class));
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
