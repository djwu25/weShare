package com.example.weshare;

public class User {

    private String username, email, uId;
    private boolean anonymity;

    public User(){
        username = "test";
        email = "test";
        uId = "test";
    }

    public User ( String username, String email, String uId, boolean anonymity ){
        this.username = username;
        this.email = email;
        this.uId = uId;
        this.anonymity = anonymity;
    }

    public String getUsername() { return username; }

    public String getEmail() {
        return email;
    }

    public String getuId() {
        return uId;
    }

    public boolean getAnonymity() { return anonymity; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setAnonymity( boolean anonymity ) { this.anonymity = anonymity; }
}
