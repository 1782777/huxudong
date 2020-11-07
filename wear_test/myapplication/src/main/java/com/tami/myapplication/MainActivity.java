package com.tami.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import com.tami.myapplication.myserver;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GpsActivity";
    EditText editText;
    TextView textView,textView_lon,textView_lat;
    Button btm;

    private MyReceiver receiver=null;
    private Intent mIntent;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        btm = findViewById(R.id.button);
        textView = findViewById(R.id.textview);
        textView_lon = findViewById(R.id.textView_lon);
        textView_lat = findViewById(R.id.textView_lat);
//        sendRequestWithHttpClient();


        GpsCheck();

        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.project.moli.demobroad.MyService");
        MainActivity.this.registerReceiver(receiver,filter);
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpsCheck();
//                Intent start = new Intent(view.getContext(),myserver.class);
//                Log.d("btm", "onClick: "+  editText.getText());
//                start.putExtra("name", editText.getText().toString());
//                startService(start);

            }
        });
    }

    private void GpsCheck() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        } else {
            Toast.makeText(this, "GPS已经开启了...", Toast.LENGTH_SHORT).show();
        }
        // 为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
//        try {
//            Location location = lm.getLastKnownLocation(bestProvider);
//        }
//        catch(SecurityException ex)
//        {
//            Toast.makeText(this, "gps更新失败了...", Toast.LENGTH_SHORT).show();
//        }
//        Location location = lm.getLastKnownLocation(bestProvider);
//        updateView(location);
        // 监听状态
//        lm.addGpsStatusListener(l
        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        try {
            lm.requestLocationUpdates(bestProvider, 1000, 1, locationListener);
        }
        catch(SecurityException ex)
        {
            Toast.makeText(this, "gps更新失败了...", Toast.LENGTH_SHORT).show();
        }
    }


    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
//            updateView(location);
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
            String lon = "经度：" + location.getLongitude();
            String lat = "经度：" + location.getLatitude();
//            textView_lon.setText(lon);
//            textView_lat.setText(lat);
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            String bestProvider = lm.getBestProvider(getCriteria(), true);
            try {
                Location location = lm.getLastKnownLocation(bestProvider);
                String lon = "经度：" + location.getLongitude();
                String lat = "经度：" + location.getLatitude();
//                textView_lon.setText(lon);
//                textView_lat.setText(lat);
            }catch(SecurityException ex)
            {
//                textView_lat.setText("no init gps location");
            }

        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
//            updateView(null);
        }

    };

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    InitGPS();
//                    // 权限被用户同意。
//                    Toast.makeText(MainActivity.this, "权限被用户同意！",Toast.LENGTH_LONG).show();
//
//                } else {
//                    // 权限被用户拒绝了。
//                    Toast.makeText(MainActivity.this, "定位权限被禁止，相关地图功能无法使用！",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    }

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
        textView_lon.setText(provider);
        try{
            Location location = mLocationManager.getLastKnownLocation(provider);
            mLocationManager.requestLocationUpdates(provider, 2000, 2, locationListener);
        }catch(SecurityException ex)
        {
            textView_lon.setText("not access");
        }
        //}


    }



    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String lon=bundle.getString("lon");
            Log.e("ACTICITY", lon);
            textView.setText(lon);
        }
    }

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
                        json.put("id",editText.getText());
                        json.put("lon",25);
                        json.put("lat",33);
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
