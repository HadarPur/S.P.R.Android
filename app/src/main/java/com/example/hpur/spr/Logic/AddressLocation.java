package com.example.hpur.spr.Logic;

public class AddressLocation {

    private double longitude, latitude;

    //c'tor
    public AddressLocation(double latitude, double longitude){
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
