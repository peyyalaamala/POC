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
import com.example.stopmindlessscrolling.utility.AppConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigureAppsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_apps);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // data to populate the RecyclerView with


        List<AppInfo> appInfoArrayList = new ArrayList<>();
        sharedPreferences= getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Set<String> selectedApps=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
        Set<String> unselectedApps=sharedPreferences.getStringSet(AppConstants.UNSELECTEDAPPS,new HashSet<>());

        Log.e("TAG", "selectedApps:size "+selectedApps.size());
        Log.e("TAG", "unselectedApps:size "+unselectedApps.size());
        Log.e("TAG", "TEST:size "+sharedPreferences.getInt(AppConstants.TEST,0));

        for (String packageName:selectedApps) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setSelected(true);
            appInfoArrayList.add(appInfo);
        }

        for (String packageName:unselectedApps) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setSelected(false);
            appInfoArrayList.add(appInfo);
        }
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConfigureAppsAdapter adapter = new ConfigureAppsAdapter(appInfoArrayList,this);
        recyclerView.setAdapter(adapter);

    }
}