package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import java.io.IOException;
import java.util.List;

public class Shelter {
    private static final String TAG ="SHELTER";
    private String mCity;
    private String Name;
    private String Street;
    private String Number;
    private ShelterLocation mShelterLocation;

    public Shelter(){
    }

    public Shelter(String name, String number ,String street) {
        this.Name = name;
        this.Street = street;
        this.Number = number;
    }

    /** calculates the location of a street by name **/
    public void findStreetLocation(Activity activity,String name){
        mShelterLocation = getLocation(name,activity);
    }

    //get location by name
    public ShelterLocation getLocation(String name, Activity activity) {
        Geocoder coder = new Geocoder(activity.getApplicationContext());
        List<Address> address;
        List<Address> streets;
        ShelterLocation location = null;

        try {
            Log.d(TAG, "name: " + name);
            address = coder.getFromLocationName(name, 5);
            if (address == null) {
                return null;
            }
            if (address.size() == 0) {
                return null;
            }

            Address add = address.get(0);
            location = new ShelterLocation(add.getLatitude(), add.getLongitude());
            String neighName = add.getSubLocality();
            streets = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            Log.d(TAG, "number of dtreets: : " + streets.size());

            for (int i = 0; i < streets.size(); i++) {
                String stname = streets.get(i).getThoroughfare();
                Log.d(TAG, "street: " + stname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String mName) {
        this.Name = mName;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String mStreet) {
        this.Street = mStreet;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String mNumber) {
        this.Number = mNumber;
    }


    public ShelterLocation getShelterLocation() {
        return mShelterLocation;
    }

    public void setShelterLocation(ShelterLocation mShelterLocation) {
        this.mShelterLocation = mShelterLocation;
    }
}
