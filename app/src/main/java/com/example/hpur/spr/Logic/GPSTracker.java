package com.example.hpur.spr.Logic;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
        private static final String TAG = GPSTracker.class.getSimpleName();
        private final int PERMISSION_LOCATION_CODE = 1234;
        private final Activity mActivity;
        private boolean mIsGPSEnabled = false;
        private boolean mIsNetworkEnabled = false;
        private boolean mIsGPSPermitted = true;
        private Location mLocation;
        protected LocationManager mLocationManager;

        public GPSTracker(Activity activity, boolean firstAction) {
            this.mActivity = activity;
            if (firstAction == true)
                setFirstCounter();
            initLocation();
        }

        //Create a GetLocation Method //
        public  void initLocation() {
            try {
                this.mLocationManager = (LocationManager) this.mActivity.getSystemService(LOCATION_SERVICE);
                this.mIsGPSEnabled = this.mLocationManager.isProviderEnabled(this.mLocationManager.GPS_PROVIDER);
                this.mIsNetworkEnabled = this.mLocationManager.isProviderEnabled(this.mLocationManager.NETWORK_PROVIDER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // for new versions
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                        requestLocation();
                    }
                    else {
                        boolean alreadyAsked = getCounter(PERMISSION_LOCATION_CODE) > 0;
                        if (!alreadyAsked) {
                            ActivityCompat.requestPermissions(this.mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_CODE);
                            incrementCounter(PERMISSION_LOCATION_CODE);
                        }
                        else {
                            this.mIsGPSPermitted =false;
                            this.mActivity.finish();
                        }
                    }
                }
                else {
                    // for older versions
                    requestLocation();
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void setFirstCounter() {
            SharedPreferences sharedPreferences = this.mActivity.getSharedPreferences("permissionCounters", Context.MODE_PRIVATE);
            String sharedPreferencesKey=String.valueOf(PERMISSION_LOCATION_CODE);
            int permissionCounter=0;
            sharedPreferences.edit().putInt(sharedPreferencesKey, permissionCounter).apply();
        }

        private int getCounter(int permissionCode) {
            SharedPreferences sharedPreferences = this.mActivity.getSharedPreferences("permissionCounters", Context.MODE_PRIVATE);
            String sharedPreferencesKey=String.valueOf(permissionCode);
            int permissionCounter=sharedPreferences.getInt(sharedPreferencesKey, 0);
            Log.d(TAG, "counter value (get counter): " +permissionCounter);
            return permissionCounter;
        }

        public void incrementCounter(int permissionCode) {
            SharedPreferences sharedPreferences = this.mActivity.getSharedPreferences("permissionCounters", Context.MODE_PRIVATE);
            String sharedPreferencesKey=String.valueOf(permissionCode);
            int permissionCounter=sharedPreferences.getInt(sharedPreferencesKey, 0);
            Log.d(TAG, "counter value (increment): " +permissionCounter);
            sharedPreferences.edit().putInt(sharedPreferencesKey, permissionCounter+1).apply();
        }

        /**
         * Already permitted
         */
        @SuppressWarnings("MissingPermission")
        private void requestLocation() {
            if (this.mIsGPSEnabled) {
                if (this.mLocation == null) {
                    this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
                    if (this.mLocationManager != null)
                        this.mLocation = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            // if lcoation is not found from GPS than it will found from network //
            if (this.mLocation == null) {
                if (this.mIsNetworkEnabled) {
                    this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                    if (this.mLocationManager != null)
                        this. mLocation = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

        public boolean ismIsGPSPermitted() {
            return this.mIsGPSPermitted;
        }

        public boolean getGPSEnable(){
            return this.mIsGPSEnabled;
        }

        public Location getPosition(){
            return this.mLocation;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onLocationChanged(Location location) {}

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
}
