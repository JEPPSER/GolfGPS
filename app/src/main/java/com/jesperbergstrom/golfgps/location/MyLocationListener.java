package com.jesperbergstrom.golfgps.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.jesperbergstrom.golfgps.MainActivity;

public class MyLocationListener implements LocationListener {

    private MainActivity main;

    public MyLocationListener(Context context) {
        super();
        main = (MainActivity) context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        main.setPlayerPosition(loc.getLatitude(), loc.getLongitude());
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
}
