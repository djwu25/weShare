package com.example.weshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    public EditText emailId, password, userNameEnter;
    public Button btnSignUp;
    public TextView tvSignIn;
    public FirebaseAuth mFirebaseAuth;
    private String email, username;
    private DatabaseReference myRefUser;
    private ArrayList<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvSignIn = findViewById(R.id.textView2);
        userNameEnter = findViewById(R.id.editUsername);
        usersList = new ArrayList<>();

        myRefUser = FirebaseDatabase.getInstance().getReference("users");
        myRefUser.addChildEventListener(new SignUpUserChildEventListener());
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent (SignUpActivity.this, LoginActivity.class);
                startActivity (intent);
            }
        });
    }

    public void signUp( View view ){
        email = emailId.getText().toString();
        String pw = password.getText().toString();
        username = userNameEnter.getText().toString();

        for( int i = 0; i < usersList.size(); i++ ){
            if( username.equals(usersList.get(i).getUsername() )) {
                userNameEnter.setError("Username is already taken");
                return;
            }
        }

        if ( email.isEmpty() ) {
            emailId.setError("Please provide a valid email");
            emailId.requestFocus();
            return;
        }
        else if ( pw.isEmpty() ) {
            password.setError("Password can't be blank!");
            return;
        }
        else if( pw.length() < 6 )
        {
            password.setError("Password must be longer!");
            return;
        }
        else if( username.isEmpty() )
        {
            userNameEnter.setError("Enter a username!");
            return;
        }
        else if( email.isEmpty() && pw.isEmpty() ) {
            Toast.makeText(SignUpActivity.this, "Fields are empty!" , Toast.LENGTH_SHORT).show();
            return;
        }
        else if ( !(email.isEmpty() && pw.isEmpty() ) ) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if  (!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Email has already been used." , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Successful Sign Up", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        User user = new User( username, email, firebaseUser.getUid(), false);
                        myRefUser.child(firebaseUser.getUid()).setValue(user);
                        startActivity(intent);
                        return;
                    }
                }
            });
        }
        else {
            Toast.makeText(SignUpActivity.this, "Error Occurred!" , Toast.LENGTH_SHORT).show();
            return;
        }

    }

    class SignUpUserChildEventListener implements ChildEventListener{
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            usersList.add(dataSnapshot.getValue(User.class));
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
