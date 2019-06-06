package com.jesperbergstrom.golfgps.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

public class MyLocationListener implements LocationListener {

    private TextView locationText;
    private double targetLat = 56.996755;
    private double targetLong = 13.254280;

    public void setLocationText(TextView locationText) {
        this.locationText = locationText;
    }

    @Override
    public void onLocationChanged(Location loc) {
        locationText.setText(String.valueOf(calculateDistance(loc.getLatitude(), loc.getLongitude())));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private double calculateDistance(double latitude, double longitude) {
        int R = 6371000;
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(targetLat);
        double deltaLat = Math.toRadians(targetLat - latitude);
        double deltaLong = Math.toRadians(targetLong - longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;
        return d;
    }
}
