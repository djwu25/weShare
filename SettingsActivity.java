package com.example.weshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private String user, email, compare, uId;
    private boolean anonymity;
    private ArrayList<User> usersList;
    private DatabaseReference myRefUser;
    private FirebaseDatabase mDatabase;
    private Switch anonymousPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        anonymousPosts = (Switch)findViewById(R.id.anonymitySwitch);

        usersList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        myRefUser = mDatabase.getReference("users");
        myRefUser.addChildEventListener(new SettingsUserChildEventListener());
    }

    public void settingsSave( View view ){
        Intent intent = new Intent(this, MainActivity.class);
        anonymousPosts = (Switch)findViewById(R.id.anonymitySwitch);
        anonymity = anonymousPosts.isChecked();
        User updateUser = new User(user, email, uId, anonymity);
        myRefUser.child(uId).setValue(updateUser);
        startActivity(intent);
    }

    private void setInfo(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView emailDisplay = (TextView)findViewById(R.id.settingsEmailDisplay);
        TextView userDisplay = (TextView)findViewById(R.id.settingsUserDisplay);
        email = firebaseUser.getEmail();
        emailDisplay.setText(email);
        for( int i = 0; i < usersList.size(); i++ )
        {
            compare = usersList.get(i).getEmail();
            if( compare.equals(email) )
            {
                user = usersList.get(i).getUsername();
                userDisplay.setText(user);
                uId = usersList.get(i).getuId();
                anonymity = usersList.get(i).getAnonymity();
                anonymousPosts.setChecked(anonymity);
            }
        }
    }

    class SettingsUserChildEventListener implements ChildEventListener{
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            usersList.add(dataSnapshot.getValue(User.class));
            SettingsActivity.this.setInfo();
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
