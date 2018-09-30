package com.example.jiheepyo.ggung;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.concurrent.ExecutionException;

public class MyLocation implements LocationListener{
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean loopFlag = false;
    private boolean isSingleCalled = false;
    private Activity mainActivity;
    private Activity mapsActivity;
    private LocationManager locationManager;
    private Context context;
    private Location curLocation = null;
    private final static long UPDATE_MIN_TIME = 1000 * 1 * 1;
    private final static long UPDATE_MIN_DISTANCE = 10;
    MyLocation(Context context){
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setMainActivity(Activity activity){
        mainActivity = activity;
    }
    public void setMapsActivity(Activity activity){
        mapsActivity = activity;
    }
    @SuppressLint("MissingPermission")
    public boolean findLocation(boolean loop){
        loopFlag = loop;
        boolean flag = false;
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled){
            if(loop)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_MIN_TIME, UPDATE_MIN_DISTANCE, this);
            else {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                isSingleCalled = false;
            }
            flag = true;
        }
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isNetworkEnabled){
            if(loop)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MIN_TIME, UPDATE_MIN_DISTANCE, this);
            else {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                isSingleCalled = false;
            }
            flag = true;
        }
        return flag;
    }

    public void removeLocationUpdates(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        curLocation = location;
        if(!loopFlag){
            if(!isSingleCalled) {
                ((MainActivity) mainActivity).toMapActivity();
                isSingleCalled = true;
            }
        }else{
            try {
                ((MapsActivity)mapsActivity).renewPosition();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        locationManager.removeUpdates(this);
    }

    public Location getCurLocation() {
        return curLocation;
    }
}
