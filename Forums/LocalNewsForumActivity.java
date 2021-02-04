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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weshare.MainActivity;
import com.example.weshare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LocalNewsForumActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ListView listView;
    private ArrayList<Forum> forumsNewsList;
    private ForumAdapter forumAdapter;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final double radius = 10.0/69.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_news_forum);

        getLocationPermission();
        mLocationPermissionGranted = true;

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("forums");

        forumsNewsList = new ArrayList<>();
        forumAdapter = new ForumAdapter(this, forumsNewsList);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();

        forumAdapter = new ForumAdapter(this, forumsNewsList);
        listView = findViewById(R.id.forumNewsList);
        LayoutInflater inflator = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflator.inflate(R.layout.forum_list_title_news, listView, false);
        listView.addHeaderView(header);

        listView.setAdapter(forumAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                Toast.makeText(LocalNewsForumActivity.this, forumsNewsList.get(position).getTitle() + " selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocalNewsForumActivity.this, ForumViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("titleNews", forumsNewsList.get(position).getTitle());
                bundle.putString("descriptionNews", forumsNewsList.get(position).getDescription());
                bundle.putString("uIdNews", forumsNewsList.get(position).getuId());
                bundle.putString("typeNews", forumsNewsList.get(position).getType());
                bundle.putInt("positionNews", forumsNewsList.get(position).getPositionType());
                bundle.putDouble("latNews", forumsNewsList.get(position).getLat());
                bundle.putDouble("lonNews", forumsNewsList.get(position).getLon());
                bundle.putString("userNews", forumsNewsList.get(position).getUsername());
                bundle.putBoolean("anonymityNews", forumsNewsList.get(position).getAnonymity());
                bundle.putInt("check", 4);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = (Location) task.getResult();
                            myRef.addChildEventListener(new NewsForumChildEventListener());
                        }else {
                            Log.d("Failed", "rip");
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("look: %s", e.getMessage());
        }
    }

    public void switchNewsBackActivity( View view )
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchNewsAddActivity( View view )
    {
        Intent intent = new Intent(this, ForumAddActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        startActivity(intent);
    }

    class NewsForumChildEventListener implements ChildEventListener
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            double lat = Math.abs(dataSnapshot.getValue(Forum.class).getLat());
            double lon = Math.abs(dataSnapshot.getValue(Forum.class).getLon());
            double latitude = Math.abs(mLastKnownLocation.getLatitude());
            double longitude = Math.abs(mLastKnownLocation.getLongitude());
            double radiusCheck = Math.sqrt(Math.pow(lat - latitude, 2) + Math.pow(lon - longitude, 2));
            String checkType = dataSnapshot.getValue(Forum.class).getType();
            String newsType = "Local News";

            if( radiusCheck <= radius && checkType.equals(newsType) ) {
                forumsNewsList.add(dataSnapshot.getValue(Forum.class));
                forumAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            forumsNewsList.remove(dataSnapshot.getValue(Forum.class));
            forumAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
