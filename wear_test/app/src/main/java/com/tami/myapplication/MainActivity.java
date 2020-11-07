package com.tami.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.io.InputStream;
import java.net.Socket;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class MainActivity extends WearableActivity {

    private TextView label_name,label_score;
    private LocationManager locationManager;
    private Button button_sendRequest;
    String str_lon="112",str_lat="33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        label_name = (TextView) findViewById(R.id.textView_name);
        Quanxian();
        button_sendRequest = (Button)findViewById(R.id.button9);

        button_sendRequest.setOnClickListener(new OnClickListener() {


        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            sendRequestWithHttpClient();
        }
         });
        // Enables Always-on
        setAmbientEnabled();
    }

    public void Quanxian(){
        locationManager = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BRG","没有权限");
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                Toast.makeText(MainActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_LONG).show();

            } else {
                InitGPS();
                // 有权限了，去放肆吧。
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("BRG","系统检测到未开启GPS定位服务");
            Toast.makeText(MainActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }

    protected void InitGPS(){



        //if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//无海拔要求   criteria.setBearingRequired(false);//无方位要求
        criteria.setCostAllowed(true);//允许产生资费   criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        // 获取最佳服务对象
        String provider = mLocationManager.getBestProvider(criteria, true);
        Log.e("gps", "InitGPS: " + provider);
//        label.setText(provider);
        try{
            Location location = mLocationManager.getLastKnownLocation(provider);
            mLocationManager.requestLocationUpdates(provider, 2000, 2, locationListener);
        }catch(SecurityException ex)
        {
//            label.setText("not access");
        }
        //}
    }

    public LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            str_lon = String.valueOf(longitude);
            str_lat = String.valueOf(latitude);
//            label.setText(str_lon+":"+str_lat);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void sendRequestWithHttpClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {

//                HttpURLConnection conn = null;
                while (true) {
                    try {

                        Thread.sleep(2000);
                        Socket socket = new Socket("182.92.114.73", 8899);
                        OutputStream outputStream = socket.getOutputStream();
                        JSONObject json = new JSONObject();
                        json.put("type", "updategps");
                        json.put("id", label_name.getText());
                        json.put("lon",str_lon);
                        json.put("lat",str_lat);
                        Log.d(this.toString(), "send: "+json.toString());
                        outputStream.write(json.toString().getBytes());
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[2048];
                        int len = inputStream.read(buffer);
                        String rev = new String(buffer,0,len);
                        Log.d(this.toString(), "rev: "+rev);
                        socket.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }
}
