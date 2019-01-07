package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
    private static final String TAG ="Radar";
    private GoogleMap mMap;
    private ArrayList<Shelter> mShelters;
    private NavigationActivity mActivityShelters;
    private ArrayList<Shelter> mShelterOnRadar;
    private MarkerOptions mMarkerOptionsShelterLocation;
    private ArrayList<Shelter> mTopFiveShelters;
    private DistanceClass[] mTasks;
    private int mCount;
    private double mLat;
    private double mLong;

    //c'tor
    public CalcDistance(GoogleMap mMap, ArrayList<Shelter> mShelters, NavigationActivity activity, double currentLatitude, double currentLongitude) {
        this.mMap = mMap;
        this.mShelters = mShelters;
        this.mActivityShelters = activity;
        this.mLat = currentLatitude;
        this.mLong = currentLongitude;
        this.mShelterOnRadar = new ArrayList<>();
        this.mCount = 0;
        this.mTasks = new DistanceClass[mShelters.size()];

        findNearbyShelters(activity);
    }

    //set markers
    private void showMarker(Shelter shelter, Activity activity){
        int status=0;
        DecimalFormat df = new DecimalFormat("#.##");
        Log.d(TAG,"status : "+status);
        ShelterLocation loc = shelter.getShelterLocation();

        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMarkerOptionsShelterLocation = new MarkerOptions();
        mMarkerOptionsShelterLocation.position(latLng);
        mMarkerOptionsShelterLocation.title("Shelter name: "+ shelter.getName());
        mMarkerOptionsShelterLocation.snippet("Location: " + shelter.getStreet() + " " + shelter.getNumber()+", "+shelter.getCity());
        mMarkerOptionsShelterLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mMap.addMarker(mMarkerOptionsShelterLocation);
        //move map camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9), 5000, null);
        Log.d(TAG,"map----done");
    }

    //search the near by shelters
    private void findNearbyShelters(Activity activity){
        ExecutorService ex = Executors.newFixedThreadPool(mShelters.size());

        for(int i=0; i< mShelters.size(); i++) {
            mTasks[i] = new DistanceClass(mShelters.get(i), activity,this);
            ex.execute(mTasks[i]);
        }
        if(ex.isTerminated()) {
            ex.shutdown();
            Log.d(TAG,"all done");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public synchronized void filterDistance(Shelter shelters, int numOfNeededShelters) {
        mShelterOnRadar.add(shelters);
        mCount++;
        if(mCount == mShelters.size()){
            Collections.sort(mShelterOnRadar,new SheltersComparator(this.mLat, this.mLong));
            Log.d(TAG,"mShelterOnRadar size: "+mShelterOnRadar.size());

            mTopFiveShelters = new ArrayList<>();
            for (int i=0 ; i < numOfNeededShelters && i < mShelterOnRadar.size() ; i++) {
                mTopFiveShelters.add(mShelterOnRadar.get(i));
                Log.d(TAG,"Shelter name size: "+mShelterOnRadar.get(i).getName() + " City: " + mShelterOnRadar.get(i).getCity());
            }

            mActivityShelters.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < mTopFiveShelters.size(); i++) {
                        showMarker(mTopFiveShelters.get(i), mActivityShelters);
                    }
                    mActivityShelters.doneLoadingPage();
                }
            });
        }
    }
}
