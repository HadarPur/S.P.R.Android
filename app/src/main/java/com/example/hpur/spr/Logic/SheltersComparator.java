package com.example.hpur.spr.Logic;

import java.util.Comparator;

public class SheltersComparator implements Comparator<Shelter> {
    private double mLat, mLong;

    // c'tor
    public SheltersComparator(double currentLatitude, double currentLongitude) {
        this.mLong = currentLongitude;
        this.mLat = currentLatitude;
    }

    // compare method to sort by closest dist shelters
    @Override
    public int compare(Shelter o1, Shelter o2) {
        if (o1.getShelterLocation() != null && o2.getShelterLocation() != null) {
            double lat1 = o1.getShelterLocation().getLatitude();
            double lon1 = o1.getShelterLocation().getLongitude();

            double lat2 = o2.getShelterLocation().getLatitude();
            double lon2 = o2.getShelterLocation().getLongitude();

            double distanceToPlace1 = distance(this.mLat, this.mLong, lat1, lon1);
            double distanceToPlace2 = distance(this.mLat, this.mLong, lat2, lon2);
            return (int) (distanceToPlace1 - distanceToPlace2);
        }
        else if (o1.getShelterLocation() != null) {
            double lat1 = o1.getShelterLocation().getLatitude();
            double lon1 = o1.getShelterLocation().getLongitude();

            return (int) distance(this.mLat, this.mLong, lat1, lon1);
        }
        else if(o2.getShelterLocation() != null) {
            double lat2 = o2.getShelterLocation().getLatitude();
            double lon2 = o2.getShelterLocation().getLongitude();

            return (int) distance(this.mLat, this.mLong, lat2, lon2);
        }
        return 0;
    }

    // calculate dist between the shelters location to user's current location
    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(Math.pow(Math.sin(deltaLat/2), 2) + Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}
