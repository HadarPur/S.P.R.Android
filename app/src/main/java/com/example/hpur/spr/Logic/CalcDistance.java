package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.hpur.spr.Logic.Models.ShelterModel;
import com.example.hpur.spr.Logic.Queries.CallableDistArr;
import com.example.hpur.spr.UI.NavigationActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcDistance implements CallableDistArr {
    private static final int NUM_OF_THREADS =14;

    private static final String TAG ="Radar";
    private GoogleMap mMap;
    private ArrayList<ShelterModel> mShelters;
    private NavigationActivity mActivityShelters;
    private ArrayList<ShelterModel> mShelterOnRadar;
    private MarkerOptions mMarkerOptionsShelterLocation;
    private ArrayList<ShelterModel> mTopFiveShelters;
    private DistanceClass[] mTasks;
    private int mCount;
    private double mLat;
    private double mLong;

    //c'tor
    public CalcDistance(GoogleMap mMap, ArrayList<ShelterModel> mShelters, NavigationActivity activity, double currentLatitude, double currentLongitude) {
        this.mMap = mMap;
        this.mShelters = mShelters;
        this.mActivityShelters = activity;
        this.mLat = currentLatitude;
        this.mLong = currentLongitude;
        this.mShelterOnRadar = new ArrayList<>();
        this.mCount = 0;
        this.mTasks = new DistanceClass[NUM_OF_THREADS];

        findNearbyShelters(activity);
    }

    //set markers
    private void showMarker(ShelterModel shelter, Activity activity){
        int status=0;
        DecimalFormat df = new DecimalFormat("#.##");
        Log.d(TAG,"status : "+status);
        AddressLocation loc = shelter.getShelterLocation();

        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMarkerOptionsShelterLocation = new MarkerOptions();
        mMarkerOptionsShelterLocation.position(latLng);
        mMarkerOptionsShelterLocation.title("ShelterModel name: "+ shelter.getName());
        mMarkerOptionsShelterLocation.snippet("Location: " + shelter.getStreet() + " " + shelter.getNumber()+", "+shelter.getCity());
        mMarkerOptionsShelterLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mMap.addMarker(mMarkerOptionsShelterLocation);
        //move map camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11), 5000, null);
        Log.d(TAG,"map----done");
    }

    //search the near by shelters
    private void findNearbyShelters(Activity activity){
        ExecutorService ex = Executors.newFixedThreadPool(NUM_OF_THREADS);

        for(int i=0; i< NUM_OF_THREADS; i++) {
            mTasks[i] = new DistanceClass(mShelters, NUM_OF_THREADS, mLat, mLong, activity,this);
            ex.execute(mTasks[i]);
        }
        if(ex.isTerminated()) {
            ex.shutdown();
            Log.d(TAG,"all done");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public synchronized void filterDistance(ArrayList<ShelterModel> shelters) {
        mTopFiveShelters = new ArrayList<>();
        mShelterOnRadar.addAll(shelters);
        mCount++;

        if(mCount == NUM_OF_THREADS) {
            mActivityShelters.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(mShelterOnRadar,new SheltersComparator(mLat, mLong));
                    int num = 0 ;
                    while (mTopFiveShelters.size() < 5) {
                        if (!mTopFiveShelters.contains(mShelterOnRadar.get(num)))
                            mTopFiveShelters.add(mShelterOnRadar.get(num));
                        num++;
                    }

                    for(int i = 0; i < mTopFiveShelters.size(); i++) {
                        showMarker(mTopFiveShelters.get(i), mActivityShelters);
                    }
                    mActivityShelters.doneLoadingPage();
                }
            });
        }
    }
}
