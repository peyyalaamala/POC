package com.example.stopmindlessscrolling.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.stopmindlessscrolling.PermissionsActivity;
import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.views.ConfigureAppsActivity;
import com.example.stopmindlessscrolling.views.HomeActivity;


public class SettingsFragment extends Fragment implements View.OnClickListener {


    private TextView configureAppsTxt;
    private Intent intent;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        configureAppsTxt=view.findViewById(R.id.configureAppsTxt);

        configureAppsTxt.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.configureAppsTxt:
                 intent=new Intent(getContext(), ConfigureAppsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
