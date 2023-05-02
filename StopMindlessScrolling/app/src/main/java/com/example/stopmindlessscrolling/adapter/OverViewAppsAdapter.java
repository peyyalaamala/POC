package com.example.stopmindlessscrolling.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;

import java.util.List;
import java.util.Set;

public class OverViewAppsAdapter extends RecyclerView.Adapter<OverViewAppsAdapter.MyViewHolder> {

    private final List<String> appsList;
    private final Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Drawable icon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView appNameTxt;
        public ImageView appIcon;

        public MyViewHolder(View view) {
            super(view);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
            appNameTxt = (TextView) view.findViewById(R.id.appNameTxt);
        }
    }


    public OverViewAppsAdapter(List<String> appsList, Context context) {
        this.appsList = appsList;
        this.context = context;
        sharedPreferences= context.getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overview_apps_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("MutatingSharedPrefs")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        try
        {
            icon = context.getPackageManager().getApplicationIcon(appsList.get(position));
            holder.appIcon.setImageDrawable(icon);
            holder.appNameTxt.setText(getApplicationName(context,appsList.get(position)));


        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return appsList.size();
//        return (Math.min(appsList.size(), 5));
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