package com.example.hpur.spr.Logic.Models;

import android.app.Activity;
import com.example.hpur.spr.Logic.AddressLocation;
import com.example.hpur.spr.UI.Utils.UtilitiesFunc;

public class ShelterModel {
    private static final String TAG ="SHELTER";
    private String mCity;
    private String Name;
    private String Street;
    private String Number;
    private AddressLocation mShelterLocation;

    public ShelterModel(){
    }

    public ShelterModel(String name, String number , String street) {
        this.Name = name;
        this.Street = street.trim();
        this.Number = number.trim();
    }

    /** calculates the location of a street by name **/
    public void findShelterLocation(Activity activity, String name){
        mShelterLocation = UtilitiesFunc.getLocation(name,activity);
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
        this.Street = mStreet.trim();
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String mNumber) {
        this.Number = mNumber.trim();
    }

    public AddressLocation getShelterLocation() {
        return mShelterLocation;
    }

    public void setShelterLocation(AddressLocation mShelterLocation) {
        this.mShelterLocation = mShelterLocation;
    }
}
