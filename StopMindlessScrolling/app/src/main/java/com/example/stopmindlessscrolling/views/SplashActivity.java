package com.example.stopmindlessscrolling.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.service.MonitorService;
import com.example.stopmindlessscrolling.utility.Utility;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LoadScreen();
    }

    public void LoadScreen() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, PermissionsActivity.class);
                startActivity(i);
            }
        }, 2000); // you can increase or decrease the timelimit of your screen
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            Log.e("TAG", "isServiceRunning: "+ Utility.isServiceRunning(this) );

            if (!Utility.isServiceRunning(this)){
                startService(new Intent(SplashActivity.this, MonitorService.class));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}