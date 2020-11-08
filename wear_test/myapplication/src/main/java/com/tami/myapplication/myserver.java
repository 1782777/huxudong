package com.tami.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


import androidx.annotation.Nullable;

public class myserver extends Service {
    private static final String TAG = "myserver";
    String name;
    String str_lon="0",str_lat="0";
    double lon=0.0,lat=0.0;
    Handler handler =null;
    LocationManager mLocationManager;
    Criteria criteria;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate:");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand:");
        name=intent.getStringExtra("name");
        Log.e(TAG, "onStartCommand: "+name);
        InitGPS();
//        sendRequestWithHttpClient();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy:");
        super.onDestroy();

    }


    private void sendRequestWithHttpClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {

//                HttpURLConnection conn = null;

                try {


                    Socket socket = new Socket("182.92.114.73", 8899);
                    OutputStream outputStream = socket.getOutputStream();
                    JSONObject json = new JSONObject();
                    json.put("type", "updategps");
                    json.put("id",name);
                    json.put("lon",str_lon);
                    json.put("lat",str_lat);
                    Log.d(this.toString(), "send: "+json.toString());
                    outputStream.write(json.toString().getBytes());
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[2048];
                    int len = inputStream.read(buffer);
                    String rev = new String(buffer,0,len);
                    Log.e(this.toString(), "rev: "+rev);
//                    Intent intent=new Intent();
//                    intent.putExtra("score", rev);
//                    intent.setAction("com.project.moli.demobroad.MyService");
//                    sendBroadcast(intent);
                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        }).start();
    }

    protected void InitGPS(){



        //if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//无海拔要求   criteria.setBearingRequired(false);//无方位要求
        criteria.setCostAllowed(true);//允许产生资费   criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        // 获取最佳服务对象
        String provider = mLocationManager.getBestProvider(criteria, true);
        Log.e("gps", "InitGPS: " + provider);
//        label.setText(provider);
        try{
//            Location location = mLocationManager.getLastKnownLocation(provider);
            mLocationManager.requestLocationUpdates(provider, 10000, 5, locationListener);
        }catch(SecurityException ex)
        {
//            label.setText("not access");
        }
        //}


    }

    public LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lon = location.getLongitude();
            lat = location.getLatitude();
            str_lon = String.valueOf(lon);
            str_lat = String.valueOf(lat);
            Log.e(TAG, "onLocationChanged: "+str_lon+"::"+str_lat );
            sendRequestWithHttpClient();
            Intent intent=new Intent();
            intent.putExtra("lon", str_lon);
            intent.putExtra("lat", str_lat);
            intent.setAction("com.project.moli.demobroad.MyService");
            sendBroadcast(intent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            String bestProvider = mLocationManager.getBestProvider(criteria, true);

            try {
                Location location = mLocationManager.getLastKnownLocation(bestProvider);
                lon = location.getLongitude();
                lat = location.getLatitude();
                str_lon = String.valueOf(lon);
                str_lat = String.valueOf(lat);
                sendRequestWithHttpClient();
                Intent intent=new Intent();
                intent.putExtra("lon", str_lon);
                intent.putExtra("lat", str_lat);
                intent.setAction("com.project.moli.demobroad.MyService");
                sendBroadcast(intent);

            }catch(SecurityException ex)
            {
//                textView_lat.setText("no init gps location");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



}
