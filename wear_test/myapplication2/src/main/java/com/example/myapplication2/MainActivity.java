package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationManager lm;
    String bestProvider="1111111111111111111111111111111111111111111111111111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        bestProvider = lm.getBestProvider(getCriteria(), true);
        Log.e("tag", "onCreate: "+bestProvider );
        Log.e("tag", "onCreate: "+lm );
        try {
            lm.requestLocationUpdates(bestProvider, 5000, 10, locationListener);
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
                Log.e("tag", "gps!!!!!!!!!!!!!!!!!: "+lon+lat );
            } catch (SecurityException ex) {
//                textView_lat.setText("no init gps location");
            }

        }
    };

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}
