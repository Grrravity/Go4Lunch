package com.error.grrravity.go4lunch.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.error.grrravity.go4lunch.R;

public class GPS extends Service implements LocationListener {

    private final Context mContext;

    private boolean canLocalize = false;

    private double mLatitude;
    private double mLongitude;

    private Location mLocation;

    // MIN TIME AND DISTANCE BETWEEN UPDATES
    private static final long MIN_DISTANCE_UPDATES = 10;
    private static final long MIN_TIME_UPDATE = 1000 * 60;

    public GPS(Context context){
        this.mContext = context;
        getLocation();
    }

    private void getLocation(){
        try {
            LocationManager locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            boolean isGPSEnable = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isNetworkEnable = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable && !isNetworkEnable){
                showParamAlert();
            } else {
                this.canLocalize = true;
                // Get location from network
                if (isNetworkEnable){
                    if(ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){

                        //get last known location
                        mLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        // take saved location
                        if (mLocation != null){
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                        // request a new location from existing location manager
                        else {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_UPDATE,
                                    MIN_DISTANCE_UPDATES,
                                    this);
                        }
                    }
                }
            }

            //Get location from GPS
            if (isGPSEnable){
                //get last known location
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // take saved location
                if(mLocation != null) {
                    mLatitude = mLocation.getLatitude();
                    mLongitude = mLocation.getLongitude();
                }
                //request new location
                else{
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_UPDATE,
                            MIN_DISTANCE_UPDATES,
                            this);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // GETTERS

    public double getLatitude(){
        if (mLocation != null){
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    public double getLongitude(){
        if (mLocation != null){
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    public boolean canLocalize(){
        return this.canLocalize;
    }

    private void showParamAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(R.string.enableGPS);
        alertDialog.setMessage(R.string.enableGPSMessage);
        alertDialog.setPositiveButton(R.string.menu_settings, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        });
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(this.canLocalize()){
            mLongitude = mLocation.getLongitude();
            mLatitude = mLocation.getLatitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
