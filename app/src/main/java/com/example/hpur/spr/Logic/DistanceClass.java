package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.util.Log;
import com.example.hpur.spr.Logic.Queries.CallableDistArr;

public class DistanceClass implements Runnable{
    private static final String TAG = "DistanceClass";
    private Shelter mSheltersOld;
    private Activity mActivity;
    private CallableDistArr mCallArr;
    private int mNumOfShelters;

    // c'tor
    public DistanceClass(Shelter sheltersOld ,Activity activity, CallableDistArr callArr){
        this.mActivity = activity;
        this.mSheltersOld = sheltersOld;
        this.mCallArr = callArr;
        this.mNumOfShelters = 5;
    }

    // the runnable method
    @Override
    public void run() {
        Shelter shelter;

        shelter = mSheltersOld;
        String add = shelter.getStreet() + " " + shelter.getNumber()+", "+shelter.getCity();
        shelter.findShelterLocation(mActivity, add);
        Log.d(TAG,"Shelter name size: "+shelter.getName() + " City: " + shelter.getCity());

        mCallArr.filterDistance(shelter, mNumOfShelters);
    }
}
