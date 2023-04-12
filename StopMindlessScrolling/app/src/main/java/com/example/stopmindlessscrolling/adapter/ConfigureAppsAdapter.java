package com.example.stopmindlessscrolling.adapter;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;

import java.util.List;

public class ConfigureAppsAdapter extends RecyclerView.Adapter<ConfigureAppsAdapter.MyViewHolder> {

    private final List<AppInfo> appsList;
    private final Context context;
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


    public ConfigureAppsAdapter(List<AppInfo> appsList, Context context) {
        this.appsList = appsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.configure_apps_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AppInfo appInfo = appsList.get(position);
        try
        {
            icon = context.getPackageManager().getApplicationIcon(appInfo.packageName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        holder.appIcon.setImageDrawable(icon);
        holder.appNameTxt.setText(getApplicationName(context, appInfo.getPackageName()));
        holder.appcheckBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }
    public static String getApplicationName(Context context,String packageName) {
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