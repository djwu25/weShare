package com.example.weshare;

import android.content.Intent;
import android.os.Bundle;

import com.example.weshare.Forums.CrimeForumActivity;
import com.example.weshare.Forums.ForumAddActivity;
import com.example.weshare.Forums.ForumMainActivity;
import com.example.weshare.Forums.JobListingForumActivity;
import com.example.weshare.Forums.LocalNewsForumActivity;
import com.example.weshare.MapActivities.MapsMainActivity;

import android.text.Layout;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    Toolbar.OnMenuItemClickListener{

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference myRefForum, myRefUser;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private String user, email, compare, uId;
    private boolean anonymity;
    private ArrayList<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        myRefForum = FirebaseDatabase.getInstance().getReference("forums");
        myRefUser = FirebaseDatabase.getInstance().getReference("users");

        usersList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_forums, R.id.nav_forums_crime,
                R.id.nav_forums_jobs, R.id.nav_forums_news, R.id.nav_sign_out)
                .setDrawerLayout(mDrawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);
        setNavigationViewListener();

        myRefUser.addChildEventListener(new UserChildEventListener());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId())
        {
            case R.id.nav_forums: {
                Toast.makeText(this, "All Forums", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ForumMainActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivity(intent);
                break;
            }

            case R.id.nav_forums_crime: {
                Toast.makeText(this, "Crime Forums", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CrimeForumActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivity(intent);
                break;
            }

            case R.id.nav_forums_jobs: {
                Toast.makeText(this, "Job Listing Forums", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, JobListingForumActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivity(intent);
                break;
            }

            case R.id.nav_forums_news: {
                Toast.makeText(this, "Local News Forums", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LocalNewsForumActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivity(intent);
                break;
            }

            case R.id.nav_sign_out: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.nav_home: {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener()
    {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void switchMapActivity( View view ){
        Intent intent = new Intent( this, MapsMainActivity.class );
        startActivity(intent);
    }

    public void switchForumAddActivity( View view ){
        Intent intent = new Intent( this, ForumAddActivity.class );
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchForumMainActivity( View view )
    {
        Intent intent = new Intent( this, ForumMainActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setInfo()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        email = userEmail.getEmail();
        View headerView = mNavigationView.getHeaderView(0);
        TextView emailDisplay = (TextView) headerView.findViewById(R.id.emailDisplay);
        emailDisplay.setText(email);
        for( int i = 0; i < usersList.size(); i++) {
            compare = usersList.get(i).getEmail();
            if (email.equals(compare)) {
                TextView usernameDisplay = (TextView) headerView.findViewById(R.id.usernameDisplay);
                user = usersList.get(i).getUsername();
                usernameDisplay.setText(user);
                uId = usersList.get(i).getuId();
                anonymity = usersList.get(i).getAnonymity();
                return;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings: {
                Intent intent = new Intent( this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
            default:
        }
        return true;
    }

    class UserChildEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            usersList.add(dataSnapshot.getValue(User.class));
            MainActivity.this.setInfo();
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
