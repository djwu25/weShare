package com.example.weshare.Forums;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weshare.MapActivities.MapsEditActivity;
import com.example.weshare.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ForumEditActivity extends AppCompatActivity {

    private TextView setTitle, setDescription;
    private String title, description, uId, type, user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Spinner spinner;
    private ArrayList<String> list;
    private int setPosition, findPosition;
    private double lat, lon;
    private boolean anonymity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_edit);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("forums");

        lat = 1;
        lon = 1;

        setTitle = (TextView)findViewById(R.id.editTitle);
        setDescription = (TextView)findViewById(R.id.editDescription);

        Bundle bundle = getIntent().getExtras();

        if( bundle.getInt("check") == 1 )
        {
            title = bundle.getString("titleEdit");
            description = bundle.getString("descriptionEdit");
            uId = bundle.getString("uIdEdit");
            type = bundle.getString("typeEdit");
            setPosition = bundle.getInt("getPosition");
            lat = bundle.getDouble("getLat");
            lon = bundle.getDouble("getLon");
            user = bundle.getString("getUsername");
            anonymity = bundle.getBoolean("getAnonymity");
        }
        if( bundle.getInt("check") == 2 )
        {
            title = bundle.getString("title");
            description = bundle.getString("description");
            uId = bundle.getString("uId");
            type = bundle.getString("typeForum");
            setPosition = bundle.getInt("position");
            lat = bundle.getDouble("latitude_value");
            lon = bundle.getDouble("longitude_value");
            user = bundle.getString("username");
            anonymity = bundle.getBoolean("anonymity");
        }

        setTitle.setText(title);
        setDescription.setText(description);

        spinner = (Spinner)findViewById(R.id.forumType);

        list = new ArrayList<>();
        list.add("Select...");
        list.add("Crime");
        list.add("Job Listings");
        list.add("Local News");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, list);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(setPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                findPosition = position;
                if( position != 0 )
                    Toast.makeText(ForumEditActivity.this, "Selected " + type, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void switchMapsActivity(View view)
    {
        Intent intent = new Intent(this, MapsEditActivity.class);
        title = setTitle.getText().toString();
        description = setDescription.getText().toString();

        Bundle maps = new Bundle();
        maps.putInt("check", 1);
        maps.putString("title", title);
        maps.putString("description", description);
        maps.putString("forumType", type);
        maps.putString("uId", uId);
        maps.putInt("position", setPosition);
        maps.putDouble("lat", lat);
        maps.putDouble("lon", lon);
        maps.putString("user", user);
        maps.putBoolean("anonymity", anonymity);
        intent.putExtras(maps);

        startActivity(intent);
    }

    public void switchForumViewActivity(View view)
    {
        Intent intent = new Intent(this, ForumMainActivity.class);

        title = setTitle.getText().toString();
        description = setDescription.getText().toString();

        if( !TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && !spinner.getItemAtPosition(findPosition).equals("Select...") )
        {
            Forum forum = new Forum( title, description, uId, type, findPosition, lat, lon, user, anonymity );
            myRef.child(uId).setValue(forum);
            Toast.makeText(this, title + " has been edited.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Fill in title, description, select forum type and location.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeForum( View view )
    {
        Intent intent = new Intent(this, ForumMainActivity.class);
        myRef.child(uId).removeValue();
        startActivity(intent);
    }
}
