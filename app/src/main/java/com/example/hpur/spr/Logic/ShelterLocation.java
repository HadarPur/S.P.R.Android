package com.example.hpur.spr.Logic;

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
