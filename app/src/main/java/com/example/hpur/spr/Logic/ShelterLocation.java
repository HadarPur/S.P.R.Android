package com.example.hpur.spr.Logic;

import android.app.Activity;

public class ShelterLocation {

    private double longitude, latitude;

    //c'tor
    public ShelterLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //getter
    public double getLongitude() {
        return longitude;
    }

    //getter
    public double getLatitude() {
        return latitude;
    }

}
