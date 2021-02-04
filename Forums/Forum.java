package com.example.weshare.Forums;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Forum {

    private String title, description, uId, type, username;
    private int positionType;
    private double lat, lon;
    private boolean anonymity;

    public Forum(){}

    public Forum( String title, String description, String uId, String type, int positionType,
                  double lat, double lon, String username, boolean anonymity )
    {
        this.title = title;
        this.description = description;
        this.uId = uId;
        this.type = type;
        this.positionType = positionType;
        this.lat = lat;
        this.lon = lon;
        this.username = username;
        this.anonymity = anonymity;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getuId() { return uId; }

    public String getType() { return type; }

    public int getPositionType() { return positionType; }

    public double getLat() { return lat; }

    public double getLon() { return lon; }

    public String getUsername() { return username; }

    public boolean getAnonymity() { return anonymity; }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setKey( String key ) { uId = key; }

    public void setType( String type ) { this.type = type; }

    public void setPositionType( int positionType ) { this.positionType = positionType; }

    public void setLat( Double lat ) { this.lat = lat; }

    public void setLon( Double lon) { this.lon = lon; }

    public void setUsername( String username ) { this.username = username; }

    public void setAnonymity( boolean anonymity ) { this.anonymity = anonymity; }
}
