package com.example.hpur.spr.Logic;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import com.example.hpur.spr.UI.NavigationActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map implements OnMapReadyCallback {
    private static final String TAG = "Map";
    private double mLatitude, mLongitude;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private MarkerOptions mMarkerOptionsMyLocation;

    public Map(SupportMapFragment mapFragment, double latitude, double longitude, Context context) {
        this.mMapFragment = mapFragment;
        this.mLatitude = latitude;
        this.mLongitude = longitude;

        this.mMapFragment.getMapAsync(this);
        this.mGeocoder = new Geocoder(context);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            this.mMap = googleMap;
            Log.d(TAG,"set my location");
            setMyLocationOnTheMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //set user location on the map
    public void setMyLocationOnTheMap() throws IOException{
        //place users markers
        this.mMap.clear();

        //Place current location marker
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        this.mMarkerOptionsMyLocation = new MarkerOptions();

        //get street name
        String street = getLocationName(mLatitude, mLongitude);

        //Place current location marker
        this.mMarkerOptionsMyLocation.position(latLng);
        this.mMarkerOptionsMyLocation.title("Current Position");


        this.mMarkerOptionsMyLocation.snippet("Location: " + street);
        this.mMap.addMarker(mMarkerOptionsMyLocation);

        //move map camera
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

    }

    public void showShelters(ArrayList<Shelter> shelters, NavigationActivity activity )  {
        try {

            LatLng latLng = null;
            setMyLocationOnTheMap();
            for (int i=0; i<shelters.size(); i++) {
                String add = shelters.get(i).getStreet() + " " + shelters.get(i).getNumber()+", "+shelters.get(i).getCity();
                Log.d(TAG, "address: "+ add);

                shelters.get(i).findStreetLocation(activity,add);
                ShelterLocation loc = shelters.get(i).getShelterLocation();
                Log.d(TAG, "loc.getLatitude(): "+ loc.getLatitude() + " loc.getLongitude(): " + loc.getLongitude());

                //Place current location marker
                latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                this.mMarkerOptionsMyLocation = new MarkerOptions();

                //Place current location marker
                this.mMarkerOptionsMyLocation.position(latLng);
                this.mMarkerOptionsMyLocation.title("Shelter name: "+shelters.get(i).getName());
                this.mMarkerOptionsMyLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                this.mMarkerOptionsMyLocation.snippet("Location: " + add);
                this.mMap.addMarker(mMarkerOptionsMyLocation);
            }

            //move map camera
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11), 5000, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //geocode function return street name
    public String getLocationName(double latitude, double longitude) throws IOException {
        //to specific address
        List<Address> ls = this.mGeocoder.getFromLocation(latitude, longitude, 1);
        android.location.Address address = ls.get(0);
        //get current province/City
        String street = address.getAddressLine(0);

        return street;
    }
}
