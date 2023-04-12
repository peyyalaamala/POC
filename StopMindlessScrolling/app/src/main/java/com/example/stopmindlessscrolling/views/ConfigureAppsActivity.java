package com.example.stopmindlessscrolling.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.adapter.AppInfo;
import com.example.stopmindlessscrolling.adapter.ConfigureAppsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigureAppsActivity extends AppCompatActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_apps);


        // data to populate the RecyclerView with

        SharedPreferences prefs = getSharedPreferences("prefs", 0);

        Set<String> stringSet=prefs.getStringSet("installedApps",null);


        List<AppInfo> appInfoArrayList = new ArrayList<>();
     
        for (String packageName:stringSet) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfoArrayList.add(appInfo);
        }
        Log.e("TAG", "appInfoArrayList: "+appInfoArrayList.size() );
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConfigureAppsAdapter adapter = new ConfigureAppsAdapter(appInfoArrayList,this);
        recyclerView.setAdapter(adapter);

    }
}