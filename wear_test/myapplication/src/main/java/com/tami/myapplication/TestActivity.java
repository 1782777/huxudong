package com.tami.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class TestActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new GpsLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5,
//                locationListener);
    }
}
