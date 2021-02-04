package com.example.weshare.Forums;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weshare.MainActivity;
import com.example.weshare.MapActivities.MapsAddActivity;
import com.example.weshare.R;
import com.example.weshare.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ForumAddActivity extends AppCompatActivity {

    DatabaseReference myRefForum, myRefUser;
    FirebaseDatabase database;
    private int positionFinder;
    private Spinner forumListTypes;
    private ArrayList<String> list;
    private ArrayList<User> usersList;
    private String forumType, title, description, email, compare, user;
    private ArrayAdapter<String> spinnerAdapter;
    private double lat, lon;
    private Bundle b;
    private EditText titleName, descriptionName;
    private boolean anonymity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_add);
        database = FirebaseDatabase.getInstance();
        myRefForum = database.getReference("forums");
        myRefUser = database.getReference("users");
        myRefUser.addChildEventListener( new addUserChildEventListener() );

        forumListTypes = (Spinner)findViewById(R.id.forumTypes);

        list = new ArrayList<>();
        usersList = new ArrayList<>();
        list.add("Select...");
        list.add("Crime");
        list.add("Job Listings");
        list.add("Local News");

        spinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, list);

        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        forumListTypes.setAdapter(spinnerAdapter);

        forumListTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                forumType = parent.getItemAtPosition(position).toString();
                positionFinder = position;
                if( position != 0 )
                    Toast.makeText(ForumAddActivity.this, "Selected " + forumType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        titleName = findViewById(R.id.inputTitle);
        descriptionName = findViewById(R.id.inputDescription);
        lat = 0;
        lon = 0;

        b = getIntent().getExtras();
        if( b.getInt("check") == 1 )
        {
            title = b.getString("title");
            description = b.getString("description");
            forumListTypes.setSelection(b.getInt("position"));
            lat = b.getDouble("latitude_value");
            lon = b.getDouble("longitude_value");
            user = b.getString("user");
            anonymity = b.getBoolean("anonymity");

            titleName.setText(title);
            descriptionName.setText(description);
        }
    }

    public void createForum( View view )
    {
        Intent intent = new Intent(this, ForumMainActivity.class );

        title = titleName.getText().toString();
        description = descriptionName.getText().toString();

        if( !TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && !spinnerAdapter.getItem(positionFinder).equals("Select...") && lat != 0 && lon != 0)
        {
            String key = myRefForum.push().getKey();
            Forum forum = new Forum( title, description, key, forumType, positionFinder, lat, lon, user, anonymity );
            myRefForum.child(key).setValue(forum);

            Toast.makeText(this, forum.getTitle() + " added to Forums.", Toast.LENGTH_LONG).show();

            titleName.setText("");
            descriptionName.setText("");

            startActivity(intent);
        }
        else
        {
            if(title.isEmpty())
                titleName.setError("Title is empty!");
            else if(description.isEmpty())
                descriptionName.setError("Description is empty!");

            Toast.makeText(this, "Fill in title, description, select a type and a location.", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchMainActivity( View view )
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        if( b.getInt("check") == 1 )
        {
            intent = new Intent(this, MapsAddActivity.class);
            title = titleName.getText().toString();
            description = descriptionName.getText().toString();

            Bundle maps = new Bundle();
            maps.putInt("check", 1);
            maps.putString("title", title);
            maps.putString("description", description);
            maps.putString("forumType", forumType);
            maps.putInt("position", positionFinder);
            maps.putDouble("lat", lat);
            maps.putDouble("lon", lon);
            maps.putString("username", user);
            maps.putBoolean("anonymity", anonymity);
            intent.putExtras(maps);
        }
        startActivity(intent);
    }

    public void viewMaps( View view ){
        Intent intent = new Intent( this, MapsAddActivity.class);

        title = titleName.getText().toString();
        description = descriptionName.getText().toString();

        Bundle maps = new Bundle();
        maps.putInt("check", 1);
        maps.putString("title", title);
        maps.putString("description", description);
        maps.putString("forumType", forumType);
        maps.putInt("position", positionFinder);
        maps.putDouble("lat", lat);
        maps.putDouble("lon", lon);
        maps.putString("username", user);
        maps.putBoolean("anonymity", anonymity);
        intent.putExtras(maps);
        startActivity(intent);
    }

    private void retrieveUserInfo()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        email = userEmail.getEmail();
        for( int i = 0; i < usersList.size(); i++) {
            compare = usersList.get(i).getEmail();
            if (email.equals(compare)) {
                user = usersList.get(i).getUsername();
                anonymity = usersList.get(i).getAnonymity();
                return;
            }
        }
    }

    class addUserChildEventListener implements ChildEventListener{
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            usersList.add(dataSnapshot.getValue(User.class));
            ForumAddActivity.this.retrieveUserInfo();
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
