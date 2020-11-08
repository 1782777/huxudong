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
import android.widget.CompoundButton;
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
import android.widget.ToggleButton;

import com.tami.myapplication.myserver;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GpsActivity";
    EditText editText;
    TextView textView,textView_lon,textView_lat;
    Button btm;
    ToggleButton toggle;
    Intent intent ;

    private MyReceiver receiver=null;
    private Intent mIntent;
    private LocationManager lm;


    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        btm = findViewById(R.id.button);
        textView = findViewById(R.id.textview);
        textView_lon = findViewById(R.id.textView_lon);
        textView_lat = findViewById(R.id.textView_lat);


        toggle =findViewById(R.id.toggleButton);


        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.project.moli.demobroad.MyService");
        MainActivity.this.registerReceiver(receiver,filter);
        intent = new Intent(this,myserver.class);


        ///////////////////////////////////////////////////
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if(arg1){
                    intent.putExtra("name", editText.getText().toString());
                    startService(intent);
                }else{
                    stopService(intent);
                }

            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
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

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String lon=bundle.getString("lon");
            String lat=bundle.getString("lat");
//            Log.e("ACTICITY", lon);
            textView_lon.setText(lon);
            textView_lat.setText(lat);
        }
    }


}
