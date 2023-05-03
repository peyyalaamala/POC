package com.example.stopmindlessscrolling.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigureAppsAdapter extends RecyclerView.Adapter<ConfigureAppsAdapter.MyViewHolder> {

    private final List<AppInfo> appsList;
    private final Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView appNameTxt;
        public ImageView appIcon;
        public CheckBox appcheckBox;

        public MyViewHolder(View view) {
            super(view);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
            appNameTxt = (TextView) view.findViewById(R.id.appNameTxt);
            appcheckBox = (CheckBox) view.findViewById(R.id.appcheckBox);
        }
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.appcheckBox.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }
    public ConfigureAppsAdapter(List<AppInfo> appsList, Context context) {
        this.appsList = appsList;
        this.context = context;
        sharedPreferences= context.getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.configure_apps_list_row, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @SuppressLint("MutatingSharedPrefs")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AppInfo appInfo = appsList.get(position);
        try
        {
            icon = context.getPackageManager().getApplicationIcon(appInfo.packageName);
            holder.appIcon.setImageDrawable(icon);
            holder.appNameTxt.setText(getApplicationName(context, appInfo.getPackageName()));
            holder.appcheckBox.setChecked(appInfo.getSelected());
            Set<String> selectedAppslatest=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
            Set<String> unselectedAppslatest=sharedPreferences.getStringSet(AppConstants.UNSELECTEDAPPS,new HashSet<>());

            if (selectedAppslatest.contains(appInfo.packageName)){
                holder.appcheckBox.setChecked(true);
            }else if (unselectedAppslatest.contains(appInfo.packageName)){
                holder.appcheckBox.setChecked(false);
            }else {
                holder.appcheckBox.setChecked(false);
            }


            holder.appcheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Set<String> selectedApps=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
                Set<String> unselectedApps=sharedPreferences.getStringSet(AppConstants.UNSELECTEDAPPS,new HashSet<>());

                if (isChecked){
                    if (unselectedApps.contains(appInfo.packageName)){
                        unselectedApps.remove(appInfo.packageName);
                        selectedApps.add(appInfo.packageName);
                        editor.putStringSet(AppConstants.SELECTEDAPPS,selectedApps);
                        editor.putStringSet(AppConstants.UNSELECTEDAPPS,unselectedApps);
                        editor.apply();
                        Toast.makeText(context,getApplicationName(context, appInfo.packageName)+" Selected",Toast.LENGTH_SHORT).show();
                    }


                }else {
                    if (selectedApps.contains(appInfo.packageName)){
                        selectedApps.remove(appInfo.packageName);
                        unselectedApps.add(appInfo.packageName);
                        editor.putStringSet(AppConstants.SELECTEDAPPS,selectedApps);
                        editor.putStringSet(AppConstants.UNSELECTEDAPPS,unselectedApps);
                        editor.apply();
                        Toast.makeText(context,getApplicationName(context, appInfo.packageName)+" Unselected",Toast.LENGTH_SHORT).show();

                    }
                }

                editor.putInt(AppConstants.TEST, selectedApps.size());
                editor.apply();

                Log.e("TAG", "SELECTEDAPPS: "+ sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>()));
                Log.e("TAG", "UNSELECTEDAPPS: "+ sharedPreferences.getStringSet(AppConstants.UNSELECTEDAPPS,new HashSet<>()));
            });

        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }
    public  String getApplicationName(Context context,String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }
}