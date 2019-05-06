package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.util.Log;
import com.example.hpur.spr.Logic.Queries.CallableDistArr;

import java.util.ArrayList;

public class DistanceClass implements Runnable{
    private static final String TAG = "DistanceClass";
    private ArrayList<Shelter> mSheltersOld;
    private ArrayList<Shelter> mSheltersNew;
    private int mNumOfThreads;
    private double mLat;
    private double mLong;
    private Activity mActivity;
    private CallableDistArr mCallArr;
    private int mNumOfShelters;

    // c'tor
    public DistanceClass(ArrayList<Shelter> sheltersOld , int numOfThreads, double lat, double ltg, Activity activity, CallableDistArr callArr){
        this.mSheltersOld = new ArrayList<>();
        this.mSheltersNew = new ArrayList<>();
        this.mNumOfThreads = numOfThreads;
        this.mLat = lat;
        this.mLong = ltg;
        this.mActivity = activity;
        this.mCallArr = callArr;
        this.mNumOfShelters = 5;

        this.mSheltersOld.addAll(sheltersOld);
    }

    // the runnable method
    @Override
    public void run() {
        int section;
        Shelter shelter;

        int tNum = (int) (Thread.currentThread().getId() % mNumOfThreads + 1);
        if (tNum < mNumOfThreads) {
            section = mSheltersOld.size() / mNumOfThreads;
        }
        else {
            section = mSheltersOld.size() % mNumOfThreads;
        }

        for (int i = (tNum - 1) * section; i < tNum * section && i < mSheltersOld.size() ; i++) {
            shelter = mSheltersOld.get(i);
            String add = shelter.getStreet() + " " + shelter.getNumber()+", "+shelter.getCity();
            shelter.findShelterLocation(mActivity, add);

            mSheltersNew.add(shelter);
        }

        mCallArr.filterDistance(mSheltersNew);
    }

    //getter
    public ArrayList<Shelter> getNewShelters() {
        return mSheltersNew;
    }
}
