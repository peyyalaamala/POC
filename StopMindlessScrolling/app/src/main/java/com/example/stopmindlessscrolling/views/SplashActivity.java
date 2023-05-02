package com.example.stopmindlessscrolling.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.service.MonitorService;
import com.example.stopmindlessscrolling.utility.Utility;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



    }


    private boolean checkPermissions() {
        boolean permissionGranted=false;
        if ((Utility.getUsageStatsList(SplashActivity.this) != null
                && Utility.getUsageStatsList(SplashActivity.this).isEmpty())){
        }else {
            permissionGranted=true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(SplashActivity.this)){
                permissionGranted=true;
            }
        }else {
            permissionGranted=true;
        }

        return permissionGranted;
    }

    public void LoadScreen() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {


            Log.e("TAG", "checkPermissions(): "+checkPermissions() );
            if (checkPermissions()){
                 intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }else {

                intent = new Intent(SplashActivity.this, PermissionsActivity.class);
                startActivity(intent);
                finishAffinity();
            }


        }, 2000); // you can increase or decrease the timelimit of your screen
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            Log.e("TAG", "isServiceRunning: "+ Utility.isServiceRunning(this) );

            if (!Utility.isServiceRunning(this)){
                Intent serviceIntent = new Intent ( SplashActivity.this, MonitorService.class );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService ( serviceIntent );
                } else {
                    startService ( serviceIntent );
                }
            }


                LoadScreen();



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}